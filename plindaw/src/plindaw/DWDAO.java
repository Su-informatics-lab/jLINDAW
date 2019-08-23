/**
 *  pLINDAW: A Fuzzy Query Based Data Warehouse System 
 *            for Real-time Pan-LINCS Data Integration and Visualization
 *
 *  Copyright (C) <2014>  <jsu@wakehealth.edu>
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package plindaw;

import java.util.Properties;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.sql.ResultSet;
import javax.sql.rowset.CachedRowSet;
import com.sun.rowset.CachedRowSetImpl;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
//import java.io.FileOutputStream;
//import java.io.OutputStream;
import java.nio.charset.Charset;
import java.io.BufferedWriter;
import java.nio.file.Files;
import java.io.IOException;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.io.File;
import java.lang.Object;
        
/**
 *
 * @author tmhjxs20
 */
public class DWDAO {
  public String dbms;
  public String jarFile;
  public String dbName; 
  public String tbDmCP2KD = "dmCP2KD_5".toLowerCase();
  public String tbDmDrugSim = "dmDrugSim".toLowerCase();
  public String tbDmKS = "dmdrugtargets_85".toLowerCase();
  public String drugSimFuzziness = "dmDrugSim_1";
  public double thDrugSim = 0;
  public String tbDmCPDesc = "dmCP_Desc".toLowerCase();
  public String userName;
  public String password;
  public String urlString;
  public Connection conn;
  public Integer maxRecords = 1000;
  
//  public ResultSet rowSet; // The ResultSet to interpret
//  public CachedRowSet cachedRowSet; // The ResultSet to interpret
//  public ResultSetMetaData metadata; // Additional information about the results
  int numcols, numrows; // How many rows and columns in the table
  
  private String driver;
  private String serverName;
  private int portNumber;
  private Properties prop;

  public Connection getConnectionToDatabase() throws SQLException {
    {
      Connection connDW = null;
      Properties connectionProps = new Properties();
      connectionProps.put("user", this.userName);
      connectionProps.put("password", this.password);

      System.out.println("Connection Props: " 
              + "jdbc:" + dbms 
              + "://" + serverName 
              + ":" + portNumber 
              + "/" + dbName 
              + ": Connection Props: " + connectionProps.toString());
      if (this.dbms.equals("mysql")) {
        DriverManager.registerDriver(new com.mysql.jdbc.Driver());
        connDW =
            DriverManager.getConnection("jdbc:" + dbms + "://" + serverName +
                                        ":" + portNumber + "/" + dbName,
                                        connectionProps);
        connDW.setCatalog(this.dbName);
      } else {
          System.out.println("Dababase type " + this.dbms + " is not supported yet");
      }
      System.out.println("Connected to database");
      return connDW;
    }
  }
 
  public String getLIFEUrlby(String sFacilityID){
      String sID = sFacilityID;
      if (sFacilityID.contains("-"))
      {
          String[] aSplitWord = sFacilityID.split("-");
          sID = aSplitWord[0] +"-"+ aSplitWord[1];
      }
            
      boolean bExists = SharedData.mapFacilityID2LincsID.containsKey(sID);
      if(bExists)
      {
          return "http://life.ccs.miami.edu/life/summary?mode=SmallMolecule&input=" + SharedData.mapFacilityID2LincsID.get( sID ) + "&source=LINCS";
      }
      if( sID.startsWith("BRD"))
      {
          return "http://life.ccs.miami.edu/life/summary?mode=SmallMolecule&input=" + sID +"&source=BROAD";
      }
      if( sID.startsWith("HMSL"))
      {
          return "http://life.ccs.miami.edu/life/summary?mode=SmallMolecule&input=" + sID.substring(4) +"&source=HMS";
      }
            
      return "http://life.ccs.miami.edu/life/summary?mode=SmallMolecule&input=" + sFacilityID + "&source=LINCS";
  }

  public void closeConnection(Connection connArg) throws SQLException {
    System.out.println("Releasing all open resources ...");
    if (connArg != null) {
      connArg.close();
      connArg = null;
    }
  }
  
  public List<String> getColNames(Connection connGetColNames){
      List<String> colNames = new ArrayList<>();    
      String createString = "DESCRIBE `lincs_staging`.`dmcp2kd`";
      Statement stmt = null; 
      try {
          stmt = connGetColNames.createStatement();
          ResultSet rs = stmt.executeQuery(createString);
          CachedRowSetImpl crs = new CachedRowSetImpl();
          crs.populate(rs);
          ResultSetMetaData metadata = crs.getMetaData();
//          System.out.println("First col: "+metadata.getColumnName(1)+"|"+metadata.getColumnName(2));
//          if (crs.isAfterLast()) System.out.println("Exception. DWDAO.getColNames: Empty CRS. ");
          while (crs.next()) {
              colNames.add(crs.getString("COLUMN_NAME"));
          }
          if (colNames.size() == 0) System.out.println("Exception. DWDAO.getColNames: Empty colNames. ");
      } catch (SQLException e) {
          System.out.println("Exception. DWDAO.getColNames: " + e.getMessage().toString());
      } finally {
          if (stmt != null) { 
              try { stmt.close(); } catch (SQLException e) {}
          }
      }
      return colNames;
  }

