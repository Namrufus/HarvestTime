package com.github.namrufus.harvest_time.rainfall_control;

import java.util.logging.Logger;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.weather.WeatherChangeEvent;

public class RainfallControlListener implements Listener {
	RainfallControlWorldUpdater rainfallControlWorldUpdater;
	Logger log;
	
	public RainfallControlListener(RainfallControlWorldUpdater rainfallControlWorldUpdater, Logger log) {
		this.rainfallControlWorldUpdater = rainfallControlWorldUpdater;
		this.log = log;
	}
	
	// ================================================================================================================
	
	@EventHandler
	public void onWeatherChangeEvent(WeatherChangeEvent event) {
		boolean targetWeatherState = rainfallControlWorldUpdater.getTargetWeatherState();
		
		// set the rainfall time to a short amount of time regardless
		event.getWorld().setWeatherDuration(120);
		
		// if the weather state is the same as the required state, do nothing
		// prevents infinite loop (setStorm() prompts another WeatherChangeEvent)
		if (event.toWeatherState() == targetWeatherState)
			return;
		
		// cancel the event so that the custom weather may be set
		event.setCancelled(true);

		// change the weather
		rainfallControlWorldUpdater.updateWeather(event.getWorld());
	}
}
