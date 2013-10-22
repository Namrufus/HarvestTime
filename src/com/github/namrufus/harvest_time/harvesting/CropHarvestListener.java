package com.github.namrufus.harvest_time.harvesting;

import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import com.github.namrufus.harvest_time.configuration.crop.CropSeasonalGrowthConfiguration;

public class CropHarvestListener implements Listener {
	public CropHarvestListener() {	
	}
	
	@EventHandler
	public void onBlockBreakEvent(BlockBreakEvent event) {
		event.setCancelled(true);
		event.getBlock().setType(Material.AIR);
		event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), new ItemStack(Material.OBSIDIAN));
		
//		Material material = event.getBlock().getType();
//		
//		List<ItemStack> dropList = CropYeildUtil.getYeild(material, 5, 0.5, true);
//		
//		if (dropList == null)
//			return;
//		
//		event.getBlock().setType(Material.AIR);
//		event.setCancelled(true);
//		
//		for (ItemStack item : dropList) {
//			event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), item);
//		}
	}
}
