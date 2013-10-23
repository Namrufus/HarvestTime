package com.github.namrufus.harvest_time;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.namrufus.harvest_time.configuration.ConfigurationLoader;
import com.github.namrufus.harvest_time.configuration.FarmlandCreationConfiguration;
import com.github.namrufus.harvest_time.configuration.SeasonalConfiguration;
import com.github.namrufus.harvest_time.farmland.FarmlandCreationListener;
import com.github.namrufus.harvest_time.seasonal.SeasonalCalendar;
import com.github.namrufus.harvest_time.util.PlayerTimerSystem;

public class HarvestTime extends JavaPlugin {
	SeasonalCalendar seasonalCalendar;
	ConfigurationLoader configurationLoader;
	
	@Override
	public void onEnable() {
		// -- load configs --------------------------------------------------------------------------------------------
		// make sure the configuration file exists, if it does not, then create a new one
		if (!this.getConfig().isSet("harvest_time")) {
			this.saveDefaultConfig();
			this.getLogger().warning("Config did not exist or was invalid, default config saved.");
		}
		this.reloadConfig();
		
		// load configuration file
		ConfigurationSection config = this.getConfig().getConfigurationSection("harvest_time");
		configurationLoader = new ConfigurationLoader(config, this.getLogger());
		
		// -- initialize calendar -------------------------------------------------------------------------------------
		// load timestamp data
        File file = new File(getDataFolder(), "timestamp.yml");
	    FileConfiguration timeConfig = YamlConfiguration.loadConfiguration(file);
	    
	    long timestamp;
	    if (timeConfig.isSet("timestamp"))
	    	timestamp = timeConfig.getLong("timestamp");
	    else {
	    	// get a starting timestamp based on the current time
	    	timestamp = SeasonalCalendar.getStartingReferenceTimestamp();

	    	// save the starting timestamp
	    	saveTimestamp(timestamp);
	    }
	    
		SeasonalConfiguration seasonalConfiguration = configurationLoader.getSeasonalConfiguration();
	    
	    this.getLogger().info("Reference Timestamp: "+timestamp);
	    Date date = new Date(timestamp);
	    this.getLogger().info("Reference Timestamp Date: "+date);
	    
	    seasonalCalendar = new SeasonalCalendar(seasonalConfiguration.getDaysInSeasonalYear(), timestamp);
		
		// -- Initialize listeners ------------------------------------------------------------------------------------
		PlayerTimerSystem playerTimerSystem = new PlayerTimerSystem();
		
		// farmland creation listener
		FarmlandCreationConfiguration farmlandConfig = new FarmlandCreationConfiguration(config.getConfigurationSection("farmland_creation"), this.getLogger());
		FarmlandCreationListener farmlandListener = new FarmlandCreationListener(playerTimerSystem, farmlandConfig);
		this.getServer().getPluginManager().registerEvents(farmlandListener, this);
//		
//		// crop growth info listener
//		GrowthStageInformationListener growthStageInformationListener = new GrowthStageInformationListener(crops, dayCalendar);
//		this.getServer().getPluginManager().registerEvents(growthStageInformationListener, this);
//		
//		// crop "tending" listener
//		GrowthListener cropTendingListener = new GrowthListener(crops, dayCalendar);
//		this.getServer().getPluginManager().registerEvents(cropTendingListener, this);
//		
	}
	
	// ================================================================================================================
	
	private void saveTimestamp(long timestamp) {
        File file = new File(getDataFolder(), "timestamp.yml");
	    FileConfiguration timeConfig = YamlConfiguration.loadConfiguration(file);
        
        timeConfig.set("timestamp", timestamp);
	    
	    try {
	    	timeConfig.save(file);
	    } catch(IOException e) {
	    	this.getLogger().info("Warning: can't update timestamp file: "+getDataFolder()+"/timedata.yml");
	    	this.getLogger().info(e.getMessage());
	    }
	}
	
	private String getTimeSummary() {
		String message = "";
		message += "Year "+seasonalCalendar.getSeasonalYear();
		message += ", Day "+seasonalCalendar.getSeasonalDay();
		return message;
	}
	