  public CachedRowSetImpl getDrug2KD(Connection connGetColNames, String drugID){
      ResultSet rs = null;
      CachedRowSetImpl crs = null;
      String createString = "SELECT * FROM `"
              + this.dbName + "`.`" + this.tbDmCP2KD 
              + "` WHERE CP_ID = '" + drugID + "' "
              + "ORDER BY Enrichment DESC LIMIT " + maxRecords + ";";
      System.out.println(createString);
      Statement stmt = null; 
      try {
          stmt = connGetColNames.createStatement();
          rs = stmt.executeQuery(createString);
//          if (crs.isAfterLast()) System.out.println("Exception. DWDAO.getColNames: Empty CRS. ");
          crs = new CachedRowSetImpl();
          crs.populate(rs);
          if (rs.isClosed()) {
                System.out.println("Exception. dwDAO.getDrug2KD: " 
                        + "rs is closed. ");
          }
      } catch (SQLException e) {
          System.out.println("Exception. DWDAO.getColNames: " + e.getMessage().toString());
      } finally {
          if (stmt != null) { 
              try { stmt.close(); } catch (SQLException e) {}
          }
      }
      return crs;
  }
  
    public CachedRowSetImpl getDrugKSTargets(Connection connGetColNames, String drugID){
      ResultSet rs = null;
      CachedRowSetImpl crs = null;
      String createString = "SELECT * FROM `"
              + this.dbName + "`.`" + this.tbDmKS 
              + "` WHERE Drug_ID = '" + drugID + "' "
              + "ORDER BY Drug_Effect DESC LIMIT " + maxRecords + ";";
      System.out.println(createString);
      Statement stmt = null; 
      try {
          stmt = connGetColNames.createStatement();
          rs = stmt.executeQuery(createString);
//          if (crs.isAfterLast()) System.out.println("Exception. DWDAO.getColNames: Empty CRS. ");
          crs = new CachedRowSetImpl();
          crs.populate(rs);
          if (rs.isClosed()) {
                System.out.println("Exception. dwDAO.getDrug2KD: " 
                        + "rs is closed. ");
          }
      } catch (SQLException e) {
          System.out.println("Exception. DWDAO.getColNames: " + e.getMessage().toString());
      } finally {
          if (stmt != null) { 
              try { stmt.close(); } catch (SQLException e) {}
          }
      }
      return crs;
  }
  
  public CachedRowSetImpl getDrugSim(Connection connGetColNames, String drugID){
      ResultSet rs = null;
      CachedRowSetImpl crs = null;
      switch (this.drugSimFuzziness){
          case "dmDrugSim_1":
              this.thDrugSim = 0.8925273;
              break;
          case "dmDrugSim_5":
              this.thDrugSim = 0.7316164;
              break;
          case "dmDrugSim_10":
              this.thDrugSim = 0.6582026;
              break;
      }
      String createString = "SELECT CP_ID2, CP_Desc, Sim2 FROM `"
              + this.dbName + "`.`" + this.tbDmDrugSim 
              + "`, `" + this.dbName + "`.`" + this.tbDmCPDesc 
              + "` WHERE CP_ID1 = '" + drugID + "' " 
              + " AND Sim2 >= " + this.thDrugSim
              + " AND " + this.tbDmDrugSim  + ".CP_ID2 = " + this.tbDmCPDesc  + ".CP_ID " 
              + " ORDER BY Sim2 DESC LIMIT " + maxRecords + ";";
      System.out.println(createString);
      Statement stmt = null; 
      try {
          stmt = connGetColNames.createStatement();
          rs = stmt.executeQuery(createString);
//          if (crs.isAfterLast()) System.out.println("Exception. DWDAO.getDrug2KD: Empty CRS. ");
          crs = new CachedRowSetImpl();
          crs.populate(rs);
          
          createString = "SELECT CP_ID1, CP_Desc, Sim2 FROM `"
              + this.dbName + "`.`" + this.tbDmDrugSim 
              + "`, `" + this.dbName + "`.`" + this.tbDmCPDesc 
              + "` WHERE CP_ID2 = '" + drugID + "' " 
              + " AND Sim2 >= " + this.thDrugSim
              + " AND  " + this.tbDmDrugSim  + ".CP_ID1 = " + this.tbDmCPDesc  + ".CP_ID " 
              + " ORDER BY Sim2 DESC LIMIT " + maxRecords + ";";
          System.out.println(createString);
          stmt = connGetColNames.createStatement();
          rs = stmt.executeQuery(createString);
          
          while (rs.next()){
              crs.moveToInsertRow();
              crs.updateString(1, rs.getString(1));
              crs.updateString(2, rs.getString(2));
              crs.updateString(3, rs.getString(3));
              crs.insertRow();
              crs.moveToCurrentRow();
          }
          System.out.println("Exception. DWDAO:getDrugSim "+crs.size());
          if (rs.isClosed()) {
                System.out.println("Exception. dwDAO.getDrug2KD: " 
                        + "rs is closed. ");
          }
      } catch (SQLException e) {
          System.out.println("Exception. DWDAO.getColNames: " + e.getMessage().toString());
      } finally {
          if (stmt != null) { 
              try { stmt.close(); } catch (SQLException e) {}
          }
      }
      return crs;      
  }
  
