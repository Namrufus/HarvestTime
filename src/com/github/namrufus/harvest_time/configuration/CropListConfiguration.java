package com.github.namrufus.harvest_time.configuration;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.bukkit.Material;
import org.bukkit.TreeType;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;

import com.github.namrufus.harvest_time.configuration.crop.CropConfiguration;
import com.github.namrufus.harvest_time.configuration.util.ConfigUtil;

public class CropListConfiguration {
	private Map<Material, CropConfiguration> blockGrowthConfigurations;
	private Map<TreeType, CropConfiguration> treeGrowthConfigurations;
	private Map<EntityType, CropConfiguration> breedingConfigurations;
	private CropConfiguration eggLayingConfiguration;
	private CropConfiguration eggHatchingConfiguration;
	private CropConfiguration fishingConfiguration;
	
	public CropListConfiguration(ConfigurationSection config, FreshWaterConfiguration freshWaterConfiguration, RegionalConfiguration regionalConfiguration, BiomeAliasesConfiguration biomeAliases, Logger log) {
		blockGrowthConfigurations = new HashMap<Material, CropConfiguration>();
		treeGrowthConfigurations = new HashMap<TreeType, CropConfiguration>();
		breedingConfigurations = new HashMap<EntityType, CropConfiguration>();
		
		eggLayingConfiguration = null;
		eggHatchingConfiguration = null;
		fishingConfiguration = null;
		
		for (String cropName : config.getKeys(false)) {
			ConfigurationSection cropConfig = config.getConfigurationSection(cropName);
			CropConfiguration cropConfiguration = new CropConfiguration(cropConfig, freshWaterConfiguration, regionalConfiguration, biomeAliases, log);
			
			if (cropName.startsWith("block_")) {
				String blockMaterialName = cropName.replaceFirst("block_", "");
				Material blockMaterial = ConfigUtil.enumFromString(Material.class, blockMaterialName);
				
				if (blockMaterial == null) {
					log.warning("Config: can't recognize block crop "+blockMaterialName+". Skipping.");
					continue;
				}
				
				blockGrowthConfigurations.put(blockMaterial, cropConfiguration);
				
			} else if (cropName.startsWith("tree_")) {
				String treeName = cropName.replaceFirst("tree_", "");
				TreeType treeType = ConfigUtil.enumFromString(TreeType.class, treeName);
				
				if (treeType == null) {
					log.warning("Config: can't recognize tree type "+treeName+". Skipping.");
					continue;
				}
				
				treeGrowthConfigurations.put(treeType, cropConfiguration);
				
			} else if (cropName.startsWith("breeding_")) {
				String entityName = cropName.replaceFirst("breeding_", "");
				EntityType entityType = ConfigUtil.enumFromString(EntityType.class, entityName);
				
				if (entityType == null) {
					log.warning("Config: can't recognize entity type "+entityName+". Skipping.");
					continue;
				}
				
				breedingConfigurations.put(entityType, cropConfiguration);
				
			} else if (cropName.equals("egg_laying")) {
				eggLayingConfiguration = cropConfiguration;
			} else if (cropName.equals("egg_hatching")) {
				eggHatchingConfiguration = cropConfiguration;
			} else if (cropName.equals("fishing")) {
				fishingConfiguration = cropConfiguration;
			}
		}
	}
	
	// ================================================================================================================
	public boolean configuresBlockGrowth(Material material) { return blockGrowthConfigurations.containsKey(material); }
	public CropConfiguration getBlockGrowthConfiguration(Material material) { return blockGrowthConfigurations.get(material); }
	
	public boolean configuresTreeGrowth(TreeType treeType) { return treeGrowthConfigurations.containsKey(treeType); }
	public CropConfiguration getTreeGrowthConfigurations(TreeType treeType) { return treeGrowthConfigurations.get(treeType); }
	
	public boolean configuresBreeding(EntityType entityType) { return breedingConfigurations.containsKey(entityType); }
	public CropConfiguration getBreedingConfiguration(EntityType entityType) { return breedingConfigurations.get(entityType); }
	
	public boolean configuresEggLaying() { return eggLayingConfiguration != null; }
	public CropConfiguration getEggLayingConfiguration() { return eggLayingConfiguration; }
	
	public boolean configuresEggHatching() { return eggHatchingConfiguration != null; }
	public CropConfiguration getEggHatchingConfiguration() { return eggHatchingConfiguration; }
	
	public boolean configuresFishing() { return fishingConfiguration != null; }
	public CropConfiguration getFishingConfiguration() { return fishingConfiguration; }
	
	// ----------------------------------------------------------------------------------------------------------------
	public void dump(Logger log) {
		log.info("");
		log.info("CropsConfiguration:");
		
		log.info("");
		log.info("-----------------------------------------------");
		log.info("blockGrowth:");
		log.info("");
		
		for (Material blockMaterial : blockGrowthConfigurations.keySet()) {
			log.info("");
			log.info(blockMaterial+":");
			blockGrowthConfigurations.get(blockMaterial).dump(log);
		}
		
		log.info("");
		log.info("-----------------------------------------------");
		log.info("treeGrowth:");
		log.info("");
		
		for (TreeType treeType : treeGrowthConfigurations.keySet()) {
			log.info("");
			log.info(treeType+":");
			treeGrowthConfigurations.get(treeType).dump(log);
		}
		
		log.info("");
		log.info("-----------------------------------------------");
		log.info("breeding:");
		log.info("");
		
		for (EntityType entityType : breedingConfigurations.keySet()) {
			log.info("");
			log.info(entityType+":");
			breedingConfigurations.get(entityType).dump(log);
		}
		
		log.info("");
		log.info("-----------------------------------------------");
		log.info("");
		
		log.info("");
		
		log.info("eggLaying: " + configuresEggLaying());
		if (configuresEggLaying())
			eggLayingConfiguration.dump(log);
		
		log.info("");
		
		log.info("eggHatching: " + configuresEggHatching());
		if (configuresEggHatching())
			eggHatchingConfiguration.dump(log);
		
		log.info("");
		
		log.info("fishing: " + configuresFishing());
		if (configuresFishing())
			fishingConfiguration.dump(log);
	}
}
