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
package com.mrlolethan.butteredpd.levels;

import android.util.Log;

import com.mrlolethan.butteredpd.Assets;
import com.mrlolethan.butteredpd.ButteredPixelDungeon;
import com.mrlolethan.butteredpd.Dungeon;
import com.mrlolethan.butteredpd.actors.Actor;
import com.mrlolethan.butteredpd.actors.Char;
import com.mrlolethan.butteredpd.actors.buffs.Buff;
import com.mrlolethan.butteredpd.actors.mobs.Bestiary;
import com.mrlolethan.butteredpd.actors.mobs.DM300;
import com.mrlolethan.butteredpd.actors.mobs.Goo;
import com.mrlolethan.butteredpd.actors.mobs.King;
import com.mrlolethan.butteredpd.actors.mobs.Mob;
import com.mrlolethan.butteredpd.actors.mobs.Tengu;
import com.mrlolethan.butteredpd.actors.mobs.Yog;
import com.mrlolethan.butteredpd.effects.CellEmitter;
import com.mrlolethan.butteredpd.effects.particles.FlameParticle;
import com.mrlolethan.butteredpd.items.Generator;
import com.mrlolethan.butteredpd.items.Heap;
import com.mrlolethan.butteredpd.items.Item;
import com.mrlolethan.butteredpd.items.rings.RingOfWealth;
import com.mrlolethan.butteredpd.items.scrolls.Scroll;
import com.mrlolethan.butteredpd.levels.painters.Painter;
import com.mrlolethan.butteredpd.levels.traps.FireTrap;
import com.mrlolethan.butteredpd.scenes.GameScene;
import com.watabou.noosa.Scene;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

public class ArenaLevel extends Level {
	
	{
		color1 = 0x801500;
		color2 = 0xa68521;
		
		viewDistance = 3;
	}
	
	private static final int ROOM_LEFT		= WIDTH / 2 - 1;
	private static final int ROOM_RIGHT		= WIDTH / 2 + 1;
	private static final int ROOM_TOP		= HEIGHT / 2 - 1;
	private static final int ROOM_BOTTOM	= HEIGHT / 2 + 1;
	
	private static final String WAVE = "wave";
	private int wave = 1;
	
	private int stairs = -1;
	private boolean enteredArena = false;
	private boolean keyDropped = false;
	
	
	public void nextWave() {
		wave++;

		if (wave % 5 == 0) {
			// Boss wave
			this.clearMobs();
			
			switch (wave) {
			case 5:
				spawnBoss(new Goo());
				break;
			case 10:
				spawnBoss(new Tengu());
				break;
			case 15:
				spawnBoss(new DM300());
				break;
			case 20:
				spawnBoss(new King());
				break;
			case 25:
				spawnBoss(new Yog());
				break;
			}
		} else {
			// Regular wave
			this.createItems();
			
			this.clearMobs();
			this.spawnMobs();
		}
	}
	
	public void spawnMobs() {
		int mobsToSpawn = Random.Int(5, 10);

		while (mobsToSpawn > 0) {
			Mob mob = Bestiary.mob( wave );
			mob.pos = Random.Int(LENGTH);

			if (findMob(mob.pos) == null && Level.passable[mob.pos] && !fieldOfView[mob.pos] && Level.distance(mob.pos, entrance) > 1) {
				mobsToSpawn--;
				if (Dungeon.level instanceof ArenaLevel) {
					GameScene.add(mob);
				} else {
					mobs.add(mob);
				}
			}
		}
	}
	
	public void spawnBoss(Mob mob) {
		while (true) {
			mob.pos = Random.Int(LENGTH);
			
			if (findMob(mob.pos) == null && Level.passable[mob.pos] && !fieldOfView[mob.pos]) {
				if (Dungeon.level != null) {
					GameScene.add(mob);
				} else {
					mobs.add(mob);
				}
				
				if (mob instanceof Yog) {
					((Yog) mob).spawnFists();
				}
				
				break;
			}
		}
	}
	
	public void clearMobs() {
		for (Mob mob : mobs.toArray( new Mob[0] )) {
			if (!mob.reset()) {
				if (ButteredPixelDungeon.scene() instanceof GameScene) {
					GameScene.remove(mob);
				} else {
					mobs.remove(mob);
				}
			}
		}
	}
	
	@Override
	public String tilesTex() {
		return Assets.TILES_HALLS;
	}
	
	@Override
	public String waterTex() {
		return Assets.WATER_HALLS;
	}
	
