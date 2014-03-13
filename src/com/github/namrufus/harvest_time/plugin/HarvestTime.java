package com.github.namrufus.harvest_time.plugin;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.namrufus.harvest_time.bonemeal.BonemealDisabledListener;
import com.github.namrufus.harvest_time.crop_growth.chance_growth.ChanceGrowthListener;
import com.github.namrufus.harvest_time.crop_growth.seasonal_growth.SeasonalGrowthListener;
import com.github.namrufus.harvest_time.farmland.FarmlandCreationListener;
import com.github.namrufus.harvest_time.plugin.global.TextCode;
import com.github.namrufus.harvest_time.seasonal_calendar.SeasonalCalendar;
import com.github.namrufus.harvest_time.seasonal_calendar.SeasonalConfiguration;

public class HarvestTime extends JavaPlugin {
	ConfigurationLoader configurationLoader;
	
	SeasonalCalendar seasonalCalendar;
	
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
	    
	    // -- Initialize player timer system --------------------------------------------------------------------------
		PlayerInteractionDelayer playerInteractionDelayer = new PlayerInteractionDelayer();
	    
		// -- Initialize listeners ------------------------------------------------------------------------------------
		
		// bonemeal disabling listener
		BonemealDisabledListener bonemealDisabledListener = new BonemealDisabledListener(configurationLoader.getBonemealDisabledConfiguration());
		this.getServer().getPluginManager().registerEvents(bonemealDisabledListener, this);
		
		// farmland creation listener
		FarmlandCreationListener farmlandListener = new FarmlandCreationListener(playerInteractionDelayer,
																				 configurationLoader.getFarmlandCreationConfiguration(),
																				 configurationLoader.getInteractionConfiguration());
		this.getServer().getPluginManager().registerEvents(farmlandListener, this);
		
		// crop seasonal growth listener
		SeasonalGrowthListener seasonalGrowthListener = new SeasonalGrowthListener(configurationLoader.getInteractionConfiguration(),
				                                                                configurationLoader.getTendingConfiguration(),
				                                                                configurationLoader.getSeasonalCropListConfiguration(),
				                                                                seasonalCalendar, playerInteractionDelayer);
		this.getServer().getPluginManager().registerEvents(seasonalGrowthListener, this);
		
		// crop chance growth listener
		ChanceGrowthListener chanceGrowthListener = new ChanceGrowthListener(configurationLoader.getInteractionConfiguration(),
																			 configurationLoader.getChanceCropListConfiguration(),
																			 this.getLogger());
		this.getServer().getPluginManager().registerEvents(chanceGrowthListener, this);
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
		} else if(cmd.getName().equalsIgnoreCase("ht-time-check")){	
			timeCheckCommand(sender, args);
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
	private void timeCheckCommand(CommandSender sender, String[] args) {
		sender.sendMessage(TextCode.BASE + TextCode.MESSAGE_PREFIX + " " + getTimeSummary());
	}
	
	// ----------------------------------------------------------------------------------------------------------------
	private void whenIncrementCommand(CommandSender sender, String[] args) {
		long incrementTime = seasonalCalendar.seasonalDayIncrementTime();
		
		long hours = incrementTime / SeasonalCalendar.millisInHour;
		long minutes = (incrementTime - hours * SeasonalCalendar.millisInHour) / SeasonalCalendar.millisInMinute;
		
		sender.sendMessage(TextCode.BASE + TextCode.MESSAGE_PREFIX + " " + TextCode.VALUE + "GMT +" + hours + "h:" + minutes + "m");
	}
	
	private void nextIncrementCommand(CommandSender sender, String[] args) {
		sender.sendMessage(TextCode.BASE + TextCode.MESSAGE_PREFIX + " " + TextCode.VALUE + seasonalCalendar.nextSeasonalDayIncrement());
	}
	
	// ----------------------------------------------------------------------------------------------------------------
	private void timeSetCommand(CommandSender sender, String[] args) {
		if (args.length != 2) {
			sender.sendMessage("command requires 2 arguments");
			return;
		}
		
		long seasonalYear, seasonalDay;
		
		try {
			seasonalYear = Long.parseLong(args[0]);
		} catch (NumberFormatException e) {
			sender.sendMessage("can't parse seasonalYear: " + args[0]);
			return;
		}
		
		try {
			seasonalDay = Long.parseLong(args[1]);
		} catch (NumberFormatException e) {
			sender.sendMessage("can't parse seasonalDay: " + args[1]);
			return;
		}
		
		seasonalCalendar.setReferenceTimestamp(seasonalYear, seasonalDay);
		sender.sendMessage(TextCode.BASE + TextCode.MESSAGE_PREFIX + " " + getTimeSummary());
		
		saveTimestamp(seasonalCalendar.getReferenceTimestamp());
		sender.sendMessage(TextCode.WARNING + TextCode.MESSAGE_PREFIX + " reference timestamp saved.");
	}
	
	private void timeIncrementCommand(CommandSender sender, String[] args) {
		if (args.length != 2) {
			sender.sendMessage(TextCode.BASE + TextCode.MESSAGE_PREFIX + " command requires 2 arguments.");
			return;
		}
		
		long increment;
		try {
			increment = Long.parseLong(args[0]);
		} catch (NumberFormatException e) {
			sender.sendMessage(TextCode.BASE + TextCode.MESSAGE_PREFIX + " can't parse increment: " + TextCode.VALUE + args[0]);
			return;
		}
		
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
			sender.sendMessage("unknown interval: "+interval);
			return;
		}
			
		seasonalCalendar.incrementReferenceTimestamp(increment);
		sender.sendMessage(TextCode.BASE + TextCode.MESSAGE_PREFIX + " " + TextCode.VALUE + getTimeSummary());
		sender.sendMessage(TextCode.BASE + TextCode.MESSAGE_PREFIX + " next increment: " + TextCode.VALUE +  seasonalCalendar.nextSeasonalDayIncrement());

		saveTimestamp(seasonalCalendar.getReferenceTimestamp());
		sender.sendMessage(TextCode.WARNING + TextCode.MESSAGE_PREFIX + " reference timestamp saved.");
	}
}
