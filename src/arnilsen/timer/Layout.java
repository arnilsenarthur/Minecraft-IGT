package arnilsen.timer;

import java.awt.Color;
import java.io.File;
import java.nio.file.Files;
import java.util.stream.Collectors;
import org.json.JSONException;
import org.json.JSONObject;
import arnilsen.timer.gui.Window.GradientDirection;
import arnilsen.timer.gui.Window.PanelBackground;

/**
 * Layout managing class
 * @author Árnilsen Arthur/Infobros2000
 * 
 */
public class Layout 
{
	//Always 3 (for now)
	public int decimal_places = 3;

	//Main
	public Color background_color = Color.gray;
	
	//User info
	public Color username_color = Color.white;
	
	//Header
	public String title = "Minecraft:Java Edition Any%";
	public Color title_color = Color.white;
	public String subtitle = "Glitchless";
	public Color subtitle_color = Color.white;
	public PanelBackground title_background = new PanelBackground(GradientDirection.NONE,Color.darkGray,null);
	
	//Timer
	public Color ingame_timer_color = Color.blue;
	public Color offgame_timer_color = Color.red;
	public Color complete_timer_color = Color.green; //on win or death (? see option in death pause)
	public PanelBackground timer_background = new PanelBackground(GradientDirection.NONE,Color.darkGray,null);
	public Color last_ingame_timer_color = Color.white;
	public String last_ingame_time_label = "Last In-Game Time";
	
	//Stats
	public PanelBackground stats_background = new PanelBackground(GradientDirection.NONE,Color.white,null);
	public Color boolean_yes = Color.green;
	public Color boolean_no = Color.red;
	public Color number = Color.gray;
	
	public String yes = "Yes";
	public String no = "No";	
	public Color stats_color = Color.black;
	
	//Player Info
	public boolean show_skull_data = true;
	public boolean skull_3d = false;
	
	/**
	 * Load layout from file
	 * @param f
	 * @return
	 * @throws Exception
	 */
	public static Layout getLayoutFromFile(File f) throws Exception
	{
			String s = Files.readAllLines(f.toPath()).stream().collect(Collectors.joining("\n"));
			
			JSONObject o = new JSONObject(s);
			
			Layout l = new Layout();
			
			//main
			l.background_color = getColor(l.background_color, o, "style","main","background");
			
			//user_info
			l.username_color = getColor(l.username_color, o, "style","user_info","color");
			
			Data.folder = getString(null, o, "config","folder");
			
			//header
			l.title_background = getBackground(l.title_background, o, "style","header","background");			
			l.title = getString(l.title, o, "style","header","title","text");
			l.title_color = getColor(l.title_color, o, "style","header","title","color");
			l.subtitle = getString(l.subtitle, o, "style","header","subtitle","text");
			l.subtitle_color = getColor(l.subtitle_color, o, "style","header","subtitle","color");
			l.show_skull_data = getBoolean(l.show_skull_data, o, "style","header","showhead");
			l.skull_3d = getBoolean(l.skull_3d, o, "style","header","three_dimensional_head");
			
			//timer
			l.timer_background = getBackground(l.timer_background, o, "style","timer","background");			
			l.ingame_timer_color = getColor(l.ingame_timer_color, o, "style","timer","ingame_color");
			l.offgame_timer_color = getColor(l.offgame_timer_color, o, "style","timer","offgame_color");
			l.complete_timer_color = getColor(l.complete_timer_color, o, "style","timer","complete_color");
			l.last_ingame_timer_color = getColor(l.last_ingame_timer_color, o, "style","timer","last_ingame_color");
			l.last_ingame_time_label = getString(l.last_ingame_time_label, o, "style","timer","last_ingame_label");
			
			
			//stats
			l.stats_background = getBackground(l.stats_background, o, "style","stats","background");
			l.stats_color = getColor(l.stats_color, o, "style","stats","color");
			
			//yes,no,number
			l.yes = getString(l.yes, o, "style","stats","yes","text");
			l.no = getString(l.no, o, "style","stats","no","text");
			l.boolean_yes = getColor(l.boolean_yes, o, "style","stats","yes","color");
			l.boolean_no = getColor(l.boolean_yes, o, "style","stats","no","color");
			l.number = getColor(l.boolean_yes, o, "style","stats","number","color");
			
			return l;
	}
	
