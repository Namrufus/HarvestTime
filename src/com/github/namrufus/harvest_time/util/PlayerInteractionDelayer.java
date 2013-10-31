package com.github.namrufus.harvest_time.util;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

// this class helps artificially extend the break times of blocks (especially instant break blocks)

public class PlayerInteractionDelayer {
	private static double timeDifference(long from, long to) {
		return (double)(to - from) / (1000.0);
	}
	
	// a record of the last block the player has interacted with
	public class TimerBlock {
		Location location;
		
		private Material material;
		
		private long timestamp;
		private long breakTimestamp;
		
		public TimerBlock(Block block) {
			location = block.getLocation();
			material = block.getType();
			timestamp = System.currentTimeMillis();
			breakTimestamp = System.currentTimeMillis();
		}
		
		// returns true iff the given block matches the recorded block
		public boolean matches(Block block) {
			return block.getLocation().equals(location) && block.getType().equals(material);
		}
		
		public long getTimestamp() { return timestamp; }
		public long getBreakTimestamp() { return breakTimestamp; }
		public void updateBreakTimestamp() { breakTimestamp = System.currentTimeMillis(); }
	}
	
	// ================================================================================================================
	private Map<String, TimerBlock> timers;
	
	public PlayerInteractionDelayer() {
		timers = new HashMap<String, TimerBlock>();
	}
	
	public boolean canBreak(Player player, Block block, double duration, double maximumBreakDuration) {
		String playerName = player.getName();
		
		if (timers.containsKey(playerName)) {
			TimerBlock timerBlock = timers.get(player.getName());
			
			// determine the durations in seconds
			long currentTime = System.currentTimeMillis();
			double currentBreakDuration = timeDifference(timerBlock.getBreakTimestamp(), currentTime);
			timerBlock.updateBreakTimestamp();
			double currentDuration = timeDifference(timerBlock.getTimestamp(), currentTime);
			
			// if the block does not match or the player has taken too long between "breaking" the block
			// then restart the timer
			if (!timerBlock.matches(block) || currentBreakDuration > maximumBreakDuration) {			
				timers.remove(playerName);
				timers.put(playerName, new TimerBlock(block));
				return false;
			}
			
			// if the current duration exceeds the specified duration then allow the block to be broken
			// and remove the record
			if (currentDuration > duration) {
				timers.remove(playerName);
				return true;
			} else
				return false;
		} else {
			// if the player was not recorded in the system, add a new entry with the specified block
			// but only if the duration is nonzero (in which case the block should break instantly)
			if (duration <= 0.0)
				return true;
			
			timers.put(playerName, new TimerBlock(block));
			
			return false;
		}
	}
}
