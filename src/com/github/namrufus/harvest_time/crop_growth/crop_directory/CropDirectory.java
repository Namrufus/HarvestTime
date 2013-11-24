package com.github.namrufus.harvest_time.crop_growth.crop_directory;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;

import com.github.namrufus.harvest_time.util.ConfigUtil;

public class CropDirectory<K> {
	protected static enum CropType {
		BLOCK, TREE, BREEDING, EGG_HATCHING, FISHING;
	}
	protected static class CropIdentifier {
		private CropType type;
		private Object payload;
		public CropIdentifier(CropType type, Object payload) {
			this.type = type;
			this.payload = payload;
		}
		public CropIdentifier(CropType type) {
			this.type = type;
			payload = null;
		}
		
		//-------------------------------------------------------------------------------------------------------------
		public boolean equals(Object object) {
			if (object == this)
				return true;
			
			if (!(object instanceof CropIdentifier))
					return false;
			
			CropIdentifier other = (CropIdentifier) object;
			if (other.type != this.type)
				return false;
			
			if (this.payload == null && other.payload == null)
				return true;
			if (this.payload == null || other.payload == null)
				return false;
			return this.payload.equals(other.payload);
		}
		
		public int hashCode() {
			if (payload != null)
				return payload.hashCode();
			else
				return 0;
		}
		
		public String toString() {
			return type.toString() + "_" + (payload != null ? payload : "");
		}
	}
	
	// ================================================================================================================
	private Map<CropIdentifier, K> crops;
	
	public CropDirectory() {
		crops = new HashMap<CropIdentifier, K>();
	}
	
	// ================================================================================================================
	protected Map<CropIdentifier, K> getCrops() {
		return crops;
	}
	
	// ----------------------------------------------------------------------------------------------------------------
	private CropIdentifier parseIdentifier(String cropName) {
		if (cropName.startsWith("block_")) {
			String blockMaterialName = cropName.replaceFirst("block_", "");
			Material blockMaterial = ConfigUtil.enumFromString(Material.class, blockMaterialName);
			
			if (blockMaterial == null)
				return null;
			
			return new CropIdentifier(CropType.BLOCK, blockMaterial);
		} else if (cropName.startsWith("tree_")) {
			String treeName = cropName.replaceFirst("tree_", "");
			TreeGrowthType treeGrowthType = ConfigUtil.enumFromString(TreeGrowthType.class, treeName);
			
			if (treeGrowthType == null)
				return null;
			
			return new CropIdentifier(CropType.TREE, treeGrowthType);
		} else if (cropName.startsWith("breeding_")) {
			String entityName = cropName.replaceFirst("breeding_", "");
			EntityType entityType = ConfigUtil.enumFromString(EntityType.class, entityName);
			
			if (entityType == null)
				return null;
			
			return new CropIdentifier(CropType.BREEDING, entityType);
		} else if (cropName.equals("egg_hatching")) {
			return new CropIdentifier(CropType.EGG_HATCHING);
		} else if (cropName.equals("fishing")) {
			return new CropIdentifier(CropType.FISHING);
		}
		
		return null;
	}
	
	// ----------------------------------------------------------------------------------------------------------------
	private CropIdentifier blockIdentifier(Material blockMaterial) { return new CropIdentifier(CropType.BLOCK, blockMaterial); }
	private CropIdentifier treeIdentifier(TreeGrowthType treeGrowthType) { return new CropIdentifier(CropType.TREE, treeGrowthType); }
	private CropIdentifier breedingIdentifier(EntityType entityType) { return new CropIdentifier(CropType.BREEDING, entityType); }
	private CropIdentifier eggHatchingIdentifier() { return new CropIdentifier(CropType.EGG_HATCHING); }
	private CropIdentifier fishingIdentifier() { return new CropIdentifier(CropType.FISHING); }
	
	// ----------------------------------------------------------------------------------------------------------------
	public boolean containsBlockCrop(Material material) { return crops.containsKey(blockIdentifier(material)); }
	public boolean containsTreeCrop(TreeGrowthType treeGrowthType) { return crops.containsKey(treeIdentifier(treeGrowthType)); }
	public boolean containsBreedingCrop(EntityType entityType) { return crops.containsKey(breedingIdentifier(entityType)); }
	public boolean containsEggHatchingCrop() { return crops.containsKey(eggHatchingIdentifier()); }
	public boolean containsFishingCrop() { return crops.containsKey(fishingIdentifier()); }
	
	// ----------------------------------------------------------------------------------------------------------------
	public boolean canParse(String cropName) {
		CropIdentifier identifier = parseIdentifier(cropName);
		return identifier != null;
	}
	
	// ----------------------------------------------------------------------------------------------------------------	
	public boolean put(String cropName, K value) {
		CropIdentifier identifier = parseIdentifier(cropName);
		
		if (identifier == null)
			return false;
		
		crops.put(identifier, value);
		return true;
	}
	
	public void putBlockCrop(Material material, K value) { crops.put(blockIdentifier(material), value); }
	public void putTreeCrop(TreeGrowthType treeGrowthType, K value) { crops.put(treeIdentifier(treeGrowthType), value); }
	public void putBreedingCrop(EntityType entityType, K value) { crops.put(breedingIdentifier(entityType), value); }
	public void putEggHatchingCrop(K value) { crops.put(eggHatchingIdentifier(), value); }
	public void putFishingCrop(K value) { crops.put(fishingIdentifier(), value); }
	
	// ----------------------------------------------------------------------------------------------------------------
	public K get(String cropName) {
		CropIdentifier identifier = parseIdentifier(cropName);
		
		if (identifier == null)
			return null;
		
		return crops.get(identifier);
	}
	
	public K getBlockCrop(Material material) { return crops.get(blockIdentifier(material)); }
	public K getTreeCrop(TreeGrowthType treeGrowthType) { return crops.get(treeIdentifier(treeGrowthType)); }
	public K getBreedingCrop(EntityType entityType) { return crops.get(breedingIdentifier(entityType)); }
	public K getEggHatchingCrop() { return crops.get(eggHatchingIdentifier()); }
	public K getFishingCrop() { return crops.get(fishingIdentifier()); }
}
