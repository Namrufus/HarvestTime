package com.github.namrufus.harvest_time.crop_growth.seasonal_growth;

import java.util.Date;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import com.github.namrufus.harvest_time.crop_growth.environment.global.region.RegionGenerator;
import com.github.namrufus.harvest_time.crop_growth.seasonal.util.CropYieldUtil;
import com.github.namrufus.harvest_time.crop_growth.seasonal.util.GrowthUtil;
import com.github.namrufus.harvest_time.plugin.InteractionConfiguration;
import com.github.namrufus.harvest_time.plugin.PlayerInteractionDelayer;
import com.github.namrufus.harvest_time.seasonal.SeasonalCalendar;

public class SeasonalGrowthListener implements Listener {
	InteractionConfiguration interactionConfiguration;
	TendingConfiguration tendingConfiguration;
	SeasonalCropListConfiguration seasonalCropList;
	RegionGenerator regionalGenerator;
	SeasonalCalendar calendar;
	PlayerInteractionDelayer playerInteractionDelayer;
	
	public SeasonalGrowthListener(InteractionConfiguration interactionConfiguration,
			                              TendingConfiguration tendingConfiguration,
			                              SeasonalCropListConfiguration seasonalCropList,
			                              RegionGenerator regionalGenerator,
			                              SeasonalCalendar calendar,
			                              PlayerInteractionDelayer playerTimerSystem) {
		this.interactionConfiguration = interactionConfiguration;
		this.tendingConfiguration = tendingConfiguration;
		this.seasonalCropList = seasonalCropList;
		this.regionalGenerator = regionalGenerator;
		this.calendar = calendar;
		this.playerInteractionDelayer = playerTimerSystem;
	}
	
	// ===============================================================================================================-
	@EventHandler
	public void onPlayerInteractEvent(PlayerInteractEvent event) {
		Block block = event.getClickedBlock();
		
		// Ensure that the player is right-clicking
		if (event.getAction() != Action.RIGHT_CLICK_BLOCK)
			return;
		
		Material blockMaterial = block.getType();
		
		// ensure that the block being clicked is a seasonal block crop type
		if (!seasonalCropList.containsBlockCrop(blockMaterial))
			return;
			
		Material toolMaterial = event.getMaterial();
		
		// the player must have something in his hand
		if (toolMaterial == null/*nothing in hand*/)
			return;
		
		// react differently depending on the item in the player's hand
		Player player = event.getPlayer();
		if (toolMaterial == interactionConfiguration.getCropGrowthCheckMaterial()) {
			growthCheckInteraction(player, block);
		} else if (toolMaterial == interactionConfiguration.getCropYieldCheckMaterial()) {
			yieldCheckInteraction(player, block);
		} else if (tendingConfiguration.isTendingTool(toolMaterial)) {
			cropTendInteraction(player, toolMaterial, block);
		}
	}
	
