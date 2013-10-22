package com.github.namrufus.harvest_time.regional.noise;

public class NoiseUtil {
	// fade function that fades from 0 to 1 over input 0 to 1
	// x < 0   ->   f(x) = 0.0
	// x > 1   ->   f(x) = 1.0
	// two spliced parabolas
	public static double interpQuad(double x) {
		if (x < 0.0) return 0.0;
		else if (x < 0.5) return 2.0 * x * x;
		else if (x < 1.0) return 1.0 - (2.0 * (1.0 - x) * (1.0 - x));
		else return 1.0;
	}
	
	// fade function range from 0, 1 over input 0 to 1
	// x < 0   ->   f(x) = 0.0
	// x > 1   ->   f(x) = 1.0
	// uses cosine function
	public static double interpSino(double x) {
		if (x < 0.0) return 0.0;
		else if (x > 1.0) return 1.0;
		return 0.5 - 0.5 * Math.cos(x * Math.PI);
	}
	
	// linearly adjust x so that f(from) -> 'to'
	// ranges != 'from' will be linearly readjusted to the range [0, 1]
	public static double rebias(double x, double from, double to) {
		if (x < from)
			return x * to / from;
		else
			return  to + (x - from) * ((1.0 - to) / (1.0 - from));
	}
	
	// create a new seed from a seed provided from a random number generator.
	public static long reseed(long seed, String string) {
		return seed ^ string.hashCode();
	}
	
	// sawtooth wave with range [0, 1] and period 1
	public static double sawtooth(double x) {
		x = x - Math.floor(x);
		if (x < 0.5)
			return x * 2.0;
		else
			return 2.0 - x * 2.0;
	}
	
	public static double extremify(double value, double exponent) {
		value = 2.0 * (value - 0.5);
		value = (value < 0.0 ? -1.0 : 1.0) * Math.pow(Math.abs(value), exponent);
		value = (value + 1.0) / 2.0;
		return value;
	}
}
