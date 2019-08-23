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

/**
 *
 * @author Jing Su
 */
package plindaw;
import com.sun.rowset.CachedRowSetImpl;
import java.awt.Dimension;
import java.awt.Font;
import java.sql.SQLException;
import java.util.List;
import java.util.Vector;
import java.util.ArrayList;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
//import java.util.Collections;
//import java.nio.file.Paths;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.table.DefaultTableModel;
//import java.util.Date;
//import java.io.File;
//import java.util.Iterator;
import java.util.HashMap;;
//import javax.swing.table.AbstractTableModel;
//import java.util.TreeMap;
//import java.util.SortedMap;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.io.File;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import java.awt.Graphics2D;
import java.lang.Math;
import java.awt.Graphics;
import java.awt.MouseInfo;
import java.awt.Point;
import javax.swing.SwingUtilities;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import javax.swing.event.ListSelectionEvent;
import java.util.Timer;
import java.util.TimerTask;
import java.lang.InterruptedException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.net.URL;
import javax.swing.SortOrder;
import javax.swing.table.TableRowSorter;
import java.awt.event.*;
import javax.help.*;



public class StartFrame extends javax.swing.JInternalFrame {

    /**
     * Creates new form StartFrame
     */
    private ImageIcon imgLogoText = null;
    private ImageIcon imgIcon = null;
    private ImageIcon imgLargeIcon = null;
    private BufferedImage imgLargeImage = null;
    private BufferedImage imgSchema = null;
    private BufferedImage imgLogo = null;
    private boolean fConnDW = false;
    private DefaultComboBoxModel selectInputTypeModel = new DefaultComboBoxModel();
    private DefaultComboBoxModel selectDrugFuzzinessTypeModel = new DefaultComboBoxModel();
    private DefaultComboBoxModel selectFuzzinessLevelModel = new DefaultComboBoxModel();
    private DefaultComboBoxModel selectSimDrugFuzzinessLevelModel = new DefaultComboBoxModel();
    private DefaultComboBoxModel selectSimDrugForCellResponseFuzzinessLevelModel = new DefaultComboBoxModel();
    private DefaultComboBoxModel selectCellForCellResponseModel = new DefaultComboBoxModel();
    private DefaultComboBoxModel selectCellResponseForCellResponseModel = new DefaultComboBoxModel();
    
    private DefaultListModel drugListModel = new DefaultListModel();
    private DefaultListModel drugListSearchModel = new DefaultListModel();
    private boolean fDrugSearchModel = false;
    private HashMap<Integer,Integer> mapDrugModels = new HashMap<>();
    private String selectedInputDrugID = null;
    private String selectedInputDrugDesc = null;
    private BufferedImage img = null;
    private double scale = 0;
    private ImagePanel imagePanel = new ImagePanel();
    private int mouseWheelZoomSpeed = 5;
    private Point tmpMouseDragPosition = new Point(-1,-1);
    private boolean tmpMouseRightButtonDown = false;
    private String resultTableType = "";
    private List<String> commonDrugs = new ArrayList();
   
//    private DrugTableModel drugTableModel = new DrugTableModel();
    
