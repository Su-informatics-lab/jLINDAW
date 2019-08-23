/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package plindaw;

//import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import java.util.List;
import java.util.ArrayList;
//import java.util.Collections;

/**
 *
 * @author tmhjxs20
 */
public class DrugTableModel extends DefaultTableModel { //AbstractTableModel  {
    public List<String> colNames = new ArrayList<>();
    public Object[][] data = {{}};
    
    @Override
    public String getColumnName(int colID) {
        return colNames.get(colID).toString();
    }
    
    @Override
    public int getRowCount() { return data.length; }
    
    @Override
    public int getColumnCount() { return colNames.size(); }
    
    @Override
    public Object getValueAt(int row, int col) {
        return data[row][col];
    }
    
    @Override
    public boolean isCellEditable(int row, int col)
        { return true; }
    
    @Override
    public void setValueAt(Object value, int row, int col) {
        data[row][col] = value;
        fireTableCellUpdated(row, col);
    }
    
    @Override
    public Class getColumnClass(int c) {
        return getValueAt(0, c).getClass();
    }

    public void DrugTableModel(){        
    }
}