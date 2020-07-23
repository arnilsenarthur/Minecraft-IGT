package arnilsen.timer.screen;

import arnilsen.timer.Layout;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.image.BufferedImage;
import java.util.Random;

import arnilsen.timer.Data;

/**
 * Main screen recognition class
 * @author Árnilsen Arthur/Infobros2000
 * 
 */
public class ScreenRecognition 
{
	
	public static boolean nether = false;
	public static boolean the_end = false;
	
	//Recognition settings
	static int points_to_check_sky = 80;
	static Rectangle sky_section;
	static Rectangle ground_section;
	static int points_to_check_ground = 80;
	static
	{
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		sky_section = new Rectangle(50,dim.height/2 - 150,dim.width - 100,250);
		ground_section = new Rectangle(50,dim.height/2 + 100,dim.width - 100,250);
	}
	
	
	static Robot r;
	static Window w;
	/**
	 * Load data from screen
	 * @param data
	 * @param layout
	 */
	public static void doRecognition(Data data,Layout layout)
	{
		//Optimization
		if(nether && the_end)
			return;
		
		Random rn = new Random();
		if(r == null)
			try {
				r = new Robot();
			} catch (AWTException e) {
				return;
			}
		
		
		
		Rectangle rectangle = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
        BufferedImage image = r.createScreenCapture(rectangle);
        
        //Check nether sky
        int matches = 0;
        for(int i = 0; i < points_to_check_sky; i ++)
        {
        	int x = sky_section.x + rn.nextInt(sky_section.width);
        	int y = sky_section.y + rn.nextInt(sky_section.height);
        	
        	int c = image.getRGB(x, y);
        	
        	 int red =   (c & 0x00ff0000) >> 16;
             int green = (c & 0x0000ff00) >> 8;
             int blue =   c & 0x000000ff;
        	
        	if(red > 30 && red < 80 && green > 0 && green < 18 && blue > 0 && blue < 18)
        		matches ++;
        }
        
        if(matches > 40)
        {
        	nether = true;
        }
        
        //Check the end ground (end stones)
        matches = 0;
        for(int i = 0; i < points_to_check_ground; i ++)
        {
        	int x = ground_section.x + rn.nextInt(ground_section.width);
        	int y = ground_section.y + rn.nextInt(ground_section.height);
        	
        	int c = image.getRGB(x, y);
        	
        	 int red =   (c & 0x00ff0000) >> 16;
             int green = (c & 0x0000ff00) >> 8;
             int blue =   c & 0x000000ff;
        	
        	if(red < 195 && green < 195 && blue < 195 && red > 85 && green > 85 && blue > 85 && green > blue && green > red)
        		matches ++;
        }
        
        if(matches > 40)
        {
        	the_end = true;
        }
        
	}
}