	// ----------------------------------------------------------------------------------------------------------------
	// display information relating to the growth status of the block to the player.
	private void growthCheckInteraction(Player player, Block block) {
		Material blockMaterial = block.getType();
		CropSeasonalGrowthConfiguration cropGrowthConfiguration = seasonalCropList.getBlockCrop(blockMaterial);
				
		int growthStage = GrowthUtil.getStage(block);
				
		int seasonalDay = (int)calendar.getSeasonalDay();
		int stageCount = GrowthUtil.getStageCount(blockMaterial);
		int targetStage = cropGrowthConfiguration.getCappedTargetStage(seasonalDay, stageCount);
		boolean allowedToGrow = cropGrowthConfiguration.isAllowedToGrow(growthStage, seasonalDay);
				
		player.sendMessage("�7[Harvest Time] Crop: �8"+blockMaterial.toString());
		player.sendMessage("�7[Harvest Time]   Year: �8" + calendar.getSeasonalYear() + "�7, Day: " + "�8" + seasonalDay);
		player.sendMessage("�7[Harvest Time]   Current growth stage: �8"+ growthStage + "�7 / �8" + (stageCount-1));
		player.sendMessage("�7[Harvest Time]   Target growth stage: �8" + targetStage + "�7 / �8" + (stageCount-1));
		if (allowedToGrow) {
			int finalViableSeasonalDay = cropGrowthConfiguration.getFinalViableSeasonalDay(growthStage);
			
			System.out.println("final seasonal day = " + finalViableSeasonalDay);
			
			Date date = calendar.getSeasonalDayStart(calendar.getSeasonalYear(), finalViableSeasonalDay + 1);
			
			player.sendMessage("�7[Harvest Time]   �3Crop is viable!�7 Tend within: " + "�8" + formatTimeDifference(date) + " (" + date + ")");
		} else if (cropGrowthConfiguration.isAtTargetStage(growthStage, seasonalDay, stageCount)) {
			String message = "�7[Harvest Time]   �3Crop at target growth.";
			if (!(growthStage == stageCount - 1)) {
				Date date;
					if (growthStage == 0)
						date = calendar.getSeasonalDayStart(calendar.getSeasonalYear(), cropGrowthConfiguration.getStartDay() + 1);
					else
						date = calendar.nextSeasonalDayIncrement();
				message += "�7 Next target growth update: " + "�8" + formatTimeDifference(date) + "(" + date + ")";
			}
			player.sendMessage(message);
		} else {
			player.sendMessage("�7[Harvest Time]   �3Crop is not viable.");
		}		
	}
	
	// ----------------------------------------------------------------------------------------------------------------
	// display information relating to the yield of the crop to the given player
	private void yieldCheckInteraction(Player player, Block block) {
		Material blockMaterial = block.getType();
		CropSeasonalGrowthConfiguration cropGrowthConfiguration = seasonalCropList.getBlockCrop(blockMaterial);
		
		if (!cropGrowthConfiguration.hasCustomYield())
			return;
		
		double targetYield = cropGrowthConfiguration.getCustomYieldConfiguration().getYieldCount(block, regionalGenerator);
		
		player.sendMessage("�7" + "[Harvest Time] Crop: " + "�8"/*dark grey*/ + blockMaterial.toString());
		cropGrowthConfiguration.getCustomYieldConfiguration().displayState(player, block, regionalGenerator);
		player.sendMessage("�7" + "[Harvest Time]   �3Total Yield: " + "�b" + String.format("%.2f", targetYield));
	}
	
	// ----------------------------------------------------------------------------------------------------------------
	// update plant growth by "tending" the crop
	private void cropTendInteraction(Player player, Material toolMaterial, Block block) {
		Material blockMaterial = block.getType();
		CropSeasonalGrowthConfiguration cropGrowthConfiguration = seasonalCropList.getBlockCrop(blockMaterial);
		
		// delay event until the time determined by the type of tool
		double tendingTime = tendingConfiguration.getToolTendingTime(toolMaterial);
		if (!playerInteractionDelayer.canBreak(player, block, tendingTime, Math.min(tendingTime, 1.0)))
			return;
		
		int seasonalDay = (int)calendar.getSeasonalDay();
		int previousGrowth = GrowthUtil.getStage(block);
		
		// update the crop clicked
		updateGrowth(block, seasonalDay, false);
		
		int currentGrowth =  GrowthUtil.getStage(block);
		
		// handle custom yields on the transition to the final stage
		if (previousGrowth != currentGrowth && currentGrowth == GrowthUtil.getStageCount(blockMaterial) - 1) {
			
			
			if (cropGrowthConfiguration.hasCustomYield()) {
				double targetYield = cropGrowthConfiguration.getCustomYieldConfiguration().getYieldCount(block, regionalGenerator);
				
				if (CropYieldUtil.cropFailureSample(blockMaterial, targetYield)) {
					// the crop has failed, destroy the crop block
					// reduce the growth stage first, so that the yield is zero
					GrowthUtil.setStage(block, 0);
					block.breakNaturally();
				} else {				
					// if the crop did not fail, then there may be additional bonus yields
					List<ItemStack> bonusItemDrops = CropYieldUtil.sampleBonusYield(blockMaterial, targetYield);
					for (ItemStack item : bonusItemDrops) {
						block.getWorld().dropItemNaturally(block.getLocation(), item);
					}
				}
			}
		}
		
		// update crops in a radius around the updated block, excluding crops that are transitioning to the final stage
		int y = block.getY();
		int radius = tendingConfiguration.getRadius();
		for (int x = block.getX() - radius; x <= block.getX() + radius; x++) {
			for (int z = block.getZ() - radius; z <= block.getZ() + radius; z++) {
				updateGrowth(block.getWorld(), x, y, z, seasonalDay, true);
			}
		}
	}
	
