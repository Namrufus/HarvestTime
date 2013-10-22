package com.github.namrufus.harvest_time.configuration.region;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.bukkit.configuration.ConfigurationSection;

public class RegionConfig {
	List<RegionPreference> preferences;
	List<RegionState> states;
	
	boolean enabled;
	
	double regionSize;
	double variationScale;
	
	Map<RegionPreference, Map<RegionState, Double>> multipliers;
	
	public RegionConfig(List<RegionPreference> preferences, List<RegionState> states, ConfigurationSection config, Logger LOG) {
		this.preferences = preferences;
		this.states = states;
		
		enabled = config.getBoolean("enabled");
		
		regionSize = config.getDouble("region_size");
		variationScale = config.getDouble("variation_scale");
		
		multipliers = new HashMap<RegionPreference, Map<RegionState, Double>>();
		ConfigurationSection multipliersConfig = config.getConfigurationSection("multipliers");
		for (String preferenceName : multipliersConfig.getKeys(false)) {
			RegionPreference preference = matchPreference(preferenceName);
			if (preference == null) {
				LOG.warning("Config: can't recognize preference name: "+preferenceName+". Skipping.");
				continue;
			}
			
			Map<RegionState, Double> stateMultipliers = new HashMap<RegionState, Double>();
			
			for (String stateName : multipliersConfig.getConfigurationSection(preferenceName).getKeys(false)) {
				RegionState state = matchState(stateName);
				if (state == null) {
					LOG.warning("Config: can't recognize state name: "+stateName+". Skipping.");
					continue;
				}
				
				stateMultipliers.put(state, multipliersConfig.getDouble(preferenceName+"."+stateName));
			}
			
			multipliers.put(preference, stateMultipliers);
		}
	}
	
	// ================================================================================================================
	private RegionPreference matchPreference(String preferenceName) {
		for (RegionPreference preference : preferences) {
			if (preference.toString().equals(preferenceName))
				return preference;
		}
		return null;
	}
	private RegionState matchState(String stateName) {
		for (RegionState state : states) {
			if (state.toString().equals(stateName))
				return state;
		}
		return null;
	}
	
	// ----------------------------------------------------------------------------------------------------------------
	public boolean isEnabled() { return enabled; }
	public double getRegionSize() { return regionSize; }
	public double getVariationScale() { return variationScale; }
	
	// ----------------------------------------------------------------------------------------------------------------
	public double getMultiplier(RegionPreference preference, RegionState state) {
		if (!enabled)
			return 1.0;
		
		if (!multipliers.containsKey(preference))
			return 1.0;
		
		if (!multipliers.get(preference).containsKey(state))
			return 1.0;
		
		return multipliers.get(preference).get(state);
	}
	
	// ----------------------------------------------------------------------------------------------------------------
	public void dump(Logger log) {
		log.info("    enabled: "+enabled);
		log.info("    regionSize: "+regionSize);
		log.info("    variationScale: "+variationScale);
		log.info("    multipliers:");
		for (RegionPreference preference : multipliers.keySet()) {
			log.info("      " + preference + ":");
			for (RegionState state : multipliers.get(preference).keySet()) {
				log.info("        " + state + ": " + multipliers.get(preference).get(state));
			}
		}
		
	}
}