  public CachedRowSetImpl getDrugCPList(Connection connGetColNames){
      ResultSet rs = null;
      CachedRowSetImpl crs = null;
      String createString = "SELECT DISTINCT CP_ID, CP_Desc FROM `"
              + this.dbName + "`.`" + this.tbDmCP2KD + "` "
              + "ORDER BY CP_Desc ASC LIMIT " + maxRecords + ";";
      System.out.println("SQL: " + createString);
      Statement stmt = null; 
      try {
          stmt = connGetColNames.createStatement();
          rs = stmt.executeQuery(createString);
          rs.next();
          //System.out.println("---RS(1): " + rs.getString("CP_ID") + rs.getString("CP_Desc"));
          crs = new CachedRowSetImpl();
          crs.populate(rs);
          crs.next();
          //System.out.println("---CRS(1): " + crs.getString("CP_ID") + crs.getString("CP_Desc"));
          
          if (crs.isAfterLast()) System.out.println("Exception. DWDAO.getColNames: Empty CRS. ");
          if (rs.isClosed()) {
                System.out.println("Exception. dwDAO.getDrug2KD: " 
                        + "rs is closed. ");
          }
      } catch (SQLException e) {
          System.out.println("Exception. DWDAO.getColNames: " + e.getMessage().toString());
      } finally {
          if (stmt != null) { 
              try { stmt.close(); } catch (SQLException e) {}
          }
      }
      return crs;
  }  

  public CachedRowSetImpl getDrugKINOMEScanList(Connection connGetColNames){
      ResultSet rs = null;
      CachedRowSetImpl crs = null;
      String createString = "SELECT DISTINCT Drug_ID, Drug_Desc FROM `"
              + this.dbName + "`.`" + "dmDrugTargets".toLowerCase() + "` "
              + "ORDER BY Drug_ID ASC LIMIT " + maxRecords + ";";
      System.out.println("SQL: " + createString);
      Statement stmt = null; 
      try {
          stmt = connGetColNames.createStatement();
          rs = stmt.executeQuery(createString);
          rs.next();
//          System.out.println("RS(1): " + rs.getString("CP_ID") + rs.getString("CP_Desc"));
          crs = new CachedRowSetImpl();
          crs.populate(rs);
          crs.next();
//          System.out.println("CRS(1): " + crs.getString("CP_ID") + crs.getString("CP_Desc"));
          if (crs.isAfterLast()) System.out.println("Exception. DWDAO.getColNames: Empty CRS. ");
          if (rs.isClosed()) {
                System.out.println("Exception. dwDAO.getDrug2KD: " 
                        + "rs is closed. ");
          }
      } catch (SQLException e) {
          System.out.println("Exception. DWDAO.getColNames: " + e.getMessage().toString());
      } finally {
          if (stmt != null) { 
              try { stmt.close(); } catch (SQLException e) {}
          }
      }
      return crs;
  }   
  
public CachedRowSetImpl getCellResponse(List<String> selectedCells,List<String>selectedCellResponses){
    // SELECT * FROM dmCommDrugMaxEffects 
//	WHERE Drug_ID in ('HMSL10008','HMSL10013','HMSL10014')
//	AND Cell_Name in ('PC-9', 'COLO-800')
//      AND Effect in ('Apoptosis');   
      String drugIDList = "";
      for (int i = 0; i < SharedData.drugIDCommCellResponse.size();i++){
          drugIDList += "'" + SharedData.drugIDCommCellResponse.get(i)+ "'";
          if (i < SharedData.drugIDCommCellResponse.size() -1 ) 
              drugIDList += ", ";
      }
      String cellList = "";
      for (int i = 0; i < selectedCells.size();i++){
          cellList += "'" + selectedCells.get(i)+ "'";
          if (i < selectedCells.size() -1 ) 
              cellList += ", ";
      }
      String cellResponseList = "";
      for (int i = 0; i < selectedCellResponses.size();i++){
          cellResponseList += "'" + selectedCellResponses.get(i)+ "'";
          if (i < selectedCellResponses.size() -1 ) 
              cellResponseList += ", ";
      }
      ResultSet rs = null;
      CachedRowSetImpl crs = null;      
      String createString = "SELECT * FROM " + "dmCommDrugMaxEffects ".toLowerCase() 
              + "WHERE Drug_ID in (" + drugIDList + ") " 
              + "AND Cell_Name IN (" + cellList + ") " 
              + "AND Effect IN (" + cellResponseList + ") " 
              + ";"; 
      System.out.println(createString);
      Statement stmt = null;     
      try {
        if (this.conn == null) 
            this.conn = this.getConnectionToDatabase();        
        stmt = this.conn.createStatement();
        rs = stmt.executeQuery(createString);
        crs = new CachedRowSetImpl();
        crs.populate(rs);
      } catch (SQLException e) {
          System.out.println("Exception. DWDAO.getColNames: " + e.getMessage().toString());
      } finally {
          if (stmt != null) { 
              try { stmt.close(); } catch (SQLException e) {}
          }
      }
      return crs;
}
  
