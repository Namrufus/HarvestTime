package com.github.namrufus.harvest_time.regional.region;

public interface RegionState {
	public enum Nutrients implements RegionState {
		VERY_FOUL, FOUL, FAIR, VERY_FAIR;
	}
	public enum Ph implements RegionState {
		VERY_BASIC, BASIC, NEUTRAL, VERY_NEUTRAL, ACIDIC, VERY_ACIDIC;
	}
	public enum Compactness implements RegionState {
		VERY_LOOSE, LOOSE, COMPACT, VERY_COMPACT;
	}
}
