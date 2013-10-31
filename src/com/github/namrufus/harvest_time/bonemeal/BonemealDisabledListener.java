package com.github.namrufus.harvest_time.bonemeal;


import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class BonemealDisabledListener implements Listener {
	private BonemealDisabledConfiguration configuration;
	
	public BonemealDisabledListener(BonemealDisabledConfiguration configuration) {
		this.configuration = configuration;
	}
	
	// ================================================================================================================
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		// detect bonemeal usage
		if (event.getAction() != Action.RIGHT_CLICK_BLOCK)
			return;
		
		// cancel the event if the block's type is in the list of disabled blocks
		if (configuration.isBonemealDisabled(event.getClickedBlock().getType()))
			event.setCancelled(true);
	}
}