  public boolean determineDrugNW(){
      ResultSet rs = null;
      CachedRowSetImpl crs = null;      
      String createString = "";
      System.out.println("SQL: " + createString);
      Statement stmt = null; 

        SharedData.drugToTargets_1.clear();
        SharedData.drugFromTargets_1.clear();
        SharedData.drugToComm_1.clear();
        SharedData.drugTargetsF_1.clear();

        SharedData.drugToTargets_2.clear();
        SharedData.drugFromTargets_2.clear();
        SharedData.drugToComm_2.clear();
        SharedData.drugTargetsF_2.clear();

        SharedData.drugComm.clear();

        SharedData.drugMid_1.clear();
        SharedData.drugComm_1.clear();
        SharedData.drugF_1.clear();

        SharedData.drugOffTargets_1.clear();
        SharedData.drugOffComm_1.clear();
        SharedData.drugOffF_1.clear();

        SharedData.drugMid_2.clear();
        SharedData.drugComm_2.clear();
        SharedData.drugF_2.clear();

        SharedData.drugOffTargets_2.clear();
        SharedData.drugOffComm_2.clear();
        SharedData.drugCommSumF.clear(); 
    
      try {
          // DrugID -> drugToTargets_1
          SharedData.drugToTargets_1 = this.getDrug2Targets(SharedData.drugID, SharedData.drugType);
                    
          // DrugID2 -> drugToTargets_2
          SharedData.drugToTargets_2 = this.getDrug2Targets(SharedData.drugID2, SharedData.drugType2);
          
          // drugToTargets_1 -> {drugFromTargets_1, drugToComm_1, drugTargetsF_1}
          for(int i = 0; i< SharedData.drugToTargets_1.size();i++){
            createString = "SELECT Gene_To,Frequency FROM dmrandwalkp2p_1 WHERE TRIM(Gene_From) = "
                + "'" + SharedData.drugToTargets_1.get(i) + "'; ";
            System.out.println(createString);
            stmt = this.conn.createStatement();
            rs = stmt.executeQuery(createString);
            crs = new CachedRowSetImpl();
            crs.populate(rs);
            if (crs.size() >0 ) {
                crs.beforeFirst();      
                while (crs.next()) {
                    SharedData.drugFromTargets_1.add(SharedData.drugToTargets_1.get(i));
                    SharedData.drugToComm_1.add(crs.getString("Gene_To"));
                    SharedData.drugTargetsF_1.add(crs.getDouble("Frequency"));
                } 
            }
          }
          System.out.println("SharedData.drugFromTargets_1: " + SharedData.drugFromTargets_1);
          System.out.println("SharedData.drugToComm_1: " + SharedData.drugToComm_1);
          System.out.println("SharedData.drugTargetsF_1: " + SharedData.drugTargetsF_1);
          // drugToTargets_2 -> {drugFromTargets_2, drugToComm_2, drugTargetsF_2}
          for(int i = 0; i< SharedData.drugToTargets_2.size();i++){
            createString = "SELECT Gene_To,Frequency FROM dmrandwalkp2p_1 WHERE TRIM(Gene_From) = "
                + "'" + SharedData.drugToTargets_2.get(i) + "'; ";
            System.out.println(createString);
            stmt = this.conn.createStatement();
            rs = stmt.executeQuery(createString);
            crs = new CachedRowSetImpl();
            crs.populate(rs);
            if (crs.size()>0) {
                crs.beforeFirst();      
                while (crs.next()) {
                    SharedData.drugFromTargets_2.add(SharedData.drugToTargets_2.get(i));
                    SharedData.drugToComm_2.add(crs.getString("Gene_To"));
                    SharedData.drugTargetsF_2.add(crs.getDouble("Frequency"));
                }
            }
          } 
          System.out.println("SharedData.drugFromTargets_2: " + SharedData.drugFromTargets_2);
          System.out.println("SharedData.drugToComm_2: " + SharedData.drugToComm_2);
          System.out.println("SharedData.drugTargetsF_2: " + SharedData.drugTargetsF_2); 
          // {drugToComm_1, drugToComm_2} -> drugComm
          for(int i = 0; i< SharedData.drugToComm_2.size();i++){
              if (SharedData.drugToComm_1.contains(SharedData.drugToComm_2.get(i))){
                  SharedData.drugComm.add(SharedData.drugToComm_2.get(i));                  
              }
          }
          if (SharedData.drugComm.size()<1) {return true;};
          System.out.println("SharedData.drugComm: " + SharedData.drugComm); 
          // IN and OFF sets
          for(int i = 0; i< SharedData.drugToComm_1.size();i++){
              if (SharedData.drugComm.contains(SharedData.drugToComm_1.get(i))) {
                  SharedData.drugMid_1.add(SharedData.drugFromTargets_1.get(i));
                  SharedData.drugComm_1.add(SharedData.drugToComm_1.get(i));
                  SharedData.drugF_1.add(SharedData.drugTargetsF_1.get(i));
              } else {
                  SharedData.drugOffTargets_1.add(SharedData.drugFromTargets_1.get(i));
                  SharedData.drugOffComm_1.add(SharedData.drugToComm_1.get(i));
                  SharedData.drugOffF_1.add(SharedData.drugTargetsF_1.get(i));                  
              }
          }
          for(int i = 0; i< SharedData.drugToComm_2.size();i++){
              if (SharedData.drugComm.contains(SharedData.drugToComm_2.get(i))) {
                  SharedData.drugMid_2.add(SharedData.drugFromTargets_2.get(i));
                  SharedData.drugComm_2.add(SharedData.drugToComm_2.get(i));
                  SharedData.drugF_2.add(SharedData.drugTargetsF_2.get(i));
              } else {
                  SharedData.drugOffTargets_2.add(SharedData.drugFromTargets_2.get(i));
                  SharedData.drugOffComm_2.add(SharedData.drugToComm_2.get(i));
                  SharedData.drugOffF_2.add(SharedData.drugTargetsF_2.get(i));                  
              }
          }
          // Remove duplicates
          List<Integer> ii ;
          ii = findDuplicates(SharedData.drugMid_1, 
                  SharedData.drugComm_1, SharedData.drugF_1);
          SharedData.drugMid_1 = removeDuplicates(SharedData.drugMid_1,ii);
          SharedData.drugComm_1 = removeDuplicates(SharedData.drugComm_1,ii);
          SharedData.drugF_1 = removeDuplicates(SharedData.drugF_1,ii,0);
          
          ii = findDuplicates(SharedData.drugOffTargets_1, 
                  SharedData.drugOffComm_1, SharedData.drugOffF_1);
          SharedData.drugOffTargets_1 = removeDuplicates(SharedData.drugOffTargets_1,ii);
          SharedData.drugOffComm_1 = removeDuplicates(SharedData.drugOffComm_1,ii);
          SharedData.drugOffF_1 = removeDuplicates(SharedData.drugOffF_1,ii,0);          

          ii = findDuplicates(SharedData.drugMid_2, 
                  SharedData.drugComm_2, SharedData.drugF_2);
          SharedData.drugMid_2 = removeDuplicates(SharedData.drugMid_2,ii);
          SharedData.drugComm_2 = removeDuplicates(SharedData.drugComm_2,ii);
          SharedData.drugF_2 = removeDuplicates(SharedData.drugF_2,ii,0);
          
          ii = findDuplicates(SharedData.drugOffTargets_2, 
                  SharedData.drugOffComm_2, SharedData.drugOffF_2);
          SharedData.drugOffTargets_2 = removeDuplicates(SharedData.drugOffTargets_2,ii);
          SharedData.drugOffComm_2 = removeDuplicates(SharedData.drugOffComm_2,ii);
          SharedData.drugOffF_2 = removeDuplicates(SharedData.drugOffF_2,ii,0); 
          System.out.println("drugMid_1: " + SharedData.drugMid_1 + "\n"
                  + SharedData.drugComm_1  + "\n"
                  + SharedData.drugF_1);
          System.out.println("drugOffTargets_1: " + SharedData.drugOffTargets_1 + "\n"
                  + SharedData.drugOffComm_1  + "\n"
                  + SharedData.drugOffF_1);
          System.out.println("drugMid_2: " + SharedData.drugMid_2 + "\n"
                  + SharedData.drugComm_2  + "\n"
                  + SharedData.drugF_2);
          System.out.println("drugOffTargets_2: " + SharedData.drugOffTargets_2 + "\n"
                  + SharedData.drugOffComm_2  + "\n"
                  + SharedData.drugOffF_2);
//          List<String> lines = new ArrayList<>();
//          lines.add("drugMid_1: ");
//          lines.add("\t" + SharedData.drugMid_1);
//          lines.add("\t" + SharedData.drugComm_1);
//          lines.add("\t" + SharedData.drugF_1);
//          lines.add("drugMid_2: ");
//          lines.add("\t" + SharedData.drugMid_2);
//          lines.add("\t" + SharedData.drugComm_2);
//          lines.add("\t" + SharedData.drugF_2);
          writeDot("tmp.dot",genDot());

      } catch (SQLException e) {
          System.out.println("Exception. DWDAO.getColNames: " + e.getMessage().toString());
      } finally {
          if (stmt != null) { 
              try { stmt.close(); } catch (SQLException e) {}
          }
      } 
      return false;
  }
  
