package com.github.namrufus.harvest_time.rainfall_control;

import java.util.logging.Logger;

import org.bukkit.World;

import com.github.namrufus.harvest_time.seasonal.SeasonalCalendar;

// this class is responsible for updating a world's rainfall state when asked

public class RainfallControlWorldUpdater {
	RainfallControl rainfallControl;
	SeasonalCalendar seasonalCalendar;
	Logger log;
	
	public RainfallControlWorldUpdater(RainfallControl rainfallControl, SeasonalCalendar seasonalCalendar, Logger log) {
		this.rainfallControl = rainfallControl;
		this.seasonalCalendar = seasonalCalendar;
		this.log = log;
	}
	
	// ================================================================================================================
	
	public boolean getTargetWeatherState() {
		return rainfallControl.getRainState((int)seasonalCalendar.getAbsoluteSeasonalDay(), (int)seasonalCalendar.getSeasonalYear());
	}
	
	// set the weather of the given world to the current target rainfall state
	public void updateWeather(World world) {			
		boolean targetWeatherState = getTargetWeatherState();
		
		log.fine("changed weather: " + world.getName() + ", rain = " + targetWeatherState);
		world.setStorm(targetWeatherState);
		
		// shorten rainfall cycle ("poll" rainfall state fairly quickly)
		// need to do this again after "world.setStorm()"
		world.setWeatherDuration(120);
	}
}
