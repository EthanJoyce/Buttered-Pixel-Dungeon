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
package com.mrlolethan.butteredpd.scenes;

import java.util.HashMap;

import com.mrlolethan.butteredpd.Assets;
import com.mrlolethan.butteredpd.Badges;
import com.mrlolethan.butteredpd.ButteredPixelDungeon;
import com.mrlolethan.butteredpd.Dungeon;
import com.mrlolethan.butteredpd.effects.BannerSprites;
import com.mrlolethan.butteredpd.effects.BannerSprites.Type;
import com.mrlolethan.butteredpd.effects.Speck;
import com.mrlolethan.butteredpd.gamemodes.GameMode;
import com.mrlolethan.butteredpd.ui.Archs;
import com.mrlolethan.butteredpd.ui.ExitButton;
import com.mrlolethan.butteredpd.ui.RedButton;
import com.mrlolethan.butteredpd.windows.WndGameMode;
import com.watabou.noosa.BitmapText;
import com.watabou.noosa.Camera;
import com.watabou.noosa.Game;
import com.watabou.noosa.Group;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.particles.Emitter;
import com.watabou.noosa.ui.Button;
import com.watabou.utils.Callback;

public class ChooseModeScene extends PixelScene {

	private static final float BUTTON_HEIGHT	= 24;

	private static final String TXT_PLAY		= "Play";

	private static final float WIDTH_P    = 116;
	private static final float HEIGHT_P    = 220;

	private static final float WIDTH_L    = 224;
	private static final float HEIGHT_L    = 124;

	private static HashMap<GameMode, ModeShield> shields = new HashMap<GameMode, ModeShield>();

	private float buttonX;
	private float buttonY;

	private GameButton btnPlay;

	private Group unlock;

	private GameMode selectedMode;

	@Override
	public void create() {

		super.create();

		Badges.loadGlobal();

		uiCamera.visible = false;

		int w = Camera.main.width;
		int h = Camera.main.height;

		float width, height;
		if (ButteredPixelDungeon.landscape()) {
			width = WIDTH_L;
			height = HEIGHT_L;
		} else {
			width = WIDTH_P;
			height = HEIGHT_P;
		}

		float left = (w - width) / 2;
		float top = (h - height) / 2;
		float bottom = h - top;

		Archs archs = new Archs();
		archs.setSize( w, h );
		add( archs );

		Image title = BannerSprites.get( Type.SELECT_YOUR_HERO );
		title.x = align( (w - title.width()) / 2 );
		title.y = align( top );
		add( title );

		buttonX = left;
		buttonY = bottom - BUTTON_HEIGHT;

		btnPlay = new GameButton( TXT_PLAY ) {
			@Override
			protected void onClick() {
				ButteredPixelDungeon.switchNoFade(StartScene.class);
			}
		};
		add( btnPlay );

		float centralHeight = buttonY - title.y - title.height();

		GameMode[] modes = {
				GameMode.REGULAR, GameMode.ARENA,
		};
		for (GameMode mode : modes) {
			ModeShield shield = new ModeShield( mode );
			shields.put( mode, shield );
			add( shield );
		}
		if (ButteredPixelDungeon.landscape()) {
			float shieldW = width / 4;
			float shieldH = Math.min( centralHeight, shieldW );
			top = title.y + title.height + (centralHeight - shieldH) / 2;
			for (int i=0; i < modes.length; i++) {
				ModeShield shield = shields.get( modes[i] );
				shield.setRect( left + i * shieldW, top, shieldW, shieldH );
			}

		} else {
			float shieldW = width / 2;
			float shieldH = Math.min( centralHeight / 2, shieldW * 1.2f );
			top = title.y + title.height() + centralHeight / 2 - shieldH;
			for (int i=0; i < modes.length; i++) {
				ModeShield shield = shields.get( modes[i] );
				shield.setRect(
						left + (i % 2) * shieldW,
						top + (i / 2) * shieldH,
						shieldW, shieldH );
			}

		}

		unlock = new Group();
		add( unlock );

		ExitButton btnExit = new ExitButton();
		btnExit.setPos( Camera.main.width - btnExit.width(), 0 );
		add( btnExit );

		updateMode(modes[ButteredPixelDungeon.lastGameMode()]);

		fadeIn();

		Badges.loadingListener = new Callback() {
			@Override
			public void call() {
				if (Game.scene() == ChooseModeScene.this) {
					ButteredPixelDungeon.switchNoFade( ChooseModeScene.class );
				}
			}
		};
	}