    private DefaultTableModel drugTableModel = new DefaultTableModel() {
        @Override public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private DefaultTableModel simDrugTableModel = new DefaultTableModel() {
        @Override public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private DefaultTableModel cellResponseTableModel = new DefaultTableModel() {
        @Override public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
        
    private DWDAO dwDAO = new DWDAO();
    
    public StartFrame() {
        initComponents();
        //splashing -- kind of
        
        
        
        HelpSet helpset = null;
        ClassLoader loader = StartFrame.class.getClassLoader();

        URL url = getClass().getResource("pLINDAWHelp.hs");
        try {
            helpset = new HelpSet(loader, url);
        } catch (HelpSetException e) {
            System.out.println("Error loading"+e.getMessage());
        }

        HelpBroker helpbroker = helpset.createHelpBroker();

        ActionListener listener = new CSH.DisplayHelpFromSource(helpbroker);
        helpContentMenuItem.addActionListener(listener);
        
        tutorialDialog.setLocationRelativeTo(this);
        this.tutorialDialog.setVisible(true);
        // Add table selection listener by Jing
        resultTable.getSelectionModel().addListSelectionListener(new ListSelectionListener(){
            @Override
            public void valueChanged(ListSelectionEvent e){
                resultTableValueChanged(e);
            };
        });
        
        // Add table selection listener by Jing
        resultSimDrugTable.getSelectionModel().addListSelectionListener(new ListSelectionListener(){
            @Override
            public void valueChanged(ListSelectionEvent e){
                resultSimDrugTableValueChanged(e);
            };
        });

        // Add table selection listener by Jing
        resultCellResponseTable.getSelectionModel().addListSelectionListener(new ListSelectionListener(){
            @Override
            public void valueChanged(ListSelectionEvent e){
                resultCellResponseTableValueChanged(e);
            };
        });
        
        statusPanel.setPreferredSize(new Dimension(this.getWidth(),16));
//        searchDrugProgressBar.setVisible(false);
        
        try {
            imgIcon = new ImageIcon(ImageIO.read(getClass().getResourceAsStream("/icons/pLINDAW_Icon2.png")));
        } catch (IOException e) {
        }        
        setFrameIcon(imgIcon);  
        try {
            imgLogo = ImageIO.read(getClass().getResourceAsStream("/icons/pLINDAW_Icon2.png"));
        } catch (IOException e) {            
        }
        
        try {
            imgLargeImage = ImageIO.read(getClass().getResourceAsStream("/icons/PastedGraphic-8.png"));
        } catch (IOException e) {
        }
        
        try {
            imgSchema = ImageIO.read(getClass().getResourceAsStream("/icons/pLINDAWSchema.png"));
        } catch (IOException e) {            
        }         
        
        imgLargeIcon = new ImageIcon(imgSchema);
        // 538 X 428 vs 319 X 254
        this.aboutLogoLabel.setIcon(scaleImage(imgLargeImage,319.0/538));
        // 960 X 720 vs 800 X 600
//        this.schemaLabel.setIcon(scaleImage(imgSchema,800.0/960));
        this.schemaLabel.setIcon(imgLargeIcon);
        aboutDialog.setIconImage(imgLogo);
        drugInputDialog.setIconImage(imgLogo);
        simDrugDialog.setIconImage(imgLogo);
        simDrugNWDialog.setIconImage(imgLogo);
        cellResponseDialog.setIconImage(imgLogo);
        tutorialDialog.setIconImage(imgLogo);
      
        updateConnDW();
        populateInputType();
        populateDrugList();
    }
    
    private void resultTableValueChanged(ListSelectionEvent e) {
        int selectedRow = resultTable.getSelectedRow();
        String extraText = "Description: No target gene available";
        if (selectedRow > -1){
            switch (this.resultTableType) {
                case "CP": 
                    extraText = "WellNameCP: " + resultTable.getModel().getValueAt(selectedRow,1).toString() + "\n"
                            + "WellNameKD: " + resultTable.getModel().getValueAt(selectedRow,2).toString() + "\n"
                            + "CP_ID: " + resultTable.getModel().getValueAt(selectedRow,3).toString() + "\n"
                            + "CD_Desc: " + resultTable.getModel().getValueAt(selectedRow,4).toString();  
                    drugDescTextArea1.setEnabled(true);
                    break;
                case "KS":
                    extraText = "";
                    break;
            }
        } else {
            extraText = "Description: No target gene available";
            drugDescTextArea1.setEnabled(true);
        }
        drugDescTextArea1.setText(extraText);
    }

    private void resultSimDrugTableValueChanged(ListSelectionEvent e) {
        int selectedRow = this.resultSimDrugTable.getSelectedRow();
        String extraText;
        if (selectedRow > -1){
            int modelRow = resultSimDrugTable.convertRowIndexToModel(selectedRow);
            extraText = "CP_ID: " + resultSimDrugTable.getModel().getValueAt(modelRow,1).toString() + "\n"
                    + "CD_Desc: " + resultSimDrugTable.getModel().getValueAt(modelRow,2).toString();  
            this.simDrugDescTextArea.setEnabled(true);
            showSimDrugNWButton.setEnabled(true);
            SharedData.drugID2 = resultSimDrugTable.getModel().getValueAt(modelRow,1).toString();
            SharedData.drugDesc2 = resultSimDrugTable.getModel().getValueAt(modelRow,2).toString(); 
            SharedData.drugType2 = dwDAO.getDrugType(SharedData.drugID2.trim());
            SharedData.drugSim = Double.parseDouble(resultSimDrugTable.getModel().getValueAt(modelRow,3).toString());
            SharedData.fDrugSim = true;
        } else {
            extraText = "Description: No target gene available";
            showSimDrugNWButton.setEnabled(false);
            showSimDrugNWButton.setEnabled(false);
            SharedData.drugID2 = null;
            SharedData.drugDesc2 = null; 
            SharedData.fDrugSim = false;            
        }
        simDrugDescTextArea.setText(extraText);
    }    
    
    private void resultCellResponseTableValueChanged(ListSelectionEvent e) {
        
    }
    
    private void updateConnDW(){
        connMenuItem.setEnabled(!fConnDW);
        disconnMenuItem.setEnabled(fConnDW);
        updateStatus();
    }
    
    private void updateStatus(){
        String connDW = (fConnDW)? "DW connected. ":"DW disconnected. ";
        statusLabel.setText("Status: " + connDW);
    }
    
    private void populateInputType(){
        //{"","Drug","Gene","Gene List"};selectInputTypeModel
        String[] selectInputTypeList = {"","Drug","Gene         [Available Soon]","Gene List [Available Soon]"};
        selectInputTypeModel.removeAllElements();
        for (String i : selectInputTypeList){
            selectInputTypeModel.addElement(i);
        }
        selectInputType.setModel(selectInputTypeModel);
        selectInputType.setEnabled(true);
        
//        selectInputType.setModel(new DefaultComboBoxModel(inputTypeList));
//        selectInputType.setSelectedIndex(0);
    }
 
    private void populateDrugList(){

        if (SharedData.drugIDs.size() == SharedData.drugDescs.size()){            
            for (int i = 0; i < SharedData.drugIDs.size(); i++) {
                drugListModel.addElement(SharedData.drugIDs.get(i)+"     "+SharedData.drugDescs.get(i));
            }
        }
        drugList.setModel(drugListModel);
        drugList.setSelectedIndex(-1);
        fDrugSearchModel = false;

    }
    
    private void selectInput(){
        //{"","Drug","Gene","Gene List"};selectInputTypeModel
        //statusLabel.setText("Status: " + "ComboBox Acted. " + new Date().toString());
        if (selectInputType.getSelectedIndex() > -1) {
            switch(selectInputType.getSelectedItem().toString()){
                case "":
                    break;
                case "Drug": 
                    System.out.println("------DRUG----");
                    SharedData.fDrugSim = false;
                    drugInputDialog.setLocationRelativeTo(this);
                    drugInputDialog.setVisible(true);

                    System.out.println(this.getName());
                    updateInputType();
                    break;
                case "Gene":
                    break;
                case "Gene List":
                    break;
            }
        }
    }

    private void updateInputType(){
        if (selectInputType.getSelectedItem().toString().equals("Drug")){
            inputLabel.setText((null == SharedData.drugID)
                    ? "Not Selected."
                    : "Drug ID: " + SharedData.drugID );
            drugDescTextArea.setText((null == SharedData.drugID)
                    ? "No Description. "
                    : "Description: " + SharedData.drugDesc);
        }
    }
    
    private boolean equalDrugSearch(
            String drugID, String drugDesc, 
            String searchID, String searchDesc,
            boolean fID, boolean fDesc, 
            boolean fBoth, boolean fAny,
            boolean fExact){
        boolean results = false;
        boolean matchID = fExact ?
                drugID.toUpperCase().trim().equals(searchID.toUpperCase().trim())
                : 0 == searchID.toUpperCase().trim().length() 
                    || drugID.toUpperCase().trim().indexOf(searchID.toUpperCase().trim())> -1;
        boolean matchDesc = fExact ?
                drugDesc.toUpperCase().trim().equals(searchDesc.toUpperCase().trim())
                : 0 == searchDesc.toUpperCase().trim().length() 
                    || drugDesc.toUpperCase().trim().indexOf(searchDesc.toUpperCase().trim())> -1;
        if (fBoth) {
            results = matchID && matchDesc;
        } else if (fAny) {
            results = matchID || matchDesc;
        } else if (fID) {
            results = matchID;
        } else {
            results = matchDesc;
        }
        return results;
    }
    private void enableDrugFuzzyQuery(){
        String[] selectFuzzinessTypeList = {
            "",
            "L1K Similarity",
            "L1K Gene Enrichment",
            "Regularity Network Similarity [Available Soon]",
            "KINOMEScan",
            "GO Enrichment Similarity"};
        selectDrugFuzzinessTypeModel.removeAllElements();
        for (String i : selectFuzzinessTypeList){
            selectDrugFuzzinessTypeModel.addElement(i);
        }
        selectFuzzinessType.setModel(selectDrugFuzzinessTypeModel);
        selectFuzzinessType.setEnabled(true);
        moreButton.setEnabled(true);
        drug2LIFEButton.setEnabled(true);
    }

    private DefaultTableModel resultSetToDrugTargetsL1KGeneEnrichmentTableModel(CachedRowSetImpl crs) {
        try {
                ResultSetMetaData metaData = crs.getMetaData();

                int numberOfColumns = metaData.getColumnCount();

                Vector columnNames = new Vector();
                // Get the column names
                columnNames.addElement("ID");
                for (int column = 1; column <= numberOfColumns; column++) {
//                    columnNames[column] = metaData.getColumnLabel(column + 1);
                    columnNames.addElement(metaData.getColumnLabel(column));
                }


                Vector rows = new Vector();
                crs.beforeFirst();
                int j = 1;
                while (crs.next()) {
//                    Object[] newRow = new Object[numberOfColumns];
                    Vector newRow = new Vector();
                    newRow.add(0, j);
                    newRow.add(1, crs.getString("WellNameCP"));
                    newRow.add(2, crs.getString("WellNameKD"));
                    newRow.add(3, crs.getString("CP_ID"));
                    newRow.add(4, crs.getString("CP_Desc"));
                    newRow.add(5, crs.getString("KD_ID"));
                    newRow.add(6, crs.getString("KD_Gene"));
                    newRow.add(7, crs.getString("Cell"));
                    newRow.add(8, crs.getFloat("Enrichment"));

                    rows.addElement(newRow);
//                    rows[j] = newRow;
                    j++;
                }
            System.out.println("StartFrame.resultsSetToTableModel" + j);
            return new DefaultTableModel(rows, columnNames);
//            }

        } catch (Exception e) {
            System.out.println("Exception. StartFrame.resultSetToDrugTargetsL1KGeneEnrichmentTableModel: " 
                    + e.toString());
            return null;
        }
    }    
            
    private DefaultTableModel resultSetToDrugTargetsKStTableModel(CachedRowSetImpl crs) {
//        desc dmdrugtargets;
//        +---------------------+--------------+------+-----+---------+-------+
//        | Field               | Type         | Null | Key | Default | Extra |
//        +---------------------+--------------+------+-----+---------+-------+
//        | Drug_ID             | varchar(45)  | YES  | MUL | NULL    |       |
//        | Drug_Desc           | varchar(300) | YES  | MUL | NULL    |       |
//        | Target_ID           | int(11)      | YES  | MUL | NULL    |       |
//        | Target_Protein_Name | varchar(45)  | YES  | MUL | NULL    |       |
//        | Target_Gene_Name    | varchar(45)  | YES  | MUL | NULL    |       |
//        | Drug_Effect         | double       | YES  |     | NULL    |       |
//        | Note                | varchar(45)  | YES  | MUL | NULL    |       |
//        | Concentration       | float        | YES  |     | NULL    |       |
//        | EffectUnit          | varchar(45)  | YES  |     | NULL    |       |
//        | StudyID             | varchar(45)  | YES  | MUL | NULL    |       |
//        +---------------------+--------------+------+-----+---------+-------+
      
        try {
            System.out.println(1);
            System.out.println(1.5);
            ResultSetMetaData metaData = crs.getMetaData();
            System.out.println(2);
            int numberOfColumns = metaData.getColumnCount();
            Vector columnNames = new Vector();
            // Get the column names
            columnNames.addElement("ID");
            for (int column = 1; column <= numberOfColumns; column++) {
                columnNames.addElement(metaData.getColumnLabel(column));
            }
            System.out.println(3);
            // Get all rows.
            Vector rows = new Vector();
            crs.beforeFirst();
            int j = 1;
            while (crs.next()) {
                Vector newRow = new Vector();
                newRow.add(0, j);
                newRow.add(1, crs.getString("Drug_ID"));
                newRow.add(2, crs.getString("Drug_Desc"));
                newRow.add(3, crs.getLong("Target_ID"));
                newRow.add(4, crs.getString("Target_Protein_Name"));
                newRow.add(5, crs.getString("Target_Gene_Name"));
                newRow.add(6, crs.getString("Drug_Effect"));
                newRow.add(7, crs.getString("Note"));
                newRow.add(8, crs.getFloat("Concentration"));
                newRow.add(9, crs.getString("EffectUnit"));
                newRow.add(10, crs.getString("StudyID"));
                rows.addElement(newRow);
                j++;
            }
            System.out.println("StartFrame.resultsSetToTableModel" + j);
            return new DefaultTableModel(rows, columnNames);
        } catch (Exception e) {
            System.out.println("Exception. StartFrame.resultSetToDrugTargetsL1KGeneEnrichmentTableModel: " 
                    + e.toString());
            return null;
        }
    }            

    private DefaultTableModel resultSetToSimDrugTargetsL1KGeneEnrichmentTableModel(CachedRowSetImpl crs) {
        try {
                ResultSetMetaData metaData = crs.getMetaData();

                int numberOfColumns = metaData.getColumnCount();

                Vector columnNames = new Vector();
                // Get the column names
                columnNames.addElement("ID");
                for (int column = 1; column <= numberOfColumns; column++) {
//                    columnNames[column] = metaData.getColumnLabel(column + 1);
                    columnNames.addElement(metaData.getColumnLabel(column));
                }

                // Get all rows.
//                Object[][] rows = new Object[numberOfRows][numberOfColumns];
                Vector rows = new Vector();
                crs.beforeFirst();
                this.cellResponseButton.setEnabled(false);
                int j = 1;
                
                // re-initialize 
                SharedData.drugIDCommCellResponse.clear();
                SharedData.drugDescCommCellResponse.clear();
                SharedData.commCellResponse.clear();

                while (crs.next()) {
                    Vector newRow = new Vector();
                    newRow.add(0, j);
                    if (this.commonDrugs.size() > 0){
                        for ( int i = 0; i < commonDrugs.size(); i++){
                            if (crs.getString(1).equals( commonDrugs.get(i))) {
                                System.out.println("++++++++++++cellline: "+commonDrugs.get(i));
                                this.cellResponseButton.setEnabled(true);
                                SharedData.drugIDCommCellResponse.add(crs.getString(1));
                                SharedData.drugDescCommCellResponse.add(crs.getString(2));
                                SharedData.commCellResponse.add(crs.getFloat(3));
                            }
                        }   
                    }
                    newRow.add(1, crs.getString(1));
                    newRow.add(2, crs.getString(2));
                    newRow.add(3, crs.getFloat(3));
//                    for (int i = 0; i < numberOfColumns; i++) {
//                        System.out.println(4);
////                        newRow[i] = crs.getObject(i+1);
//                        newRow.addElement(crs.getObject(i));
//                    }
                    rows.addElement(newRow);
//                    rows[j] = newRow;
                    j++;
                }
            System.out.println("StartFrame.resultsSetToTableModel" + j);
            return new DefaultTableModel(rows, columnNames);
//            }

        } catch (Exception e) {
            System.out.println("Exception. StartFrame.resultSetToDrugTargetsL1KGeneEnrichmentTableModel: " 
                    + e.toString());
            return null;
        }
    }

    private DefaultTableModel resultSetToCellResponseTableModel(CachedRowSetImpl crs) {    
        try {
            ResultSetMetaData metaData = crs.getMetaData();
            int numberOfColumns = metaData.getColumnCount();
            Vector columnNames = new Vector();
            // Get the column names
            columnNames.addElement("ID");
            for (int column = 1; column <= numberOfColumns; column++) {
                columnNames.addElement(metaData.getColumnLabel(column));
            }
            // Get all rows.
            Vector rows = new Vector();
            crs.beforeFirst();
            this.cellResponseButton.setEnabled(false);
            int j = 1;
            // re-initialize 
            while (crs.next()) {
                Vector newRow = new Vector();
//                BOOKMARK;
                newRow.add(0, j);
//                Drug_ID    varchar(45)
//                Drug_Desc    varchar(300)
//                Cell_ID    int(11)
//                Cell_Name    varchar(45)
//                Effect    varchar(45)
//                Concentration    double
//                Drug_Effect    double
//                EffectUnit    varchar(45)     
//                StudyID    varchar(45)
                newRow.add(1, crs.getString(1));
                newRow.add(2, crs.getString(2));
                newRow.add(3, crs.getInt(3));
                newRow.add(4, crs.getString(4));
                newRow.add(5, crs.getString(5));
                newRow.add(6, crs.getDouble(6));
                newRow.add(7, crs.getDouble(7));
                newRow.add(8, crs.getString(8));
                newRow.add(9, crs.getString(9));                
                rows.addElement(newRow);
                j++;
            }
            return new DefaultTableModel(rows, columnNames);
        } catch (Exception e) {
            System.out.println("Exception. StartFrame.resultSetToDrugTargetsL1KGeneEnrichmentTableModel: " 
                    + e.toString());
            return null;
        }
    }
    
    private void qPopulateDrugTargetsL1KSimilarityTableModel(String dmCP2KDFuzziness){
        dwDAO.tbDmCP2KD = dmCP2KDFuzziness.toLowerCase();
        if (dwDAO.conn == null) {
            try {
                dwDAO.conn = dwDAO.getConnectionToDatabase();
            } catch(SQLException e) {
                System.out.println("Exception. DWDAO.getColNames: " + e.getSQLState().toString());
            }
        }
        Font f = resultTable.getTableHeader().getFont();
        drugTableModel = resultSetToDrugTargetsL1KGeneEnrichmentTableModel(dwDAO.getDrug2KD(dwDAO.conn, SharedData.drugID));
        resultTable.setModel(drugTableModel);
        resultTable.getTableHeader().setFont(f.deriveFont(f.getStyle() | Font.BOLD));
//        for (int i = 0; i < colNames.size(); i++){
//            resultTable.getColumnModel().getColumn(i).setHeaderValue(colNames.get(i));            
//        }
//        String[] b = {"1","2","3"};
//        drugTableModel.addRow(b);
        resultTable.removeColumn(resultTable.getColumnModel().getColumn(1));
        resultTable.removeColumn(resultTable.getColumnModel().getColumn(1));
        resultTable.removeColumn(resultTable.getColumnModel().getColumn(1));
        resultTable.removeColumn(resultTable.getColumnModel().getColumn(1));
//        resultTable.removeColumnSelectionInterval(1, 4);
        this.resultTableType = "CP";
        resultTable.setEnabled(true); 
        simDrugButton.setEnabled(true);
    }
    
    private void qPopulateDrugTargetsL1KGeneEnrichmentTableModel(String dmCP2KDFuzziness){
//        List<String> colNames = new ArrayList<>(); 
        dwDAO.tbDmCP2KD = dmCP2KDFuzziness.toLowerCase();
        if (dwDAO.conn == null) {
            try {
                dwDAO.conn = dwDAO.getConnectionToDatabase();
            } catch(SQLException e) {
                System.out.println("Exception. DWDAO.getColNames: " + e.getSQLState().toString());
            }
        }
//        colNames = dwDAO.getColNames(dwDAO.conn);
        Font f = resultTable.getTableHeader().getFont();
        drugTableModel = resultSetToDrugTargetsL1KGeneEnrichmentTableModel(dwDAO.getDrug2KD(dwDAO.conn, SharedData.drugID));
        resultTable.setModel(drugTableModel);
        resultTable.getTableHeader().setFont(f.deriveFont(f.getStyle() | Font.BOLD));

        resultTable.removeColumn(resultTable.getColumnModel().getColumn(1));
        resultTable.removeColumn(resultTable.getColumnModel().getColumn(1));
        resultTable.removeColumn(resultTable.getColumnModel().getColumn(1));
        resultTable.removeColumn(resultTable.getColumnModel().getColumn(1));
//        resultTable.removeColumnSelectionInterval(1, 4);
        this.resultTableType = "CP";
        resultTable.setEnabled(true); 
        simDrugButton.setEnabled(true);
    }
    
    private void qPopulateDrugTargetsKSTableModel(String dmKSFuzziness){
//        List<String> colNames = new ArrayList<>(); 
        dwDAO.tbDmKS = dmKSFuzziness;
        if (dwDAO.conn == null) {
            try {
                dwDAO.conn = dwDAO.getConnectionToDatabase();
            } catch(SQLException e) {
                System.out.println("Exception. DWDAO.getColNames: " + e.getSQLState().toString());
            }
        }
//        colNames = dwDAO.getColNames(dwDAO.conn);
        Font f = resultTable.getTableHeader().getFont();
        drugTableModel = resultSetToDrugTargetsKStTableModel(dwDAO.getDrugKSTargets(dwDAO.conn, SharedData.drugID));
        resultTable.setModel(drugTableModel);
        resultTable.getTableHeader().setFont(f.deriveFont(f.getStyle() | Font.BOLD));
//            newRow.add(0, j);
//            newRow.add(1, crs.getString("Drug_ID"));
//            newRow.add(2, crs.getString("Drug_Desc"));
//            newRow.add(3, crs.getLong("Target_ID"));
//            newRow.add(4, crs.getString("Target_Protein_Name"));
//            newRow.add(5, crs.getString("Target_Gene_Name"));
//            newRow.add(6, crs.getString("Drug_Effect"));
//            newRow.add(7, crs.getString("Note"));
//            newRow.add(8, crs.getFloat("Concentration"));
//            newRow.add(9, crs.getString("EffectUnit"));
//            newRow.add(10, crs.getString("StudyID"));
        resultTable.removeColumn(resultTable.getColumnModel().getColumn(7));
        this.resultTableType = "KS";
        resultTable.setEnabled(true); 
        simDrugButton.setEnabled(true);
    }    
       
    private void qPopulateDrugTargetsGOEnrichmentSimilarityTableModel(String dmCP2KDFuzziness){
//        List<String> colNames = new ArrayList<>(); 
        dwDAO.tbDmCP2KD = dmCP2KDFuzziness.toLowerCase();
        if (dwDAO.conn == null) {
            try {
                dwDAO.conn = dwDAO.getConnectionToDatabase();
            } catch(SQLException e) {
                System.out.println("Exception. DWDAO.getColNames: " + e.getSQLState().toString());
            }
        }
//        colNames = dwDAO.getColNames(dwDAO.conn);
        Font f = resultTable.getTableHeader().getFont();
        drugTableModel = resultSetToDrugTargetsL1KGeneEnrichmentTableModel(dwDAO.getDrug2KD(dwDAO.conn, SharedData.drugID));
        resultTable.setModel(drugTableModel);
        resultTable.getTableHeader().setFont(f.deriveFont(f.getStyle() | Font.BOLD));
//        for (int i = 0; i < colNames.size(); i++){
//            resultTable.getColumnModel().getColumn(i).setHeaderValue(colNames.get(i));            
//        }
//        String[] b = {"1","2","3"};
//        drugTableModel.addRow(b);
        resultTable.removeColumn(resultTable.getColumnModel().getColumn(1));
        resultTable.removeColumn(resultTable.getColumnModel().getColumn(1));
        resultTable.removeColumn(resultTable.getColumnModel().getColumn(1));
        resultTable.removeColumn(resultTable.getColumnModel().getColumn(1));
//        resultTable.removeColumnSelectionInterval(1, 4);
        this.resultTableType = "CP";
        resultTable.setEnabled(true); 
        simDrugButton.setEnabled(true);
    }
    
    
    private void qPopulateSimDrugTargetsL1KGeneEnrichmentTableModel(String dmDrugSimFuzziness){
//        List<String> colNames = new ArrayList<>(); 
        dwDAO.drugSimFuzziness = dmDrugSimFuzziness;
//        dwDAO.tbDmDrugSim = dmDrugSimFuzziness;
        if (dwDAO.conn == null) {
            try {
                dwDAO.conn = dwDAO.getConnectionToDatabase();
            } catch(SQLException e) {
                System.out.println("Exception. DWDAO.getColNames: " + e.getSQLState().toString());
            }
        }
//        colNames = dwDAO.getColNames(dwDAO.conn);
        Font f = resultTable.getTableHeader().getFont();
        simDrugTableModel = resultSetToSimDrugTargetsL1KGeneEnrichmentTableModel(dwDAO.getDrugSim(dwDAO.conn, SharedData.drugID));
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(simDrugTableModel);
        resultSimDrugTable.setModel(simDrugTableModel);
        resultSimDrugTable.setRowSorter(sorter);
        List <TableRowSorter.SortKey> sortKeys = new ArrayList<>();
        sortKeys.add(new TableRowSorter.SortKey(3, SortOrder.DESCENDING));
        sorter.setSortKeys(sortKeys);
        resultSimDrugTable.getTableHeader().setFont(f.deriveFont(f.getStyle() | Font.BOLD));

//        resultTable.removeColumn(resultTable.getColumnModel().getColumn(1));
//        resultTable.removeColumn(resultTable.getColumnModel().getColumn(1));
//        resultTable.removeColumn(resultTable.getColumnModel().getColumn(1));
//        resultTable.removeColumn(resultTable.getColumnModel().getColumn(1));
        resultSimDrugTable.setEnabled(true); 
    }    
    
    private void qPopulateCellResponseTableModel(   List<String> selectedCells, 
                                                    List<String> selectedCellResponses){
//        BOOKMARK;
        Font f = resultTable.getTableHeader().getFont();
        cellResponseTableModel = resultSetToCellResponseTableModel(
                dwDAO.getCellResponse(selectedCells,selectedCellResponses));
        resultCellResponseTable.setModel(cellResponseTableModel);
        resultCellResponseTable.getTableHeader().setFont(f.deriveFont(f.getStyle() | Font.BOLD));
//        resultTable.removeColumn(resultTable.getColumnModel().getColumn(1));
        resultCellResponseTable.setEnabled(true); 
    }    
    
    private void populateSelectSimDrugForCellResponseFuzzinessLevel(){
        String[] itemSelectSimDrugForCellResponseFuzzinessLevelModel = {
            selectSimDrugFuzzinessLevel.getSelectedItem().toString()}; 
        selectSimDrugForCellResponseFuzzinessLevelModel.removeAllElements();
        for (int i = 0; i < itemSelectSimDrugForCellResponseFuzzinessLevelModel.length; i++){
            selectSimDrugForCellResponseFuzzinessLevelModel.addElement(itemSelectSimDrugForCellResponseFuzzinessLevelModel[i]);
        }            
        selectSimDrugForCellResponseFuzzinessLevel.setModel(this.selectSimDrugForCellResponseFuzzinessLevelModel);        
        selectSimDrugForCellResponseFuzzinessLevel.setSelectedIndex(0);
        queryCellResponse.setEnabled(false);
    }
    
    private void populateSelectCellForCellResponse(){
        List<String> itemSelectCellForCellResponse = dwDAO.getCellByDrugIDMaxEffect();
        selectCellForCellResponseModel.removeAllElements();
        if (itemSelectCellForCellResponse.size() > 0) {           
            selectCellForCellResponseModel.addElement("All");
            for (int i = 0; i < itemSelectCellForCellResponse.size(); i++){
                selectCellForCellResponseModel.addElement(itemSelectCellForCellResponse.get(i));
            }            
            selectCellForCellResponse.setModel(this.selectCellForCellResponseModel);
            selectCellForCellResponse.setEnabled(true);
            selectCellForCellResponse.setSelectedIndex(0);
        } else {
            selectCellForCellResponse.setEnabled(true);
        }        
    }
    
    private void populateSelectCellResponseForCellResponse(String cell){
        List<String> selectedCells = new ArrayList<>();
        if (selectCellForCellResponse.getSelectedItem().equals("All") 
                & selectCellForCellResponse.getItemCount() > 1){
            for (int i = 1; i < selectCellForCellResponse.getItemCount(); i++) {
                    selectedCells.add(selectCellForCellResponse.getItemAt(i).toString());
                }
        } else {
            selectedCells.add(selectCellForCellResponse.getSelectedItem().toString());
        }         
        
        List<String> itemSelectCellResponseForCellResponse = dwDAO.getCellResponseByCellMaxEffect(selectedCells);
        selectCellResponseForCellResponseModel.removeAllElements();
        if (itemSelectCellResponseForCellResponse.size() > 0) {           
            selectCellResponseForCellResponseModel.addElement("All");
            for (int i = 0; i < itemSelectCellResponseForCellResponse.size(); i++){
                selectCellResponseForCellResponseModel.addElement(itemSelectCellResponseForCellResponse.get(i));
            }            
            selectCellResponseForCellResponse.setModel(this.selectCellResponseForCellResponseModel);
            selectCellResponseForCellResponse.setEnabled(true);
            queryCellResponse.setEnabled(true);
        } else {
            selectCellResponseForCellResponse.setEnabled(false);
            queryCellResponse.setEnabled(false);
        }
    }
    
    private void initCellResponseDialog(){
        if (SharedData.drugIDCommCellResponse.isEmpty()){
            System.out.println("initCellResponseDialog: SharedData.drugIDCommCellResponse.isEmpty. ");
        } else {
            selectedDrugIDForCellResponseTextField.setText(SharedData.drugID);
            selectedDrugDescForCellResponseTextArea.setText(SharedData.drugDesc);
            
            populateSelectSimDrugForCellResponseFuzzinessLevel();
            selectSimDrugForCellResponseFuzzinessLevel.setEnabled(false);            
            populateSelectCellForCellResponse();
         }        
    }
    
    private void initSimDrugDialog(){
        boolean enableThis = false;
        this.selectedDrugIDTextField1.setText(SharedData.drugID);
        this.selectedDrugDescTextArea1.setText(SharedData.drugDesc);
//        String[] selectFuzzinessLevelListL1K = {
//            ""};         
        String[] selectFuzzinessLevelList = {
            "","Less Fuzzy (<=1%)","Fuzzy (<= 5%)","Very Fuzzy (<=10%)"};
        selectSimDrugFuzzinessLevelModel.removeAllElements();
        for (String i : selectFuzzinessLevelList){ 
            selectSimDrugFuzzinessLevelModel.addElement(i);
        }
        if (commonDrugs.size() < 1){
            commonDrugs = dwDAO.getCommonDrug();
//            System.out.println("commonDrugs size: " + commonDrugs.size());
//            for (int i = 0; i < commonDrugs.size(); i++) {
//                System.out.println("\tcommonDrugs " + i + ": " +commonDrugs.get(i));
//            }
        } 
        enableThis = true;
        
//        String[] selectFuzzinessLevelListRegularityNetwork = {
//            ""};         
//        String[] selectFuzzinessLevelListKINOMEScanTarget = {
//            "","EC85","All"}; 
//        String[] selectFuzzinessLevelListGOEnrichment = {
//            ""};         
//        selectSimDrugFuzzinessLevelModel.removeAllElements();
////        "L1K Similarity", "L1K Gene Enrichment", "Regularity Network Similarity", "KINOMEScan", "GO Enrichment Similarity"
//        switch (this.selectFuzzinessType.getSelectedItem().toString()) {
//            case "L1K Similarity":
//                for (String i : selectFuzzinessLevelListL1K){ 
//                    selectSimDrugFuzzinessLevelModel.addElement(i);
//                }
//                enableThis = false;
//                break;
//            case "L1K Gene Enrichment":
//                for (String i : selectFuzzinessLevelListL1KGeneEnrichment){ 
//                    selectSimDrugFuzzinessLevelModel.addElement(i);
//                }
//                enableThis = true;
//                break;
//            case "Regularity Network Similarity":
//                for (String i : selectFuzzinessLevelListRegularityNetwork){ 
//                    selectSimDrugFuzzinessLevelModel.addElement(i);
//                }
//                enableThis = false;
//                break;
//            case "KINOMEScan":
//                for (String i : selectFuzzinessLevelListKINOMEScanTarget){ 
//                    selectSimDrugFuzzinessLevelModel.addElement(i);
//                }
//                enableThis = true;
//                break;
//            case "GO Enrichment Similarity":
//                for (String i : selectFuzzinessLevelListGOEnrichment){ 
//                    selectSimDrugFuzzinessLevelModel.addElement(i);
//                }
//                enableThis = false;
//                break; 
//        }
        resultSimDrugTable.setModel(new DefaultTableModel());
        selectSimDrugFuzzinessLevel.setModel(selectSimDrugFuzzinessLevelModel);
        selectSimDrugFuzzinessLevel.setEnabled(enableThis);
    }
    
    private ImageIcon scaleImage(BufferedImage src, double scale) {
        int w = (int)(scale*src.getWidth(this));
        int h = (int)(scale*src.getHeight(this));
        int type = BufferedImage.TYPE_INT_RGB;
        BufferedImage dst = new BufferedImage(w, h, type);
        Graphics2D g2 = dst.createGraphics();
        g2.drawImage(src, 0, 0, w, h, this);
        g2.dispose();
        return new ImageIcon(dst);
    }
  
    private Point mousePositionInImagePanel() {
        if(MouseInfo.getPointerInfo().getLocation().x >= simDrugNWScrollPane.getLocationOnScreen().x
                && MouseInfo.getPointerInfo().getLocation().x <= simDrugNWScrollPane.getLocationOnScreen().x + simDrugNWScrollPane.getWidth()
                && MouseInfo.getPointerInfo().getLocation().y >= simDrugNWScrollPane.getLocationOnScreen().y
                && MouseInfo.getPointerInfo().getLocation().y <= simDrugNWScrollPane.getLocationOnScreen().y + simDrugNWScrollPane.getHeight()) {
           return new Point(MouseInfo.getPointerInfo().getLocation().x - simDrugNWScrollPane.getLocationOnScreen().x,
                   MouseInfo.getPointerInfo().getLocation().y - simDrugNWScrollPane.getLocationOnScreen().y);
        } else {
           return new Point((int)simDrugNWScrollPane.getWidth()/2,
                   (int)simDrugNWScrollPane.getHeight()/2);
        }
     }    
    private void link2Website(String searchItem, String websiteName){
        String sURI = "";
        switch (websiteName){
            case "UCSC": 
                sURI = "http://genome.ucsc.edu/cgi-bin/hgTracks?hgHubConnect.destUrl=..%2Fcgi-bin%2FhgTracks&clade=mammal&org=Human&db=hg19&position="
                    + searchItem 
                    + "&hgt.positionInput="
                    + searchItem 
                    + "&hgt.suggestTrack=knownGene&Submit=submit&hgsid=310078549" ;
                break;
            case "GeneCard":
                sURI = "http://www.genecards.org/cgi-bin/carddisp.pl?gene=" 
                        + searchItem 
                        + "&search=" 
                        + searchItem;
                break;
            case "NCI_Nature":
                sURI = "http://pid.nci.nih.gov/search/advanced_landing.shtml?what=graphic&svg=&jpg=true&xml=&biopax=&complex_uses=on&family_uses=on&degree=1&molecule=" 
                        + searchItem
                        + "&pathway=&macro_process=&source_id=5&evidence_code=NIL&evidence_code=IAE&evidence_code=IC&evidence_code=IDA&evidence_code=IFC&evidence_code=IGI&evidence_code=IMP&evidence_code=IOS&evidence_code=IPI&evidence_code=RCA&evidence_code=RGE&evidence_code=TAS&output-format=graphic&Submit=Go";
                break; 
            case "KEGG":
                sURI = "http://www.kegg.jp/kegg-bin/search_pathway_text?map=hsa&keyword="
                        + searchItem
                        + "&mode=1&viewImage=true";
                break;
            case "Google":
                sURI = "http://www.google.com/#hl=en&tbo=d&sclient=psy-ab&q="
                        + searchItem
                        + "+gene+human&oq=" 
                        + searchItem
                        + "+gene+human";
        }
        if (sURI.length() > 0){
            System.out.println("sURI: " + sURI);            
            try {
                URI myURL = new URI(sURI);
                System.out.println("myURL: " + myURL);
                java.awt.Desktop.getDesktop().browse(myURL);
            } catch (URISyntaxException | IOException e){            
            }  
        }
        sURI = "";
    }
           
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        drugInputDialog = new javax.swing.JDialog();
        CancelButton = new javax.swing.JButton();
        OkButton = new javax.swing.JButton();
        drugScrollPane = new javax.swing.JScrollPane();
        drugList = new javax.swing.JList();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        selectedDrugDescTextArea = new javax.swing.JTextArea();
        jPanel1 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        searchDrugIDTextField = new javax.swing.JTextField();
        searchDrugButton = new javax.swing.JButton();
        matchBothDrugID_DescCheckBox = new javax.swing.JCheckBox();
        showAllDrugButton = new javax.swing.JButton();
        matchDrugIDCheckBox = new javax.swing.JCheckBox();
        matchDrugDescCheckBox = new javax.swing.JCheckBox();
        matchExactDrugCheckBox = new javax.swing.JCheckBox();
        matchAnyDrugID_DescCheckBox = new javax.swing.JCheckBox();
        jLabel5 = new javax.swing.JLabel();
        searchDrugDescTextField = new javax.swing.JTextField();
        selectedDrugIDTextField = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        aboutDialog = new javax.swing.JDialog();
        OkButton1 = new javax.swing.JButton();
        jScrollPane5 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        aboutLogoLabel = new javax.swing.JLabel();
        jScrollPane6 = new javax.swing.JScrollPane();
        jTextArea2 = new javax.swing.JTextArea();
        simDrugDialog = new javax.swing.JDialog();
        resultsPanel1 = new javax.swing.JPanel();
        jScrollPane7 = new javax.swing.JScrollPane();
        resultSimDrugTable = new javax.swing.JTable();
        jScrollPane8 = new javax.swing.JScrollPane();
        simDrugDescTextArea = new javax.swing.JTextArea();
        showSimDrugNWButton = new javax.swing.JButton();
        cellResponseButton = new javax.swing.JButton();
        selectedDrugIDTextField1 = new javax.swing.JTextField();
        jScrollPane9 = new javax.swing.JScrollPane();
        selectedDrugDescTextArea1 = new javax.swing.JTextArea();
        exitSimDrugButton = new javax.swing.JButton();
        jLabel9 = new javax.swing.JLabel();
        querySimDrugButton = new javax.swing.JButton();
        selectSimDrugFuzzinessLevel = new javax.swing.JComboBox();
        jLabel8 = new javax.swing.JLabel();
        simDrugNWDialog = new javax.swing.JDialog();
        jLabel10 = new javax.swing.JLabel();
        resultsPanel2 = new javax.swing.JPanel();
        simDrugNWIDTextField1 = new javax.swing.JTextField();
        exitSimDrugNWButton = new javax.swing.JButton();
        jScrollPane10 = new javax.swing.JScrollPane();
        simDrugNWDescTextArea1 = new javax.swing.JTextArea();
        simDrugNWIDTextField2 = new javax.swing.JTextField();
        jScrollPane11 = new javax.swing.JScrollPane();
        simDrugNWDescTextArea2 = new javax.swing.JTextArea();
        simDrugNWSimilarityTextField = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        simDrugNWScrollPane = new javax.swing.JScrollPane();
        imgLabel = new javax.swing.JLabel();
        imgScaleTextField = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        scaleSlider = new javax.swing.JSlider();
        plotSimDrugNWButton = new javax.swing.JButton();
        jLabel15 = new javax.swing.JLabel();
        selectGraphEngine = new javax.swing.JComboBox();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        wheelZoomSpinner = new javax.swing.JSpinner();
        loadSimDrugNWButton1 = new javax.swing.JButton();
        kdGenePopupMenu = new javax.swing.JPopupMenu();
        linkKDGene2UCSCMenuItem1 = new javax.swing.JMenuItem();
        linkKDGene2GeneCardMenuItem = new javax.swing.JMenuItem();
        linkKDGene2NCI_NatureMenuItem = new javax.swing.JMenuItem();
        linkKDGene2KEGGMenuItem = new javax.swing.JMenuItem();
        linkKDGene2GoogleMenuItem = new javax.swing.JMenuItem();
        cellResponseDialog = new javax.swing.JDialog();
        resultsPanel3 = new javax.swing.JPanel();
        jScrollPane12 = new javax.swing.JScrollPane();
        resultCellResponseTable = new javax.swing.JTable();
        selectedDrugIDForCellResponseTextField = new javax.swing.JTextField();
        jScrollPane14 = new javax.swing.JScrollPane();
        selectedDrugDescForCellResponseTextArea = new javax.swing.JTextArea();
        exitCellResponseButton1 = new javax.swing.JButton();
        jLabel19 = new javax.swing.JLabel();
        queryCellResponse = new javax.swing.JButton();
        selectSimDrugForCellResponseFuzzinessLevel = new javax.swing.JComboBox();
        jLabel21 = new javax.swing.JLabel();
        selectCellForCellResponse = new javax.swing.JComboBox();
        jLabel22 = new javax.swing.JLabel();
        selectCellResponseForCellResponse = new javax.swing.JComboBox();
        jLabel20 = new javax.swing.JLabel();
        tutorialDialog = new javax.swing.JDialog();
        schemaLabel = new javax.swing.JLabel();
        tutorialOKButton = new javax.swing.JButton();
        simDrugPopupMenu = new javax.swing.JPopupMenu();
        linkSimDrug2LIFEMenuItem = new javax.swing.JMenuItem();
        linkSimDrug2DrugBankMenuItem = new javax.swing.JMenuItem();
        statusPanel = new javax.swing.JPanel();
        statusLabel = new javax.swing.JLabel();
        inputPanel = new javax.swing.JPanel();
        selectInputType = new javax.swing.JComboBox();
        inputLabel = new javax.swing.JLabel();
        reselectButton = new javax.swing.JButton();
        deselectButton = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        drugDescTextArea = new javax.swing.JTextArea();
        uploadButton = new javax.swing.JButton();
        testButton = new javax.swing.JButton();
        fuzzyPanel = new javax.swing.JPanel();
        selectFuzzinessType = new javax.swing.JComboBox();
        queryButton = new javax.swing.JButton();
        selectFuzzinessLevel = new javax.swing.JComboBox();
        jLabel1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        resultsPanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        resultTable = new javax.swing.JTable();
        jScrollPane4 = new javax.swing.JScrollPane();
        drugDescTextArea1 = new javax.swing.JTextArea();
        simDrugButton = new javax.swing.JButton();
        drug2LIFEButton = new javax.swing.JButton();
        moreButton = new javax.swing.JButton();
        reportButton = new javax.swing.JButton();
        exitButton = new javax.swing.JButton();
        exportButton = new javax.swing.JButton();
        startMenuBar = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        exportMenuItem = new javax.swing.JMenuItem();
        generateReportMenuItem = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        exitMenuItem = new javax.swing.JMenuItem();
        jMenu1 = new javax.swing.JMenu();
        uploadMenuItem = new javax.swing.JMenuItem();
        connMenu = new javax.swing.JMenu();
        connMenuItem = new javax.swing.JMenuItem();
        disconnMenuItem = new javax.swing.JMenuItem();
        helpMenu = new javax.swing.JMenu();
        helpContentMenuItem = new javax.swing.JMenuItem();
        tutorialMenuItem = new javax.swing.JMenuItem();
        aboutMenuItem = new javax.swing.JMenuItem();

        drugInputDialog.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        drugInputDialog.setTitle("pLINDAW: Select Drug");
        drugInputDialog.setMinimumSize(new java.awt.Dimension(860, 725));
        drugInputDialog.setModal(true);

        CancelButton.setText("Cancel");
        CancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CancelButtonActionPerformed(evt);
            }
        });

