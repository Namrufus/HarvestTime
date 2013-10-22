package com.github.namrufus.harvest_time.growth;

import java.util.Map;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import com.github.namrufus.harvest_time.configuration.crop.CropSeasonalGrowthConfiguration;
import com.github.namrufus.harvest_time.seasonal.SeasonalCalendar;

public class GrowthStageInformationListener implements Listener {
	private Material checkItemMaterial;
	Map<Material, CropSeasonalGrowthConfiguration> crops;
	SeasonalCalendar calendar;
	
	public GrowthStageInformationListener(Map<Material, CropSeasonalGrowthConfiguration> crops, SeasonalCalendar calendar) {
		checkItemMaterial = Material.WATCH;
		
		this.crops = crops;
		this.calendar = calendar;
	}
	
	@EventHandler
	public void onPlayerInteractEvent(PlayerInteractEvent event) {
		Block block = event.getClickedBlock();
		
		// Ensure that the player is right-clicking with a clock on a valid crop type
		// Hoe using right click
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			Material blockMaterial = block.getType();
			// Watch on crop
			if (crops.containsKey(blockMaterial)) {
				Material toolMaterial = event.getMaterial();
				// match check item material
				if (toolMaterial != null/*nothing in hand*/ && toolMaterial == checkItemMaterial) {
					Player player = event.getPlayer();
					CropSeasonalGrowthConfiguration cropGrowthConfiguration = crops.get(blockMaterial);
					
					int growthStage = block.getData();
					
					int seasonalDay = (int)calendar.getSeasonalDay();
					int targetStage = cropGrowthConfiguration.getTargetStage(seasonalDay);
					int stageCount = GrowthUtil.getStageCount(blockMaterial);
					boolean allowedToGrow = cropGrowthConfiguration.isAllowedToGrow(growthStage, seasonalDay);
					
					player.sendMessage("§7" + "[Harvest Time] Crop: "+blockMaterial.toString());
					player.sendMessage("§7" + "[Harvest Time] Year: "+calendar.getSeasonalYear()+", Day: "+seasonalDay);
					player.sendMessage("§7" + "[Harvest Time] Current Growth Stage: " + growthStage + "/" + (stageCount-1));
					player.sendMessage("§7" + "[Harvest Time] Target Growth Stage: " + targetStage + "/" + (stageCount-1));
					if (allowedToGrow) {
						player.sendMessage("§7" + "[Harvest Time] Crop can grow within next §9" + "XYZ" + "§7 days. Use bonemeal.");
					} else if (cropGrowthConfiguration.isAtTargetStage(growthStage, seasonalDay)) {
						player.sendMessage("§7" + "[Harvest Time] Crop at target growth. Next growth in §9" + "XYZ" + "§7 days");
					} else {
						player.sendMessage("§7" + "[Harvest Time] §4Crop cannot grow.");
					}
				}
			}
		}
	}
}
