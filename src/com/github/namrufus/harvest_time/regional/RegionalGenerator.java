package com.github.namrufus.harvest_time.regional;

import java.util.Random;

import org.bukkit.Location;

import com.github.namrufus.harvest_time.configuration.RegionalConfiguration;
import com.github.namrufus.harvest_time.configuration.region.RegionState;
import com.github.namrufus.harvest_time.regional.noise.Noise;
import com.github.namrufus.harvest_time.regional.noise.Voronoi;

public class RegionalGenerator {
	private RegionalConfiguration regionalConfiguration;
	
	private Voronoi voronoi;
	private Noise nutrientNoise;
	private Noise phNoise;
	private Noise compactnessNoise;
	
	public RegionalGenerator(RegionalConfiguration regionalConfiguration) {
		this.regionalConfiguration = regionalConfiguration;
		
		Random random = new Random(regionalConfiguration.getSeed().hashCode());
		
		voronoi = new Voronoi(regionalConfiguration.getCellSize(), random);
		
		double frequency = regionalConfiguration.getVariationSize();
		nutrientNoise = new Noise(random, frequency);
		phNoise = new Noise(random, frequency);
		compactnessNoise = new Noise(random, frequency);
	}
	
	// ================================================================================================================
	public RegionState.Nutrients getNutrientState(Location location) {
		Voronoi.Cell cell = voronoi.sample(location.getX(), location.getZ());
		
		double value = nutrientNoise.sample(cell.getX(), cell.getY());
		
		if (value < 0.25)
			return RegionState.Nutrients.VERY_FOUL;
		else if (value < 0.5)
			return RegionState.Nutrients.FOUL;
		else if (value < 0.75)
			return RegionState.Nutrients.FAIR;
		else
			return RegionState.Nutrients.VERY_FAIR;
	}
	public RegionState.Ph getPhState(Location location) {
		Voronoi.Cell cell = voronoi.sample(location.getX(), location.getZ());
		
		double value = phNoise.sample(cell.getX(), cell.getY());
		
		if (value < 0.15)
			return RegionState.Ph.VERY_BASIC;
		else if (value < 0.35)
			return RegionState.Ph.BASIC;
		else if (value < 0.45)
			return RegionState.Ph.NEUTRAL;
		else if (value < 0.55)
			return RegionState.Ph.VERY_NEUTRAL;
		else if (value < 0.65)
			return RegionState.Ph.NEUTRAL;
		else if (value < 0.85)
			return RegionState.Ph.ACIDIC;
		else
			return RegionState.Ph.VERY_ACIDIC;

	}
	public RegionState.Compactness getCompactnessState(Location location) {
		Voronoi.Cell cell = voronoi.sample(location.getX(), location.getZ());
		
		double value = compactnessNoise.sample(cell.getX(), cell.getY());
		
		if (value < 0.25)
			return RegionState.Compactness.VERY_LOOSE;
		else if (value < 0.5)
			return RegionState.Compactness.LOOSE;
		else if (value < 0.75)
			return RegionState.Compactness.COMPACT;
		else
			return RegionState.Compactness.VERY_COMPACT;
	}
}
