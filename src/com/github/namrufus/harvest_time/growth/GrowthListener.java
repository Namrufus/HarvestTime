package com.github.namrufus.harvest_time.growth;

import java.util.Map;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import com.github.namrufus.harvest_time.configuration.crop.CropSeasonalGrowthConfiguration;
import com.github.namrufus.harvest_time.seasonal.SeasonalCalendar;

public class GrowthListener implements Listener {
	private int bonemealRadius;
	
	Map<Material, CropSeasonalGrowthConfiguration> crops;
	SeasonalCalendar calendar;
	
	public GrowthListener(Map<Material, CropSeasonalGrowthConfiguration> crops, SeasonalCalendar calendar) {
		bonemealRadius = 5;
		
		this.crops = crops;
		this.calendar = calendar;
	}
	
	// handle events that turn dirt into farmland in order to extend the time it takes to create a farmland block
	// and to require the presence certain "fertilizer" blocks in order to create that block
	@EventHandler
	public void onPlayerInteractEvent(PlayerInteractEvent event) {
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			ItemStack toolItem = event.getItem();
			
			// stick
			if (toolItem.getType() == Material.STICK) {
				System.out.println("right click with a stick!");
				
				Block block = event.getClickedBlock();
				Material blockMaterial = block.getType();
				
				if (crops.containsKey(blockMaterial)) {
					int y = block.getY();
					World world = block.getWorld();
					
					int seasonalDay = (int)calendar.getSeasonalDay();
					
					for (int x = block.getX() - bonemealRadius; x <= block.getX() + bonemealRadius; x++) {
						for (int z = block.getZ() - bonemealRadius; z <= block.getZ() + bonemealRadius; z++) {
							updateGrowth(world, x, y, z, seasonalDay);
						}
					}
				}
			}
		}
	}
	
	private void updateGrowth(World world, int x, int y, int z, int seasonalDay) {
		Block block = world.getBlockAt(x, y, z);
		if (block == null)
			return;
		
		Material blockMaterial = block.getType();
		
		if (crops.containsKey(blockMaterial)) {
			CropSeasonalGrowthConfiguration cropGrowthConfiguration = crops.get(blockMaterial);
			
			int currentStage = GrowthUtil.getStage(block);
			
			if (cropGrowthConfiguration.isAllowedToGrow(currentStage, seasonalDay)) {
				int targetStage = cropGrowthConfiguration.getTargetStage(seasonalDay);
				GrowthUtil.setStage(block, targetStage);
			}
		}
	}
}
