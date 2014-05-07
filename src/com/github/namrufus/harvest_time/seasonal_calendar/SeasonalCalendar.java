package com.github.namrufus.harvest_time.seasonal_calendar;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.bukkit.configuration.ConfigurationSection;

// determine the current "seasonal" day and year based on the current time and the 

public class SeasonalCalendar {
	public static final long millisInMinute = 1000 * 60;
	public static final long millisInHour = millisInMinute * 60;
	public static final long millisInDay = millisInHour * 24;
	
	// ----------------------------------------------------------------------------------------------------------------
	// the number of IRL days in a game year
	public long daysInSeasonalYear;
	public long millisecondsInSeasonalDay;
	// time zero (first instant of year 0, day 0
	// unix time
	public long referenceTimestamp;
	
	public SeasonalCalendar(long daysInGameYear, long referenceTimestamp) {
		this.daysInSeasonalYear = daysInGameYear;
		this.referenceTimestamp = referenceTimestamp;
	}
	public SeasonalCalendar(long daysInGameYear) {
		this(daysInGameYear, System.currentTimeMillis());
	}
	public SeasonalCalendar(long daysInGameYear, ConfigurationSection timestampFile) {
		// TODO
	}
	
	// ----------------------------------------------------------------------------------------------------------------
	// get the reference time for the current time, rounding down 1 day to 1 hour before the midnight local time
	public static long getStartingReferenceTimestamp() {
    	long timestamp = System.currentTimeMillis(); // default - start now
    	
    	Calendar calendar = new GregorianCalendar();
    	calendar.setTimeInMillis(timestamp);
    	
    	// truncate to the start of the current day in the local timezone
    	calendar.set(Calendar.MILLISECOND, 0);
    	calendar.set(Calendar.SECOND, 0);
    	calendar.set(Calendar.MINUTE, 0);
    	calendar.set(Calendar.HOUR, 0);
    	
    	// move back 1 hour
    	calendar.add(Calendar.HOUR, -1);
    	
    	long roundedTimestamp = calendar.getTimeInMillis();
    	
    	// if the rounded timestamp is more than 24h behind the current time, add 24 hours to the
    	// reference timestamp
    	if (timestamp - roundedTimestamp > SeasonalCalendar.millisInDay)
    		roundedTimestamp += SeasonalCalendar.millisInDay;
    	
    	return roundedTimestamp;
	}
	
	// ----------------------------------------------------------------------------------------------------------------
	public void setReferenceTimestamp(long referenceTimestamp) {
		this.referenceTimestamp = referenceTimestamp;
	}
	
	public void setReferenceTimestamp(long seasonalYear, long seasonalDay) {
		long seasonalDayAbsolute = seasonalYear * daysInSeasonalYear + seasonalDay;
		
		// subtract from the referenceTimestamp(time zero) to add to the current time
		referenceTimestamp -= (seasonalDayAbsolute - getAbsoluteSeasonalDay()) * millisInDay;
	}
	
	public void incrementReferenceTimestamp(long increment) {
		// subtract from the referenceTimestamp(time zero) to add to the current time
		referenceTimestamp -= increment;
	}
	
	public long getReferenceTimestamp() { return referenceTimestamp; }
	
	// ----------------------------------------------------------------------------------------------------------------
	// get the number of full days since the reference timestamp
	private long getAbsoluteSeasonalDay() {
		return (System.currentTimeMillis() - referenceTimestamp) / millisInDay;
	}
	
	// get the number of full Seasonal years since the reference timestamp
	public long getSeasonalYear() {
		return getAbsoluteSeasonalDay() / daysInSeasonalYear;
	}
	
	// get the number of full seasonal days since the start of the current seasonal year
	public long getSeasonalDay() {
		return getAbsoluteSeasonalDay() - (getSeasonalYear() * daysInSeasonalYear);
	}
	
	// ----------------------------------------------------------------------------------------------------------------
	// return the datetime of the next time the seasonal day will increment
	public Date nextSeasonalDayIncrement() {
		// get the timestamp of the next day increment by rounding the current time to the previous day 
		// and adding one day.
		long nextDayMillis = (getAbsoluteSeasonalDay() + 1) * millisInDay  + referenceTimestamp;
		
		return new Date(nextDayMillis);
	}
	
	// return the real date of the start of the given day
	public Date getSeasonalDayStart(long seasonalYear, long seasonalDay) {
		long absoluteSeasonalDay = seasonalYear * daysInSeasonalYear + seasonalDay;
		
		long dateMillis = absoluteSeasonalDay * millisInDay + referenceTimestamp;
		
		return new Date(dateMillis);
	}
	
	// return the number of hours & minutes +/- from midnight GMT the seasonal day increments
	public long seasonalDayIncrementTime() {
		return referenceTimestamp % millisInDay;
	}
}
