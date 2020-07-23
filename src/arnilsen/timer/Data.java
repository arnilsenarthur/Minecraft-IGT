package arnilsen.timer;

import java.awt.Color;
import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import org.json.JSONArray;
import org.json.JSONObject;
import arnilsen.timer.Data.Stat.StatIcon;
import arnilsen.timer.Data.Stat.StatIconType;
import arnilsen.timer.Data.Stat.StatType;

/**
 * Holds all game data
 * @author Árnilsen Arthur/Infobros2000
 *
 */
public class Data 
{
	//Current minecraft folder in use (null = default)
	public static String folder;
	
	//Provides a method to retrieve the data (must be removed in future)
	public static interface DataStat {

	    public int get(Data d);
	}

	//DataStat provider
	public static HashMap<String, DataStat> STATS = new HashMap<>();

	/**
	 * List all stats on stats.json file
	 * @param file
	 * @return
	 * @throws Exception
	 */
	public static List<Stat> getStatsFromFile(File f) throws Exception
	{
			String s = Files.readAllLines(f.toPath()).stream().collect(Collectors.joining("\n"));
			
			JSONArray o = new JSONArray(s);
			
			
			List<Stat> ls = new ArrayList<Stat>();
			
			for(int i = 0; i < o.length(); i ++)
			{
				JSONObject ob = o.getJSONObject(i);
				//Parse current data from json
				Stat stat = new Stat(null, null, null, null);
				stat.id = Layout.getString(null, ob, "id");
				stat.label = Layout.getString(null, ob, "label");
				String type = Layout.getString("", ob, "type");
				stat.type = type.equalsIgnoreCase("count") ? StatType.COUNT : (type.equalsIgnoreCase("boolean") ? StatType.BOOLEAN : null);
				
				//Check data
				if(stat.label == null || stat.type == null || stat.id == null)
					continue;
				if(!STATS.containsKey(stat.id))
					continue;
				
				//Parse the icon
				if(ob.has("icon"))
					if(ob.get("icon") instanceof JSONObject)
					{
						JSONObject icon = ob.getJSONObject("icon");
						//Just parse it
						StatIcon sicon = new StatIcon(null, null);
						sicon.value = Layout.getString(null, icon, "id");
						
						String itype = Layout.getString("", icon, "type");
						
						if(itype.equalsIgnoreCase("resource"))
							sicon.type = StatIconType.RESOURCE;
						else if(itype.equalsIgnoreCase("skull"))
							sicon.type = StatIconType.SKULL;
						else if(itype.equalsIgnoreCase("skull3d"))
							sicon.type = StatIconType.SKULL_3D;
						else if(itype.equalsIgnoreCase("base64"))
							sicon.type = StatIconType.BASE_64;
						
						if(sicon.value != null && sicon.type != null)
						{
							stat.icon = sicon;
						}						
					}			
				ls.add(stat);			
			}			
			return ls;	
	}
	
	
	//Main Info
	public boolean ingame = false;
	public int killed_ender_dragons = 0;
	public boolean ender_dragon_killed = false;
	
	//Internal timer
	public long ingame_time = 0;
	public long last_stats_file_update = 0;
	public int exit_count = 0;
	
	//Player Info
	public String player_uuid;
	public String player_nick;
	public ImageIcon player_icon;
	public long discount_time = 0;
	public long paused_time = 0;
	
	/**
	 * Gets the estimate time (or the final time if the speedrun has ended)
	 * @return
	 */
	public long estimateTimeForNow()
	{
		if(!ingame)
			return ingame_time;
		
		return ingame_time == 0 ? 0 : (paused_time != 0 ? paused_time : (ingame_time + (System.currentTimeMillis() - last_stats_file_update) - discount_time));
	}
	
	/**
	 * Main stat handling class
	 * @author Árnilsen Arthur/Infobros2000
	 */
	public static class Stat
	{
		public String id;
		public String label;
		public StatType type;
		public StatIcon icon;
		public JLabel j_label;
		
		public Stat(String id,String label,StatType type,StatIcon icon)
		{
			this.id = id;
			this.label = label;
			this.type = type;
			this.icon = icon;
		}
		
		public static enum StatType
		{
			COUNT,
			BOOLEAN
		}
		
		public static class StatIcon
		{
			public StatIconType type;
			public String value;
			public StatIcon(StatIconType type,String value)
			{
				this.type = type;
				this.value = value;
			}
		}
		
		public static enum StatIconType
		{
			SKULL,
			BASE_64,
			RESOURCE, SKULL_3D,
		}
		
		//Update the label of a stat
		public void updateLabel(Layout l,Data d) 
		{
			int value = STATS.get(id).get(d);
			Color color = null;
			
			if(type == StatType.BOOLEAN)
				color = (value == 0) ? l.boolean_no : l.boolean_yes;
			else
				color = l.number;
			
			String lvalue = type == StatType.COUNT ? value + "" : (value == 1 ? l.yes : l.no);
			
			j_label.setForeground(l.stats_color);
			j_label.setText("<html>" + label + ": <font color='" + String.format("#%02X%02X%02X", color.getRed(), color.getGreen(), color.getBlue()) + "'>" + lvalue + "</font></html>");
		}	
	}
	
	/*
	 * List of advancement states providers
	 * (Integration with minecraft advancements)
	 */
	public static HashMap<String, DataProvider> ADVANCEMENTS_PROVIDER = new HashMap<>();
	
	/*
	 * List of stats providers
	 * (Integration with minecraft stats)
	*/
	public static HashMap<String, DataProvider> STATS_PROVIDER = new HashMap<>();
	
	public static class DataProvider
	{
		public int value;
		public DataProviderType type;
		public String[] path; 
	}
	
	public static enum DataProviderType
	{
		ADVANCEMENT,
		STATS;
	}

	/*
	 * Load structure from data.json
	 */
	public static void loadDataStructure(File file)  throws Exception
	{
		{
			String s = Files.readAllLines(file.toPath()).stream().collect(Collectors.joining("\n"));
			
			JSONObject o = new JSONObject(s);
			
			for(String key : JSONObject.getNames(o))
			{
				JSONObject ob = o.getJSONObject(key);
				String type = Layout.getString("", ob, "type");
				if(type.equalsIgnoreCase("advancement"))
				{
					DataProvider provider = new DataProvider();
					provider.value = 0;
					provider.type = DataProviderType.ADVANCEMENT;
					
					if(!ob.has("path"))
						continue;
					if(ob.get("path") instanceof JSONArray)
					{
						JSONArray arr = ob.getJSONArray("path");
						String[] path = new String[arr.length()];
						for(int i = 0;i < arr.length(); i ++)
							path[i] = arr.get(i).toString();
						
						provider.path = path;
						ADVANCEMENTS_PROVIDER.put(key, provider);
						STATS.put(key,(d) -> ADVANCEMENTS_PROVIDER.get(key).value);
							
					}
				}
				else if(type.equalsIgnoreCase("stat"))
				{
					DataProvider provider = new DataProvider();
					provider.value = 0;
					provider.type = DataProviderType.STATS;
					
					JSONArray arr = ob.getJSONArray("path");
					String[] path = new String[arr.length()];
					for(int i = 0;i < arr.length(); i ++)
						path[i] = arr.get(i).toString();
					
					provider.path = path;
					STATS_PROVIDER.put(key, provider);
					STATS.put(key,(d) -> STATS_PROVIDER.get(key).value);
				}
			}
		}
	}
	
}
