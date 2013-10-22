package com.github.namrufus.harvest_time.regional.noise;

import java.util.Random;

// This class is an implementation of Voronoi noise

// the noise region is broken up into a grid of cells of unit size (in cell
// coordinates) each containing a 'point'.

// the region may be sampled for the closest 'point' to the sample point.

public class Voronoi {
	public class Cell {
		private long seed;
		private double x, y;
		private int cellX, cellY;
		
		public Cell(long seed, double x, double y, int cellX, int cellY) {
			this.seed = seed;
			this.x = x;
			this.y = y;
			
			this.cellX = cellX;
			this.cellY = cellY;
		}
		
		// ------------------------------------------------------------------------------------------------------------
		public long getSeed() { return seed; }
		public double getX() { return x; }
		public double getY() { return y; }
		
		// ------------------------------------------------------------------------------------------------------------
		public int hashCode() {
			return (int)seed;
		}
		
		public boolean equals(Object obj) {
			if (! (obj instanceof Cell))
				return false;
			
			Cell cell = (Cell)obj;
			
			return cellX == cell.cellX && cellY == cell.cellY;
		}
	}
	
	// ================================================================================================================
	private GeoRandom random;

	private double xOffset, yOffset;
	private double scale;
	
	public Voronoi(double cellSize, Random seed) {
		scale = 1.0 / cellSize;
		
		random = new GeoRandom(seed.nextLong());
		
		xOffset = seed.nextDouble();
		yOffset = seed.nextDouble();
	}
	
	// ================================================================================================================
	public Cell sample(double x, double y) {
		x = x * scale + xOffset;
		y = y * scale + yOffset;
		int xc = (int)Math.floor(x), yc = (int)Math.floor(y);
		
		// iterate over the 9 closest cells to find the closest point
		double minDistanceSquared = -1.0;
		Cell closestCell = null;
		
		for (int xs = xc - 1; xs <= xc + 1; xs++) {
			for (int ys = yc - 1; ys <= yc + 1; ys++) {
				Random cellRandom = new Random(random.sampleLong(xs, ys));
				
				double cellX = ((double)xs + cellRandom.nextDouble());
				double cellY = ((double)ys + cellRandom.nextDouble());
				long cellSeed = cellRandom.nextLong();
				double distanceSquared = Math.pow(x - cellX, 2) + Math.pow(y - cellY, 2);
				
				cellX = (cellX - xOffset) / scale;
				cellY = (cellY - yOffset) / scale;
				
				if (closestCell == null || distanceSquared < minDistanceSquared) {
					closestCell = new Cell(cellSeed, cellX, cellY, xs, ys);
					minDistanceSquared = distanceSquared;
				}
			}
		}
		
		return closestCell;
	}
}