        OkButton.setText("OK");
        OkButton.setEnabled(false);
        OkButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                OkButtonActionPerformed(evt);
            }
        });

        drugScrollPane.setAutoscrolls(true);
        drugScrollPane.setPreferredSize(new java.awt.Dimension(166, 96));

        drugList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        drugList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                drugListMouseClicked(evt);
            }
        });
        drugList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                drugListValueChanged(evt);
            }
        });
        drugScrollPane.setViewportView(drugList);

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel2.setText("Please select the drug of interest from the list. ");

        selectedDrugDescTextArea.setEditable(false);
        selectedDrugDescTextArea.setColumns(20);
        selectedDrugDescTextArea.setLineWrap(true);
        selectedDrugDescTextArea.setRows(5);
        selectedDrugDescTextArea.setText("No description available.");
        jScrollPane3.setViewportView(selectedDrugDescTextArea);

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel4.setText("Drug ID");

        searchDrugIDTextField.setMinimumSize(new java.awt.Dimension(60, 20));
        searchDrugIDTextField.setPreferredSize(new java.awt.Dimension(60, 20));
        searchDrugIDTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchDrugIDTextFieldActionPerformed(evt);
            }
        });

        searchDrugButton.setText("Search");
        searchDrugButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchDrugButtonActionPerformed(evt);
            }
        });

        matchBothDrugID_DescCheckBox.setText("Match Both");
        matchBothDrugID_DescCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                matchBothDrugID_DescCheckBoxActionPerformed(evt);
            }
        });

        showAllDrugButton.setText("Show All");
        showAllDrugButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showAllDrugButtonActionPerformed(evt);
            }
        });

        matchDrugIDCheckBox.setSelected(true);
        matchDrugIDCheckBox.setText("Match Drug ID");
        matchDrugIDCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                matchDrugIDCheckBoxActionPerformed(evt);
            }
        });

        matchDrugDescCheckBox.setSelected(true);
        matchDrugDescCheckBox.setText("Match Drug Description");
        matchDrugDescCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                matchDrugDescCheckBoxActionPerformed(evt);
            }
        });

        matchExactDrugCheckBox.setText("Exact Match");

        matchAnyDrugID_DescCheckBox.setSelected(true);
        matchAnyDrugID_DescCheckBox.setText("Match Any");
        matchAnyDrugID_DescCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                matchAnyDrugID_DescCheckBoxActionPerformed(evt);
            }
        });

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel5.setText("Drug Description");

        searchDrugDescTextField.setMinimumSize(new java.awt.Dimension(60, 20));
        searchDrugDescTextField.setPreferredSize(new java.awt.Dimension(60, 20));
        searchDrugDescTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchDrugDescTextFieldActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel5)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(searchDrugIDTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 184, Short.MAX_VALUE)
                    .addComponent(searchDrugDescTextField, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(matchDrugIDCheckBox)
                        .addGap(42, 42, 42))
                    .addComponent(matchDrugDescCheckBox))
                .addGap(4, 4, 4)
                .addComponent(matchBothDrugID_DescCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(matchExactDrugCheckBox)
                    .addComponent(matchAnyDrugID_DescCheckBox))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(searchDrugButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(showAllDrugButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel4)
                                .addComponent(searchDrugIDTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(searchDrugButton))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel5)
                            .addComponent(searchDrugDescTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(matchAnyDrugID_DescCheckBox)
                            .addComponent(matchBothDrugID_DescCheckBox)
                            .addComponent(matchDrugIDCheckBox))
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(matchExactDrugCheckBox)
                                    .addComponent(showAllDrugButton)))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(4, 4, 4)
                                .addComponent(matchDrugDescCheckBox)))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        selectedDrugIDTextField.setEditable(false);
        selectedDrugIDTextField.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        selectedDrugIDTextField.setText("  No Drug Selected.   ");
        selectedDrugIDTextField.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        selectedDrugIDTextField.setMaximumSize(new java.awt.Dimension(400, 18));
        selectedDrugIDTextField.setName(""); // NOI18N

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel6.setText("Drug ID");

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel7.setText("Drug Description");

        javax.swing.GroupLayout drugInputDialogLayout = new javax.swing.GroupLayout(drugInputDialog.getContentPane());
        drugInputDialog.getContentPane().setLayout(drugInputDialogLayout);
        drugInputDialogLayout.setHorizontalGroup(
            drugInputDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(drugInputDialogLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(drugInputDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(drugInputDialogLayout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(drugInputDialogLayout.createSequentialGroup()
                        .addComponent(selectedDrugIDTextField, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(OkButton, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(CancelButton))
                    .addComponent(drugScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(15, Short.MAX_VALUE))
            .addGroup(drugInputDialogLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jLabel6)
                .addGap(133, 133, 133)
                .addComponent(jLabel7)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        drugInputDialogLayout.setVerticalGroup(
            drugInputDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(drugInputDialogLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(drugInputDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(OkButton)
                    .addComponent(CancelButton)
                    .addComponent(selectedDrugIDTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 60, Short.MAX_VALUE)
                .addGroup(drugInputDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(drugScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 434, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        aboutDialog.setTitle("About the pLINDAW-SCHALE Fuzzy Query System");
        aboutDialog.setMinimumSize(new java.awt.Dimension(750, 500));
        aboutDialog.setType(java.awt.Window.Type.POPUP);

        OkButton1.setText("OK");
        OkButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                OkButton1ActionPerformed(evt);
            }
        });

        jTextArea1.setEditable(false);
        jTextArea1.setColumns(20);
        jTextArea1.setFont(new java.awt.Font("Monospaced", 1, 13)); // NOI18N
        jTextArea1.setRows(5);
        jTextArea1.setText("pLINDAW - SCHALE System\n\nTMHRI and UF LINCS Center\n\nA pan-LINCS Data Warehouse system to \n  - seamlessly integrate LINCS data, \n  - solidify data processing and mining \napproaches developed by TMHRI and other\nLINCS centers,\n  - powered by fuzzy query engines based on \nmultiple similarity metrics, \n  - provide user friendly interface. ");
        jTextArea1.setWrapStyleWord(true);
        jScrollPane5.setViewportView(jTextArea1);

        aboutLogoLabel.setMinimumSize(new java.awt.Dimension(233, 185));
        aboutLogoLabel.setName(""); // NOI18N
        aboutLogoLabel.setPreferredSize(new java.awt.Dimension(233, 185));

        jTextArea2.setColumns(20);
        jTextArea2.setRows(5);
        jTextArea2.setText("Product Version: pLINDAW PreVer 0.11.15.2012 (Build 20121115)\nJava: 1.7.0_09; Java HotSpot(TM) 64-Bit Server VM 23.5-b02\nSystem: Windows 7; Windows XP; Linux X Windows; Mac OS\nLicense: GNU Lesser General Public License v3.0 (www.gnu.org/copyleft/lesser.html)\n\njLINDAW: the Java frontend of pLINDAW\nDeveloper: Jing Su\npLINDAW: the data ware house\nDeveloper: Jing Su, Caty Chung, Chenglin Liu, Amar Koleti, and Christopher Mader\n");
        jTextArea2.setToolTipText("");
        jScrollPane6.setViewportView(jTextArea2);

        javax.swing.GroupLayout aboutDialogLayout = new javax.swing.GroupLayout(aboutDialog.getContentPane());
        aboutDialog.getContentPane().setLayout(aboutDialogLayout);
        aboutDialogLayout.setHorizontalGroup(
            aboutDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(aboutDialogLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(aboutDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane6)
                    .addGroup(aboutDialogLayout.createSequentialGroup()
                        .addComponent(aboutLogoLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 320, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(aboutDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(aboutDialogLayout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(OkButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 392, Short.MAX_VALUE))))
                .addContainerGap())
        );
        aboutDialogLayout.setVerticalGroup(
            aboutDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, aboutDialogLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(aboutDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane5)
                    .addComponent(aboutLogoLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 254, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(OkButton1)
                .addContainerGap())
        );

        simDrugDialog.setTitle("pLINDAW: Discovering Similar Drugs");
        simDrugDialog.setMinimumSize(new java.awt.Dimension(741, 480));
        simDrugDialog.setModal(true);

        resultsPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Results", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.BELOW_TOP, new java.awt.Font("Tahoma", 1, 12))); // NOI18N
        resultsPanel1.setName("Fuzziness"); // NOI18N

        resultSimDrugTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "ID", "Name", "Score", "Notes"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        resultSimDrugTable.setEnabled(false);
        resultSimDrugTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        resultSimDrugTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                resultSimDrugTableMouseClicked(evt);
            }
        });
        jScrollPane7.setViewportView(resultSimDrugTable);

        simDrugDescTextArea.setEditable(false);
        simDrugDescTextArea.setColumns(20);
        simDrugDescTextArea.setLineWrap(true);
        simDrugDescTextArea.setRows(5);
        simDrugDescTextArea.setText("Description: ");
        simDrugDescTextArea.setEnabled(false);
        jScrollPane8.setViewportView(simDrugDescTextArea);

        showSimDrugNWButton.setText("Regulatory Network");
        showSimDrugNWButton.setEnabled(false);
        showSimDrugNWButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showSimDrugNWButtonActionPerformed(evt);
            }
        });

        cellResponseButton.setText("Cell Response");
        cellResponseButton.setEnabled(false);
        cellResponseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cellResponseButtonActionPerformed(evt);
            }
        });

        selectedDrugIDTextField1.setEditable(false);
        selectedDrugIDTextField1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        selectedDrugIDTextField1.setText("  No Drug Selected.   ");
        selectedDrugIDTextField1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        selectedDrugIDTextField1.setMaximumSize(new java.awt.Dimension(400, 18));
        selectedDrugIDTextField1.setName(""); // NOI18N

        selectedDrugDescTextArea1.setEditable(false);
        selectedDrugDescTextArea1.setColumns(20);
        selectedDrugDescTextArea1.setLineWrap(true);
        selectedDrugDescTextArea1.setRows(5);
        selectedDrugDescTextArea1.setText("No description available.");
        jScrollPane9.setViewportView(selectedDrugDescTextArea1);

        exitSimDrugButton.setText("OK");
        exitSimDrugButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitSimDrugButtonActionPerformed(evt);
            }
        });

        jLabel9.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel9.setText("Select Fuzziness Level");

        querySimDrugButton.setText("Query");
        querySimDrugButton.setActionCommand("Set");
        querySimDrugButton.setEnabled(false);
        querySimDrugButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                querySimDrugButtonActionPerformed(evt);
            }
        });

        selectSimDrugFuzzinessLevel.setEnabled(false);
        selectSimDrugFuzzinessLevel.setPreferredSize(new java.awt.Dimension(40, 20));
        selectSimDrugFuzzinessLevel.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                selectSimDrugFuzzinessLevelItemStateChanged(evt);
            }
        });
        selectSimDrugFuzzinessLevel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectSimDrugFuzzinessLevelActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout resultsPanel1Layout = new javax.swing.GroupLayout(resultsPanel1);
        resultsPanel1.setLayout(resultsPanel1Layout);
        resultsPanel1Layout.setHorizontalGroup(
            resultsPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(resultsPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(resultsPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(resultsPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(jScrollPane9, javax.swing.GroupLayout.PREFERRED_SIZE, 619, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(resultsPanel1Layout.createSequentialGroup()
                            .addComponent(selectedDrugIDTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 193, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(jLabel9)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(selectSimDrugFuzzinessLevel, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(querySimDrugButton, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 619, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(resultsPanel1Layout.createSequentialGroup()
                        .addComponent(jScrollPane8, javax.swing.GroupLayout.PREFERRED_SIZE, 423, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(resultsPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(cellResponseButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(showSimDrugNWButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(exitSimDrugButton, javax.swing.GroupLayout.PREFERRED_SIZE, 188, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        resultsPanel1Layout.setVerticalGroup(
            resultsPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(resultsPanel1Layout.createSequentialGroup()
                .addGroup(resultsPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(selectedDrugIDTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(querySimDrugButton)
                    .addComponent(selectSimDrugFuzzinessLevel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane9, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 186, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(resultsPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(resultsPanel1Layout.createSequentialGroup()
                        .addComponent(showSimDrugNWButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cellResponseButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(exitSimDrugButton))
                    .addComponent(jScrollPane8, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel8.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel8.setText("Similar Drugs");

        javax.swing.GroupLayout simDrugDialogLayout = new javax.swing.GroupLayout(simDrugDialog.getContentPane());
        simDrugDialog.getContentPane().setLayout(simDrugDialogLayout);
        simDrugDialogLayout.setHorizontalGroup(
            simDrugDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(simDrugDialogLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(simDrugDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel8)
                    .addComponent(resultsPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(50, Short.MAX_VALUE))
        );
        simDrugDialogLayout.setVerticalGroup(
            simDrugDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, simDrugDialogLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(resultsPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(44, Short.MAX_VALUE))
        );

        simDrugNWDialog.setTitle("pLINDAW: Supply Chain Model & Random Walk MCMC");
        simDrugNWDialog.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        simDrugNWDialog.setMinimumSize(new java.awt.Dimension(740, 540));
        simDrugNWDialog.setModal(true);

        jLabel10.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel10.setText("Similar Drugs: Regulatory Network");

        resultsPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.BELOW_TOP, new java.awt.Font("Tahoma", 1, 12))); // NOI18N
        resultsPanel2.setName("Fuzziness"); // NOI18N
        resultsPanel2.setPreferredSize(new java.awt.Dimension(706, 480));

        simDrugNWIDTextField1.setEditable(false);
        simDrugNWIDTextField1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        simDrugNWIDTextField1.setText("  No Drug Selected.   ");
        simDrugNWIDTextField1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        simDrugNWIDTextField1.setMaximumSize(new java.awt.Dimension(400, 18));
        simDrugNWIDTextField1.setName(""); // NOI18N

        exitSimDrugNWButton.setText("OK");
        exitSimDrugNWButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitSimDrugNWButtonActionPerformed(evt);
            }
        });

        simDrugNWDescTextArea1.setEditable(false);
        simDrugNWDescTextArea1.setColumns(20);
        simDrugNWDescTextArea1.setLineWrap(true);
        simDrugNWDescTextArea1.setRows(5);
        simDrugNWDescTextArea1.setText("No description available.");
        jScrollPane10.setViewportView(simDrugNWDescTextArea1);

        simDrugNWIDTextField2.setEditable(false);
        simDrugNWIDTextField2.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        simDrugNWIDTextField2.setText("  No Drug Selected.   ");
        simDrugNWIDTextField2.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        simDrugNWIDTextField2.setMaximumSize(new java.awt.Dimension(400, 18));
        simDrugNWIDTextField2.setName(""); // NOI18N

        simDrugNWDescTextArea2.setEditable(false);
        simDrugNWDescTextArea2.setColumns(20);
        simDrugNWDescTextArea2.setLineWrap(true);
        simDrugNWDescTextArea2.setRows(5);
        simDrugNWDescTextArea2.setText("No description available.");
        jScrollPane11.setViewportView(simDrugNWDescTextArea2);

        simDrugNWSimilarityTextField.setEditable(false);
        simDrugNWSimilarityTextField.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        simDrugNWSimilarityTextField.setText("  No Similarity Available.   ");
        simDrugNWSimilarityTextField.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        simDrugNWSimilarityTextField.setMaximumSize(new java.awt.Dimension(400, 18));
        simDrugNWSimilarityTextField.setName(""); // NOI18N

        jLabel11.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel11.setText("Drug 1");

        jLabel12.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel12.setText("Similarity");

        jLabel13.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel13.setText("Drug 2");

        simDrugNWScrollPane.setMaximumSize(new java.awt.Dimension(812, 488));
        simDrugNWScrollPane.setPreferredSize(new java.awt.Dimension(680, 264));
        simDrugNWScrollPane.setWheelScrollingEnabled(false);
        simDrugNWScrollPane.addMouseWheelListener(new java.awt.event.MouseWheelListener() {
            public void mouseWheelMoved(java.awt.event.MouseWheelEvent evt) {
                simDrugNWScrollPaneMouseWheelMoved(evt);
            }
        });
        simDrugNWScrollPane.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                simDrugNWScrollPaneMouseReleased(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                simDrugNWScrollPaneMousePressed(evt);
            }
        });
        simDrugNWScrollPane.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                simDrugNWScrollPaneMouseDragged(evt);
            }
        });

        imgLabel.setMaximumSize(new java.awt.Dimension(2147483647, 2147483647));
        imgLabel.setName(""); // NOI18N
        imgLabel.setPreferredSize(new java.awt.Dimension(680, 264));
        simDrugNWScrollPane.setViewportView(imgLabel);

        imgScaleTextField.setEditable(false);
        imgScaleTextField.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        imgScaleTextField.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        imgScaleTextField.setMaximumSize(new java.awt.Dimension(400, 18));
        imgScaleTextField.setName(""); // NOI18N

        jLabel14.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel14.setText("Scale");

        scaleSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                scaleSliderStateChanged(evt);
            }
        });

        plotSimDrugNWButton.setText("Plot Network");
        plotSimDrugNWButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                plotSimDrugNWButtonActionPerformed(evt);
            }
        });

        jLabel15.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel15.setText("Select Graph Engine");

        selectGraphEngine.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        selectGraphEngine.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "circo", "dot", "fdp", "neato", "nop", "nop1", "nop2", "sfdp", "twopi" }));
        selectGraphEngine.setSelectedIndex(1);
        selectGraphEngine.setPreferredSize(new java.awt.Dimension(40, 20));
        selectGraphEngine.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                selectGraphEngineItemStateChanged(evt);
            }
        });
        selectGraphEngine.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectGraphEngineActionPerformed(evt);
            }
        });

        jLabel16.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel16.setText("(+)");

        jLabel17.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel17.setText("Zoom (-)");

        jLabel18.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel18.setText("Zooming speed");

        wheelZoomSpinner.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        wheelZoomSpinner.setModel(new javax.swing.SpinnerNumberModel(5, 1, 15, 2));
        wheelZoomSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                wheelZoomSpinnerStateChanged(evt);
            }
        });

        javax.swing.GroupLayout resultsPanel2Layout = new javax.swing.GroupLayout(resultsPanel2);
        resultsPanel2.setLayout(resultsPanel2Layout);
        resultsPanel2Layout.setHorizontalGroup(
            resultsPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(resultsPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(resultsPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(resultsPanel2Layout.createSequentialGroup()
                        .addGroup(resultsPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(simDrugNWIDTextField1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                        .addGroup(resultsPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(jLabel12)
                            .addComponent(simDrugNWSimilarityTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 193, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(resultsPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 235, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(simDrugNWIDTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, 207, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, resultsPanel2Layout.createSequentialGroup()
                        .addComponent(jScrollPane10)
                        .addGap(357, 357, 357))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, resultsPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel14)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(imgScaleTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel15)
                        .addGap(16, 16, 16)
                        .addComponent(selectGraphEngine, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(plotSimDrugNWButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(exitSimDrugNWButton, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(28, 28, 28))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, resultsPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel17)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(scaleSlider, javax.swing.GroupLayout.PREFERRED_SIZE, 380, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel16)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, resultsPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(resultsPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(simDrugNWScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 654, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane11, javax.swing.GroupLayout.PREFERRED_SIZE, 311, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(resultsPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel18)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(wheelZoomSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(38, 38, 38))
        );
        resultsPanel2Layout.setVerticalGroup(
            resultsPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(resultsPanel2Layout.createSequentialGroup()
                .addGroup(resultsPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(resultsPanel2Layout.createSequentialGroup()
                        .addGroup(resultsPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel11)
                            .addComponent(jLabel13))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(resultsPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(simDrugNWIDTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(simDrugNWIDTextField2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(resultsPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel12)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(simDrugNWSimilarityTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(4, 4, 4)
                .addGroup(resultsPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane10, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(jScrollPane11, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(simDrugNWScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 302, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(resultsPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel16)
                    .addComponent(jLabel17)
                    .addComponent(scaleSlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel18)
                    .addComponent(wheelZoomSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(resultsPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel14)
                    .addComponent(imgScaleTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(resultsPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(selectGraphEngine, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel15)
                        .addComponent(plotSimDrugNWButton)
                        .addComponent(exitSimDrugNWButton)))
                .addGap(13, 13, 13))
        );

        loadSimDrugNWButton1.setText("Show Network");
        loadSimDrugNWButton1.setMaximumSize(new java.awt.Dimension(0, 0));
        loadSimDrugNWButton1.setMinimumSize(new java.awt.Dimension(0, 0));
        loadSimDrugNWButton1.setPreferredSize(new java.awt.Dimension(0, 0));
        loadSimDrugNWButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loadSimDrugNWButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout simDrugNWDialogLayout = new javax.swing.GroupLayout(simDrugNWDialog.getContentPane());
        simDrugNWDialog.getContentPane().setLayout(simDrugNWDialogLayout);
        simDrugNWDialogLayout.setHorizontalGroup(
            simDrugNWDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(simDrugNWDialogLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(simDrugNWDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(simDrugNWDialogLayout.createSequentialGroup()
                        .addComponent(jLabel10)
                        .addGap(68, 68, 68)
                        .addComponent(loadSimDrugNWButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(resultsPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 680, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(50, Short.MAX_VALUE))
        );
        simDrugNWDialogLayout.setVerticalGroup(
            simDrugNWDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, simDrugNWDialogLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(simDrugNWDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(loadSimDrugNWButton1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(resultsPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 450, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(56, Short.MAX_VALUE))
        );

        linkKDGene2UCSCMenuItem1.setText("Search in UCSC Gene Browser...");
        linkKDGene2UCSCMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                linkKDGene2UCSCMenuItem1ActionPerformed(evt);
            }
        });
        kdGenePopupMenu.add(linkKDGene2UCSCMenuItem1);

        linkKDGene2GeneCardMenuItem.setText("Search in GeneCard...");
        linkKDGene2GeneCardMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                linkKDGene2GeneCardMenuItemActionPerformed(evt);
            }
        });
        kdGenePopupMenu.add(linkKDGene2GeneCardMenuItem);

        linkKDGene2NCI_NatureMenuItem.setText("Search in NCI-Nature...");
        linkKDGene2NCI_NatureMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                linkKDGene2NCI_NatureMenuItemActionPerformed(evt);
            }
        });
        kdGenePopupMenu.add(linkKDGene2NCI_NatureMenuItem);

        linkKDGene2KEGGMenuItem.setText("Search in KEGG...");
        linkKDGene2KEGGMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                linkKDGene2KEGGMenuItemActionPerformed(evt);
            }
        });
        kdGenePopupMenu.add(linkKDGene2KEGGMenuItem);

        linkKDGene2GoogleMenuItem.setText("search in Google...");
        linkKDGene2GoogleMenuItem.setToolTipText("");
        linkKDGene2GoogleMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                linkKDGene2GoogleMenuItemActionPerformed(evt);
            }
        });
        kdGenePopupMenu.add(linkKDGene2GoogleMenuItem);

        cellResponseDialog.setTitle("pLINDAW: Cell Responses");
        cellResponseDialog.setMinimumSize(new java.awt.Dimension(900, 500));
        cellResponseDialog.setModal(true);

        resultsPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Results", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.BELOW_TOP, new java.awt.Font("Tahoma", 1, 12))); // NOI18N
        resultsPanel3.setName("Fuzziness"); // NOI18N

        resultCellResponseTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "ID", "Name", "Score", "Notes"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        resultCellResponseTable.setEnabled(false);
        resultCellResponseTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        resultCellResponseTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                resultCellResponseTableMouseClicked(evt);
            }
        });
        jScrollPane12.setViewportView(resultCellResponseTable);

        selectedDrugIDForCellResponseTextField.setEditable(false);
        selectedDrugIDForCellResponseTextField.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        selectedDrugIDForCellResponseTextField.setText("  No Drug Selected.   ");
        selectedDrugIDForCellResponseTextField.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        selectedDrugIDForCellResponseTextField.setMaximumSize(new java.awt.Dimension(400, 18));
        selectedDrugIDForCellResponseTextField.setName(""); // NOI18N

        selectedDrugDescForCellResponseTextArea.setEditable(false);
        selectedDrugDescForCellResponseTextArea.setColumns(20);
        selectedDrugDescForCellResponseTextArea.setLineWrap(true);
        selectedDrugDescForCellResponseTextArea.setRows(5);
        selectedDrugDescForCellResponseTextArea.setText("No description available.");
        jScrollPane14.setViewportView(selectedDrugDescForCellResponseTextArea);

        exitCellResponseButton1.setText("OK");
        exitCellResponseButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitCellResponseButton1ActionPerformed(evt);
            }
        });

        jLabel19.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel19.setText("Fuzziness Level");

        queryCellResponse.setText("Query");
        queryCellResponse.setActionCommand("Set");
        queryCellResponse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                queryCellResponseActionPerformed(evt);
            }
        });

        selectSimDrugForCellResponseFuzzinessLevel.setPreferredSize(new java.awt.Dimension(40, 20));
        selectSimDrugForCellResponseFuzzinessLevel.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                selectSimDrugForCellResponseFuzzinessLevelItemStateChanged(evt);
            }
        });
        selectSimDrugForCellResponseFuzzinessLevel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectSimDrugForCellResponseFuzzinessLevelActionPerformed(evt);
            }
        });

        jLabel21.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel21.setText("Cell");

        selectCellForCellResponse.setPreferredSize(new java.awt.Dimension(40, 20));
        selectCellForCellResponse.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                selectCellForCellResponseItemStateChanged(evt);
            }
        });
        selectCellForCellResponse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectCellForCellResponseActionPerformed(evt);
            }
        });

        jLabel22.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel22.setText("Cell Response");

        selectCellResponseForCellResponse.setPreferredSize(new java.awt.Dimension(40, 20));
        selectCellResponseForCellResponse.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                selectCellResponseForCellResponseItemStateChanged(evt);
            }
        });
        selectCellResponseForCellResponse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectCellResponseForCellResponseActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout resultsPanel3Layout = new javax.swing.GroupLayout(resultsPanel3);
        resultsPanel3.setLayout(resultsPanel3Layout);
        resultsPanel3Layout.setHorizontalGroup(
            resultsPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(resultsPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(resultsPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane12, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane14, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(resultsPanel3Layout.createSequentialGroup()
                        .addGroup(resultsPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(resultsPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel19)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(selectSimDrugForCellResponseFuzzinessLevel, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(selectedDrugIDForCellResponseTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 299, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 113, Short.MAX_VALUE)
                        .addGroup(resultsPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel22, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel21, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(resultsPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(selectCellForCellResponse, 0, 220, Short.MAX_VALUE)
                            .addComponent(selectCellResponseForCellResponse, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                        .addComponent(queryCellResponse, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, resultsPanel3Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(exitCellResponseButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        resultsPanel3Layout.setVerticalGroup(
            resultsPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(resultsPanel3Layout.createSequentialGroup()
                .addGroup(resultsPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(queryCellResponse)
                    .addGroup(resultsPanel3Layout.createSequentialGroup()
                        .addGroup(resultsPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(selectedDrugIDForCellResponseTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(selectCellForCellResponse, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel21))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 8, Short.MAX_VALUE)
                        .addGroup(resultsPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(resultsPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(selectCellResponseForCellResponse, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel22))
                            .addGroup(resultsPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(selectSimDrugForCellResponseFuzzinessLevel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel19)))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane14, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane12, javax.swing.GroupLayout.PREFERRED_SIZE, 240, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(exitCellResponseButton1)
                .addContainerGap())
        );

        jLabel20.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel20.setText("Cell Responses");

        javax.swing.GroupLayout cellResponseDialogLayout = new javax.swing.GroupLayout(cellResponseDialog.getContentPane());
        cellResponseDialog.getContentPane().setLayout(cellResponseDialogLayout);
        cellResponseDialogLayout.setHorizontalGroup(
            cellResponseDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(cellResponseDialogLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(cellResponseDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel20)
                    .addComponent(resultsPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(49, Short.MAX_VALUE))
        );
        cellResponseDialogLayout.setVerticalGroup(
            cellResponseDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, cellResponseDialogLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel20)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(resultsPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(52, Short.MAX_VALUE))
        );

        tutorialDialog.setTitle("Tutorial: The Schema of pLINDAW");
        tutorialDialog.setAlwaysOnTop(true);
        tutorialDialog.setMinimumSize(new java.awt.Dimension(1000, 760));
        tutorialDialog.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tutorialDialogMouseClicked(evt);
            }
        });

        schemaLabel.setMaximumSize(null);
        schemaLabel.setMinimumSize(new java.awt.Dimension(960, 720));
        schemaLabel.setPreferredSize(new java.awt.Dimension(960, 720));

        tutorialOKButton.setLabel("OK");
        tutorialOKButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tutorialOKButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout tutorialDialogLayout = new javax.swing.GroupLayout(tutorialDialog.getContentPane());
        tutorialDialog.getContentPane().setLayout(tutorialDialogLayout);
        tutorialDialogLayout.setHorizontalGroup(
            tutorialDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tutorialDialogLayout.createSequentialGroup()
                .addGroup(tutorialDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(tutorialOKButton, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(schemaLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 30, Short.MAX_VALUE))
        );
        tutorialDialogLayout.setVerticalGroup(
            tutorialDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tutorialDialogLayout.createSequentialGroup()
                .addComponent(schemaLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(22, 22, 22)
                .addComponent(tutorialOKButton)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        linkSimDrug2LIFEMenuItem.setText("Search in LIFE...");
        linkSimDrug2LIFEMenuItem.setActionCommand("");
        linkSimDrug2LIFEMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                linkSimDrug2LIFEMenuItemActionPerformed(evt);
            }
        });
        simDrugPopupMenu.add(linkSimDrug2LIFEMenuItem);

        linkSimDrug2DrugBankMenuItem.setText("search in DrugBank...");
        linkSimDrug2DrugBankMenuItem.setToolTipText("");
        linkSimDrug2DrugBankMenuItem.setActionCommand("");
        linkSimDrug2DrugBankMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                linkSimDrug2DrugBankMenuItemActionPerformed(evt);
            }
        });
        simDrugPopupMenu.add(linkSimDrug2DrugBankMenuItem);

        setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("Start");
        setToolTipText("");
        setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        setMinimumSize(new java.awt.Dimension(0, 0));
        setName("StartFrame"); // NOI18N
        setPreferredSize(new java.awt.Dimension(950, 740));
        addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                formFocusGained(evt);
            }
        });

        statusPanel.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));

        statusLabel.setText("Status: ");

        javax.swing.GroupLayout statusPanelLayout = new javax.swing.GroupLayout(statusPanel);
        statusPanel.setLayout(statusPanelLayout);
        statusPanelLayout.setHorizontalGroup(
            statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(statusLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        statusPanelLayout.setVerticalGroup(
            statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, statusPanelLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(statusLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        inputPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Input", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.BELOW_TOP, new java.awt.Font("Tahoma", 1, 12))); // NOI18N

        selectInputType.setToolTipText("Select Type of Input Data");
        selectInputType.setPreferredSize(new java.awt.Dimension(40, 20));
        selectInputType.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                selectInputTypeItemStateChanged(evt);
            }
        });
        selectInputType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectInputTypeActionPerformed(evt);
            }
        });

        inputLabel.setText("Not selected. ");
        inputLabel.setToolTipText("Your selected ID will be posted here");
        inputLabel.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        inputLabel.setMaximumSize(new java.awt.Dimension(800, 16));
        inputLabel.setRequestFocusEnabled(false);

        reselectButton.setText("Reselect");
        reselectButton.setToolTipText("Reslect another type of Data");
        reselectButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                reselectButtonActionPerformed(evt);
            }
        });

        deselectButton.setText("Deselect");
        deselectButton.setToolTipText("Clear input field");
        deselectButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deselectButtonActionPerformed(evt);
            }
        });

        drugDescTextArea.setEditable(false);
        drugDescTextArea.setColumns(20);
        drugDescTextArea.setLineWrap(true);
        drugDescTextArea.setRows(5);
        drugDescTextArea.setToolTipText("Description of the selected Drug or Gene");
        jScrollPane2.setViewportView(drugDescTextArea);

        uploadButton.setText("Upload");
        uploadButton.setEnabled(false);
        uploadButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                uploadButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout inputPanelLayout = new javax.swing.GroupLayout(inputPanel);
        inputPanel.setLayout(inputPanelLayout);
        inputPanelLayout.setHorizontalGroup(
            inputPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(inputPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(inputPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(inputLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(inputPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addGroup(inputPanelLayout.createSequentialGroup()
                            .addComponent(selectInputType, javax.swing.GroupLayout.PREFERRED_SIZE, 458, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(uploadButton)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(reselectButton)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(deselectButton))
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 841, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        inputPanelLayout.setVerticalGroup(
            inputPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(inputPanelLayout.createSequentialGroup()
                .addGroup(inputPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(selectInputType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(reselectButton)
                    .addComponent(deselectButton)
                    .addComponent(uploadButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(inputLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        testButton.setText("Test");
        testButton.setName("Test"); // NOI18N
        testButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                testButtonActionPerformed(evt);
            }
        });

        fuzzyPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Fuzziness", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.BELOW_TOP, new java.awt.Font("Tahoma", 1, 12))); // NOI18N
        fuzzyPanel.setName("Fuzziness"); // NOI18N

        selectFuzzinessType.setToolTipText("Please Select a kind of  Fuzziness Type");
        selectFuzzinessType.setEnabled(false);
        selectFuzzinessType.setPreferredSize(new java.awt.Dimension(40, 20));
        selectFuzzinessType.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                selectFuzzinessTypeItemStateChanged(evt);
            }
        });
        selectFuzzinessType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectFuzzinessTypeActionPerformed(evt);
            }
        });

        queryButton.setText("Query");
        queryButton.setToolTipText("Click this button to query result from server.");
        queryButton.setActionCommand("Set");
        queryButton.setEnabled(false);
        queryButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                queryButtonActionPerformed(evt);
            }
        });

        selectFuzzinessLevel.setToolTipText("please select level of fuzziness");
        selectFuzzinessLevel.setEnabled(false);
        selectFuzzinessLevel.setPreferredSize(new java.awt.Dimension(40, 20));
        selectFuzzinessLevel.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                selectFuzzinessLevelItemStateChanged(evt);
            }
        });
        selectFuzzinessLevel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectFuzzinessLevelActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel1.setText("Select Fuzziness Type");

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel3.setText("Select Fuzziness Level");

        javax.swing.GroupLayout fuzzyPanelLayout = new javax.swing.GroupLayout(fuzzyPanel);
        fuzzyPanel.setLayout(fuzzyPanelLayout);
        fuzzyPanelLayout.setHorizontalGroup(
            fuzzyPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(fuzzyPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(selectFuzzinessType, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(16, 16, 16)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(selectFuzzinessLevel, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(queryButton, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        fuzzyPanelLayout.setVerticalGroup(
            fuzzyPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(fuzzyPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(selectFuzzinessType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(queryButton)
                .addComponent(selectFuzzinessLevel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(jLabel1)
                .addComponent(jLabel3))
        );

        resultsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Results", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.BELOW_TOP, new java.awt.Font("Tahoma", 1, 12))); // NOI18N
        resultsPanel.setName("Fuzziness"); // NOI18N

        resultTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "ID", "Name", "Score", "Notes"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        resultTable.setToolTipText("");
        resultTable.setEnabled(false);
        resultTable.setSelectionMode(javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        resultTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                resultTableMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(resultTable);

        drugDescTextArea1.setEditable(false);
        drugDescTextArea1.setColumns(20);
        drugDescTextArea1.setLineWrap(true);
        drugDescTextArea1.setRows(5);
        drugDescTextArea1.setText("Description: ");
        drugDescTextArea1.setEnabled(false);
        jScrollPane4.setViewportView(drugDescTextArea1);

        simDrugButton.setText("Similar Drugs");
        simDrugButton.setEnabled(false);
        simDrugButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                simDrugButtonActionPerformed(evt);
            }
        });

        drug2LIFEButton.setToolTipText("Search related data on LIFE websit");
        drug2LIFEButton.setEnabled(false);
        drug2LIFEButton.setLabel("Search LIFE");
        drug2LIFEButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                drug2LIFEButtonActionPerformed(evt);
            }
        });

        moreButton.setText("Search Drug Bank");
        moreButton.setToolTipText("Search related data on Drug Bank websit.");
        moreButton.setEnabled(false);
        moreButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                moreButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout resultsPanelLayout = new javax.swing.GroupLayout(resultsPanel);
        resultsPanel.setLayout(resultsPanelLayout);
        resultsPanelLayout.setHorizontalGroup(
            resultsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(resultsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(resultsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 837, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(resultsPanelLayout.createSequentialGroup()
                        .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 690, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(28, 28, 28)
                        .addGroup(resultsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(simDrugButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(moreButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(drug2LIFEButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        resultsPanelLayout.setVerticalGroup(
            resultsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(resultsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 243, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(resultsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(resultsPanelLayout.createSequentialGroup()
                        .addComponent(simDrugButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(drug2LIFEButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(moreButton))
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap())
        );

        reportButton.setText("Report");
        reportButton.setEnabled(false);
        reportButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                reportButtonActionPerformed(evt);
            }
        });

        exitButton.setText("Exit");
        exitButton.setToolTipText("Close this software");
        exitButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitButtonActionPerformed(evt);
            }
        });

        exportButton.setText("Export");
        exportButton.setEnabled(false);
        exportButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exportButtonActionPerformed(evt);
            }
        });

        fileMenu.setText("File");
        fileMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fileMenuActionPerformed(evt);
            }
        });

        exportMenuItem.setText("Export...");
        fileMenu.add(exportMenuItem);

        generateReportMenuItem.setText("Generate Report");
        fileMenu.add(generateReportMenuItem);
        fileMenu.add(jSeparator1);

        exitMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_W, java.awt.event.InputEvent.CTRL_MASK));
        exitMenuItem.setText("Exit");
        exitMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(exitMenuItem);

        startMenuBar.add(fileMenu);

        jMenu1.setText("Data");

        uploadMenuItem.setText("Upload");
        jMenu1.add(uploadMenuItem);

        startMenuBar.add(jMenu1);

        connMenu.setText("Data Warehouse");
        connMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                connMenuActionPerformed(evt);
            }
        });

        connMenuItem.setText("Connect");
        connMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                connMenuItemActionPerformed(evt);
            }
        });
        connMenu.add(connMenuItem);

        disconnMenuItem.setText("Disconnect");
        disconnMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                disconnMenuItemActionPerformed(evt);
            }
        });
        connMenu.add(disconnMenuItem);

        startMenuBar.add(connMenu);

        helpMenu.setText("Help");

        helpContentMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F1, 0));
        helpContentMenuItem.setText("Help Contents");
        helpMenu.add(helpContentMenuItem);

        tutorialMenuItem.setText("Tutorial");
        tutorialMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tutorialMenuItemActionPerformed(evt);
            }
        });
        helpMenu.add(tutorialMenuItem);

        aboutMenuItem.setText("About");
        aboutMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aboutMenuItemActionPerformed(evt);
            }
        });
        helpMenu.add(aboutMenuItem);

        startMenuBar.add(helpMenu);

        setJMenuBar(startMenuBar);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(inputPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(fuzzyPanel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(resultsPanel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(testButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(exportButton, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(reportButton, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(exitButton, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(statusPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(inputPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(fuzzyPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(resultsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(exitButton)
                    .addComponent(reportButton)
                    .addComponent(exportButton)
                    .addComponent(testButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(statusPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void exitMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitMenuItemActionPerformed
        System.exit(0);
    }//GEN-LAST:event_exitMenuItemActionPerformed

    private void connMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_connMenuActionPerformed

    }//GEN-LAST:event_connMenuActionPerformed

    private void connMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_connMenuItemActionPerformed
        try{
            dwDAO.conn = dwDAO.getConnectionToDatabase();
            fConnDW = true;
            updateConnDW();
        }
        catch (SQLException ex) {
            System.out.println("Exception: " + ex.getMessage().toString());
        }             

    }//GEN-LAST:event_connMenuItemActionPerformed

    private void disconnMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_disconnMenuItemActionPerformed
        try{
            dwDAO.closeConnection(dwDAO.conn);
            fConnDW = false;
            updateConnDW();
        }
        catch (SQLException ex) {
            System.out.println("Exception: " + ex.getMessage().toString());
        }          
    }//GEN-LAST:event_disconnMenuItemActionPerformed

    private void selectInputTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectInputTypeActionPerformed
//        statusLabel.setText("Status: " + "ComboBox Acted. " + new Date().toString());
//        if (inputType.getSelectedItem().toString().equals("Drug")){
//            drugInputDialog.setLocationRelativeTo(this);
//            drugInputDialog.setVisible(true);
//            updateInputType();
//            InputDrugFrame inputDrugFrame = new InputDrugFrame();
//            PLINDAW.mainFrame.addFrame(inputDrugFrame);
//        }
    }//GEN-LAST:event_selectInputTypeActionPerformed

    private void formFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_formFocusGained
        //updateInputType();
    }//GEN-LAST:event_formFocusGained

    private void testButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_testButtonActionPerformed
//        File dir = new File(".");
//        try{
//            statusLabel.setText("Status: " 
//                + dir.getAbsolutePath() + "  " 
//                + dir.getCanonicalPath() + "  " 
//                + new Date().toString());
//        } catch (Exception e) {}
//        statusLabel.setText("Status: " 
//                + SharedData.drugIDs_DescsFileName + "  "
//                + (new File(SharedData.drugIDs_DescsFileName)).exists());
//        statusLabel.setText("Status: " 
//            + SharedData.drugIDs_Descs.toArray()[4]);
//        for (Iterator i = SharedData.drugIDs_Descs.iterator(); i.hasNext();) {
//            statusLabel.setText("Status: " + i.next());
//        }        
//        statusLabel.setText("Status: " 
//            + SharedData.drugIDs_Descs.size());
//        statusLabel.setText("Status: " 
//            + this.getName() + "   "
//            + this.toString());
//        SharedData.drugID="BRD-A19500257-001-02-1";
//        SharedData.drugID2="BRD-A90515964-001-09-0";
//        if (dwDAO.conn == null) {
//            try {
//                dwDAO.conn = dwDAO.getConnectionToDatabase();
//            } catch(SQLException e) {
//                System.out.println("Exception. DWDAO.getColNames: " + e.getSQLState().toString());
//            }
//        }
//        System.out.println("dwDAO.determineDrugNW(): " + dwDAO.determineDrugNW());
//        String sURI = "https://www.google.com/search?q=use+java+to+open+a+web+page&oq=use+java+to+open+a+web+page&sugexp=chrome,mod=0&sourceid=chrome&ie=UTF-8&safe=active";
//        try {
//            URI myURL = new URI(sURI);
//            java.awt.Desktop.getDesktop().browse(myURL);
//        } catch (URISyntaxException | IOException e){
//        }
        if (SharedData.drugID.length() >0 ){
            System.out.println("dwDAO.getDrugType(SharedData.drugID)" + dwDAO.getDrugType(SharedData.drugID));
        }
    }//GEN-LAST:event_testButtonActionPerformed

    private void CancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CancelButtonActionPerformed
//        selectedDrugDescTextArea.setText(drugInputDialog.getName() + "\n\n"
//            + drugInputDialog.toString());

//        selectedDrugDescTextArea.setText(" SPane VP: " + drugScrollPane.getViewport().toString()
//                + ";\n\n SPane VP getViewSize: " + drugScrollPane.getViewport().getViewSize()
//                + ";\n\n SPnae getView: " + drugScrollPane.getViewport().getView());
//        DefaultListModel drugListModel2 = new DefaultListModel(); 
//        drugListModel2.addElement("Clear to empty");
//        drugList.setModel(drugListModel2);
//
//        selectedDrugDescTextArea.setText("Length of searchDrugIDTextField: " 
//                + searchDrugIDTextField.getText().toUpperCase().trim().length());
        
        drugList.setModel(drugListModel);
        drugList.setSelectedIndex(-1);
        fDrugSearchModel = false;
        drugInputDialog.setVisible(false);
//        this.setEnabled(true);
    }//GEN-LAST:event_CancelButtonActionPerformed

    private void OkButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_OkButtonActionPerformed
//        if (SharedData.fDrugSim){
//            SharedData.drugID2 = selectedInputDrugID;        
//            SharedData.drugDesc2 = selectedInputDrugDesc;  
//        } else {
            SharedData.drugID = selectedInputDrugID;        
            SharedData.drugDesc = selectedInputDrugDesc;  
            SharedData.drugType = dwDAO.getDrugType(SharedData.drugID.trim());
            drugInputDialog.setVisible(false);
            updateInputType();
            drugList.setModel(drugListModel);
            drugList.setSelectedIndex(-1);
            fDrugSearchModel = false;
//            this.setEnabled(true);
            enableDrugFuzzyQuery();
//        }

    }//GEN-LAST:event_OkButtonActionPerformed

    private void selectInputTypeItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_selectInputTypeItemStateChanged
        System.out.println("----selectInputTypeItemStateChanged" + evt.toString());
        if(evt.getStateChange()==java.awt.event.ItemEvent.SELECTED)
        {
         selectInput();           
        }
    }//GEN-LAST:event_selectInputTypeItemStateChanged

    private void drugListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_drugListValueChanged
        if (0 != drugList.getModel().getSize()){
//            jLabel2.setText("; drugIDs size:" + SharedData.drugIDs.size()
//                    + "; fDrugSearchModel: " + fDrugSearchModel 
//                    + "; drugList selected: " + drugList.getSelectedIndex()
//                    + "; mapDrugModels size: " + mapDrugModels.size());
            int i = fDrugSearchModel
                    ? -1 == drugList.getSelectedIndex() 
                        ? -1
                        : mapDrugModels.get(drugList.getSelectedIndex())
                    : drugList.getSelectedIndex();
            OkButton.setEnabled(true);
//            jLabel2.setText("i: " + i + jLabel2.getText());
            selectedInputDrugID = -1 == i ? "" : SharedData.drugIDs.get(i);
            selectedInputDrugDesc = -1 == i ? "" : SharedData.drugDescs.get(i);
            selectedDrugIDTextField.setText(" Selected Drug: " + selectedInputDrugID + "  ");
            selectedDrugDescTextArea.setText(" Drug Description: \n\t" + selectedInputDrugDesc);
        }
    }//GEN-LAST:event_drugListValueChanged

    private void reselectButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_reselectButtonActionPerformed
        resultTable.setModel(new DefaultTableModel());
        System.out.println("----reselectButtonActionPerformed");
        selectInput();
    }//GEN-LAST:event_reselectButtonActionPerformed

    private void deselectButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deselectButtonActionPerformed
        SharedData.drugID = null;
        System.out.println("Deselect: "+(null == SharedData.drugID));
        updateInputType();
    }//GEN-LAST:event_deselectButtonActionPerformed

    private void selectFuzzinessTypeItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_selectFuzzinessTypeItemStateChanged
        //{"","L1K Similarity","L1K Gene Enrichment","Regularity Network Similarity","GO Enrichment Similarity"}
        boolean enableThis = false;
        this.selectedDrugIDTextField1.setText(SharedData.drugID);
        this.selectedDrugDescTextArea1.setText(SharedData.drugDesc);
        String[] selectFuzzinessLevelListL1K = {
            "","Less Fuzzy (<=5%)","Fuzzy (<= 10%)","Very Fuzzy (<=25%)"};         
        String[] selectFuzzinessLevelListL1KGeneEnrichment = {
            "","Less Fuzzy (<=5%)","Fuzzy (<= 10%)","Very Fuzzy (<=25%)"};
        String[] selectFuzzinessLevelListRegularityNetwork = {
            ""};         
        String[] selectFuzzinessLevelListKINOMEScanTarget = {
            "","EC85","All"}; 
        String[] selectFuzzinessLevelListGOEnrichment = {
            "","Less Fuzzy (<=5%)","Fuzzy (<= 10%)","Very Fuzzy (<=25%)"};         
        if (selectFuzzinessType.getSelectedIndex() > -1) {
            switch (selectFuzzinessType.getSelectedItem().toString()){
                case "": 
//                    selectFuzzinessLevel.setEnabled(false);
                    enableThis = false;
                    break;
                case "L1K Similarity":
//                    String[] selectFuzzinessLevelListL1KSimilarity = {"","Less Fuzzy (<=5%)","Fuzzy (<= 10%)","Very Fuzzy (<=25%)"};
                    selectFuzzinessLevelModel.removeAllElements();
                    for (String i : selectFuzzinessLevelListL1K){
                        selectFuzzinessLevelModel.addElement(i);
                    }
                    selectFuzzinessLevel.setModel(selectFuzzinessLevelModel);
//                    selectFuzzinessLevel.setEnabled(true);   
                    enableThis = true;
                    break;
                case "L1K Gene Enrichment":
//                    String[] selectFuzzinessLevelListL1KGeneEnrichment = {"","Less Fuzzy (<=5%)","Fuzzy (<= 10%)","Very Fuzzy (<=25%)"};
                    selectFuzzinessLevelModel.removeAllElements();
                    for (String i : selectFuzzinessLevelListL1KGeneEnrichment){
                        selectFuzzinessLevelModel.addElement(i);
                    }
                    selectFuzzinessLevel.setModel(selectFuzzinessLevelModel);
//                    selectFuzzinessLevel.setEnabled(true); 
                    enableThis = true;
                    break;
                case "Regularity Network Similarity": 
//                    selectFuzzinessLevel.setEnabled(false);
                    enableThis = false;
                    break;
                case "KINOMEScan":
                    selectFuzzinessLevelModel.removeAllElements();
                    for (String i : selectFuzzinessLevelListKINOMEScanTarget){ 
                        selectFuzzinessLevelModel.addElement(i);
                    }
                    selectFuzzinessLevel.setModel(selectFuzzinessLevelModel);
                    enableThis = true;
                    break;                    
                case "GO Enrichment Similarity": 
//                    selectFuzzinessLevel.setEnabled(false);
                    
//                    String[] selectFuzzinessLevelListL1KGeneEnrichment = {"","Less Fuzzy (<=5%)","Fuzzy (<= 10%)","Very Fuzzy (<=25%)"};
                    selectFuzzinessLevelModel.removeAllElements();
                    for (String i : selectFuzzinessLevelListGOEnrichment){
                        selectFuzzinessLevelModel.addElement(i);
                    }
                    selectFuzzinessLevel.setModel(selectFuzzinessLevelModel);
//                    selectFuzzinessLevel.setEnabled(true);                     
                    enableThis = true;
                    break;
                default:
//                    selectFuzzinessLevel.setEnabled(false);
                    enableThis = false;
            }
        }
        selectFuzzinessLevel.setEnabled(enableThis);
    }//GEN-LAST:event_selectFuzzinessTypeItemStateChanged

    private void selectFuzzinessTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectFuzzinessTypeActionPerformed
