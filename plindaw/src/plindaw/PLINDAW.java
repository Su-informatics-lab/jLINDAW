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
 * @author tmhjxs20
 */

public class PLINDAW {

    /**
     * @param args the command line arguments
     */
    
    public static MainFrame mainFrame;
    public static void main(String[] args) {
        SharedData.SharedData();
        mainFrame = new MainFrame();
        mainFrame.setVisible(true);
    }
}
