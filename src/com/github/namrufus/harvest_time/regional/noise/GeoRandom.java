package com.github.namrufus.harvest_time.regional.noise;

// This class represents a random number generator that generates random numbers based on inputed coordinates and a seed
// the random number generation should be seeded, infinite (in practice), and non-symmetric.

public class GeoRandom {
	private long seed;
	
	public GeoRandom(long seed) {
		this.seed = seed;
	}
	
	// 64-bit linear congruential generator: http://nuclear.llnl.gov/CNP/rng/rngman/node4.html
	// not the "most random" but fast and good enough
	private static long a = 2862933555777941757L;
	private static long b = 3037000493L;
	private static long randomize(long seed) {
		return a * seed + b /*implicit mod 2^64*/;
	}
	
	// map integers to natural numbers (positive, nonzero) for cantor function
	private static long interleave(long k) {
		if (k < 0)
			return -2*k; // -1->2, -2->4, -3->6, ...
		else
			return 2*k + 1; // 0->1, 1->3, 2->5, 3->7, ...
	}
	
	// cantor pairing function -- map two integers to a single natural number
	// http://en.wikipedia.org/wiki/Cantor_pairing_function#Cantor_pairing_function
	private static long cantorPairing(long k1, long k2) {
		// cantor pairing function requires natural number
		k1 = interleave(k1);
		k2 = interleave(k2);
		
		return ((k1 + k2) * (k1 + k2 + 1)) / 2 + k2;
	}
	
	// sample the random number at the coordinates specified
	public long sampleLong(long x, long y, long z) {
		// combine and chain randomizations to the coordinates and seed
		
		// obscure periodicity of randomization function
		x = x * x * (x ^ seed);
		y = y * y * (y ^ seed);
		z = z * z * (z ^ seed);
		
		return randomize(seed ^ cantorPairing(x, cantorPairing(y, z)));
	}
	public long sampleLong(long x, long y) {
		return sampleLong(x, y, 0);
	}
	public long sampleLong(long x) {
		return sampleLong(x, 0, 0);
	}
	
	// sample doubles in range [0, 1]
	public double sampleDouble(long x, long y, long z) {
		return Math.abs((double)sampleLong(x, y, z) / (double)Long.MAX_VALUE);
	}
	public double sampleDouble(long x, long y) {
		return Math.abs((double)sampleLong(x, y) / (double)Long.MAX_VALUE);
	}
	public double sampleDouble(long x) {
		return Math.abs((double)sampleLong(x) / (double)Long.MAX_VALUE);
	}
}
