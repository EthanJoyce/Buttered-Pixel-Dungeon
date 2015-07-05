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
package com.mrlolethan.butteredpd.levels.traps;

import com.mrlolethan.butteredpd.Assets;
import com.mrlolethan.butteredpd.Dungeon;
import com.mrlolethan.butteredpd.actors.mobs.Mob;
import com.mrlolethan.butteredpd.effects.CellEmitter;
import com.mrlolethan.butteredpd.effects.Speck;
import com.mrlolethan.butteredpd.utils.GLog;
import com.watabou.noosa.audio.Sample;

public class AlarmTrap extends Trap {

	// 0xDD3333
	{
		name = "Alarm trap";
		image = 4;
	}

	@Override
	public void activate() {

		for (Mob mob : Dungeon.level.mobs) {
				mob.beckon( pos );
		}

		if (Dungeon.visible[pos]) {
			GLog.w( "The trap emits a piercing sound that echoes throughout the dungeon!" );
			CellEmitter.center( pos ).start( Speck.factory( Speck.SCREAM ), 0.3f, 3 );
		}

		Sample.INSTANCE.play( Assets.SND_ALERT );
	}
}
