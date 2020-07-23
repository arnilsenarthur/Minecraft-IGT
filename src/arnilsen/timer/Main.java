package arnilsen.timer;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import arnilsen.timer.Data.DataProvider;
import arnilsen.timer.Data.Stat;
import arnilsen.timer.gui.ScreenRecognitionConfidence;
import arnilsen.timer.gui.SlapshScreen;
import arnilsen.timer.gui.Window;
import arnilsen.timer.screen.ScreenRecognition;

/**
 * 
 * Árnilsen's Minecraft IGTimer
 * 
 * @author Árnilsen Arthur/Infobros2000
 *
 */
public class Main {

	///Current instances of core classes
	public static Data data;
	public static Window window;
	public static String last_world = null;
	public static Image player_head;
	public static Layout layout = new Layout();
	
	/**
	 * Main entry function: Starts whole program
	 * @param args
	 */
	public static void main(String[] args) 
	{		
		//Set L&F to current OS look and feel
		try 
		{
	        UIManager.setLookAndFeel(
	            UIManager.getSystemLookAndFeelClassName());
	    } 
	    catch (Exception e) 
		{
	    }
    
		//Opens the splash screen
	 	SlapshScreen frame = new SlapshScreen();
		frame.setVisible(true);
			
		//Try to read main files	
		String f = null;
		try 
		{
			f = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getPath();
		} 
		catch (URISyntaxException e2) 
		{
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
	
		
		f = new File(f).getParent();
		
		if(!new File(f + "\\layout.json").exists())
			try 
			{
				ExportResource("/res/deflt/layout.txt",f + "\\layout.json");
			} catch (Exception e2)
			{
				e2.printStackTrace();
			}
		
		frame.progressBar_1.setValue(25);
		
		if(!new File(f + "\\data.json").exists())
			try 
			{
				ExportResource("/res/deflt/data.txt",f + "\\data.json");
			} 
			catch (Exception e2) 
			{
				e2.printStackTrace();
			}
		
		frame.progressBar_1.setValue(50);
		
		if(!new File(f + "\\stats.json").exists())
			try 
			{
				ExportResource("/res/deflt/stats.txt",f + "\\stats.json");
			} 
			catch (Exception e2) 
			{
				e2.printStackTrace();
			}
		
		frame.progressBar_1.setValue(75);
		List<Stat> st = new ArrayList<>();
		try 
		{					
			//Load layout and stats file	
			layout = Layout.getLayoutFromFile(new File(f + "\\layout.json"));
			
			//Load data structure
			Data.loadDataStructure(new File(f + "\\data.json"));
			st = Data.getStatsFromFile(new File(f + "\\stats.json"));			
		}
		catch(Exception e)
		{
			JOptionPane.showMessageDialog(null, e.getMessage());
			e.printStackTrace();
		}
		
		//Create data holder
		data = new Data();
		
		//Create main timer window
		window = new Window(st,layout,data);
		window.frame.setAlwaysOnTop(true);
		window.applyLayout(layout);
		window.labelTimer.setForeground(layout.offgame_timer_color);
		window.labelTimer.setText("<html>0:00<font size='8'>" + (layout.decimal_places == 0 ? "" : "." + new String(new char[layout.decimal_places]).replace("\0", "0")) + "</font></html>");
		window.labelLastRealTimer.setText(layout.last_ingame_time_label + ": 0:00" + (layout.decimal_places == 0 ? "" : "." + new String(new char[layout.decimal_places]).replace("\0", "0")));

		frame.progressBar_1.setValue(90);
		
		//ScreenRecognitionConfidence src = new ScreenRecognitionConfidence();
		//src.setVisible(true);
		
		
		
		//Try to retrieve Steve head
		try 
		{
			BufferedImage im = getImageFromUUID("c06f89064c8a49119c29ea1dbd1aab82",layout.skull_3d);
			im = makeRoundedCorner(im, 6);
			window.playerHead.setIcon(data.player_icon = new ImageIcon(im));
		} 
		catch (IOException e1) 
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		frame.progressBar_1.setValue(100);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.dispose();
		window.frame.setVisible(true);
		
		//Main timer
		{
			Timer timer = new Timer();

		    TimerTask task = new TimerTask()
		    {
		       
		        public void run()
		        {
		        	try 
		        	{
		        		//Check current last world
		        		File last = getLatestWorld();
		        		
		        		if(last_world != null && last != null)
		        		{
		        			if(!last_world.equals(last.getPath()))
		        			{
		        				System.out.println("new w");
		        				data = new Data();
		        			}
		        		}
		        		
		        		//Update stats from world
		        		updateStats(last);
		        		last_world = last.getPath();
		        		
		        		//Update stats labels
		        		for(Stat s : window.stats)
		        			s.updateLabel(layout, data);
		        		
		        		//Screen recognition
		        		
		        				        		
		        	}
		        	catch(Exception e)
		        	{
		        		
		        	}
		        	
		        }
		    };
		    timer.scheduleAtFixedRate(task, 0, 1000);
		}
	    
		//Timer timer
		{
			Timer timer = new Timer();

		    TimerTask task = new TimerTask()
		    {
		    	int i = 0;
		        public void run()
		        {
		        	
		        	i ++;
		        	if(i >= 4)
		        	{
		        		ScreenRecognition.doRecognition(data, layout);
		        		i = 0;
		        	}
		        	
		        	//Update label color - For future GUILayoutManager
		        	window.labelLastRealTimer.setForeground(layout.last_ingame_timer_color);

		        	if(!data.ingame && !data.ender_dragon_killed)
		        	{
		        		//not in game
		        		window.labelTimer.setForeground(layout.offgame_timer_color);
		        		window.labelTimer.setText("<html>0:00<font size='8'>" + (layout.decimal_places == 0 ? "" : "." + new String(new char[layout.decimal_places]).replace("\0", "0")) + "</font></html>");
		        		window.labelLastRealTimer.setText(layout.last_ingame_time_label + ": 0:00" + (layout.decimal_places == 0 ? "" : "." + new String(new char[layout.decimal_places]).replace("\0", "0")));
		        	}
		        	else
		        	{
			        	
		        		int duv = 1;
		        		if(layout.decimal_places == 1)
		        			duv = 100;
		        		else if(layout.decimal_places == 2)
		        			duv = 10;
		        		
		        		//Show: Real Game Timer Last Update
		        		{
		        			float f = data.ingame_time/1000f;
		        			
		        			int seconds = (int) Math.floor(f);
				        	int minutes = (int) (seconds/60f);
				        	seconds = seconds%60;
				        	
				        	int hours = (int) minutes/60;
				        	minutes = minutes%60;
				        	
				        	int mil = (int) (data.ingame_time%1000)/duv;//choose decimal places (/100 = 1 /10 = 2 /1 = 3)
				        	String millisecs = mil < 10 ? mil + "00" : (mil < 100? mil + "0" : mil + "");
		        			
				        	window.labelTimer.setForeground(data.ender_dragon_killed ? layout.complete_timer_color :  layout.ingame_timer_color);
				        	
		        			window.labelLastRealTimer.setText(layout.last_ingame_time_label + ": " + (hours > 0 ? (hours + ":" + (minutes < 10 ? "0" : "")) : 0) + minutes + ":" + (seconds < 10 ? "0" + seconds : seconds) + (layout.decimal_places == 0 ? "" : "." + millisecs));
		        		}
			        	
			        	//Show: Estimated time
		        		{
		        			float f = data.estimateTimeForNow()/1000f;
				        	
				        	int seconds = (int) Math.floor(f);
				        	int minutes = (int) (seconds/60f);
				        	seconds = seconds%60;
				        	
				        	int hours = (int) minutes/60;
				        	minutes = minutes%60;
				        	
				        	int mil = (int) (data.estimateTimeForNow()%1000)/duv;//choose decimal places (/100 = 1 /10 = 2 /1 = 3)
				        	String millisecs = mil < 10 ? mil + "00" : (mil < 100? mil + "0" : mil + "");
				        	
				        	String tcontent = "<html>" + minutes + ":" + (seconds < 10 ? "0" + seconds : seconds) + "<font size='8'>." + millisecs + "</font></html>";
				        	
				        	if(hours > 0)
				        	{
				        		tcontent = "<html>" + hours + ":" + (minutes < 10 ? "0" + minutes : minutes)  + ":" + (seconds < 10 ? "0" + seconds : seconds) + "<font size='8'>" + (layout.decimal_places == 0 ? "" : "." + millisecs) + "</font></html>";
					        	
				        	}
				        	
				        	window.labelTimer.setText(tcontent);
		        		}
			        	
			        	if(data.ender_dragon_killed)
			        	{
			        		
			        	}
		        	}
		        	
		        }
		    };
		    timer.scheduleAtFixedRate(task, 0, 50);
		}
		
		
	}
	
	//https://stackoverflow.com/questions/7603400/how-to-make-a-rounded-corner-image-in-java/7603815
	/**
	 * Round an image
	 * @param image
	 * @param cornerRadius
	 * @return
	 */
	public static BufferedImage makeRoundedCorner(BufferedImage image, int cornerRadius) 
	{
	    int w = image.getWidth();
	    int h = image.getHeight();
	    BufferedImage output = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);

	    Graphics2D g2 = output.createGraphics();

	    g2.setComposite(AlphaComposite.Src);
	    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	    g2.setColor(Color.WHITE);
	    g2.fill(new RoundRectangle2D.Float(0, 0, w, h, cornerRadius, cornerRadius));

	    g2.setComposite(AlphaComposite.SrcAtop);
	    g2.drawImage(image, 0, 0, null);

	    g2.dispose();

	    return output;
	}
	
