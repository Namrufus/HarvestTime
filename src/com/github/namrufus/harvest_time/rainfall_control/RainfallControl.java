package com.github.namrufus.harvest_time.rainfall_control;

import java.util.Random;

import org.bukkit.command.CommandSender;

import com.github.namrufus.harvest_time.util.noise.GeoRandom;

// system that controls the rainfall state based on the configuration and time

public class RainfallControl {
	private RainfallControlConfiguration rainfallControlConfiguration;
	
	// random number generators resposible for generating the weather
	private GeoRandom yearlyRandom;
	private GeoRandom dailyRandom;
	
	public RainfallControl(RainfallControlConfiguration rainfallControlConfiguration) {
		this.rainfallControlConfiguration = rainfallControlConfiguration;
		
		Random seeder = new Random(rainfallControlConfiguration.getSeed().hashCode());
		
		yearlyRandom = new GeoRandom(seeder.nextLong());
		dailyRandom = new GeoRandom(seeder.nextLong());
	}
	
	// ================================================================================================================
	
	private RainfallClimateState sampleRainfallClimateState(int year) {
		// go into monsoon, normal, and drought mode, depending on a random number keyed to this year
		double yearlyState = yearlyRandom.sampleDouble(year);
		return rainfallControlConfiguration.sampleRainfallClimateState(yearlyState);
	}
	
	private boolean sampleRainfallState(RainfallClimateState rainfallClimateState, int absoluteDay) {
		double dailyState = dailyRandom.sampleDouble(absoluteDay);
		return rainfallControlConfiguration.sampleIsRaining(rainfallClimateState, dailyState);
	}
	
	// ================================================================================================================
	
	public boolean isEnabled() { return rainfallControlConfiguration.isEnabled(); }
	
	// ----------------------------------------------------------------------------------------------------------------
	
	// determine if the world should be in a rain state based on the day index and the season
	public boolean getRainState(int absoluteDay, int year) {
		RainfallClimateState rainfallClimateState = sampleRainfallClimateState(year);
		return sampleRainfallState(rainfallClimateState, absoluteDay);
	}

	// ----------------------------------------------------------------------------------------------------------------
	
	public void displayRainfallClimateState(CommandSender sender, int year) {
		double yearlyState = yearlyRandom.sampleDouble(year);
		
		RainfallClimateState rainfallClimateState = rainfallControlConfiguration.sampleRainfallClimateState(yearlyState);
		
		sender.sendMessage("§7[Harvest Time] Rainfall Climate: " + rainfallClimateState);
	}
}
