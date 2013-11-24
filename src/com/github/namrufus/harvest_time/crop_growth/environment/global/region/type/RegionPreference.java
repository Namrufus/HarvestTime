package com.github.namrufus.harvest_time.crop_growth.environment.global.region.type;

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
