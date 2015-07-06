/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015  Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2015 Evan Debenham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */
package com.mrlolethan.butteredpd.items;
//If it weren't super obvious, this is going to become an artifact soon.

import java.util.ArrayList;

import com.mrlolethan.butteredpd.Dungeon;
import com.mrlolethan.butteredpd.actors.Actor;
import com.mrlolethan.butteredpd.actors.hero.Hero;
import com.mrlolethan.butteredpd.gamemodes.GameMode;
import com.mrlolethan.butteredpd.levels.ArenaLevel;
import com.mrlolethan.butteredpd.levels.Level;
import com.mrlolethan.butteredpd.scenes.InterlevelScene;
import com.mrlolethan.butteredpd.sprites.ItemSprite.Glowing;
import com.mrlolethan.butteredpd.sprites.ItemSpriteSheet;
import com.mrlolethan.butteredpd.utils.GLog;
import com.watabou.noosa.Game;

public class ArenaShopKey extends Item {

	private static final String TXT_CREATURES =
		"Psychic aura of neighbouring creatures doesn't allow you to use the Pixel Mart Key at this moment.";
	
	private static final String TXT_FAIL =
		"The key fails to activate.";
	
	private static final String TXT_INFO =
		"The Pixel Mart Key is a magical key that will teleport you to a nearby Pixel Mart branch!\n" +
		"\n" +
		"This key's magical power seems to be fading. It will probably only work once.";
	
	public static final String AC_ACTIVATE       = "ACTIVATE";
	
	{
		name = "Pixel Mart Key";
		image = ItemSpriteSheet.GOLDEN_KEY;

		defaultAction = AC_ACTIVATE;
	}
	
	@Override
	public ArrayList<String> actions( Hero hero ) {
		ArrayList<String> actions = super.actions( hero );
		actions.add(AC_ACTIVATE);
		return actions;
	}
	
	@Override
	public void execute( Hero hero, String action ) {

		if (Dungeon.gamemode != GameMode.ARENA) {
			GLog.n("\"You shouldn't have this,\" whispers a ghostly voice. The key vanishes.");
			detach(hero.belongings.backpack);
			return;
		}

		if (action == AC_ACTIVATE) {
			for (int i=0; i < Level.NEIGHBOURS4.length; i++) {
				if (Actor.findChar(hero.pos + Level.NEIGHBOURS4[i]) != null) {
					GLog.w(TXT_CREATURES);
					return;
				}
			}
			
			// If we aren't in the arena level, or it's a boss wave ...
			if (!(Dungeon.level instanceof ArenaLevel) || ((ArenaLevel) Dungeon.level).isBossWave()) {
				// ... prevent use of the key
				GLog.n(TXT_FAIL);
				return;
			}
			ArenaLevel level = (ArenaLevel) Dungeon.level;
			
			final int shopDepth = level.getWave() / 5 * 5;
			Dungeon.depth = Math.min(Math.max(shopDepth, 5), 20);
			
			this.switchToNewShop();
			detach(hero.belongings.backpack);
		} else {
			super.execute( hero, action );
		}
	}

	public void switchToNewShop() {
		InterlevelScene.mode = InterlevelScene.Mode.DESCEND;
		Game.switchScene( InterlevelScene.class );
	}

	@Override
	public String desc() {
		return TXT_INFO;
	}

	@Override
	public boolean isUpgradable() {
		return false;
	}

	@Override
	public Glowing glowing() {
		return new Glowing(0xFFFF00);
	}

}