//        //{"","L1K Similarity","Regularity Network Similarity","GO Enrichment Similarity"}
//        switch (selectFuzzinessType.getSelectedItem().toString()){
//            case "": 
//                selectFuzzinessLevel.setEnabled(false);
//                break;
//            case "L1K Similarity":
//                String[] selectFuzzinessLevelList = {"","Less Fuzzy (<=5%)","Fuzzy (<= 10%)","Very Fuzzy (<=25%)"};
//                selectFuzzinessLevelModel.removeAllElements();
//                for (String i : selectFuzzinessLevelList){
//                    selectFuzzinessLevelModel.addElement(i);
//                }
//                selectFuzzinessLevel.setModel(selectFuzzinessLevelModel);
//                selectFuzzinessLevel.setEnabled(true);
//                //statusLabel.setText("Status: " + connDW);
//                break;
//            case "Regularity Network Similarity": 
//                selectFuzzinessLevel.setEnabled(false);
//                break;
//            case "GO Enrichment Similarity": 
//                selectFuzzinessLevel.setEnabled(false);
//                break;                
//        }
    }//GEN-LAST:event_selectFuzzinessTypeActionPerformed

    private void queryButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_queryButtonActionPerformed
        if (selectInputType.getSelectedIndex() > -1) {
            switch(selectInputType.getSelectedItem().toString()){
                case "":
                    break;
                case "Drug": 
                    if (selectFuzzinessType.getSelectedIndex() > -1) {
                    switch (selectFuzzinessType.getSelectedItem().toString()){
                        case "": 
                            break;
                        case "L1K Similarity":                            
                            if (selectFuzzinessLevel.getSelectedIndex() > -1) {
                                switch (selectFuzzinessLevel.getSelectedItem().toString()){
                                    case "": 
                                        break;
                                    case "Less Fuzzy (<=5%)":
                                        qPopulateDrugTargetsL1KSimilarityTableModel("dmCP2KD_5");
                                        break;
                                    case "Fuzzy (<= 10%)": 
                                        qPopulateDrugTargetsL1KSimilarityTableModel("dmCP2KD_10");
                                        break;
                                    case "Very Fuzzy (<=25%)": 
                                        qPopulateDrugTargetsL1KSimilarityTableModel("dmCP2KD_25");
                                        break;
                                }
                            }                                    
                            break;
                        case "L1K Gene Enrichment":
                            if (selectFuzzinessLevel.getSelectedIndex() > -1) {
                                switch (selectFuzzinessLevel.getSelectedItem().toString()){
                                    case "": 
                                        break;
                                    case "Less Fuzzy (<=5%)":
                                        qPopulateDrugTargetsL1KGeneEnrichmentTableModel("dmCP2KD_5");
                                        break;
                                    case "Fuzzy (<= 10%)": 
                                        qPopulateDrugTargetsL1KGeneEnrichmentTableModel("dmCP2KD_10");
                                        break;
                                    case "Very Fuzzy (<=25%)": 
                                        qPopulateDrugTargetsL1KGeneEnrichmentTableModel("dmCP2KD_25");
                                        break;
                                }
                            }
                             break;
                        case "KINOMEScan":
                            if (selectFuzzinessLevel.getSelectedIndex() > -1) {
//                                "","EC85","All"
                                switch (selectFuzzinessLevel.getSelectedItem().toString()){
                                    case "": 
                                        break;
                                    case "EC85": 
                                        qPopulateDrugTargetsKSTableModel("dmdrugtargets_85");
                                        break;
                                    case "All": 
                                        qPopulateDrugTargetsKSTableModel("dmdrugtargets");
                                        break;
                                }
                            }
                             break;                            
                        case "Regularity Network Similarity": 
                            break;
                        case "GO Enrichment Similarity": 
                            if (selectFuzzinessLevel.getSelectedIndex() > -1) {
                                switch (selectFuzzinessLevel.getSelectedItem().toString()){
                                    case "": 
                                        break;
                                    case "Less Fuzzy (<=5%)":
                                        qPopulateDrugTargetsGOEnrichmentSimilarityTableModel("dmCP2KD_5");
                                        break;
                                    case "Fuzzy (<= 10%)": 
                                        qPopulateDrugTargetsGOEnrichmentSimilarityTableModel("dmCP2KD_10");
                                        break;
                                    case "Very Fuzzy (<=25%)": 
                                        qPopulateDrugTargetsGOEnrichmentSimilarityTableModel("dmCP2KD_25");
                                        break;
                                }
                            } 
                            break;                
                    }
                }
                    break;
                case "Gene":
                    break;
                case "Gene List":
                    break;
            }
        }

        