  private List<String> getDrug2Targets(String drugID, String drugType){
      List <String> drugToTargets = new ArrayList<>();
      ResultSet rs = null;
      CachedRowSetImpl crs = null;      
      String createString = "";
      Statement stmt = null;
      try {
          // Test Drug Type of DrugID
          switch (drugType){
              case "CP":
                  createString = "SELECT KD_Gene FROM dmcp2kd_5 WHERE TRIM(CP_ID) = "
                          + "'" + drugID + "'; ";    
                  break;
              case "KINOMEScan":
                  createString = "SELECT Target_Gene_Name FROM dmdrugtargets_85 WHERE TRIM(Drug_ID) = "
                          + "'" + drugID + "'; "; 
                  break;
              case "NA":
              default: 
                  throw new SQLException("Cannot find drugID: " + drugID);
          }
          
          // DrugID -> drugToTargets_1
          System.out.println("SQL: " + createString);
          stmt = this.conn.createStatement();
          rs = stmt.executeQuery(createString);
          crs = new CachedRowSetImpl();
          crs.populate(rs);
          if (crs.size()<1) {return drugToTargets;};
          crs.beforeFirst(); 
          while (crs.next()) {
               drugToTargets.add(crs.getString(1));
          }
          System.out.println("drugToTargets: " + drugToTargets);
      } catch (SQLException e) {
          System.out.println("Exception. DWDAO.getColNames: " + e.getMessage().toString());
      } finally {
          if (stmt != null) { 
              try { stmt.close(); } catch (SQLException e) {}
          }
      }
      return drugToTargets;
  }
  
