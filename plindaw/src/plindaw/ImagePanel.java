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
import javax.swing.JPanel;
import java.awt.image.BufferedImage;
import java.awt.Graphics2D;
import java.awt.Graphics;
import javax.imageio.ImageIO; 
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.Dimension;
import java.io.File;
import java.io.IOException;

class ImagePanel extends JPanel  
{  
    BufferedImage image;  
    double scale = 1.0;  

    public ImagePanel()
    {  
    }
    
    public ImagePanel(String imageFileName)  
    {  
        loadImage(imageFileName);  
    }
    
    public ImagePanel(BufferedImage inputImage){
        image = inputImage;
    }
   
    @Override
    protected void paintComponent(Graphics g)  
    {  
        super.paintComponent(g);  
        Graphics2D g2 = (Graphics2D)g;  
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,  
                            RenderingHints.VALUE_INTERPOLATION_BICUBIC);  
        int w = getWidth();  
        int h = getHeight();  
        int imageWidth = 0;
        int imageHeight = 0;
        if (null != image) {
            imageWidth = image.getWidth();
            imageHeight = image.getHeight();  
            double x = (w - scale * imageWidth)/2;  
            double y = (h - scale * imageHeight)/2;  
            AffineTransform at = AffineTransform.getTranslateInstance(x,y);  
            at.scale(scale, scale);
            g2.drawRenderedImage(image, at);  
        }
    }  
   
    /** 
     * For the scroll pane. 
     */  
    @Override
    public Dimension getPreferredSize()  
    {  
        int w = 0;
        int h = 0;
        if (null != image) {
            w = (int)(scale * image.getWidth());
            h = (int)(scale * image.getHeight());  
        }          
        return new Dimension(w, h);  
    }  
   
    public void setScale(double s)  
    {  
        scale = s;  
        revalidate();      // update the scroll pane  
        repaint();  
    }  
   
    public void loadImage(String imageFileName)  
    {  
        String fileName = "images/greathornedowl.jpg";  
        try  
        {  
            image = ImageIO.read(new File(fileName));
        }    
        catch(IOException ioe)  
        {  
            System.out.println("read trouble: " + ioe.getMessage());  
        }  
    }
    public void setImage(BufferedImage inputImage){
        image = inputImage;
        revalidate();      // update the scroll pane  
        repaint();         
    }
}  