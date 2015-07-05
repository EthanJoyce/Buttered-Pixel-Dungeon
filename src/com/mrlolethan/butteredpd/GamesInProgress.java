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
package com.mrlolethan.butteredpd;

import java.util.HashMap;

import com.mrlolethan.butteredpd.actors.hero.HeroClass;
import com.mrlolethan.butteredpd.gamemodes.GameMode;
import com.watabou.utils.Bundle;

public class GamesInProgress {

	private static HashMap<GameMode, HashMap<HeroClass, Info>> state = new HashMap<GameMode, HashMap<HeroClass, Info>>();
	static {
		for (GameMode mode : GameMode.values()) {
			HashMap<HeroClass, Info> infoMap = new HashMap<HeroClass, Info>();
			state.put(mode, infoMap);
		}
	}
	
	
	public static Info check( GameMode gamemode, HeroClass cl ) {
		
		if (state.containsKey(gamemode) && state.get(gamemode).containsKey( cl )) {
			
			return state.get(gamemode).get(cl);
			
		} else {
			
			Info info;
			try {
				
				Bundle bundle = Dungeon.gameBundle( Dungeon.gameFile( gamemode, cl ) );
				info = new Info();
				Dungeon.preview( info, bundle );

			} catch (Exception e) {
				info = null;
			}
			
			state.get(gamemode).put(cl, info);
			return info;
			
		}
	}

	public static void set( GameMode gamemode, HeroClass cl, int depth, int level, boolean challenges ) {
		Info info = new Info();
		info.depth = depth;
		info.level = level;
		info.challenges = challenges;
		state.get(gamemode).put( cl, info );
	}
	
	public static void setUnknown( GameMode gamemode, HeroClass cl ) {
		state.get(gamemode).remove( cl );
	}
	
	public static void delete( GameMode gamemode, HeroClass cl ) {
		state.get(gamemode).put( cl, null );
	}
	
	public static class Info {
		public int depth;
		public int level;
		public boolean challenges;
	}
}