  List<String> getCellResponseByCellMaxEffect(List<String> selectedCells) {
//      select DISTINCT Effect from dmCommDrugMaxEffects 
//	where Drug_ID in ('HMSL10008','HMSL10013','HMSL10014')
//	and Cell_Name in ('PC-9', 'COLO-800');
      List<String> cellResponses = new ArrayList();
      String drugIDList = "";
      for (int i = 0; i < SharedData.drugIDCommCellResponse.size();i++){
          drugIDList += "'" + SharedData.drugIDCommCellResponse.get(i)+ "'";
          if (i < SharedData.drugIDCommCellResponse.size() -1 ) 
              drugIDList += ", ";
      }
      String cellList = "";
      for (int i = 0; i < selectedCells.size();i++){
          cellList += "'" + selectedCells.get(i)+ "'";
          if (i < selectedCells.size() -1 ) 
              cellList += ", ";
      }
      ResultSet rs = null;
      CachedRowSetImpl crs = null;      
      String createString = "SELECT DISTINCT Effect FROM " + "dmCommDrugMaxEffects ".toLowerCase()
              + "WHERE Drug_ID in (" + drugIDList + ") " 
              + "AND Cell_Name IN (" + cellList + ") " 
              + ";"; 
      System.out.println(createString);
      Statement stmt = null;     
      try {
        if (this.conn == null) {
            this.conn = this.getConnectionToDatabase();
        }          
          stmt = this.conn.createStatement();
          rs = stmt.executeQuery(createString);
          crs = new CachedRowSetImpl();
          crs.populate(rs);
          while (crs.next()) {
              cellResponses.add(crs.getString(1));
          } 
          if (rs.isClosed()) {
                System.out.println("Exception. dwDAO.getDrug2KD: " 
                        + "rs is closed. ");
          }          
      } catch (SQLException e) {
          System.out.println("Exception. DWDAO.getColNames: " + e.getMessage().toString());
      } finally {
          if (stmt != null) { 
              try { stmt.close(); } catch (SQLException e) {}
          }
      }
      return cellResponses;
  }
  
  List<String> getCellByDrugIDMaxEffect(){
//      select DISTINCT Cell_Name from dmCommDrugMaxEffects where Drug_ID in ('HMSL10008','HMSL10013','HMSL10014');
      List<String> cells = new ArrayList();
      String drugIDList = "";
      for (int i = 0; i < SharedData.drugIDCommCellResponse.size();i++){
          drugIDList += "'" + SharedData.drugIDCommCellResponse.get(i)+ "'";
          if (i < SharedData.drugIDCommCellResponse.size() -1 ) 
              drugIDList += ", ";
      }
      ResultSet rs = null;
      CachedRowSetImpl crs = null;      
      String createString = "SELECT DISTINCT Cell_Name FROM " + "dmCommDrugMaxEffects ".toLowerCase() 
              + "WHERE Drug_ID in (" 
              + drugIDList + ")"
              + ";"; 
      System.out.println(createString);
      Statement stmt = null;     
      try {
        if (this.conn == null) {
            this.conn = this.getConnectionToDatabase();
        }          
          stmt = this.conn.createStatement();
          rs = stmt.executeQuery(createString);
          crs = new CachedRowSetImpl();
          crs.populate(rs);
          while (crs.next()) {
              cells.add(crs.getString(1));
          } 
          if (rs.isClosed()) {
                System.out.println("Exception. dwDAO.getDrug2KD: " 
                        + "rs is closed. ");
          }          
      } catch (SQLException e) {
          System.out.println("Exception. DWDAO.getColNames: " + e.getMessage().toString());
      } finally {
          if (stmt != null) { 
              try { stmt.close(); } catch (SQLException e) {}
          }
      }
      return cells;
  }
  