	/*
	 *  Web Api's
	 */
	/**
	 * Get username from player uuid
	 * @param uuid
	 * @return
	 */
	public static String getUsername(String uuid)
	{
		
		try (InputStream in = new URL("https://api.mojang.com/user/profiles/" + uuid + "/names").openStream()) 
		{
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));

			JSONArray o = new JSONArray(reader.lines().collect(Collectors.joining(System.lineSeparator())));
			
			return o.getJSONObject(o.length() - 1).getString("name");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return null;
	}
	
	/**
	 * Get head image from player uuid (2d or 3d)
	 * @param uuid
	 * @param three_dimensional
	 * @return
	 * @throws IOException
	 */
	public static BufferedImage getImageFromUUID(String uuid,boolean three_dimensional) throws IOException
	{
		//
		URL url = new URL(three_dimensional ? "https://crafatar.com/renders/head/" + uuid + "?scale=1&default=MHF_Steve&overlay" : "https://crafatar.com/avatars/" + uuid + "?size=24&default=MHF_Steve&overlay");  
        return ImageIO.read(url);
	}
	
	/*
	 * End web Api's
	 */
	
	/**
	 * Update stats and advancements from a world
	 * @param world
	 */
	public static void updateStats(File world)
	{
		//Load stats from world folder
		File sfolder = new File(String.valueOf(world.getAbsolutePath()) + "\\stats");
		for(File f : sfolder.listFiles())
		{
			String uuid = f.getName().split("\\.")[0].replaceAll("-", "");

			//Update player data
			if(data.player_uuid == null || !data.player_uuid.equals(uuid))
			{
				data.player_uuid = uuid;
				data.player_nick = getUsername(uuid);
				
				//Get player head
				try 
				{
					BufferedImage im = getImageFromUUID(uuid,layout.skull_3d);
					im = makeRoundedCorner(im, 6);
					window.playerHead.setIcon(data.player_icon = new ImageIcon(im));
					window.playerHead.setText(data.player_nick);
				} 
				catch (IOException e1) 
				{
					e1.printStackTrace();
				}
			}
			
				
			try 
			{
				//Parse stats to JSON
				String s = Files.readAllLines(f.toPath()).stream().collect(Collectors.joining("\n"));
				JSONObject obj = new JSONObject(s).getJSONObject("stats");
				
				//Update stats provider
				for(DataProvider provider : Data.STATS_PROVIDER.values())
				{
					provider.value = getValue(obj,provider.path);
				}
						
				//Update timer and ingame data
				data.ingame_time = getValue(obj,"minecraft:custom","minecraft:play_one_minute") * 50;
				if(data.last_stats_file_update != f.lastModified())
				{
					data.discount_time = 0;
					
					if(data.ender_dragon_killed)
						data.ingame = false;
					
					else if(data.exit_count != getValue(obj, "minecraft:custom","minecraft:leave_game"))
						data.ingame = false;
					else	
						data.ingame = true;
				}
				
				//Update exit count
				data.exit_count = getValue(obj, "minecraft:custom","minecraft:leave_game");
				//Update time of last stats file update
				data.last_stats_file_update = f.lastModified();
				//Update ender dragon status
				int ender_dragons_killed = getValue(obj,"minecraft:killed","minecraft:ender_dragon");
				if(data.killed_ender_dragons != ender_dragons_killed && ender_dragons_killed != 0)
				{
					data.ender_dragon_killed = true;
					data.ingame = false;
				}				
			} 
			catch (Exception e)
			{
				e.printStackTrace();
			}
			
		}
		
		//Load advancements from world folder
		sfolder = new File(String.valueOf(world.getAbsolutePath()) + "\\advancements");
		for(File f : sfolder.listFiles())
		{
			
			try {
				//Parse advancements to JSON
				String s = Files.readAllLines(f.toPath()).stream().collect(Collectors.joining("\n"));				
				JSONObject obj = new JSONObject(s);
				
				//Update advancements provider
				for(DataProvider provider : Data.ADVANCEMENTS_PROVIDER.values())
				{
					provider.value = checkForAdvancement(obj, provider.path[0]) ? 1 : 0;
				}
				
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		
	}
	
	/**
	 * Check status of an achievment
	 * @param o
	 * @param achievment
	 * @return
	 */
	public static boolean checkForAdvancement(JSONObject o,String achievment)
	{
		//Use ScreenRecongnition to check if player reached nether or the end
		if(achievment.equalsIgnoreCase("minecraft:nether/root") && ScreenRecognition.nether)
		{
			return true;
		}		
		if(achievment.equalsIgnoreCase("minecraft:end/root") && ScreenRecognition.the_end)
		{
			return true;
		}
			
		//Get advancement data
		try {
			if(o.has(achievment) && o.get(achievment) instanceof JSONObject)
			{
				return o.getJSONObject(achievment).getBoolean("done");
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	/**
	 * Get int from JSON object
	 * @param o
	 * @param st
	 * @return
	 * @throws JSONException
	 */
	public static int getValue(JSONObject o,String... st) throws JSONException
	{
		JSONObject bo = o;
		for(int i = 0; i < st.length; i ++)
		{
			if(i == st.length - 1)
			{
				if(bo.has(st[i]))
				{
					
					return bo.getInt(st[i]);
				}
				else
					return 0;
			}
			else
			{
				if(bo.has(st[i]))
					bo = bo.getJSONObject(st[i]);
				else
					return 0;
			}
		}
		return 0;
	}
	
	/**
	 * Get latest Minecraft world
	 * @return
	 */
	public static File getLatestWorld()
	{
		try 
		{
			File folder = new File(String.valueOf(getDirectory().getAbsolutePath()) + "\\saves");
			
			File[] saves = folder.listFiles(new FilenameFilter() {			
				@Override
				public boolean accept(File dir, String name) {
					return new File(dir, name).isDirectory();
				}
			});
			
			
			List<File> files = new ArrayList<File>(Arrays.asList(saves));
			
			while(files.size() > 1)
			{
				if(files.get(0).lastModified() > files.get(1).lastModified())
					files.remove(1);
				else
					files.remove(0);
			}
			
			
			
			return files.size() > 0 ? files.get(0) : null;
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return null;
	}
	
	/**
	 * Get current Minecraft directory
	 * @return
	 */
	public static File getDirectory() 
	{
		//Check if a custom directory is set
		if(Data.folder != null)
			return new File(Data.folder);
		
	    try {
	    	OperatingSystem os = getOperatingSystem();
	      if (os == OperatingSystem.WINDOWS)
	        return new File(String.valueOf(System.getProperty("user.home")) + "/AppData/Roaming/.minecraft"); 
	      if (os == OperatingSystem.MAC)
	        return new File("/Users/" + System.getProperty("user.name") + "/Library/Application Support/minecraft"); 
	      if (os == OperatingSystem.LINUX)
	        return new File("/home/" + System.getProperty("user.name") + "/.minecraft"); 
	    } catch (Exception e) {
	      e.printStackTrace();
	    } 
	    return null;
	}
	
	/**
	 * Get current OS
	 * @return
	 */
	public static OperatingSystem getOperatingSystem()
	{
		String os = System.getProperty("os.name").toLowerCase();
	    if (os.contains("win"))
	      return OperatingSystem.WINDOWS; 
	    if (os.contains("mac"))
	      return OperatingSystem.MAC; 
	    if (os.contains("nix") || os.contains("nux") || os.contains("aix"))
	      return OperatingSystem.LINUX; 
	    
	    return OperatingSystem.UNKNOW;
	}
	
	public enum OperatingSystem
	{
		WINDOWS,
		MAC,
		LINUX,
		UNKNOW,
	}
	
	/**
	 * Export file from jar
	 * @param resourceName
	 * @param output
	 * @return
	 * @throws Exception
	 */
	static public String ExportResource(String resourceName,String output) throws Exception 
	{
		InputStream stream = null;
		OutputStream resStreamOut = null;
		String jarFolder;
		try 
		{
			stream = Main.class.getResourceAsStream(resourceName);
			if(stream == null) 
			{
				throw new Exception("Cannot get resource \"" + resourceName + "\" from Jar file.");
			}
			
			int readBytes;
			byte[] buffer = new byte[4096];
			jarFolder = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath()).getParentFile().getPath().replace('\\', '/');
			resStreamOut = new FileOutputStream(output);
			while ((readBytes = stream.read(buffer)) > 0) 
			{
				resStreamOut.write(buffer, 0, readBytes);
			}
		} 
		catch (Exception ex) 
		{
			throw ex;
		} 
		finally 
		{
			stream.close();
			resStreamOut.close();
		}
		return jarFolder + resourceName;
	}
}
