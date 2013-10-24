package com.github.namrufus.harvest_time.regional.noise;

import java.util.Random;

// infinitely random Value Noise generator

public class Noise {
	private GeoRandom geoRandom;
	private double frequency;
	
	// xyzStore
	double aaaStore, aabStore, abaStore, abbStore;
	double baaStore, babStore, bbaStore, bbbStore;
	long xRecord, yRecord, zRecord;
	int sampledDimensions;
	
	double xOffset, yOffset, zOffset;
	
	public Noise(Random random, double frequency) {
		geoRandom = new GeoRandom(random.nextLong());
		this.frequency = frequency;
		
		xRecord = 0;
		yRecord = 0;
		zRecord = 0;
		
		aaaStore = aabStore = abaStore = abbStore = 0.0;
		baaStore = babStore = abbStore = bbbStore = 0.0;
		
		sampledDimensions = 0;
		
		xOffset = random.nextDouble();
		yOffset = random.nextDouble();
		zOffset = random.nextDouble();
		
		xOffset = 0.0;
		yOffset = 0.0;
		zOffset = 0.0;
	}
	
	public double sample(double x, double y, double z) {
		// scale values
		x /= frequency;
		y /= frequency;
		z /= frequency;
		
		// offset value randomly
		x += xOffset;
		y += yOffset;
		z += zOffset;
		
		// -- grid values ---------------------------------------------------------------------------------------------
		// find the coordinates of the gridspace to be sampled
		long xL = (long)Math.floor(x), yL = (long)Math.floor(y), zL = (long)Math.floor(z);
		
		// sample values
		// a = minus, b plus
		double aaa, aab, aba, abb, baa, bab, bba, bbb;
		
		if (sampledDimensions < 3 || xL != xRecord || yL != yRecord || zL != zRecord) {
			// if the current grind value was not the last sampled,
			// sampled the grid values now and store them for later
			aaaStore = aaa = geoRandom.sampleDouble(xL, yL, zL);
			aabStore = aab = geoRandom.sampleDouble(xL, yL, zL+1);
			abaStore = aba = geoRandom.sampleDouble(xL, yL+1, zL);
			abbStore = abb = geoRandom.sampleDouble(xL, yL+1, zL+1);
			baaStore = baa = geoRandom.sampleDouble(xL+1, yL, zL);
			babStore = bab = geoRandom.sampleDouble(xL+1, yL, zL+1);
			bbaStore = bba = geoRandom.sampleDouble(xL+1, yL+1, zL);
			bbbStore = bbb = geoRandom.sampleDouble(xL+1, yL+1, zL+1);
			
			// record this grid coordinate as the last sampled
			xRecord = xL;
			yRecord = yL;
			zRecord = zL;
			sampledDimensions = 3;
		} else {
			// if the current grid value was the last sampled, then 
			// simply use the stored values
			aaa = aaaStore;
			aab = aabStore;
			aba = abaStore;
			abb = abbStore;
			baa = baaStore;
			bab = babStore;
			bba = bbaStore;
			bbb = bbbStore;
		}
		
		// -- interpolation -------------------------------------------------------------------------------------------
		// find the relative coordinates of the sample point inside the grid
		double xD = x - xL, yD = y - yL, zD = z - zL;
		// interpolation function values
		double xInterpB = NoiseUtil.interpQuad(xD);
		double yInterpB = NoiseUtil.interpQuad(yD);
		double zInterpB = NoiseUtil.interpQuad(zD);
		double xInterpA = 1.0 - xInterpB, yInterpA = 1.0 - yInterpB, zInterpA = 1.0 - zInterpB;
		
		// interpolate the grid values
		
		double ans = xInterpA * (yInterpA * ((aaa * zInterpA) + (aab * zInterpB)) + 
					             yInterpB * ((aba * zInterpA) + (abb * zInterpB)));
		ans +=       xInterpB * (yInterpA * ((baa * zInterpA) + (bab * zInterpB)) + 
	                             yInterpB * ((bba * zInterpA) + (bbb * zInterpB)));
		return ans;
	}
	// ================================================================================================================
	public double sample(double x, double y) {
		// scale values
		x /= frequency;
		y /= frequency;
		
		// offset value randomly
		x += xOffset;
		y += yOffset;
		
		// -- grid values ---------------------------------------------------------------------------------------------
		// find the coordinates of the gridspace to be sampled
		long xL = (long)Math.floor(x), yL = (long)Math.floor(y);
		
		// sample values
		// a = minus, b plus
		double aa, ab, ba, bb;
		
		if (sampledDimensions < 2 || xL != xRecord || yL != yRecord || zRecord != 0) {
			// if the current grind value was not the last sampled,
			// sampled the grid values now and store them for later
			aaaStore = aa = geoRandom.sampleDouble(xL, yL);
			abaStore = ab = geoRandom.sampleDouble(xL, yL+1);
			baaStore = ba = geoRandom.sampleDouble(xL+1, yL);
			bbaStore = bb = geoRandom.sampleDouble(xL+1, yL+1);
			
			// record this grid coordinate as the last sampled
			xRecord = xL;
			yRecord = yL;
			zRecord = 0;
			sampledDimensions = 2;
		} else {
			// if the current grid value was the last sampled, then 
			// simply use the stored values
			aa = aaaStore;
			ab = abaStore;
			ba = baaStore;
			bb = bbaStore;
		}
		
		// -- interpolation -------------------------------------------------------------------------------------------
		// find the relative coordinates of the sample point inside the grid
		double xD = x - xL,  yD = y - yL;
		// interpolation function values
		double xInterpB = NoiseUtil.interpQuad(xD), yInterpB = NoiseUtil.interpQuad(yD);
		double xInterpA = 1.0 - xInterpB, yInterpA = 1.0 - yInterpB;
		
		// interpolate the grid values
		return (aa * xInterpA * yInterpA) + (ab * xInterpA * yInterpB) + 
			   (ba * xInterpB * yInterpA) + (bb * xInterpB * yInterpB);
	}
	
	public double sample(double x) {
		// scale values
		x /= frequency;
		
		// offset value
		x += xOffset;
		
		// -- grid values ---------------------------------------------------------------------------------------------
		//find the coordinates of the gridspace to be sampled
		long xL = (long)Math.floor(x);
		
		// sample values a = minus, b = plus
		double a, b;
		
		if (sampledDimensions < 1 || xL != xRecord || yRecord != 0 || zRecord != 0) {
			aaaStore = a = geoRandom.sampleDouble(xL);
			baaStore = b = geoRandom.sampleDouble(xL+1);
			
			xRecord = xL;
			yRecord = 0;
			zRecord = 0;
			sampledDimensions = 1;
		} else {
			// if the current grid value was the last sampled, then 
			// simply use the stored values
			a = aaaStore;
			b = baaStore;
		}
		
		// -- interpolation -------------------------------------------------------------------------------------------
		// find the relative coordinates of the sample point inside the grid
		double xD = x - xL;
		// interpolation function values
		double xInterpB = NoiseUtil.interpQuad(xD);
		double xInterpA = xInterpB - 1.0;
		
		return (a * xInterpA) + (b * xInterpB);
	}	
}