	private static final String STAIRS	= "stairs";
	private static final String ENTERED	= "entered";
	private static final String DROPPED	= "droppped";
	
	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle( bundle );
		bundle.put( STAIRS, stairs );
		bundle.put( ENTERED, enteredArena );
		bundle.put( DROPPED, keyDropped );
		bundle.put(WAVE, wave);
	}
	
	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle( bundle );
		stairs = bundle.getInt( STAIRS );
		enteredArena = bundle.getBoolean( ENTERED );
		keyDropped = bundle.getBoolean( DROPPED );
		wave = bundle.getInt(WAVE);
	}
	
	@Override
	protected boolean build() {
		
		for (int i=0; i < 5; i++) {
			
			int top = Random.IntRange( 2, ROOM_TOP - 1 );
			int bottom = Random.IntRange( ROOM_BOTTOM + 1, 22 );
			Painter.fill( this, 2 + i * 4, top, 4, bottom - top + 1, Terrain.EMPTY );
			
			if (i == 2) {
				exit = (i * 4 + 3) + (top - 1) * WIDTH ;
			}
			
			for (int j=0; j < 4; j++) {
				if (Random.Int( 2 ) == 0) {
					int y = Random.IntRange( top + 1, bottom - 1 );
					map[i*4+j + y*WIDTH] = map[i*4+j + 1 + y*WIDTH] = map[i*4+j - 1 + y*WIDTH] = Terrain.WALL_DECO;
				}
			}
		}
		
		map[exit] = Terrain.LOCKED_EXIT;
		
		Painter.fill( this, ROOM_LEFT - 1, ROOM_TOP - 1,
			ROOM_RIGHT - ROOM_LEFT + 3, ROOM_BOTTOM - ROOM_TOP + 3, Terrain.WALL );
		Painter.fill( this, ROOM_LEFT, ROOM_TOP,
			ROOM_RIGHT - ROOM_LEFT + 1, ROOM_BOTTOM - ROOM_TOP + 1, Terrain.EMPTY );
		
		entrance = Random.Int( ROOM_LEFT + 1, ROOM_RIGHT - 1 ) +
			Random.Int( ROOM_TOP + 1, ROOM_BOTTOM - 1 ) * WIDTH;
		map[entrance] = Terrain.ENTRANCE;
		
		boolean[] patch = Patch.generate( 0.45f, 6 );
		for (int i=0; i < LENGTH; i++) {
			if (map[i] == Terrain.EMPTY && patch[i]) {
				map[i] = Terrain.WATER;
			}
		}
		
		return true;
	}
	
	@Override
	protected void decorate() {
		
		for (int i=0; i < LENGTH; i++) {
			if (map[i] == Terrain.EMPTY && Random.Int( 10 ) == 0) {
				map[i] = Terrain.EMPTY_DECO;
			}
		}
	}
	
	@Override
	protected void createMobs() {
		this.spawnMobs();
	}
	
	public Actor respawner() {
		return null;
	}
	
	@Override
	protected void createItems() {
		Generator.reset();
		
		int nItems = 6;
		int bonus = 0;
		for (Buff buff : Dungeon.hero.buffs(RingOfWealth.Wealth.class)) {
			bonus += ((RingOfWealth.Wealth) buff).level;
		}
		//just incase someone gets a ridiculous ring, cap this at 80%
		bonus = Math.min(bonus, 10);
		while (Random.Float() < (0.3f + bonus*0.05f)) {
			nItems++;
		}
		
		for (int i=0; i < nItems; i++) {
			drop( Generator.random(), randomDropCell() ).type = Heap.Type.HEAP;
		}

		for (Item item : itemsToSpawn) {
			int cell = randomDropCell();
			if (item instanceof Scroll) {
				while ((map[cell] == Terrain.TRAP || map[cell] == Terrain.SECRET_TRAP)
						&& traps.get( cell ) instanceof FireTrap) {
					cell = randomDropCell();
				}
			}
			drop( item, cell ).type = Heap.Type.HEAP;
		}
	}
	
	protected int randomDropCell() {
		while (true) {
			int pos = Random.Int(LENGTH);
			if (passable[pos]) {
				return pos;
			}
		}
	}
	
	
	@Override
	public void press( int cell, Char hero ) {
		
		super.press( cell, hero );
		
		if (!enteredArena && hero == Dungeon.hero && cell != entrance) {
			
			enteredArena = true;
			
			for (int i=ROOM_LEFT-1; i <= ROOM_RIGHT + 1; i++) {
				doMagic( (ROOM_TOP - 1) * WIDTH + i );
				doMagic( (ROOM_BOTTOM + 1) * WIDTH + i );
			}
			for (int i=ROOM_TOP; i < ROOM_BOTTOM + 1; i++) {
				doMagic( i * WIDTH + ROOM_LEFT - 1 );
				doMagic( i * WIDTH + ROOM_RIGHT + 1 );
			}
			doMagic( entrance );
			GameScene.updateMap();

			Dungeon.observe();
			
			stairs = entrance;
			entrance = -1;
		}
	}
	
	private void doMagic( int cell ) {
		set( cell, Terrain.EMPTY_SP );
		CellEmitter.get( cell ).start( FlameParticle.FACTORY, 0.1f, 3 );
	}
	
	@Override
	public String tileName( int tile ) {
		switch (tile) {
		case Terrain.WATER:
			return "Cold lava";
		case Terrain.GRASS:
			return "Embermoss";
		case Terrain.HIGH_GRASS:
			return "Emberfungi";
		case Terrain.STATUE:
		case Terrain.STATUE_SP:
			return "Pillar";
		default:
			return super.tileName( tile );
		}
	}
	
	@Override
	public String tileDesc(int tile) {
		switch (tile) {
		case Terrain.WATER:
			return "It looks like lava, but it's cold and probably safe to touch.";
		case Terrain.STATUE:
		case Terrain.STATUE_SP:
			return "The pillar is made of real humanoid skulls. Awesome.";
		default:
			return super.tileDesc( tile );
		}
	}
	
	@Override
	public void addVisuals( Scene scene ) {
		HallsLevel.addVisuals( this, scene );
	}


	/*
	 * Accessors
	 */
	public int getWave() {
		return wave;
	}
	
	public boolean isBossWave() {
		return wave % 5 == 0;
	}
}
