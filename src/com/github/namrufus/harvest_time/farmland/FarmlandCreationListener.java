package com.github.namrufus.harvest_time.farmland;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import com.github.namrufus.harvest_time.plugin.InteractionConfiguration;
import com.github.namrufus.harvest_time.plugin.PlayerInteractionDelayer;

public class FarmlandCreationListener implements Listener {
	FarmlandCreationConfiguration config;
	InteractionConfiguration interactionConfiguration;
	
	private PlayerInteractionDelayer playerInteractionDelayer;
	
	public FarmlandCreationListener(PlayerInteractionDelayer playerTimerSystem, FarmlandCreationConfiguration config, InteractionConfiguration interactionConfiguration) {
		this.playerInteractionDelayer = playerTimerSystem;
		this.config = config;
		this.interactionConfiguration = interactionConfiguration;
	}
	
	// handle events that turn dirt into farmland in order to extend the time it takes to create a farmland block
	// and to require the presence certain "fertilizer" blocks in order to create that block
	@EventHandler
	public void onPlayerInteractEvent(PlayerInteractEvent event) {
		Block block = event.getClickedBlock();
		
		// -- Ensure that the player is hoeing the ground -------------------------------------------------------------
		// Hoe using right click
		if (event.getAction() != Action.RIGHT_CLICK_BLOCK)
			return;
		
		Material blockMaterial = block.getType();
		
		// Match the block material (DIRT and GRASS convert to farmland)
		if (!(blockMaterial == Material.DIRT || blockMaterial == Material.GRASS))
			return;
			
		Material toolMaterial = event.getMaterial();
		
		// match the tool material
		if (!(toolMaterial != null/*nothing in hand*/ && config.isFarmlandTool(toolMaterial)))
			return;
					
		// -- handle HOE event ----------------------------------------------------------------------------------------
		// if the player has not been hoeing on the same block for long enough, then cancel the event and allow the
		// player to continue hoeing.
		Double duration = config.getToolTime(toolMaterial);
		if (playerInteractionDelayer.canBreak(event.getPlayer(), event.getClickedBlock(), duration, 1.0)) {
			// check if the block is fertilized appropriately
			if (checkFertilizerBlocks(block, false)) {
				// *** FARMLAND block created ***
				// consume fertilizer blocks
				checkFertilizerBlocks(block, true);
			} else /* not enough fertilizer blocks */ { 
				// don't create farmland
				event.setCancelled(true);
			}
		} else /* not enough time */ {
			// play sound as the player hits the ground and hoeing is possible
			if (interactionConfiguration.isSoundEnabled() && checkFertilizerBlocks(block, false))
				event.getPlayer().playSound(block.getLocation(), Sound.DIG_GRAVEL, 1.0f, 1.0f);
			
			// don't create farmland
			event.setCancelled(true);
		}
	}
	
	// determine if the required number and type of fertilizer blocks are directly underneath the specified block
	// if the "consume" flag is set, the blocks will also be converted to dirt
	private boolean checkFertilizerBlocks(Block farmlandBlock, boolean consume) {
		int farmlandY = farmlandBlock.getY();
		int farmlandX = farmlandBlock.getX(), farmlandZ = farmlandBlock.getZ();
		World world = farmlandBlock.getWorld();
		
		// loop through a column "fertilizerBlockColumn" deep
		for (int y = farmlandY - 1; y >= farmlandY - config.getBlockCount(); y--) {
			// check if we went through the bottom of the world
			if (y < 0)
				return false;
			
			Block currentBlock = world.getBlockAt(farmlandX, y, farmlandZ);
			
			// check if the block is of the correct type
			// if the "consume" flag is set, then convert the fertilizer block to DIRT
			if (currentBlock.getType() == config.getBlockType()) {
				if (consume) {
					currentBlock.setType(Material.DIRT);
					currentBlock.setData((byte)0);
				}
			} else
				return false;
		}
		
		return true;
	}
}
