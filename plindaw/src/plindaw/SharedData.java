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

/**
 *
 * @author Jing Su
 */
import com.sun.rowset.CachedRowSetImpl;
import java.io.BufferedReader;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.List;
import java.util.HashMap; 
import java.util.Iterator;
import java.util.ArrayList;

public class SharedData {
    public static String drugID = "";
    public static String drugDesc = "";
    public static String drugType = "";
    public static String drugID2 = "";
    public static String drugDesc2 = "";    
    public static String drugType2 = "";
    public static double drugSim = 0;
    public static boolean fDrugSim = false;
    public static String geneName = null;
    public static String[] geneNames = null;
    public static String drugIDsFileName = "./data/ListOfCP_ID.txt";
    public static String drugDescsFileName = "./data/ListOfCP_Desc.txt";
    public static String sFileName_FacilityID2LincsID = "/plindaw/LincsID2FacilityID_batchOct0113_Added.txt";
//    public static String drugIDs_DescsFileName = "./data/test.txt";
    public static List<String> drugIDs = new ArrayList<>();
    public static List<String>  drugDescs = new ArrayList<>();
    public static HashMap<String, String> drugMap = new HashMap<String, String>();
    public static HashMap<String, String> mapFacilityID2LincsID = new HashMap<String, String>();

    public static List<String> drugToTargets_1 = new ArrayList<>();
    public static List<String> drugFromTargets_1 = new ArrayList<>();
    public static List<String> drugToComm_1 = new ArrayList<>();
    public static List<Double> drugTargetsF_1 = new ArrayList<>();

    public static List<String> drugToTargets_2= new ArrayList<>();
    public static List<String> drugFromTargets_2 = new ArrayList<>();
    public static List<String> drugToComm_2 = new ArrayList<>();
    public static List<Double> drugTargetsF_2 = new ArrayList<>();

    public static List<String> drugComm = new ArrayList<>();

    public static List<String> drugMid_1 = new ArrayList<>();
    public static List<String> drugComm_1 = new ArrayList<>();
    public static List<Double> drugF_1 = new ArrayList<>();
    
    public static List<String> drugOffTargets_1 = new ArrayList<>();
    public static List<String> drugOffComm_1 = new ArrayList<>();
    public static List<Double> drugOffF_1 = new ArrayList<>();

    public static List<String> drugMid_2 = new ArrayList<>();
    public static List<String> drugComm_2 = new ArrayList<>();
    public static List<Double> drugF_2 = new ArrayList<>();
    
    public static List<String> drugOffTargets_2 = new ArrayList<>();
    public static List<String> drugOffComm_2 = new ArrayList<>();
    public static List<Double> drugOffF_2 = new ArrayList<>();
    
    public static List<Double> drugCommSumF = new ArrayList<>();
    
    public static List<String> drugIDCommCellResponse = new ArrayList<>();
    public static List<String> drugDescCommCellResponse = new ArrayList<>();
    public static List<Float> commCellResponse = new ArrayList<>();
    
    public static String graphEngine = "fdp";
    public static boolean bSuccDrawGraph = true;

    private static void populateDrugListFromFile(){
        try {
            drugIDs = Files.readAllLines(Paths.get(drugIDsFileName), 
                    StandardCharsets.ISO_8859_1);
            drugDescs = Files.readAllLines(Paths.get(drugDescsFileName), 
                    StandardCharsets.ISO_8859_1);            
            if (drugIDs.size() == drugDescs.size()){
                for (int i = 0; i < drugIDs.size(); i++) {
                    drugMap.put(drugIDs.get(i), drugDescs.get(i));
                }            
             
            } else {
                System.out.println("The Drug ID and Decs are of different sizes: "
                        + "ID: " + drugIDs.size()
                        + "; Desc: " + drugDescs.size());
            }
        } catch (IOException e) {
            System.out.println("I got problems: " + e.getMessage() + "; " + e.toString());
        }        
    }
    
    private static void populateDrugListFromDW(){
        DWDAO dwDAO = new DWDAO();
        dwDAO.tbDmCP2KD = "dmCP2KD_25".toLowerCase();
        if (dwDAO.conn == null) {
            try {
                dwDAO.conn = dwDAO.getConnectionToDatabase();
            } catch(SQLException e) {
                System.out.println("Exception. DWDAO.getColNames: " + e.getSQLState().toString());
            }
        }
        if (dwDAO.conn == null) {
            System.out.println("Connection failed. ");            
        }
        CachedRowSetImpl crs = dwDAO.getDrugCPList(dwDAO.conn);
        try {
            while (crs.next()) {
                drugIDs.add(crs.getString("CP_ID").toString());
                drugDescs.add(crs.getString("CP_Desc").toString());                
            }
        } catch (Exception e) {
            System.out.println("Exception. StartFrame.resultSetToDrugTargetsL1KGeneEnrichmentTableModel: " 
                    + e.toString());
        }        
        if (drugIDs == null) {
            System.out.println("Exception. drugIDs is empty. ");
        }
        
        if (dwDAO.conn == null) {
            try {
                dwDAO.conn = dwDAO.getConnectionToDatabase();
            } catch(SQLException e) {
                System.out.println("Exception. DWDAO.getColNames: " + e.getSQLState().toString());
            }
        }        
        crs = dwDAO.getDrugKINOMEScanList(dwDAO.conn);
        try {
            while (crs.next()) {
                drugIDs.add(crs.getString("Drug_ID").toString());
                drugDescs.add(crs.getString("Drug_Desc").toString());                
            }
        } catch (Exception e) {
            System.out.println("Exception. StartFrame.resultSetToDrugTargetsL1KGeneEnrichmentTableModel: " 
                    + e.toString());
        }
        if (drugIDs == null) {
            System.out.println("Exception. drugIDs is empty. ");
        }        
    }
    
    public static void SharedData(){
//        populateDrugListFromFile();
        populateDrugListFromDW();


    }
}