	// ****************************************************************************************************************
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
		if (cmd.getName().equalsIgnoreCase("ht-config-dump")) {
			configDumpCommand(sender, args);
		} else if (cmd.getName().equalsIgnoreCase("ht-config-dump-crop")) {
			configDumpCropCommand(sender, args);
		} else if(cmd.getName().equalsIgnoreCase("ht-timecheck")){	
			timecheckCommand(sender, args);
		} else if (cmd.getName().equalsIgnoreCase("ht-when-increment")) {
			whenIncrementCommand(sender, args);
		} else if (cmd.getName().equalsIgnoreCase("ht-next-increment")) {
			nextIncrementCommand(sender, args);
		} else if (cmd.getName().equalsIgnoreCase("ht-time-set")) {
			timeSetCommand(sender, args);
		} else if (cmd.getName().equalsIgnoreCase("ht-time-increment")) {
			timeIncrementCommand(sender, args);
		} else /* no matching command */ {
			return false;
		}
		
		return true;
	}
	
	// = command handlers =============================================================================================
	private void configDumpCommand(CommandSender sender, String[] args) {
		configurationLoader.dumpConfiguration(getLogger());
	}
	
	private void configDumpCropCommand(CommandSender sender, String[] args) {
		if (args.length != 1) {
			sender.sendMessage("command requires 1 argument.");
			return;
		}
		
		configurationLoader.dumpCrop(args[0], getLogger());
	}
	// ----------------------------------------------------------------------------------------------------------------
	private void timecheckCommand(CommandSender sender, String[] args) {
		sender.sendMessage("§7"/*light grey*/ + "[Demeter] " + getTimeSummary());
	}
	
	// ----------------------------------------------------------------------------------------------------------------
	private void whenIncrementCommand(CommandSender sender, String[] args) {
		long incrementTime = seasonalCalendar.seasonalDayIncrementTime();
		
		long hours = incrementTime / SeasonalCalendar.millisInHour;
		long minutes = (incrementTime - hours * SeasonalCalendar.millisInHour) / SeasonalCalendar.millisInMinute;
		
		sender.sendMessage("§7"/*light grey*/ + "[Demeter] " + "GMT +" + hours + "h:" + minutes + "m");
	}
	
	private void nextIncrementCommand(CommandSender sender, String[] args) {
		sender.sendMessage("§7"/*light grey*/ + "[Demeter] " + seasonalCalendar.nextSeasonalDayIncrement());
	}
	
	// ----------------------------------------------------------------------------------------------------------------
	private void timeSetCommand(CommandSender sender, String[] args) {
		long seasonalYear = Long.parseLong(args[0]);
		long seasonalDay = Long.parseLong(args[1]);
		
		seasonalCalendar.setReferenceTimestamp(seasonalYear, seasonalDay);
		sender.sendMessage("§7"/*light grey*/ + "[Harvest Time] " + getTimeSummary());
		
		saveTimestamp(seasonalCalendar.getReferenceTimestamp());
		sender.sendMessage("§c"/*light red*/  + "[Harvest Time] reference timestamp saved.");
	}
	
	private void timeIncrementCommand(CommandSender sender, String[] args) {
		long increment = Long.parseLong(args[0]);
		
		String interval = args[1];
		
		if (interval.equalsIgnoreCase("minute") || interval.equalsIgnoreCase("minutes"))
			increment *= SeasonalCalendar.millisInMinute;
		else if (interval.equalsIgnoreCase("hour") || interval.equalsIgnoreCase("hours"))
			increment *= SeasonalCalendar.millisInHour;
		else if (interval.equalsIgnoreCase("day") || interval.equalsIgnoreCase("days"))
			increment *= SeasonalCalendar.millisInDay;
		else if (interval.equalsIgnoreCase("week") || interval.equalsIgnoreCase("weeks"))
			increment *= SeasonalCalendar.millisInDay * 7;
		else {
			this.getLogger().warning("unknown interval: "+interval);
			return;
		}
			
		seasonalCalendar.incrementReferenceTimestamp(increment);
		sender.sendMessage("§7"/*light grey*/ + "[Harvest Time] " + getTimeSummary());
		sender.sendMessage("§7"/*light grey*/ + "[Harvest Time] next increment: " + seasonalCalendar.nextSeasonalDayIncrement());

		saveTimestamp(seasonalCalendar.getReferenceTimestamp());
		sender.sendMessage("§c"/*light red*/  + "[Harvest Time] reference timestamp saved.");
	}
}
