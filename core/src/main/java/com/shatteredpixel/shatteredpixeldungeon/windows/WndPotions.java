/*
 * Shattered Pixel Dungeon - Cheat-Mod
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.shatteredpixel.shatteredpixeldungeon.windows;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.Potion;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.brews.AquaBrew;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.brews.BlizzardBrew;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.brews.CausticBrew;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.brews.InfernalBrew;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.brews.ShockingBrew;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.brews.UnstableBrew;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.elixirs.ElixirOfAquaticRejuvenation;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.elixirs.ElixirOfArcaneArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.elixirs.ElixirOfDragonsBlood;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.elixirs.ElixirOfFeatherFall;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.elixirs.ElixirOfHoneyedHealing;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.elixirs.ElixirOfIcyTouch;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.elixirs.ElixirOfMight;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.elixirs.ElixirOfToxicEssence;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.exotic.ExoticPotion;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.ScrollingListPane;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.Reflection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//Cheat-Mod: Liste aller Tränke, Elixiere und Gebräue
public class WndPotions extends Window {

	private static final int WIDTH  = 120;
	private static final int HEIGHT = 180;

	@SuppressWarnings("unchecked")
	private static final List<Class<? extends Potion>> ELIXIRS = Arrays.asList(
			ElixirOfHoneyedHealing.class, ElixirOfAquaticRejuvenation.class,
			ElixirOfMight.class, ElixirOfDragonsBlood.class, ElixirOfIcyTouch.class,
			ElixirOfToxicEssence.class, ElixirOfArcaneArmor.class, ElixirOfFeatherFall.class );

	@SuppressWarnings("unchecked")
	private static final List<Class<? extends Potion>> BREWS = Arrays.asList(
			UnstableBrew.class, CausticBrew.class, BlizzardBrew.class,
			ShockingBrew.class, InfernalBrew.class, AquaBrew.class );

	public WndPotions() {
		super();

		ScrollingListPane list = new ScrollingListPane();
		add( list );

		ArrayList<Class<? extends Potion>> regular = new ArrayList<>();
		for (Class<?> c : Generator.Category.POTION.classes){
			regular.add( c.asSubclass(Potion.class) );
		}

		addSection( list, "regular", regular );
		addSection( list, "exotic", new ArrayList<>(ExoticPotion.regToExo.values()) );
		addSection( list, "elixirs", ELIXIRS );
		addSection( list, "brews", BREWS );

		resize( WIDTH, Math.min( HEIGHT, (int)list.content().height() ) );
		list.setRect( 0, 0, width, height );
	}

	private void addSection( ScrollingListPane list, String titleKey, List<? extends Class<? extends Potion>> classes ){
		list.addTitle( Messages.get(this, titleKey) );
		for (Class<? extends Potion> cls : classes){
			Potion display = displayPotion( cls );
			ScrollingListPane.ListItem item = new ScrollingListPane.ListItem(
					new ItemSprite( display ), Messages.titleCase( display.name() ) ){
				@Override
				public boolean onClick( float x, float y ) {
					if (inside( x, y )) {
						ShatteredPixelDungeon.scene().addToFront( new WndPotionInfo( WndPotions.this, cls ) );
						return true;
					}
					return false;
				}
			};
			list.addItem( item );
		}
	}

	//anonymer Trank zur Anzeige: zeigt Name & Beschreibung, ohne den Trank im Spiel zu identifizieren
	private static Potion displayPotion( Class<? extends Potion> cls ){
		Potion colored = Reflection.newInstance( cls );
		Potion display = Reflection.newInstance( cls );
		display.anonymize();
		display.image = colored.image;
		return display;
	}

	//gibt dem Helden den Trank und führt sofort dessen Standard-Aktion aus (trinken / werfen)
	private static void activate( Class<? extends Potion> cls ){
		Hero hero = Dungeon.hero;
		Potion potion = giveToHero( cls );
		if (potion == null) return;

		Item stack = hero.belongings.getItem( cls );
		if (stack == null) stack = potion;
		stack.execute( hero, stack.defaultAction() );
		Item.updateQuickslot();
	}

	private static Potion giveToHero( Class<? extends Potion> cls ){
		Potion potion = Reflection.newInstance( cls );
		potion.identify();
		if (!potion.collect()){
			GLog.w( Messages.get(WndPotions.class, "full") );
			return null;
		}
		Item.updateQuickslot();
		return potion;
	}

	public static class WndPotionInfo extends WndInfoItem {

		private static final int BTN_HEIGHT = 18;
		private static final int GAP = 2;

		public WndPotionInfo( Window owner, Class<? extends Potion> cls ){
			super( displayPotion( cls ) );

			float y = height + GAP;

			RedButton use = new RedButton( Messages.get(WndPotions.class, "use"), 8 ){
				@Override
				protected void onClick() {
					hide();
					if (owner != null && owner.parent != null) owner.hide();
					activate( cls );
				}
			};
			use.textColor( TITLE_COLOR );
			add( use );
			use.setRect( 0, y, (width - GAP) / 2f, BTN_HEIGHT );

			RedButton give = new RedButton( Messages.get(WndPotions.class, "give"), 8 ){
				@Override
				protected void onClick() {
					if (giveToHero( cls ) != null){
						GLog.p( Messages.get(WndPotions.class, "given", Messages.titleCase(displayPotion(cls).name())) );
					}
				}
			};
			add( give );
			give.setRect( use.right() + GAP, y, width - use.right() - GAP, BTN_HEIGHT );

			resize( width, (int)(y + BTN_HEIGHT) );
		}
	}
}
