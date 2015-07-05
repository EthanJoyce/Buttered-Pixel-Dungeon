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
package com.mrlolethan.butteredpd.windows;

import com.mrlolethan.butteredpd.gamemodes.GameMode;
import com.mrlolethan.butteredpd.scenes.PixelScene;
import com.watabou.noosa.BitmapTextMultiline;
import com.watabou.noosa.Group;

public class WndGameMode extends WndTabbed {

	private static final int WIDTH			= 110;

	private static final int TAB_WIDTH	= 50;

	private GameMode gamemode;

	private DescriptionTab tabDesc;

	public WndGameMode( GameMode gamemode ) {

		super();

		this.gamemode = gamemode;

		tabDesc = new DescriptionTab();
		add( tabDesc );

		Tab tab = new RankingTab( gamemode.getTitle(), tabDesc );
		tab.setSize( TAB_WIDTH, tabHeight() );
		add( tab );

		resize( (int)tabDesc.width, (int)tabDesc.height );

		layoutTabs();

		select( 0 );
	}

	private class RankingTab extends LabeledTab {

		private Group page;

		public RankingTab( String label, Group page ) {
			super( label );
			this.page = page;
		}

		@Override
		protected void select( boolean value ) {
			super.select( value );
			if (page != null) {
				page.visible = page.active = selected;
			}
		}
	}

	private class DescriptionTab extends Group {

		private static final int MARGIN	= 4;

		public float height;
		public float width;

		public DescriptionTab() {
			super();

			float dotWidth = 0;

			float pos = MARGIN;

			BitmapTextMultiline item = PixelScene.createMultiline( gamemode.getDescription(), 6 );
			item.x = pos;
			item.y = pos;
			item.maxWidth = (int)(WIDTH - MARGIN * 2 - dotWidth);
			item.measure();
			add( item );

			pos += item.height();
			float w = item.width();
			if (w > width) {
				width = w;
			}

			width += MARGIN + dotWidth;
			height = pos + MARGIN;
		}
	}
}