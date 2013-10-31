package com.github.namrufus.harvest_time.util.configuration;

public class ConfigUtil {
	// take an enum class and a string and return the appropriate Enum value
	// if there is no matching enum, then return null
	public static <E extends Enum<E>> E enumFromString(Class<E> enumType, String string) {
		if (string == null)
			return null;
		
		try {
			return Enum.valueOf(enumType, string);
		} catch (IllegalArgumentException e) {
			return null;
		}
	}
}
