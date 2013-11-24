package com.github.namrufus.harvest_time.crop_growth.environment.global.region;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import org.bukkit.Location;

import com.github.namrufus.harvest_time.crop_growth.environment.global.region.type.RegionState;


public class RegionSamplerUtil {
	private static interface Sampler {
		public Color sample(double x, double y);
	}
	
	private static Color alphaBlend(Color base, Color overlay, float alpha) {
		float alphaInv = 1.0f - alpha;
		
		float baseR = alphaInv * base.getRed() / 256.0f;
		float baseG = alphaInv * base.getGreen() / 256.0f;
		float baseB = alphaInv * base.getBlue() / 256.0f;
		
		float overlayR = alpha * overlay.getRed() / 256.0f;
		float overlayG = alpha * overlay.getGreen() / 256.0f;
		float overlayB = alpha * overlay.getBlue() / 256.0f;
		
		return new Color(baseR + overlayR, baseG + overlayG, baseB + overlayB);
	}
	
	// ================================================================================================================
	private static void sampleImage(int imageSize, double scale, Sampler sampler, File outputfile, Logger log) {
		log.info("Initializing Image...");
		BufferedImage image = new BufferedImage(imageSize, imageSize, BufferedImage.TYPE_INT_ARGB);
		
		log.info("Sampling Image...");
		long time = System.nanoTime();
		for (int x = 0; x < imageSize; x++) {
			for (int y = 0; y < imageSize; y++) {
				double xBlock = (x - imageSize/2.0) * scale;
				double yBlock = (y - imageSize/2.0) * scale;
				
				Color color = sampler.sample(xBlock, yBlock);

				// grid lines for kilometer grids
				if (Math.floor((xBlock + scale) / 1000.0) != Math.floor(xBlock / 1000.0)) {
					if (Math.floor((xBlock + scale) / 1000.0) == 0)
						color = color.brighter();
					else
						color = color.darker();
				}
				if (Math.floor((yBlock + scale) / 1000.0) != Math.floor(yBlock / 1000.0)) {
					if (Math.floor((yBlock + scale) / 1000.0) == 0)
						color = color.brighter();
					else
						color = color.darker();
				}
				
				// darken stuff outside of a circle the size of the image
				double distanceSquared = xBlock * xBlock + yBlock * yBlock;
				double radius = imageSize * scale / 2.0;
				if (distanceSquared > radius * radius)
					color = color.darker().darker();
				
				image.setRGB(x, y, color.getRGB());
			}
		}
		
		double elapsedTimeMS = (double)(System.nanoTime() - time) / 1.0e6;
		log.info((imageSize * imageSize) + " samples in "+elapsedTimeMS+" milliseconds.");
		log.info(1000.0 * ((double)(imageSize * imageSize) / elapsedTimeMS) + " samples per second");
		
		log.info("Writing Image... " + outputfile.getAbsolutePath());
		try {
		    ImageIO.write(image, "png", outputfile);
		} catch (IOException e) {
		    e.printStackTrace();
		}
		log.info("Writing Image Complete.");
	}
	
	// ================================================================================================================
	private static class CompactnessSampler implements Sampler {
		RegionGenerator regionalGenerator;
		
		public CompactnessSampler(RegionGenerator regionalGenerator) {
			this.regionalGenerator = regionalGenerator;
		}
		
		public Color sample(double x, double y) {
			Location location = new Location(null, x, 0, y);
			
			Color result = Color.BLACK;
			
			// compactness
			RegionState.Compactness compactnessState = regionalGenerator.getCompactnessState(location);
			if (compactnessState == RegionState.Compactness.VERY_COMPACT)
				result = alphaBlend(result, Color.ORANGE, 0.5f);
			if (compactnessState == RegionState.Compactness.COMPACT)
				result = alphaBlend(result, Color.ORANGE, 0.25f);
			if (compactnessState == RegionState.Compactness.LOOSE)
				result = alphaBlend(result, Color.MAGENTA, 0.25f);
			if (compactnessState == RegionState.Compactness.VERY_LOOSE)
				result = alphaBlend(result, Color.MAGENTA, 0.5f);
			
			return result;
		}
	}
	private static class PhSampler implements Sampler {
		RegionGenerator regionalGenerator;
		
		public PhSampler(RegionGenerator regionalGenerator) {
			this.regionalGenerator = regionalGenerator;
		}
		
		public Color sample(double x, double y) {
			Location location = new Location(null, x, 0, y);
			
			Color result = Color.BLACK;

			// ph
			RegionState.Ph phState = regionalGenerator.getPhState(location);
			if (phState == RegionState.Ph.VERY_BASIC)
				result = alphaBlend(result, Color.BLUE, 0.5f);
			if (phState == RegionState.Ph.BASIC)
				result = alphaBlend(result, Color.BLUE, 0.25f);
			if (phState == RegionState.Ph.NEUTRAL)
				result = alphaBlend(result, Color.YELLOW, 0.25f);
			if (phState == RegionState.Ph.VERY_NEUTRAL)
				result = alphaBlend(result, Color.YELLOW, 0.5f);
			if (phState == RegionState.Ph.ACIDIC)
				result = alphaBlend(result, Color.RED, 0.25f);
			if (phState == RegionState.Ph.VERY_ACIDIC)
				result = alphaBlend(result, Color.RED, 0.5f);
			
			return result;
		}
	}
	private static class NutrientsSampler implements Sampler {
		RegionGenerator regionalGenerator;
		
		public NutrientsSampler(RegionGenerator regionalGenerator) {
			this.regionalGenerator = regionalGenerator;
		}
		
		public Color sample(double x, double y) {
			Location location = new Location(null, x, 0, y);
			
			Color result = Color.BLACK;
			
			// nutrients
			RegionState.Nutrients nutrientState = regionalGenerator.getNutrientState(location);
			if (nutrientState == RegionState.Nutrients.VERY_FAIR)
				result = alphaBlend(result, Color.CYAN, 0.5f);
			if (nutrientState == RegionState.Nutrients.FAIR)
				result = alphaBlend(result, Color.CYAN, 0.25f);
			if (nutrientState == RegionState.Nutrients.FOUL)
				result = alphaBlend(result, Color.GREEN, 0.25f);
			if (nutrientState == RegionState.Nutrients.VERY_FOUL)
				result = alphaBlend(result, Color.GREEN, 0.5f);
			
			return result;
		}
	}
	
	public static void sampleCompactnessImage(int imageSize, double scale, RegionGenerator regionalGenerator, File outputFile, Logger log) {
		Sampler sampler = new CompactnessSampler(regionalGenerator);
		sampleImage(imageSize, scale, sampler, outputFile, log);
	}
	public static void samplePhImage(int imageSize, double scale, RegionGenerator regionalGenerator, File outputFile, Logger log) {
		Sampler sampler = new PhSampler(regionalGenerator);
		sampleImage(imageSize, scale, sampler, outputFile, log);
	}
	public static void sampleNutrientsImage(int imageSize, double scale, RegionGenerator regionalGenerator, File outputFile, Logger log) {
		Sampler sampler = new NutrientsSampler(regionalGenerator);
		sampleImage(imageSize, scale, sampler, outputFile, log);
	}
}