//        String[] colNames = {"Drug ID","Score","Notes"};
//        Font f = resultTable.getTableHeader().getFont();
//        drugTableModel.setRowCount(0);
//        drugTableModel.setColumnCount(colNames.length);
//        resultTable.setModel(drugTableModel);
//        resultTable.getTableHeader().setFont(f.deriveFont(f.getStyle() | Font.BOLD));
//        for (int i = 0; i < colNames.length; i++){
//            resultTable.getColumnModel().getColumn(i).setHeaderValue(colNames[i]);            
//        }
//        String[] b = {"1","2","3"};
//        drugTableModel.addRow(b);
//        resultTable.setEnabled(true);
//        Font f = resultTable.getTableHeader().getFont();
//        drugTableModel.setRowCount(0);
//        drugTableModel.setColumnCount(colNames.size());
//        resultTable.setModel(drugTableModel);
//        resultTable.getTableHeader().setFont(f.deriveFont(f.getStyle() | Font.BOLD));
//        for (int i = 0; i < colNames.size(); i++){
//            resultTable.getColumnModel().getColumn(i).setHeaderValue(colNames.get(i));            
//        }
////        String[] b = {"1","2","3"};
////        drugTableModel.addRow(b);
//        resultTable.setEnabled(true);        
    }//GEN-LAST:event_queryButtonActionPerformed

    private void reportButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_reportButtonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_reportButtonActionPerformed

    private void selectFuzzinessLevelItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_selectFuzzinessLevelItemStateChanged
        //{"","Less Fuzzy (<=5%)","Fuzzy (<= 10%)","Very Fuzzy (<=25%)"}
