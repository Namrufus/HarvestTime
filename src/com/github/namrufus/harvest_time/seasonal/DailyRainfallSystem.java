package com.github.namrufus.harvest_time.seasonal;

import java.util.Random;

import org.bukkit.command.CommandSender;

import com.github.namrufus.harvest_time.util.noise.GeoRandom;

public class DailyRainfallSystem {
	SeasonalConfiguration seasonalConfiguration;
	GeoRandom yearlyRandom;
	GeoRandom dailyRandom;
	
	public DailyRainfallSystem(SeasonalConfiguration seasonalConfiguration) {
		this.seasonalConfiguration = seasonalConfiguration;
		
		Random seeder = new Random(seasonalConfiguration.getRainfallSeed().hashCode());
		
		yearlyRandom = new GeoRandom(seeder.nextLong());
		dailyRandom = new GeoRandom(seeder.nextLong());
	}
	
	public boolean isEnabled() { return seasonalConfiguration.isRainfallControlEnabled(); }
	
	// determine if the world should be in a rain state based on the day index and the season
	public boolean getRainState(SeasonalCalendar seasonalCalendar) {
		// go into monsoon, normal, and drought mode, depending on a random number keyed to this day
		double yearlyState = yearlyRandom.sampleDouble(seasonalCalendar.getSeasonalYear());
		double dailyState = dailyRandom.sampleDouble(seasonalCalendar.getAbsoluteSeasonalDay());

		return seasonalConfiguration.sampleIsRaining(seasonalConfiguration.sampleNewRainfallType(yearlyState), dailyState);
	}
	
	public void displayState(CommandSender sender, SeasonalCalendar seasonalCalendar) {
		double yearlyState = yearlyRandom.sampleDouble(seasonalCalendar.getSeasonalYear());
		
		SeasonalConfiguration.RainfallType rainfallType = seasonalConfiguration.sampleNewRainfallType(yearlyState);
		
		sender.sendMessage("§7[Harvest Time] Rainfall Climate: " + rainfallType);
	}
}