  List<String> getCommonDrug(){
      List<String> commDrugs = new ArrayList();
      ResultSet rs = null;
      CachedRowSetImpl crs = null;      
      String createString = "SELECT DISTINCT Drug_ID FROM " + "dmCommDrugCellEffects".toLowerCase() + ";";  
      System.out.println(createString);
      Statement stmt = null;     
      try {
        if (this.conn == null) {
            this.conn = this.getConnectionToDatabase();
        }          
          stmt = this.conn.createStatement();
          rs = stmt.executeQuery(createString);
          crs = new CachedRowSetImpl();
          crs.populate(rs);
          while (crs.next()) {
              commDrugs.add(crs.getString(1));
          } 
          if (rs.isClosed()) {
                System.out.println("Exception. dwDAO.getDrug2KD: " 
                        + "rs is closed. ");
          }          
      } catch (SQLException e) {
          System.out.println("Exception. DWDAO.getColNames: " + e.getMessage().toString());
      } finally {
          if (stmt != null) { 
              try { stmt.close(); } catch (SQLException e) {}
          }
      }
      return commDrugs;
  }
  
  public long sqlCount(String createString) {
      long l = 0;
      ResultSet rs = null;
      CachedRowSetImpl crs = null;
      System.out.println("SQL: " + createString);
      Statement stmt = null; 
      try {
        if (this.conn == null) {
            this.conn = this.getConnectionToDatabase();
        }          
          stmt = this.conn.createStatement();
          rs = stmt.executeQuery(createString);
          crs = new CachedRowSetImpl();
          crs.populate(rs);
          if (crs.size()>0) {
              crs.next();
              l = crs.getLong(1);
          } 
          if (rs.isClosed()) {
                System.out.println("Exception. dwDAO.getDrug2KD: " 
                        + "rs is closed. ");
          }          
      } catch (SQLException e) {
          System.out.println("Exception. DWDAO.getColNames: " + e.getMessage().toString());
      } finally {
          if (stmt != null) { 
              try { stmt.close(); } catch (SQLException e) {}
          }
      }
      return l;
  }
  
  public String getDrugType(String drugID){
      String result = "NA";
      String createString = "SELECT COUNT(*) FROM `"
              + this.dbName + "`.`" + this.tbDmCP2KD.toLowerCase() + "` "
              + "WHERE CP_ID = " + "'" + drugID + "'"
              + " LIMIT " + maxRecords + ";";
      System.out.println("SQL: " + createString);
      if (sqlCount(createString) > 0){
          result = "CP";          
      } else {
          createString = "SELECT COUNT(*) FROM `"
              + this.dbName + "`.`" + "dmDrugTargets".toLowerCase() + "` "
              + "WHERE Drug_ID = " + "'" + drugID + "'" 
              + " LIMIT " + maxRecords + ";";
          System.out.println(createString);
          if (sqlCount(createString) > 0){
              result = "KINOMEScan";          
          }          
      }      
      return result;
  }
  