//         "","EC85","All"

        // statusLabel.setText("Status:" + selectFuzzinessLevel.getSelectedIndex());
        if (selectFuzzinessLevel.getSelectedIndex() > -1) {
            switch (selectFuzzinessLevel.getSelectedItem().toString()){
                case "": 
                    queryButton.setEnabled(false);
                    break;
                case "Less Fuzzy (<=5%)":
                    queryButton.setEnabled(true);
                    break;
                case "Fuzzy (<= 10%)": 
                    queryButton.setEnabled(true);
                    break;
                case "Very Fuzzy (<=25%)": 
                    queryButton.setEnabled(true);
                    break;
                case "EC85": 
                    queryButton.setEnabled(true);
                    break;
                case "All)": 
                    queryButton.setEnabled(true);
                    break;                    
            }
        }
    }//GEN-LAST:event_selectFuzzinessLevelItemStateChanged

    private void selectFuzzinessLevelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectFuzzinessLevelActionPerformed
//        //{"","Less Fuzzy (<=5%)","Fuzzy (<= 10%)","Very Fuzzy (<=25%)"}
//        switch (selectFuzzinessLevel.getSelectedItem().toString()){
//            case "": 
//                queryButton.setEnabled(false);
//                break;
//            case "Less Fuzzy (<=5%)":
//                queryButton.setEnabled(true);
//                break;
//            case "Fuzzy (<= 10%)": 
//                queryButton.setEnabled(false);
//                break;
//            case "Very Fuzzy (<=25%)": 
//                queryButton.setEnabled(false);
//                break;                
//        }        
    }//GEN-LAST:event_selectFuzzinessLevelActionPerformed

    private void exitButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitButtonActionPerformed
        System.exit(0);
    }//GEN-LAST:event_exitButtonActionPerformed

    private void moreButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_moreButtonActionPerformed
        String sURI = "http://www.drugbank.ca/search?utf8=%E2%9C%93&query=" 
                + SharedData.drugDesc.trim() +"&commit=Search";
        System.out.println("sURI: " + sURI);
        System.out.println("SharedData.drugDesc: `" + SharedData.drugDesc + "`");
        try {
            URI myURL = new URI(sURI);
            System.out.println("myURL: " + myURL);
            java.awt.Desktop.getDesktop().browse(myURL);
        } catch (URISyntaxException | IOException e){            
        }                
    }//GEN-LAST:event_moreButtonActionPerformed

    private void OkButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_OkButton1ActionPerformed
        aboutDialog.setVisible(false);
    }//GEN-LAST:event_OkButton1ActionPerformed

    private void aboutMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aboutMenuItemActionPerformed
        aboutDialog.setAlwaysOnTop(true);
        aboutDialog.setLocationRelativeTo(getRootPane());
        aboutDialog.setVisible(true);
    }//GEN-LAST:event_aboutMenuItemActionPerformed

    private void searchDrugButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchDrugButtonActionPerformed
        drugListSearchModel.clear();
        mapDrugModels.clear(); 
        if (SharedData.drugIDs.size() == SharedData.drugDescs.size()){
//            searchDrugProgressBar.setVisible(true);
//            searchDrugProgressBar.setMinimum(0);
//            searchDrugProgressBar.setMaximum(SharedData.drugIDs.size()-1);
            int j = 0;
            for (int i = 0; i < SharedData.drugIDs.size(); i++) {
                if (equalDrugSearch(
                        SharedData.drugIDs.get(i),
                        SharedData.drugDescs.get(i),
                        searchDrugIDTextField.getText(),
                        searchDrugDescTextField.getText(),
                        matchDrugIDCheckBox.isSelected(),
                        matchDrugDescCheckBox.isSelected(),
                        matchBothDrugID_DescCheckBox.isSelected(),
                        matchAnyDrugID_DescCheckBox.isSelected(),
                        matchExactDrugCheckBox.isSelected())){
                    drugListSearchModel.addElement(SharedData.drugIDs.get(i)+"     "+SharedData.drugDescs.get(i));
                    mapDrugModels.put(j, i);
                    j++;                    
                }
//                searchDrugProgressBar.setValue(i);
            }
            //searchDrugProgressBar.setValue(searchDrugProgressBar.getMinimum());
//            searchDrugProgressBar.setVisible(false);
        }
