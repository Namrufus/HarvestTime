package com.github.namrufus.harvest_time.seasonal;

import java.util.logging.Logger;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.weather.WeatherChangeEvent;

public class SeasonalRainfallListener implements Listener {
	DailyRainfallSystem dailyRainfallSystem;
	SeasonalCalendar seasonalCalendar;
	Logger log;
	
	public SeasonalRainfallListener(DailyRainfallSystem dailyRainfallSystem, SeasonalCalendar seasonalCalendar, Logger log) {
		this.dailyRainfallSystem = dailyRainfallSystem;
		this.seasonalCalendar = seasonalCalendar;
		this.log = log;
	}
	
	@EventHandler
	public void onWeatherChangeEvent(WeatherChangeEvent event) {
		// if the rainfall system is disabled, do nothing
		if (!dailyRainfallSystem.isEnabled())
			return;
			
		// shorten rainfall cycle ("poll" rainfall state fairly quickly)
		event.getWorld().setWeatherDuration(120);
		
		boolean targetWeatherState = dailyRainfallSystem.getRainState(seasonalCalendar);
		
		// if the weather state is the same as the required state, do nothing
		// prevents infinite loop (setStorm() prompts another WeatherChangeEvent)
		if (event.toWeatherState() == targetWeatherState)
			return;
		
		// cancel the event so that the custom weather may be set
		event.setCancelled(true);
		log.info("changed weather: " + event.getWorld().getName() + ", rain = " + targetWeatherState);
		event.getWorld().setStorm(targetWeatherState);
		
		// shorten rainfall cycle ("poll" rainfall state fairly quickly)
		// need to do this again after "world.setStorm()"
		event.getWorld().setWeatherDuration(120);
	}
}
