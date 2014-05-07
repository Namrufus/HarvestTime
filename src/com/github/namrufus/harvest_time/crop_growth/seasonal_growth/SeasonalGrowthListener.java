package com.github.namrufus.harvest_time.crop_growth.seasonal_growth;

import java.util.Date;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import com.github.namrufus.harvest_time.crop_growth.seasonal_growth.util.CropYieldUtil;
import com.github.namrufus.harvest_time.crop_growth.seasonal_growth.util.GrowthUtil;
import com.github.namrufus.harvest_time.plugin.InteractionConfiguration;
import com.github.namrufus.harvest_time.plugin.PlayerInteractionDelayer;
import com.github.namrufus.harvest_time.plugin.global.TextCode;
import com.github.namrufus.harvest_time.seasonal_calendar.SeasonalCalendar;

public class SeasonalGrowthListener implements Listener {
	InteractionConfiguration interactionConfiguration;
	TendingConfiguration tendingConfiguration;
	SeasonalCropListConfiguration seasonalCropList;
	SeasonalCalendar calendar;
	PlayerInteractionDelayer playerInteractionDelayer;
	
	public SeasonalGrowthListener(InteractionConfiguration interactionConfiguration,
			                              TendingConfiguration tendingConfiguration,
			                              SeasonalCropListConfiguration seasonalCropList,
			                              SeasonalCalendar calendar,
			                              PlayerInteractionDelayer playerTimerSystem) {
		this.interactionConfiguration = interactionConfiguration;
		this.tendingConfiguration = tendingConfiguration;
		this.seasonalCropList = seasonalCropList;
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
				
		player.sendMessage(TextCode.BASE + TextCode.MESSAGE_PREFIX + " Crop: " + TextCode.VALUE + blockMaterial.toString());
		player.sendMessage(TextCode.BASE + TextCode.MESSAGE_PREFIX + " Growing season: day " + TextCode.VALUE + cropGrowthConfiguration.getStartDay() + TextCode.BASE + " to " + TextCode.VALUE + cropGrowthConfiguration.getFinalDay(stageCount));
		player.sendMessage(TextCode.BASE + TextCode.MESSAGE_PREFIX + "   Year: " + TextCode.VALUE + calendar.getSeasonalYear() + TextCode.BASE + ", Day: " + TextCode.VALUE + seasonalDay);
		player.sendMessage(TextCode.BASE + TextCode.MESSAGE_PREFIX + "   Current growth stage: " + TextCode.VALUE + growthStage + TextCode.BASE + " / " + TextCode.VALUE + (stageCount-1));
		player.sendMessage(TextCode.BASE + TextCode.MESSAGE_PREFIX + "   Target growth stage: " + TextCode.VALUE + targetStage + TextCode.BASE + " / " + TextCode.VALUE + (stageCount-1));
		if (allowedToGrow) {
			int finalViableSeasonalDay = cropGrowthConfiguration.getFinalViableSeasonalDay(growthStage);
			
			Date date = calendar.getSeasonalDayStart(calendar.getSeasonalYear(), finalViableSeasonalDay + 1);
			
			player.sendMessage(TextCode.BASE + TextCode.MESSAGE_PREFIX + "   " + TextCode.HIGHLIGHT + "Crop is viable!" + TextCode.BASE + " Tend within: " + TextCode.VALUE + formatTimeDifference(date) + " (" + date + ")");
		} else if (cropGrowthConfiguration.isAtTargetStage(growthStage, seasonalDay, stageCount)) {
			String message = TextCode.BASE + TextCode.MESSAGE_PREFIX + "   " + TextCode.HIGHLIGHT + "Crop at target growth.";
			if (!(growthStage == stageCount - 1)) {
				Date date;
					if (growthStage == 0)
						date = calendar.getSeasonalDayStart(calendar.getSeasonalYear(), cropGrowthConfiguration.getStartDay() + 1);
					else
						date = calendar.nextSeasonalDayIncrement();
				message += TextCode.BASE + " Next target growth update: " + TextCode.VALUE + formatTimeDifference(date) + "(" + date + ")";
			}
			player.sendMessage(message);
		} else {
			player.sendMessage(TextCode.BASE + TextCode.MESSAGE_PREFIX + "   " + TextCode.HIGHLIGHT + "Crop is not viable.");
		}		
	}
	