	// ----------------------------------------------------------------------------------------------------------------
	// update the crop at the given block to the corresponding growth stage for the given seasonal day
	// will not update to the final stage if the "notFinalStage" flag is set
	private void updateGrowth(Block block, int seasonalDay, boolean notFinalStage) {
		Material blockMaterial = block.getType();
		
		if (seasonalCropList.containsBlockCrop(blockMaterial)) {
			CropSeasonalGrowthConfiguration cropGrowthConfiguration = seasonalCropList.getBlockCrop(blockMaterial);
			
			int currentStage = GrowthUtil.getStage(block);
			
			if (cropGrowthConfiguration.isAllowedToGrow(currentStage, seasonalDay)) {
				int targetStage = cropGrowthConfiguration.getCappedTargetStage(seasonalDay, GrowthUtil.getStageCount(blockMaterial));
				
				// cancel if this is the final stage and the "notFinalStage" flag is set
				if (notFinalStage && targetStage >= GrowthUtil.getStageCount(blockMaterial) - 1)
					return;
				
				GrowthUtil.setStage(block, targetStage);
			}
		}
	}
	private void updateGrowth(World world, int x, int y, int z, int seasonalDay, boolean notFinalStage) {
		Block block = world.getBlockAt(x, y, z);
		
		// if the block does not exist, then do nothing
		if (block == null)
			return;
		
		updateGrowth(block, seasonalDay, notFinalStage);
	}
	
	// ================================================================================================================
	@EventHandler
	public void onBlockPlaceEvent(BlockPlaceEvent event) {
		// stop the player from placing a crop block if it is not "planting time"
		
		Block block = event.getBlock();
		Material blockMaterial = block.getType();
		
		if (seasonalCropList.containsBlockCrop(blockMaterial)) {
			CropSeasonalGrowthConfiguration cropGrowthConfiguration = seasonalCropList.getBlockCrop(blockMaterial);
			
			int currentDay = (int)calendar.getSeasonalDay();
			int startDay = cropGrowthConfiguration.getStartDay();
			int endDay = cropGrowthConfiguration.getFinalPlantingDay();
			
			if (currentDay < startDay || currentDay > endDay) {
				event.setCancelled(true);

				event.getPlayer().sendMessage("�7[Harvest Time] planting season for " + blockMaterial + ": day �8" + startDay + " �7to �8" + endDay);
				event.getPlayer().sendMessage("�7[Harvest Time] current day: �8" + calendar.getSeasonalDay());
			}
		}
	}
	
	// ================================================================================================================
	// cancel all growth events involving seasonal growth crops
	@EventHandler
	public void onBlockGrowEvent(BlockGrowEvent event) {
		if (seasonalCropList.containsBlockCrop(event.getBlock().getType()))
			event.setCancelled(true);
	}
	
	// ================================================================================================================
	public String formatTimeDifference(Date date) {
		long timeDifference = date.getTime() - System.currentTimeMillis();
		long minutes = timeDifference / (1000 * 60);
		long hours = minutes / 60;
		minutes -= hours * 60;
		
		return hours + " Hours, " + minutes + " Minutes";
	}
}
