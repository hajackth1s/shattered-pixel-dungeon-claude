/*
 * Shattered Pixel Dungeon - Cheat-Mod
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.shatteredpixel.shatteredpixeldungeon.windows;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Cheats;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.CheckBox;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;

public class WndCheats extends Window {

	private static final int WIDTH      = 120;
	private static final int BTN_HEIGHT = 18;
	private static final int GAP        = 2;

	private float pos;

	public WndCheats() {
		super();

		RenderedTextBlock title = PixelScene.renderTextBlock( Messages.get(this, "title"), 9 );
		title.hardlight( TITLE_COLOR );
		title.setPos( (WIDTH - title.width()) / 2f, 0 );
		add( title );
		pos = title.bottom() + 2*GAP;

		CheckBox god = new CheckBox( Messages.get(this, "god_mode") ) {
			@Override
			protected void onClick() {
				super.onClick();
				Cheats.godMode = checked();
				GLog.p( Messages.get(WndCheats.class, checked() ? "god_on" : "god_off") );
			}
		};
		god.checked( Cheats.godMode );
		place( god );

		addButtons(
				button( "str_1",  () -> Cheats.addStrength(1) ),
				button( "str_5",  () -> Cheats.addStrength(5) ) );
		addButtons(
				button( "gold",   () -> Cheats.addGold(9999) ),
				button( "energy", () -> Cheats.addEnergy(999) ) );
		addButtons(
				button( "heal",   Cheats::fullHeal ),
				button( "level",  Cheats::levelUp ) );
		addButtons(
				button( "upgrades", () -> Cheats.giveUpgrades(10) ),
				button( "identify", Cheats::identifyAll ) );
		place( button( "map", Cheats::revealMap ) );

		resize( WIDTH, (int)pos );
	}

	private RedButton button( String key, Runnable action ){
		return new RedButton( Messages.get(this, key), 7 ) {
			@Override
			protected void onClick() {
				action.run();
				Sample.INSTANCE.play( Assets.Sounds.ITEM );
				GLog.p( Messages.get(WndCheats.class, "done", Messages.get(WndCheats.class, key)) );
				GameScene.updateTags = true;
			}
		};
	}

	private void place( com.watabou.noosa.ui.Component c ){
		add( c );
		c.setRect( 0, pos, WIDTH, BTN_HEIGHT );
		pos += BTN_HEIGHT + GAP;
	}

	private void addButtons( RedButton b1, RedButton b2 ){
		add( b1 );
		b1.setRect( 0, pos, (WIDTH - GAP) / 2f, BTN_HEIGHT );
		add( b2 );
		b2.setRect( b1.right() + GAP, pos, WIDTH - b1.right() - GAP, BTN_HEIGHT );
		pos += BTN_HEIGHT + GAP;
	}
}