	// ----------------------------------------------------------------------------------------------------------------
	// display information relating to the yield of the crop to the given player
	private void yieldCheckInteraction(Player player, Block block) {
		Material blockMaterial = block.getType();
		CropSeasonalGrowthConfiguration cropGrowthConfiguration = seasonalCropList.getBlockCrop(blockMaterial);
		
		if (!cropGrowthConfiguration.hasCustomYield())
			return;
		
		double targetYield = cropGrowthConfiguration.getCustomYieldConfiguration().getYieldCount(block);
		
		player.sendMessage(TextCode.BASE + TextCode.MESSAGE_PREFIX + " Crop: " + TextCode.VALUE + blockMaterial.toString());
		cropGrowthConfiguration.getCustomYieldConfiguration().displayState(player, block);
		player.sendMessage(TextCode.BASE + TextCode.MESSAGE_PREFIX + "   " + TextCode.HIGHLIGHT + "Total Yield: " + TextCode.HIGHLIGHT_VALUE + String.format("%.2f", targetYield));
	}
	
	// ----------------------------------------------------------------------------------------------------------------
	// update plant growth by "tending" the crop
	private void cropTendInteraction(Player player, Material toolMaterial, Block block) {
		Material blockMaterial = block.getType();
		CropSeasonalGrowthConfiguration cropGrowthConfiguration = seasonalCropList.getBlockCrop(blockMaterial);
		
		int seasonalDay = (int)calendar.getSeasonalDay();
		int previousGrowth = GrowthUtil.getStage(block);
		
		// ............................................................................................................
		// delay event until the time determined by the type of tool hitting the crop
		
		double tendingTime = tendingConfiguration.getToolTendingTime(toolMaterial);
		if (!playerInteractionDelayer.canBreak(player, block, tendingTime, Math.min(tendingTime, 1.0))) {
			// play a sound while hitting the plant if a growth event is forth-coming
			if (interactionConfiguration.isSoundEnabled() && cropGrowthConfiguration.isAllowedToGrow(previousGrowth, seasonalDay))
				player.playSound(block.getLocation(), Sound.DIG_GRAVEL, 1.0f, 1.0f);
			return;
		}
		
		// ............................................................................................................
		// the correct amount of time has passed, now actually grow the crop
		
		// update the crop clicked
		updateGrowth(block, seasonalDay, false);
		int currentGrowth =  GrowthUtil.getStage(block);
		
		// ............................................................................................................
		// handle custom yields on the transition to the final stage
		
		if (previousGrowth != currentGrowth && currentGrowth == GrowthUtil.getStageCount(blockMaterial) - 1) {
			
			
			if (cropGrowthConfiguration.hasCustomYield()) {
				double targetYield = cropGrowthConfiguration.getCustomYieldConfiguration().getYieldCount(block);
				
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

				event.getPlayer().sendMessage(TextCode.BASE + TextCode.MESSAGE_PREFIX + " planting season for " + blockMaterial + ": day" + TextCode.VALUE + startDay + TextCode.BASE + " to " + TextCode.VALUE + endDay);
				event.getPlayer().sendMessage(TextCode.BASE + TextCode.MESSAGE_PREFIX + " current day: " + TextCode.VALUE + calendar.getSeasonalDay());
			}
		}
	}
	
	// ================================================================================================================
	// cancel all growth events involving seasonal growth crops
	@EventHandler
	public void onBlockGrowEvent(BlockGrowEvent event) {
		if (seasonalCropList.containsBlockCrop(event.getNewState().getType()))
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