//        drugInputDialog.setTitle("mapDrugModels size: " + mapDrugModels.size());
        drugList.setModel(drugListSearchModel);
        drugList.setSelectedIndex(-1);
        fDrugSearchModel = true;
    }//GEN-LAST:event_searchDrugButtonActionPerformed

    private void uploadButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_uploadButtonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_uploadButtonActionPerformed

    private void exportButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exportButtonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_exportButtonActionPerformed

    private void fileMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fileMenuActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_fileMenuActionPerformed

    private void showAllDrugButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showAllDrugButtonActionPerformed
        drugList.setModel(drugListModel);
        drugList.setSelectedIndex(-1);
        fDrugSearchModel = false;
    }//GEN-LAST:event_showAllDrugButtonActionPerformed

    private void matchBothDrugID_DescCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_matchBothDrugID_DescCheckBoxActionPerformed
        if (matchBothDrugID_DescCheckBox.isSelected()){
            matchAnyDrugID_DescCheckBox.setSelected(false);
            matchDrugIDCheckBox.setSelected(true);
            matchDrugDescCheckBox.setSelected(true);
        } else if (matchDrugIDCheckBox.isSelected() && matchDrugDescCheckBox.isSelected()) {
            matchAnyDrugID_DescCheckBox.setSelected(true);
        }   
    }//GEN-LAST:event_matchBothDrugID_DescCheckBoxActionPerformed

    private void matchAnyDrugID_DescCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_matchAnyDrugID_DescCheckBoxActionPerformed
        if (matchAnyDrugID_DescCheckBox.isSelected()){
            matchBothDrugID_DescCheckBox.setSelected(false);
            matchDrugIDCheckBox.setSelected(true);
            matchDrugDescCheckBox.setSelected(true);
        } else if (matchDrugIDCheckBox.isSelected() && matchDrugDescCheckBox.isSelected()) {
            matchBothDrugID_DescCheckBox.setSelected(true);
        }
    }//GEN-LAST:event_matchAnyDrugID_DescCheckBoxActionPerformed

    private void matchDrugIDCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_matchDrugIDCheckBoxActionPerformed
        if (!matchDrugIDCheckBox.isSelected()){
            matchBothDrugID_DescCheckBox.setSelected(false);
            matchAnyDrugID_DescCheckBox.setSelected(false);
            matchDrugDescCheckBox.setSelected(true);
        } else if (matchDrugDescCheckBox.isSelected() 
                && !matchBothDrugID_DescCheckBox.isSelected()){
            matchAnyDrugID_DescCheckBox.setSelected(true);
        }
    }//GEN-LAST:event_matchDrugIDCheckBoxActionPerformed

    private void matchDrugDescCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_matchDrugDescCheckBoxActionPerformed
        if (!matchDrugDescCheckBox.isSelected()){
            matchBothDrugID_DescCheckBox.setSelected(false);
            matchAnyDrugID_DescCheckBox.setSelected(false);
            matchDrugIDCheckBox.setSelected(true);
        } else if (matchDrugIDCheckBox.isSelected() 
                && !matchBothDrugID_DescCheckBox.isSelected()){
            matchAnyDrugID_DescCheckBox.setSelected(true);
        }
    }//GEN-LAST:event_matchDrugDescCheckBoxActionPerformed

    private void simDrugButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_simDrugButtonActionPerformed
        SharedData.fDrugSim = true;
        initSimDrugDialog();
        simDrugDialog.setLocationRelativeTo(this);
        simDrugDialog.setVisible(true);
//        this.setEnabled(false);
    }//GEN-LAST:event_simDrugButtonActionPerformed

    private void showSimDrugNWButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showSimDrugNWButtonActionPerformed
        this.simDrugNWIDTextField1.setText(SharedData.drugID);
        this.simDrugNWDescTextArea1.setText(SharedData.drugDesc);
        this.simDrugNWIDTextField2.setText(SharedData.drugID2);
        this.simDrugNWDescTextArea2.setText(SharedData.drugDesc2);
        this.simDrugNWSimilarityTextField.setText(Double.toString(SharedData.drugSim));
        dwDAO.determineDrugNW();
//        try {
//            img = ImageIO.read(new File("tmp.png"));
//        } catch (IOException e) {
//        }
////        ImageIcon img = new ImageIcon("data/TestMap2.png");
//        Integer iH = img.getHeight();
//        Integer iW = img.getWidth();
//        Integer lH = imgLabel.getPreferredSize().height;
//        Integer lW = imgLabel.getPreferredSize().width;
//        scale = Math.min(1.0*lH/iH,1.0*lW/iW);
//        imgScaleTextField.setText(Double.toString(scale));
//        scaleSlider.setMinimum(0);
//        scaleSlider.setMaximum(100);
//        scaleSlider.setMinorTickSpacing(1);
//        scaleSlider.setMajorTickSpacing(10);
//        scaleSlider.setValue((int)(100.0*scale));
//        System.out.println("Scale: " + scale + "; iH: " + iH 
//                + ", iW: " + iW + "; lH: " + lH + ", lW: " + lW);
//        imgLabel.setIcon(scaleImage(img,scale));
        imgLabel.setIcon(null);
        simDrugNWDialog.setLocationRelativeTo(this);
        simDrugNWDialog.setVisible(true);
//        simDrugDialog.setEnabled(false);
    }//GEN-LAST:event_showSimDrugNWButtonActionPerformed

    private void cellResponseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cellResponseButtonActionPerformed
        initCellResponseDialog();
        cellResponseDialog.setLocationRelativeTo(this);
        cellResponseDialog.setVisible(true);
    }//GEN-LAST:event_cellResponseButtonActionPerformed

    private void exitSimDrugButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitSimDrugButtonActionPerformed
        SharedData.fDrugSim = false;
        simDrugDialog.setLocationRelativeTo(this);
        simDrugDialog.setVisible(false);
        this.setEnabled(true);
    }//GEN-LAST:event_exitSimDrugButtonActionPerformed

    private void selectSimDrugFuzzinessLevelItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_selectSimDrugFuzzinessLevelItemStateChanged
        //{"","Less Fuzzy (<=5%)","Fuzzy (<= 10%)","Very Fuzzy (<=25%)"}

        // statusLabel.setText("Status:" + selectFuzzinessLevel.getSelectedIndex());
        if (selectSimDrugFuzzinessLevel.getSelectedIndex() > -1) {
            switch (selectSimDrugFuzzinessLevel.getSelectedItem().toString()){
                case "": 
                    querySimDrugButton.setEnabled(false);
                    break;
                case "Less Fuzzy (<=1%)":
                    querySimDrugButton.setEnabled(true);
                    break;
                case "Fuzzy (<= 5%)": 
                    querySimDrugButton.setEnabled(true);
                    break;
                case "Very Fuzzy (<=10%)": 
                    querySimDrugButton.setEnabled(true);
                    break;
            }
        }
    }//GEN-LAST:event_selectSimDrugFuzzinessLevelItemStateChanged

    private void selectSimDrugFuzzinessLevelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectSimDrugFuzzinessLevelActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_selectSimDrugFuzzinessLevelActionPerformed

    private void querySimDrugButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_querySimDrugButtonActionPerformed
        if (selectSimDrugFuzzinessLevel.getSelectedIndex() > -1) {
            switch (selectSimDrugFuzzinessLevel.getSelectedItem().toString()){
                case "": 
                    break;
                case "Less Fuzzy (<=1%)":
                    qPopulateSimDrugTargetsL1KGeneEnrichmentTableModel("dmDrugSim_1");
                    break;
                case "Fuzzy (<= 5%)": 
                    qPopulateSimDrugTargetsL1KGeneEnrichmentTableModel("dmDrugSim_5");
                    break;
                case "Very Fuzzy (<=10%)": 
                    qPopulateSimDrugTargetsL1KGeneEnrichmentTableModel("dmDrugSim_10");
                    break;
            }
        }
    }//GEN-LAST:event_querySimDrugButtonActionPerformed

    private void exitSimDrugNWButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitSimDrugNWButtonActionPerformed
        if(thDrawGraphFile != null && thDrawGraphFile.isAlive()){
            System.out.println("--------draw graph exit");
            try{
                plotSimDrugNWButton.setEnabled(false);
                raDrawGraphFile.shutdown();                
                thDrawGraphFile.join();
                thDrawGraphFile = null;
                raDrawGraphFile = null;
                plotSimDrugNWButton.setText("Plot Network");
                plotSimDrugNWButton.setEnabled(true);
                loadSimDrugNWButton1ActionPerformed(new ActionEvent(
                    loadSimDrugNWButton1,ActionEvent.ACTION_PERFORMED,"")); 
            }catch(InterruptedException e){               
            }          
        }
        
        simDrugNWDialog.setLocationRelativeTo(this);
        simDrugNWDialog.setVisible(false);
        simDrugDialog.setEnabled(true);
    }//GEN-LAST:event_exitSimDrugNWButtonActionPerformed

    private void scaleSliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_scaleSliderStateChanged
        double scaleNow = scaleSlider.getValue()/100.0;
        Point pMouse = mousePositionInImagePanel();
        Point pViewPort = simDrugNWScrollPane.getViewport().getViewPosition();
        Dimension dOldImage = new Dimension(imgLabel.getIcon().getIconWidth(),imgLabel.getIcon().getIconHeight());
        this.imgScaleTextField.setText(String.valueOf(scaleNow));
//        imagePanel.setScale(scaleNow);
        imgLabel.setIcon(scaleImage(img,scaleNow));
        imgLabel.setPreferredSize(new Dimension(
                imgLabel.getIcon().getIconWidth(),
                imgLabel.getIcon().getIconHeight()));
//        imgLabel.setPreferredSize(new Dimension((int)Math.ceil(imgLabel.getHeight()*scaleNow/scale), 
//                (int)Math.ceil(imgLabel.getWidth()*scaleNow/scale)));
        imgLabel.revalidate();
        imgLabel.repaint();
        Dimension dNewImage = new Dimension(imgLabel.getIcon().getIconWidth(),imgLabel.getIcon().getIconHeight());
        if (dOldImage.height > 2){
//            System.out.println("pMouse: " + pMouse 
//                    + "\t" + "pViewPort: " + pViewPort
//                    + "\t" + "dOldImage: " + dOldImage
//                    + "\t" + "dNewImage: " + dNewImage
//                    + "\t" + "New VP Location: " + new Point(
//                    (int)((double)dNewImage.width/dOldImage.width*(pViewPort.x+pMouse.x)-pMouse.x),
//                    (int)((double)dNewImage.height/dOldImage.height*(pViewPort.y+pMouse.y)-pMouse.y))); 
            simDrugNWScrollPane.getViewport().setViewPosition( new Point(
                    (int)((double)dNewImage.width/dOldImage.width*(pViewPort.x+pMouse.x)-pMouse.x),
                    (int)((double)dNewImage.height/dOldImage.height*(pViewPort.y+pMouse.y)-pMouse.y)));    
        }        
//        this.simDrugNWScrollPane.getViewport().removeAll();
//        this.simDrugNWScrollPane.getViewport().add(imgLabel);
//        this.simDrugNWScrollPane.revalidate();
//        this.simDrugNWScrollPane.repaint();
//        JLabel tmpLabel = new JLabel(scaleImage(img,scale));
//        this.simDrugNWScrollPane.getViewport().removeAll();
//        this.simDrugNWScrollPane.getViewport().add(tmpLabel);
//        this.add(tmpLabel);
//        this.pack();
//        tmpLabel.repaint();
//        tmpLabel.revalidate();
//        System.out.println("The size of imgLabel is: " + imgLabel.getSize()); 
//        System.out.println("The size of simDrugNWScrollPane ViewPort is: " + simDrugNWScrollPane.getViewport().getSize());
//        System.out.println("JLabel Size: " + this.imgLabel.getSize()
//                +"\t" + "JLabel Preferred Size: " + this.imgLabel.getPreferredSize() 
//                +"\t" + "Icon Size: " + imgLabel.getIcon().getIconHeight()+ ": " +  imgLabel.getIcon().getIconWidth()
//                +"\t" + "Correct Size: " + img.getHeight()*scaleNow + ": " + img.getWidth()*scaleNow);
    }//GEN-LAST:event_scaleSliderStateChanged
    
    private class DrawGraphFileRunnable implements Runnable{
        private Process pDraw = null;
        
        public void shutdown(){
            SharedData.bSuccDrawGraph = false;
            if(pDraw != null){
                pDraw.destroy();
                System.out.println("--------draw graph 7");
            }
            
        }
        
        public synchronized void run(){
            try{
               SharedData.bSuccDrawGraph = true;
               pDraw = Runtime.getRuntime().exec(SharedData.graphEngine 
                    + " -Tpng tmp.dot -o tmp.png"); 
               System.out.println("--------draw graph 4");
               
               pDraw.waitFor();
               System.out.println("--------draw graph 5");

            } catch(InterruptedException | IOException e){
                
            }
            pDraw = null;
            plotSimDrugNWButton.setText("Plot Network");
            System.out.println("--------draw graph 6");

            loadSimDrugNWButton1ActionPerformed(new ActionEvent(
              loadSimDrugNWButton1,ActionEvent.ACTION_PERFORMED,""));  
     
        }        
    };
    
    private DrawGraphFileRunnable raDrawGraphFile = null; //new DrawGraphFileRunnable();
    private Thread thDrawGraphFile = null; //new Thread(raDrawGraphFile);
    
    private void StartDrawGraphFile(){
        System.out.println("--------draw graph 1");
        if(thDrawGraphFile != null && thDrawGraphFile.isAlive()){
            System.out.println("--------draw graph 2");
            try{
                plotSimDrugNWButton.setEnabled(false);
                raDrawGraphFile.shutdown();                
                thDrawGraphFile.join();
                thDrawGraphFile = null;
                raDrawGraphFile = null;
                plotSimDrugNWButton.setText("Plot Network");
                plotSimDrugNWButton.setEnabled(true);
                loadSimDrugNWButton1ActionPerformed(new ActionEvent(
                    loadSimDrugNWButton1,ActionEvent.ACTION_PERFORMED,"")); 
            }catch(InterruptedException e){               
            }          
        }
        else{
            raDrawGraphFile = new DrawGraphFileRunnable();
            thDrawGraphFile = new Thread(raDrawGraphFile);

            System.out.println("--------draw graph 3");
            SharedData.bSuccDrawGraph = false;
            thDrawGraphFile.start();
            plotSimDrugNWButton.setText("Stop");            
        }
    }
            
    private void plotSimDrugNWButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_plotSimDrugNWButtonActionPerformed
        StartDrawGraphFile();
/*        Timer t = null;
        try {
            t = new Timer();
            
            SharedData.bSuccDrawGraph = true;
            final Process p = Runtime.getRuntime().exec(SharedData.graphEngine 
                    + " -Tpng tmp.dot -o tmp.png");
            t.schedule(new TimerTask(){
                @Override
                public void run(){
                    p.destroy();
                    SharedData.bSuccDrawGraph = false;
                    System.out.println("Graph generation interrupted: " 
                            + SharedData.graphEngine + " -Tpng tmp.dot -o tmp.png");
                }
            }, 10000);
            p.waitFor();
        } catch (InterruptedException|IOException e){
            
        }        
        loadSimDrugNWButton1ActionPerformed(new ActionEvent(
                loadSimDrugNWButton1,ActionEvent.ACTION_PERFORMED,""));
*/
    }//GEN-LAST:event_plotSimDrugNWButtonActionPerformed

    private void selectGraphEngineItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_selectGraphEngineItemStateChanged
        SharedData.graphEngine = selectGraphEngine.getSelectedItem().toString();
    }//GEN-LAST:event_selectGraphEngineItemStateChanged

    private void selectGraphEngineActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectGraphEngineActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_selectGraphEngineActionPerformed

    private void loadSimDrugNWButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loadSimDrugNWButton1ActionPerformed
        System.out.println("(new File(\"tmp.png\")).canRead(): " + (new File("tmp.png")).canRead());
        try {
            String strGraphFileName = "tmp.png";
            if(!SharedData.bSuccDrawGraph){
                strGraphFileName = "/icons/FailedPlot.png";
                img = ImageIO.read(getClass().getResourceAsStream(strGraphFileName));
            }
            else{
               img = ImageIO.read(new File(strGraphFileName));
            }
            
        } catch (IOException e) {
        }
        int iH = img.getHeight();
        int iW = img.getWidth();
        int lH = simDrugNWScrollPane.getViewport().getSize().height;
        int lW = simDrugNWScrollPane.getViewport().getSize().width;
        scale = Math.min(1.0*lH/iH,1.0*lW/iW);        
        imgLabel.setIcon(scaleImage(img,scale));
        simDrugNWScrollPane.setViewportView(imgLabel);  
//        ImageIcon img = new ImageIcon("data/TestMap2.png");
        imgScaleTextField.setText(Double.toString(scale));
        scaleSlider.setMinimum((int)(100*scale));
        scaleSlider.setMaximum(100);
        scaleSlider.setMinorTickSpacing(1);
        scaleSlider.setMajorTickSpacing(10);
        scaleSlider.setValue((int)(100.0*scale));
        System.out.println("Scale: " + scale + "; iH: " + iH 
                + ", iW: " + iW + "; lH: " + lH + ", lW: " + lW);   
        
//        imagePanel.setImage(img);
//        imagePanel.setScale(scale);
//        simDrugNWScrollPane.setViewportView(imagePanel);
        
//        g2d = (Graphics2D)imagePanel.getGraphics();
//        g2d.drawImage(img, 0,0,null);
//        imagePanel.paint(g2d);
//        imagePanel.revalidate();
//        imagePanel.repaint();
        