  public List<String> genDot(){
      int penNormal = 1;
      int penEnriched = 4;
      int penTarget = 5;
      List<String> lines = new ArrayList<>();   
      lines.clear();
      String tmpS = "";
//      begin to write "dot-file"
      lines.add("digraph G{ ");
//      lines.add( "graph [dpi=1200]; ratio=\"fill\"; size=\"6.8,2.4\"; ");
      lines.add( "graph [dpi=1200]; size=\"6.5,3\"; ");      
      lines.add("\""+SharedData.drugID + "\" [fillcolor=\"#32cd32\", style=\"filled\"];");
      lines.add("\""+SharedData.drugID2 + "\" [fillcolor=\"#cd00cd\", style=\"filled\"];");
      for (int i = 0; i < SharedData.drugComm.size(); i++){
          lines.add("\""+SharedData.drugComm.get(i) + "\" [fillcolor=\"#ff4500\", style=\"filled\"];");
      }
      for (int i = 0; i < SharedData.drugToTargets_1.size(); i++){
          lines.add("\""+SharedData.drugID + "\""+" -> " + "\""+SharedData.drugToTargets_1.get(i)+ "\""
                  + " [penwidth="+ penEnriched +"] " + ";");
      }
//      for (int i = 0; i < SharedData.drugFromTargets_1.size(); i++){
//          lines.add("\""+SharedData.drugFromTargets_1.get(i) + "\""+" -> " + "\""+SharedData.drugToComm_1.get(i) + "\""+";");
//      } 
      for (int i = 0; i < SharedData.drugMid_1.size(); i++){
          lines.add("\""+SharedData.drugMid_1.get(i) + "\""+" -> " + "\""+SharedData.drugComm_1.get(i) + "\" " 
                  + " [color=\"#7d26cd\" ,penwidth="+ penTarget +"] " + ";");
      } 
      for (int i = 0; i < SharedData.drugOffTargets_1.size(); i++){
          lines.add("\""+SharedData.drugOffTargets_1.get(i) + "\""+" -> " + "\""+SharedData.drugOffComm_1.get(i) + "\"" 
                  + " [penwidth="+ penEnriched +"] " + ";");
      }      
      for (int i = 0; i < SharedData.drugToTargets_2.size(); i++){
          lines.add("\""+SharedData.drugID2 + "\""+" -> " + "\""+SharedData.drugToTargets_2.get(i) + "\"" 
                  + " [penwidth="+ penEnriched +"] " + ";");
      }
//      for (int i = 0; i < SharedData.drugFromTargets_2.size(); i++){
//          lines.add("\""+SharedData.drugFromTargets_2.get(i) + "\""+" -> " + "\""+SharedData.drugToComm_2.get(i) + "\""+";");
//      } 
      for (int i = 0; i < SharedData.drugMid_2.size(); i++){
          lines.add("\""+SharedData.drugMid_2.get(i) + "\""+" -> " + "\""+SharedData.drugComm_2.get(i) + "\" " 
                  + " [color=\"#7fff00\" ,penwidth="+ penTarget +"] " +";");
      } 
      for (int i = 0; i < SharedData.drugOffTargets_2.size(); i++){
          lines.add("\""+SharedData.drugOffTargets_2.get(i) + "\""+" -> " + "\""+SharedData.drugOffComm_2.get(i) + "\"" 
                  + " [penwidth="+ penEnriched +"] " + ";");
      }       
      tmpS = " { rank = sink; " ;
      for (int i = 0; i < SharedData.drugComm.size(); i++){
          tmpS += ("\""+SharedData.drugComm.get(i) + "\""+"; ");
      }
      for (int i = 0; i < SharedData.drugOffComm_1.size(); i++){
          tmpS += ("\""+SharedData.drugOffComm_1.get(i) + "\""+"; ");
      }
      for (int i = 0; i < SharedData.drugOffComm_2.size(); i++){
          tmpS += ("\""+SharedData.drugOffComm_2.get(i) + "\""+"; ");
      }
      tmpS += "} ";
      lines.add(tmpS);
      lines.add("{ rank = source; " + "\""+SharedData.drugID + "\""+";" + "\""+SharedData.drugID2 +"\""+ "; }");
      lines.add("}");
      return lines;
  }
  
  public void writeDot(String writeDotFileName,List<String> lines){
    Charset charset = Charset.forName("UTF-8");
    Path file = Paths.get(writeDotFileName);
    File f = new File(writeDotFileName);
    if (f.isFile()) f.delete();
    try (BufferedWriter writer = Files.newBufferedWriter(file, charset)) {
        for (String line : lines){
            writer.write(line);
            writer.newLine();
        }
        writer.close();
    } catch (IOException x) {
        System.err.format("IOException: %s%n", x);
    } 
  }
  
  private List<Integer> findDuplicates(List<String> fromNodes, List<String> toNodes, List<Double> fValues) {
      List<Integer> ii = new ArrayList<>();
      ii.clear();
      if (fromNodes.size() <= 1) { return ii; };
      for (int i = fromNodes.size()-1;i >= 1; i--){
          for (int j = i-1;j >= 0; j--){
              if (fromNodes.get(i) == fromNodes.get(j) & toNodes.get(i) == toNodes.get(j)){
                  if (fValues.get(i) <= fValues.get(j) & !ii.contains(i)) {
                      ii.add(i);
                  } else if (!ii.contains(j)){
                      ii.add(j);
                  } 
              }
          }
      }
      Collections.sort(ii);
      return ii;
  }
  
  private List<String> removeDuplicates(List<String> inputL, List<Integer> ii){
      for (int i = ii.size()-1;i >= 0; i--){
          inputL.remove(ii.get(i));
      }
      return inputL;
  }
  
  private List<Double> removeDuplicates(List<Double> inputL, List<Integer> ii, int j){
      for (int i = ii.size()-1;i >= 0; i--){
          inputL.remove(ii.get(i));
      }
      return inputL;
  }  
 
  @Override
  protected void finalize() throws Throwable {
    try{
        this.closeConnection(this.conn);        
    } catch (SQLException ex) {
        System.out.println("Exception: " + ex.getMessage().toString());
    } finally {
        super.finalize();
    }
  }
  
  public DWDAO(){
      try {
            // The newInstance() call is a work around for some
            // broken Java implementations
          Class.forName("com.mysql.jdbc.Driver").newInstance();
      } catch (Exception ex) {
          System.out.println("Exception: " + ex.getMessage().toString());
      }      
//      
      this.userName = "pLINDAW";
      this.password = "pLINDAW";
      this.portNumber = 3306;
      this.dbName = "pLINDAW";
      this.serverName = "metacity.is.wfubmc.edu";
      this.dbms = "mysql";
      this.conn = null;
  }
    
}