	@Override
	public void destroy() {

		Badges.saveGlobal();
		Badges.loadingListener = null;

		super.destroy();

	}

	private void updateMode( GameMode mode ) {

		if (selectedMode == mode) {
			add( new WndGameMode( mode ) );
			return;
		}

		if (selectedMode != null) {
			shields.get( selectedMode ).highlight( false );
		}
		shields.get( selectedMode = mode ).highlight( true );

		btnPlay.visible = true;
		btnPlay.secondary( null, false );
		btnPlay.setRect( buttonX, buttonY, Camera.main.width - buttonX * 2, BUTTON_HEIGHT );
		
		Dungeon.gamemode = mode;
	}

	@Override
	protected void onBackPressed() {
		ButteredPixelDungeon.switchNoFade( TitleScene.class );
	}

	private static class GameButton extends RedButton {

		private static final int SECONDARY_COLOR_N    = 0xCACFC2;
		private static final int SECONDARY_COLOR_H    = 0xFFFF88;

		private BitmapText secondary;

		public GameButton( String primary ) {
			super( primary );

			this.secondary.text( null );
		}

		@Override
		protected void createChildren() {
			super.createChildren();

			secondary = createText( 6 );
			add( secondary );
		}

		@Override
		protected void layout() {
			super.layout();

			if (secondary.text().length() > 0) {
				text.y = align( y + (height - text.height() - secondary.baseLine()) / 2 );

				secondary.x = align( x + (width - secondary.width()) / 2 );
				secondary.y = align( text.y + text.height() );
			} else {
				text.y = align( y + (height - text.baseLine()) / 2 );
			}
		}

		public void secondary( String text, boolean highlighted ) {
			secondary.text( text );
			secondary.measure();

			secondary.hardlight( highlighted ? SECONDARY_COLOR_H : SECONDARY_COLOR_N );
		}
	}

	private class ModeShield extends Button {

		private static final float MIN_BRIGHTNESS	= 0.6f;

		private static final int BASIC_NORMAL        = 0x444444;
		private static final int BASIC_HIGHLIGHTED    = 0xCACFC2;

		private static final int WIDTH	= 32;
		private static final int HEIGHT	= 32;
		private static final float SCALE	= 1.75f;

		private GameMode mode;

		private Image image;
		private BitmapText name;
		private Emitter emitter;

		private float brightness;

		private int normal;
		private int highlighted;

		public ModeShield( GameMode mode ) {
			super();

			this.mode = mode;

			image.frame( mode.ordinal() * WIDTH, 0, WIDTH, HEIGHT );
			image.scale.set( SCALE );

			normal = BASIC_NORMAL;
			highlighted = BASIC_HIGHLIGHTED;

			name.text( mode.getTitle() );
			name.measure();
			name.hardlight( normal );

			brightness = MIN_BRIGHTNESS;
			updateBrightness();
		}

		@Override
		protected void createChildren() {

			super.createChildren();

			image = new Image( Assets.GAMEMODES );
			add( image );

			name = PixelScene.createText( 9 );
			add( name );

			emitter = new Emitter();
			add( emitter );
		}

		@Override
		protected void layout() {

			super.layout();

			image.x = align( x + (width - image.width()) / 2 );
			image.y = align( y + (height - image.height() - name.height()) / 2 );

			name.x = align( x + (width - name.width()) / 2 );
			name.y = image.y + image.height() + SCALE;

			emitter.pos( image.x, image.y, image.width(), image.height() );
		}

		@Override
		protected void onTouchDown() {

			emitter.revive();
			emitter.start( Speck.factory( Speck.LIGHT ), 0.05f, 7 );

			Sample.INSTANCE.play( Assets.SND_CLICK, 1, 1, 1.2f );
			updateMode( mode );
		}

		@Override
		public void update() {
			super.update();

			if (brightness < 1.0f && brightness > MIN_BRIGHTNESS) {
				if ((brightness -= Game.elapsed) <= MIN_BRIGHTNESS) {
					brightness = MIN_BRIGHTNESS;
				}
				updateBrightness();
			}
		}

		public void highlight( boolean value ) {
			if (value) {
				brightness = 1.0f;
				name.hardlight( highlighted );
			} else {
				brightness = 0.999f;
				name.hardlight( normal );
			}

			updateBrightness();
		}

		private void updateBrightness() {
			image.gm = image.bm = image.rm = image.am = brightness;
		}
	}

}