	/**
	 * Get boolean from JSON object on certain path
	 * @param actual
	 * @param o
	 * @param st
	 * @return
	 * @throws JSONException
	 */
	public static boolean getBoolean(boolean actual,JSONObject o,String... st) throws JSONException
	{
		JSONObject bo = o;
		for(int i = 0; i < st.length; i ++)
		{
			if(i == st.length - 1)
			{
				if(bo.has(st[i]) && bo.get(st[i]) instanceof Boolean)
				{
					
					return bo.getBoolean(st[i]);
				}
				else
					return actual;
			}
			else
			{
				if(bo.has(st[i]) && bo.get(st[i]) instanceof JSONObject)
					bo = bo.getJSONObject(st[i]);
				else
					return actual;
			}
		}
		return actual;
	}
	
	/**
	 * Get int from JSON object on certain path
	 * @param actual
	 * @param o
	 * @param st
	 * @return
	 * @throws JSONException
	 */
	public static int getInt(int actual,JSONObject o,String... st) throws JSONException
	{
		JSONObject bo = o;
		for(int i = 0; i < st.length; i ++)
		{
			if(i == st.length - 1)
			{
				if(bo.has(st[i]) && bo.get(st[i]) instanceof Integer)
				{
					
					return bo.getInt(st[i]);
				}
				else
					return actual;
			}
			else
			{
				if(bo.has(st[i]) && bo.get(st[i]) instanceof JSONObject)
					bo = bo.getJSONObject(st[i]);
				else
					return actual;
			}
		}
		return actual;
	}
	
	/**
	 * Get String from JSON object on certain path
	 * @param actual
	 * @param o
	 * @param st
	 * @return
	 * @throws JSONException
	 */
	public static String getString(String actual,JSONObject o,String... st) throws JSONException
	{
		JSONObject bo = o;
		for(int i = 0; i < st.length; i ++)
		{
			if(i == st.length - 1)
			{
				if(bo.has(st[i]) && bo.get(st[i]) instanceof String)
				{
					
					return bo.getString(st[i]);
				}
				else
					return actual;
			}
			else
			{
				if(bo.has(st[i]) && bo.get(st[i]) instanceof JSONObject)
					bo = bo.getJSONObject(st[i]);
				else
					return actual;
			}
		}
		return actual;
	}
	
	/**
	 * Get Color from JSON object on certain path
	 * @param actual
	 * @param o
	 * @param st
	 * @return
	 * @throws JSONException
	 */
	public static Color getColor(Color actual,JSONObject o,String... st) throws JSONException
	{
		JSONObject bo = o;
		for(int i = 0; i < st.length; i ++)
		{
			if(i == st.length - 1)
			{
				
				if(bo.has(st[i]) && bo.get(st[i]) instanceof String)
				{
					try
					{
						return Color.decode(bo.getString(st[i]));
					}catch(Exception e)
					{
						return actual;
					}
				}
				else
					return actual;
			}
			else
			{
				if(bo.has(st[i]) && bo.get(st[i]) instanceof JSONObject)
					bo = bo.getJSONObject(st[i]);
				else
					return actual;
			}
		}
		return actual;
	}
	
	/**
	 * Get PanelBackground from JSON object on certain path
	 * @param actual
	 * @param o
	 * @param st
	 * @return
	 * @throws JSONException
	 */
	public static PanelBackground getBackground(PanelBackground actual,JSONObject o,String... st) throws JSONException
	{
		JSONObject bo = o;
		for(int i = 0; i < st.length; i ++)
		{
			if(i == st.length - 1)
			{
				
				if(bo.has(st[i]) && bo.get(st[i]) instanceof String)
				{
					
					try
					{
						String[] dt = bo.getString(st[i]).split(" ");
						if(dt.length == 1)
							return new PanelBackground(GradientDirection.NONE, Color.decode(dt[0]),null);
						
						if(dt[0].equalsIgnoreCase("gradient-vertical") || dt[0].equalsIgnoreCase("gradient-horizontal"))
						{
							return new PanelBackground(dt[0].equalsIgnoreCase("gradient-vertical") ? GradientDirection.VERTICAL : GradientDirection.HORIZONTAL, Color.decode(dt[1]), Color.decode(dt[2]));
						}
					}
					catch(Exception e)
					{
						return actual;
					}
					
				}
				else
					return actual;
			}
			else
			{
				if(bo.has(st[i]) && bo.get(st[i]) instanceof JSONObject)
					bo = bo.getJSONObject(st[i]);
				else
					return actual;
			}
		}
		return actual;
	}
	
	
}
