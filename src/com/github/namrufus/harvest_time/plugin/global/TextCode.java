package com.github.namrufus.harvest_time.plugin.global;

import org.bukkit.ChatColor;

// defines custom color codes to be used in chat interface
// utility to simplify application of chat codes

public enum TextCode {
	BASE ("" + ChatColor.GRAY),
	VALUE ("" + ChatColor.DARK_GRAY),
	HIGHLIGHT ("" + ChatColor.AQUA),
	HIGHLIGHT_VALUE ("" + ChatColor.DARK_AQUA),
	WARNING ("" + ChatColor.RED);
	
	public static String MESSAGE_PREFIX = "[Harvest Time]";
	
	// ****************************************************************************************************************
	
	private final String code;
	
	private TextCode(ChatColor chatColor) {
		this(chatColor.toString());
	}
	private TextCode(String code) {
		this.code = ChatColor.RESET + code;
	}
	
	@Override
	public String toString() { return code; }
	
	// ****************************************************************************************************************

}
