package com.github.namrufus.harvest_time.regional.region;

public interface RegionPreference {
	public enum Nutrients implements RegionPreference {
		NONE, FAIR, FOUL;
	}
	public enum Ph implements RegionPreference {
		NONE, BASIC, NEUTRAL, ACIDIC;
	}
	public enum Compactness implements RegionPreference {
		NONE, LOOSE, COMPACT;
	}
}
