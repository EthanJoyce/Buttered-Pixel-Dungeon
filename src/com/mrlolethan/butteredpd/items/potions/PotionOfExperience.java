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
package com.mrlolethan.butteredpd.items.potions;

import com.mrlolethan.butteredpd.Assets;
import com.mrlolethan.butteredpd.Dungeon;
import com.mrlolethan.butteredpd.actors.hero.Hero;
import com.mrlolethan.butteredpd.gamemodes.GameMode;
import com.mrlolethan.butteredpd.levels.ArenaLevel;
import com.mrlolethan.butteredpd.utils.GLog;
import com.watabou.noosa.audio.Sample;

public class PotionOfExperience extends Potion {

	{
		name = "Potion of Experience";
		initials = "Ex";

		bones = true;
	}
	
	@Override
	public void apply( Hero hero ) {
		setKnown();
		
		if (Dungeon.gamemode == GameMode.ARENA) {
			if (Dungeon.level instanceof ArenaLevel) {
				ArenaLevel level = (ArenaLevel) Dungeon.level;
				if (level.isBossWave()) {
					GLog.n("You accidentally drop the bottle and it smashes at your feet. Maybe you should just focus on the boss?");
					Sample.INSTANCE.play( Assets.SND_SHATTER );
					splash( hero.pos );
					return;
				}
			}
		}
		
		hero.earnExp( hero.maxExp() );
	}
	
	@Override
	public String desc() {
		return
			"The storied experiences of multitudes of battles reduced to liquid form, " +
			"this draught will instantly raise your experience level.";
	}
	
	@Override
	public int price() {
		return isKnown() ? 80 * quantity : super.price();
	}
}
