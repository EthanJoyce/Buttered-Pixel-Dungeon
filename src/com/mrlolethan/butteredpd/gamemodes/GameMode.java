package com.mrlolethan.butteredpd.gamemodes;


public enum GameMode {

	REGULAR("Classic", "The regular game. Get to the bottom of the dungeon and obtain the Amulet of Yendor!"),
	ARENA("Arena", "Fend off monsters in an arena, while improving your gear and amassing survival items!"),
	;


	private String title;
	private String description;

	private GameMode(String title, String description) {
		this.title = title;
		this.description = description;
	}


	/*
	 * Accessors
	 */
	public String getTitle() {
		return this.title;
	}

	public String getDescription() {
		return this.description;
	}

	public String getSavesPrefix() {
		return this.name().toLowerCase();
	}

}