//        imgLabel.revalidate();
    }//GEN-LAST:event_loadSimDrugNWButton1ActionPerformed

    private void simDrugNWScrollPaneMouseWheelMoved(java.awt.event.MouseWheelEvent evt) {//GEN-FIRST:event_simDrugNWScrollPaneMouseWheelMoved
        int newScale = -evt.getWheelRotation()*this.mouseWheelZoomSpeed + scaleSlider.getValue();
        int setScale = newScale;
        if (newScale > scaleSlider.getMaximum()) {
            setScale = scaleSlider.getMaximum();
        } else if (newScale < scaleSlider.getMinimum()){
            setScale = scaleSlider.getMinimum();
        }
        scaleSlider.setValue(setScale);
    }//GEN-LAST:event_simDrugNWScrollPaneMouseWheelMoved

    private void simDrugNWScrollPaneMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_simDrugNWScrollPaneMouseDragged
        if (SwingUtilities.isRightMouseButton(evt)){
            simDrugNWScrollPane.getViewport().setViewPosition(new Point(
                    simDrugNWScrollPane.getViewport().getX()+evt.getX()-this.tmpMouseDragPosition.x,
                    simDrugNWScrollPane.getViewport().getY()+evt.getY()-this.tmpMouseDragPosition.y));
//            imgLabel.revalidate();
//            imgLabel.repaint();
            simDrugNWScrollPane.scrollRectToVisible(new Rectangle(
                    simDrugNWScrollPane.getViewport().getX()+evt.getX()-this.tmpMouseDragPosition.x,
                    simDrugNWScrollPane.getViewport().getY()+evt.getY()-this.tmpMouseDragPosition.y,
                    simDrugNWScrollPane.getViewport().getWidth(),
                    simDrugNWScrollPane.getViewport().getHeight()));
            this.tmpMouseDragPosition = evt.getPoint();
        }
    }//GEN-LAST:event_simDrugNWScrollPaneMouseDragged

    private void wheelZoomSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_wheelZoomSpinnerStateChanged
        System.out.println("wheelZoomSpinner.getValue(): "+wheelZoomSpinner.getValue());
        System.out.println("Integer.getInteger(wheelZoomSpinner.getValue().toString()): "+Integer.getInteger(wheelZoomSpinner.getValue().toString()));
        this.mouseWheelZoomSpeed = (int)wheelZoomSpinner.getValue();
    }//GEN-LAST:event_wheelZoomSpinnerStateChanged

    private void simDrugNWScrollPaneMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_simDrugNWScrollPaneMousePressed
        if (SwingUtilities.isRightMouseButton(evt)) {
            this.tmpMouseRightButtonDown = true;
            this.tmpMouseDragPosition = evt.getPoint();
        }
    }//GEN-LAST:event_simDrugNWScrollPaneMousePressed

    private void simDrugNWScrollPaneMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_simDrugNWScrollPaneMouseReleased
        if (SwingUtilities.isRightMouseButton(evt)) {
            this.tmpMouseRightButtonDown = false;
            this.tmpMouseDragPosition = new Point(-1,-1);
        }
    }//GEN-LAST:event_simDrugNWScrollPaneMouseReleased

    private void searchDrugIDTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchDrugIDTextFieldActionPerformed
        searchDrugButtonActionPerformed(new ActionEvent(
                this.searchDrugButton,ActionEvent.ACTION_PERFORMED,""));
    }//GEN-LAST:event_searchDrugIDTextFieldActionPerformed

    private void searchDrugDescTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchDrugDescTextFieldActionPerformed
        searchDrugButtonActionPerformed(new ActionEvent(
                this.searchDrugButton,ActionEvent.ACTION_PERFORMED,""));
    }//GEN-LAST:event_searchDrugDescTextFieldActionPerformed

    private void drugListMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_drugListMouseClicked
        if (evt.getClickCount() == 2){
            if (-1 != drugList.getSelectedIndex()){
                drugListValueChanged(new ListSelectionEvent(
                        this.drugList,drugList.getFirstVisibleIndex(),
                        drugList.getLastVisibleIndex(),
                        drugList.getValueIsAdjusting()) );
                OkButtonActionPerformed(new ActionEvent(
                        this.OkButton,ActionEvent.ACTION_PERFORMED,""));
            }
        }
    }//GEN-LAST:event_drugListMouseClicked

    private void resultSimDrugTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_resultSimDrugTableMouseClicked
        if (evt.getClickCount() == 2 & SwingUtilities.isRightMouseButton(evt)){
            if (-1 != drugList.getSelectedIndex()){
                resultSimDrugTableValueChanged(new ListSelectionEvent(
                        this.resultSimDrugTable,0,resultSimDrugTable.getModel().getRowCount(),false) );
                OkButtonActionPerformed(new ActionEvent(this.OkButton,ActionEvent.ACTION_PERFORMED,""));
            }
        }    
        if (SwingUtilities.isRightMouseButton(evt) & -1 != resultSimDrugTable.getSelectedRow()){
            this.simDrugPopupMenu.show(evt.getComponent(),evt.getX(),evt.getY());
        }
    }//GEN-LAST:event_resultSimDrugTableMouseClicked

    private void resultTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_resultTableMouseClicked
        if (SwingUtilities.isRightMouseButton(evt) & -1 != resultTable.getSelectedRow()){
            this.kdGenePopupMenu.show(evt.getComponent(),evt.getX(),evt.getY());
        }
    }//GEN-LAST:event_resultTableMouseClicked

    private void linkKDGene2UCSCMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_linkKDGene2UCSCMenuItem1ActionPerformed
        String geneName = "";
        switch(this.resultTableType){
            case "CP": 
                geneName = resultTable.getModel().getValueAt(this.resultTable.getSelectedRow(),6).toString().trim();
                break;
            case "KS":
                geneName = resultTable.getModel().getValueAt(this.resultTable.getSelectedRow(),5).toString().trim();
        }
        link2Website(geneName, "UCSC");
    }//GEN-LAST:event_linkKDGene2UCSCMenuItem1ActionPerformed

    private void linkKDGene2GeneCardMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_linkKDGene2GeneCardMenuItemActionPerformed
        String geneName = "";
        switch(this.resultTableType){
            case "CP": 
                geneName = resultTable.getModel().getValueAt(this.resultTable.getSelectedRow(),6).toString().trim();
                break;
            case "KS":
                geneName = resultTable.getModel().getValueAt(this.resultTable.getSelectedRow(),5).toString().trim();
        }        
        link2Website(geneName, "GeneCard");
    }//GEN-LAST:event_linkKDGene2GeneCardMenuItemActionPerformed

    private void linkKDGene2NCI_NatureMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_linkKDGene2NCI_NatureMenuItemActionPerformed
        String geneName = "";
        switch(this.resultTableType){
            case "CP": 
                geneName = resultTable.getModel().getValueAt(this.resultTable.getSelectedRow(),6).toString().trim();
                break;
            case "KS":
                geneName = resultTable.getModel().getValueAt(this.resultTable.getSelectedRow(),5).toString().trim();
        }        
        link2Website(geneName, "NCI_Nature");
    }//GEN-LAST:event_linkKDGene2NCI_NatureMenuItemActionPerformed

    private void linkKDGene2KEGGMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_linkKDGene2KEGGMenuItemActionPerformed
        String geneName = "";
        switch(this.resultTableType){
            case "CP": 
                geneName = resultTable.getModel().getValueAt(this.resultTable.getSelectedRow(),6).toString().trim();
                break;
            case "KS":
                geneName = resultTable.getModel().getValueAt(this.resultTable.getSelectedRow(),5).toString().trim();
        }        
        link2Website(geneName, "KEGG");
    }//GEN-LAST:event_linkKDGene2KEGGMenuItemActionPerformed

    private void linkKDGene2GoogleMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_linkKDGene2GoogleMenuItemActionPerformed
        String geneName = "";
        switch(this.resultTableType){
            case "CP": 
                geneName = resultTable.getModel().getValueAt(this.resultTable.getSelectedRow(),6).toString().trim();
                break;
            case "KS":
                geneName = resultTable.getModel().getValueAt(this.resultTable.getSelectedRow(),5).toString().trim();
        }        
        link2Website(geneName, "Google");
    }//GEN-LAST:event_linkKDGene2GoogleMenuItemActionPerformed

    private void resultCellResponseTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_resultCellResponseTableMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_resultCellResponseTableMouseClicked

    private void exitCellResponseButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitCellResponseButton1ActionPerformed
        cellResponseDialog.setVisible(false);
    }//GEN-LAST:event_exitCellResponseButton1ActionPerformed

    private void queryCellResponseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_queryCellResponseActionPerformed
//        BOOKMARK;
        List<String> selectedCells = new ArrayList<>();
        if (selectCellForCellResponse.getSelectedItem().equals("All") 
                & selectCellForCellResponse.getItemCount() > 1){
            for (int i = 1; i < selectCellForCellResponse.getItemCount(); i++) {
                    selectedCells.add(selectCellForCellResponse.getItemAt(i).toString());
                }
        } else {
            selectedCells.add(selectCellForCellResponse.getSelectedItem().toString());
        } 
        List<String> selectedCellResponses = new ArrayList<>();
        if (selectCellResponseForCellResponse.getSelectedItem().equals("All") 
                & selectCellResponseForCellResponse.getItemCount() > 1){
            for (int i = 1; i < selectCellResponseForCellResponse.getItemCount(); i++) {
                    selectedCellResponses.add(selectCellResponseForCellResponse.getItemAt(i).toString());
                }
        } else {
            selectedCellResponses.add(selectCellResponseForCellResponse.getSelectedItem().toString());
        }         
        qPopulateCellResponseTableModel(selectedCells,selectedCellResponses);
    }//GEN-LAST:event_queryCellResponseActionPerformed

    private void selectSimDrugForCellResponseFuzzinessLevelItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_selectSimDrugForCellResponseFuzzinessLevelItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_selectSimDrugForCellResponseFuzzinessLevelItemStateChanged

    private void selectSimDrugForCellResponseFuzzinessLevelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectSimDrugForCellResponseFuzzinessLevelActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_selectSimDrugForCellResponseFuzzinessLevelActionPerformed

    private void selectCellForCellResponseItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_selectCellForCellResponseItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_selectCellForCellResponseItemStateChanged

    private void selectCellForCellResponseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectCellForCellResponseActionPerformed
        if (selectCellForCellResponse.getSelectedIndex() > -1){
            populateSelectCellResponseForCellResponse(selectCellForCellResponse.getSelectedItem().toString());
        }
    }//GEN-LAST:event_selectCellForCellResponseActionPerformed

    private void selectCellResponseForCellResponseItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_selectCellResponseForCellResponseItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_selectCellResponseForCellResponseItemStateChanged

    private void selectCellResponseForCellResponseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectCellResponseForCellResponseActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_selectCellResponseForCellResponseActionPerformed

    private void tutorialOKButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tutorialOKButtonActionPerformed
        this.tutorialDialog.setVisible(false);
    }//GEN-LAST:event_tutorialOKButtonActionPerformed

    private void tutorialMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tutorialMenuItemActionPerformed
        this.tutorialDialog.setVisible(true);
    }//GEN-LAST:event_tutorialMenuItemActionPerformed

    private void drug2LIFEButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_drug2LIFEButtonActionPerformed
//        http://baoquery.ccs.miami.edu/life/hms/summary?input=HMSL10008&mode=compound
       // String sURI = "http://baoquery.ccs.miami.edu/life/hms/summary?input=" 
       //         + SharedData.drugID.trim() +"&mode=compound";
      
       String sURI = dwDAO.getLIFEUrlby(SharedData.drugID.trim());
       
       //String sURI = "http://life.ccs.miami.edu/life/summary?mode=SmallMolecule&input=" + sLINCSID + "&source=LINCS";
        
        System.out.println("sURI: " + sURI);
        System.out.println("SharedData.drugDesc: `" + SharedData.drugDesc + "`");
        try {
            URI myURL = new URI(sURI);
            System.out.println("myURL: " + myURL);
            java.awt.Desktop.getDesktop().browse(myURL);
        } catch (URISyntaxException | IOException e){            
        }  
    }//GEN-LAST:event_drug2LIFEButtonActionPerformed

    private void linkSimDrug2LIFEMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_linkSimDrug2LIFEMenuItemActionPerformed
//        http://baoquery.ccs.miami.edu/life/hms/summary?input=HMSL10008&mode=compound
//        BOOKMARK;
//        extraText = "CP_ID: " + resultSimDrugTable.getModel().getValueAt(modelRow,1).toString() + "\n"
//        + "CD_Desc: " + resultSimDrugTable.getModel().getValueAt(modelRow,2).toString(); 
        int selectedRow = this.resultSimDrugTable.getSelectedRow();
        int modelRow = resultSimDrugTable.convertRowIndexToModel(selectedRow);
        String sURI = "http://baoquery.ccs.miami.edu/life/hms/summary?input=" 
                + resultSimDrugTable.getModel().getValueAt(modelRow,1).toString() +"&mode=compound";
        System.out.println("sURI: " + sURI);
        System.out.println("SharedData.drugDesc: `" + SharedData.drugDesc + "`");
        try {
            URI myURL = new URI(sURI);
            System.out.println("myURL: " + myURL);
            java.awt.Desktop.getDesktop().browse(myURL);
        } catch (URISyntaxException | IOException e){            
        }  
    }//GEN-LAST:event_linkSimDrug2LIFEMenuItemActionPerformed

    private void linkSimDrug2DrugBankMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_linkSimDrug2DrugBankMenuItemActionPerformed
//        http://baoquery.ccs.miami.edu/life/hms/summary?input=HMSL10008&mode=compound
//        BOOKMARK;
//        extraText = "CP_ID: " + resultSimDrugTable.getModel().getValueAt(modelRow,1).toString() + "\n"
//        + "CD_Desc: " + resultSimDrugTable.getModel().getValueAt(modelRow,2).toString(); 
        int selectedRow = this.resultSimDrugTable.getSelectedRow();
        int modelRow = resultSimDrugTable.convertRowIndexToModel(selectedRow);        
        String sURI = "http://www.drugbank.ca/search?utf8=%E2%9C%93&query=" 
                + resultSimDrugTable.getModel().getValueAt(modelRow,2).toString() +"&commit=Search";
        System.out.println("sURI: " + sURI);
        System.out.println("SharedData.drugDesc: `" + SharedData.drugDesc + "`");
        try {
            URI myURL = new URI(sURI);
            System.out.println("myURL: " + myURL);
            java.awt.Desktop.getDesktop().browse(myURL);
        } catch (URISyntaxException | IOException e){            
        }
    }//GEN-LAST:event_linkSimDrug2DrugBankMenuItemActionPerformed

    private void tutorialDialogMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tutorialDialogMouseClicked
        this.tutorialDialog.setVisible(false);
    }//GEN-LAST:event_tutorialDialogMouseClicked

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton CancelButton;
    private javax.swing.JButton OkButton;
    private javax.swing.JButton OkButton1;
    private javax.swing.JDialog aboutDialog;
    private javax.swing.JLabel aboutLogoLabel;
    private javax.swing.JMenuItem aboutMenuItem;
    private javax.swing.JButton cellResponseButton;
    private javax.swing.JDialog cellResponseDialog;
    private javax.swing.JMenu connMenu;
    private javax.swing.JMenuItem connMenuItem;
    private javax.swing.JButton deselectButton;
    private javax.swing.JMenuItem disconnMenuItem;
    private javax.swing.JButton drug2LIFEButton;
    private javax.swing.JTextArea drugDescTextArea;
    private javax.swing.JTextArea drugDescTextArea1;
    private javax.swing.JDialog drugInputDialog;
    private javax.swing.JList drugList;
    private javax.swing.JScrollPane drugScrollPane;
    private javax.swing.JButton exitButton;
    private javax.swing.JButton exitCellResponseButton1;
    private javax.swing.JMenuItem exitMenuItem;
    private javax.swing.JButton exitSimDrugButton;
    private javax.swing.JButton exitSimDrugNWButton;
    private javax.swing.JButton exportButton;
    private javax.swing.JMenuItem exportMenuItem;
    private javax.swing.JMenu fileMenu;
    private javax.swing.JPanel fuzzyPanel;
    private javax.swing.JMenuItem generateReportMenuItem;
    private javax.swing.JMenuItem helpContentMenuItem;
    private javax.swing.JMenu helpMenu;
    private javax.swing.JLabel imgLabel;
    private javax.swing.JTextField imgScaleTextField;
    private javax.swing.JLabel inputLabel;
    private javax.swing.JPanel inputPanel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane10;
    private javax.swing.JScrollPane jScrollPane11;
    private javax.swing.JScrollPane jScrollPane12;
    private javax.swing.JScrollPane jScrollPane14;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JScrollPane jScrollPane9;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextArea jTextArea2;
    private javax.swing.JPopupMenu kdGenePopupMenu;
    private javax.swing.JMenuItem linkKDGene2GeneCardMenuItem;
    private javax.swing.JMenuItem linkKDGene2GoogleMenuItem;
    private javax.swing.JMenuItem linkKDGene2KEGGMenuItem;
    private javax.swing.JMenuItem linkKDGene2NCI_NatureMenuItem;
    private javax.swing.JMenuItem linkKDGene2UCSCMenuItem1;
    private javax.swing.JMenuItem linkSimDrug2DrugBankMenuItem;
    private javax.swing.JMenuItem linkSimDrug2LIFEMenuItem;
    private javax.swing.JButton loadSimDrugNWButton1;
    private javax.swing.JCheckBox matchAnyDrugID_DescCheckBox;
    private javax.swing.JCheckBox matchBothDrugID_DescCheckBox;
    private javax.swing.JCheckBox matchDrugDescCheckBox;
    private javax.swing.JCheckBox matchDrugIDCheckBox;
    private javax.swing.JCheckBox matchExactDrugCheckBox;
    private javax.swing.JButton moreButton;
    private javax.swing.JButton plotSimDrugNWButton;
    private javax.swing.JButton queryButton;
    private javax.swing.JButton queryCellResponse;
    private javax.swing.JButton querySimDrugButton;
    private javax.swing.JButton reportButton;
    private javax.swing.JButton reselectButton;
    private javax.swing.JTable resultCellResponseTable;
    private javax.swing.JTable resultSimDrugTable;
    private javax.swing.JTable resultTable;
    private javax.swing.JPanel resultsPanel;
    private javax.swing.JPanel resultsPanel1;
    private javax.swing.JPanel resultsPanel2;
    private javax.swing.JPanel resultsPanel3;
    private javax.swing.JSlider scaleSlider;
    private javax.swing.JLabel schemaLabel;
    private javax.swing.JButton searchDrugButton;
    private javax.swing.JTextField searchDrugDescTextField;
    private javax.swing.JTextField searchDrugIDTextField;
    private javax.swing.JComboBox selectCellForCellResponse;
    private javax.swing.JComboBox selectCellResponseForCellResponse;
    private javax.swing.JComboBox selectFuzzinessLevel;
    private javax.swing.JComboBox selectFuzzinessType;
    private javax.swing.JComboBox selectGraphEngine;
    private javax.swing.JComboBox selectInputType;
    private javax.swing.JComboBox selectSimDrugForCellResponseFuzzinessLevel;
    private javax.swing.JComboBox selectSimDrugFuzzinessLevel;
    private javax.swing.JTextArea selectedDrugDescForCellResponseTextArea;
    private javax.swing.JTextArea selectedDrugDescTextArea;
    private javax.swing.JTextArea selectedDrugDescTextArea1;
    private javax.swing.JTextField selectedDrugIDForCellResponseTextField;
    private javax.swing.JTextField selectedDrugIDTextField;
    private javax.swing.JTextField selectedDrugIDTextField1;
    private javax.swing.JButton showAllDrugButton;
    private javax.swing.JButton showSimDrugNWButton;
    private javax.swing.JButton simDrugButton;
    private javax.swing.JTextArea simDrugDescTextArea;
    private javax.swing.JDialog simDrugDialog;
    private javax.swing.JTextArea simDrugNWDescTextArea1;
    private javax.swing.JTextArea simDrugNWDescTextArea2;
    private javax.swing.JDialog simDrugNWDialog;
    private javax.swing.JTextField simDrugNWIDTextField1;
    private javax.swing.JTextField simDrugNWIDTextField2;
    private javax.swing.JScrollPane simDrugNWScrollPane;
    private javax.swing.JTextField simDrugNWSimilarityTextField;
    private javax.swing.JPopupMenu simDrugPopupMenu;
    private javax.swing.JMenuBar startMenuBar;
    private javax.swing.JLabel statusLabel;
    private javax.swing.JPanel statusPanel;
    private javax.swing.JButton testButton;
    private javax.swing.JDialog tutorialDialog;
    private javax.swing.JMenuItem tutorialMenuItem;
    private javax.swing.JButton tutorialOKButton;
    private javax.swing.JButton uploadButton;
    private javax.swing.JMenuItem uploadMenuItem;
    private javax.swing.JSpinner wheelZoomSpinner;
    // End of variables declaration//GEN-END:variables
}
