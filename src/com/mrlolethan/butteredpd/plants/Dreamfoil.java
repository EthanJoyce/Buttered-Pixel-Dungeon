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
package com.mrlolethan.butteredpd.plants;

import com.mrlolethan.butteredpd.actors.Actor;
import com.mrlolethan.butteredpd.actors.Char;
import com.mrlolethan.butteredpd.actors.buffs.Bleeding;
import com.mrlolethan.butteredpd.actors.buffs.Buff;
import com.mrlolethan.butteredpd.actors.buffs.Cripple;
import com.mrlolethan.butteredpd.actors.buffs.Drowsy;
import com.mrlolethan.butteredpd.actors.buffs.MagicalSleep;
import com.mrlolethan.butteredpd.actors.buffs.Poison;
import com.mrlolethan.butteredpd.actors.buffs.Slow;
import com.mrlolethan.butteredpd.actors.buffs.Vertigo;
import com.mrlolethan.butteredpd.actors.buffs.Weakness;
import com.mrlolethan.butteredpd.actors.hero.Hero;
import com.mrlolethan.butteredpd.actors.mobs.Mob;
import com.mrlolethan.butteredpd.items.potions.PotionOfPurity;
import com.mrlolethan.butteredpd.sprites.ItemSpriteSheet;
import com.mrlolethan.butteredpd.utils.GLog;

public class Dreamfoil extends Plant {

	private static final String TXT_DESC =
			"The Dreamfoil's prickly flowers contain a chemical which is known for its " +
			"properties as a strong neutralizing agent. Most weaker creatures are overwhelmed " +
			"and knocked unconscious, which gives the plant its namesake.";

	{
		image = 10;
		plantName = "Dreamfoil";
	}

	@Override
	public void activate() {
		Char ch = Actor.findChar(pos);

		if (ch != null) {
			if (ch instanceof Mob)
				Buff.affect(ch, MagicalSleep.class);
			else if (ch instanceof Hero){
				GLog.i( "You feel refreshed." );
				Buff.detach( ch, Poison.class );
				Buff.detach( ch, Cripple.class );
				Buff.detach( ch, Weakness.class );
				Buff.detach( ch, Bleeding.class );
				Buff.detach( ch, Drowsy.class );
				Buff.detach( ch, Slow.class );
				Buff.detach( ch, Vertigo.class);
		   }
		}
	}

	@Override
	public String desc() {
		return TXT_DESC;
	}

	public static class Seed extends Plant.Seed {
		{
			plantName = "Dreamfoil";

			name = "seed of " + plantName;
			image = ItemSpriteSheet.SEED_DREAMFOIL;

			plantClass = Dreamfoil.class;
			alchemyClass = PotionOfPurity.class;
		}

		@Override
		public String desc() {
			return TXT_DESC;
		}
	}
}