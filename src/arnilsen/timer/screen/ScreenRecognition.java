package arnilsen.timer.screen;

import arnilsen.timer.Layout;
import arnilsen.timer.Main;
import arnilsen.timer.gui.ScreenRecognitionConfidence;

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
	//Dirt colors on loading screen
	public static int[] dirt_colors = {-15331574, -15000805, -14871023, -14805745, -14540254, -14279917, -13754089};
	
	//Current status
	public static boolean nether = false;
	public static boolean the_end = false;
	public static boolean on_loading_screen = false;
	
	//Loading screen timing data
	public static long last_loading_screen_detection = 0;
	public static long last_non_loading_screen = 0;
	
	//Recognition settings
	static int points_to_check_sky = 80;
	static Rectangle sky_section;
	static Rectangle ground_section;
	static int points_to_check_ground = 80;
	static int points_to_check_loading_screen=80;
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
		if(last_loading_screen_detection == 0)
			last_loading_screen_detection = System.currentTimeMillis();
		
		Random rn = new Random();
		if(r == null)
			try {
				r = new Robot();
			} catch (AWTException e) {
				return;
			}
		
		
		
		Rectangle rectangle = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
        BufferedImage image = r.createScreenCapture(rectangle);
        
        
        int matches = 0;
        //Checks loading screen
        for(int i = 0; i < points_to_check_loading_screen; i ++)
        {
        	int x = 150 + rn.nextInt(rectangle.width - 300);
        	int y = 150 + rn.nextInt(rectangle.height - 300);
        	
        	int c = image.getRGB(x, y);
        	
	    	 
	         
        	for(int j = 0; j < dirt_colors.length; j ++)
			{
				 if(dirt_colors[j] == c)
					 matches ++;
				 else if(dirt_colors[j] > c)
					 break;
			}
        }
        
        //Debug Only
        /*
        if(ScreenRecognitionConfidence.screen != null)
        {
        	ScreenRecognitionConfidence.screen.loading_pb.setValue((int) ((matches*1f)/points_to_check_loading_screen * 100));
        	ScreenRecognitionConfidence.screen.loading_pb.setForeground(matches > 60? Color.green : Color.red);
        }
        */
        //Pause time if detects loading screen
        if(matches > 60)
        {
        	if(last_non_loading_screen == 0)
        	{
        		//pause the timer at this time
        		data.paused_time = data.estimateTimeForNow();
        		last_non_loading_screen = 1;
        	}
        }
        else
        {
        	if(last_non_loading_screen == 1 )
        	{
        		//resume the timer discouting amount
        		last_non_loading_screen = 0;
        		
        		long i = data.paused_time;
        		data.paused_time = 0;
        		data.discount_time += data.estimateTimeForNow() - i;
        		
        	}
        }
        
        //Check nether sky (or netherrack?)
        matches = 0;
        //if(!nether)
        {
        	 for(int i = 0; i < points_to_check_sky; i ++)
             {
             	int x = sky_section.x + rn.nextInt(sky_section.width);
             	int y = sky_section.y + rn.nextInt(sky_section.height);
             	
             	int c = image.getRGB(x, y);
             	
             	int red =   (c & 0x00ff0000) >> 16;
                int green = (c & 0x0000ff00) >> 8;
                int blue =   c & 0x000000ff;
             	
                //nether sky
             	if(red > 30 && red < 80 && green > 0 && green < 18 && blue > 0 && blue < 18)
             		matches ++;
             	//netherrack
             	else if(green < 20 && blue < 20 && red > 30 && red < 50)
             		matches ++;
             }
             
             if(matches > 40)
             {
             	nether = true;
             }
        }
        
        //Debug Only
        /*
        if(ScreenRecognitionConfidence.screen != null)
        {
        	ScreenRecognitionConfidence.screen.nether_pb.setValue((int) ((matches*1f)/points_to_check_sky * 100));
        	ScreenRecognitionConfidence.screen.nether_pb.setForeground(matches > 40? Color.green : Color.red);
        }
        */
        
        //Check the end ground (end stones)
        matches = 0;
        //if(!the_end)
        {
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
        
        //Debug Only
        /*
        if(ScreenRecognitionConfidence.screen != null)
        {
        	ScreenRecognitionConfidence.screen.end_pb.setValue((int) ((matches*1f)/points_to_check_ground * 100));
        	ScreenRecognitionConfidence.screen.end_pb.setForeground(matches > 40? Color.green : Color.red);
        }
        */
	}
}
