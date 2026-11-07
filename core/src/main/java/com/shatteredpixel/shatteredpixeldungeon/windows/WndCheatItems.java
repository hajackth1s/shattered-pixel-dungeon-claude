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
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.Scroll;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic.ExoticScroll;
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

//Cheat-Mod: Liste aller Tränke/Elixiere/Gebräue bzw. aller Schriftrollen
public class WndCheatItems extends Window {

	private static final int WIDTH  = 120;
	private static final int HEIGHT = 180;

	@SuppressWarnings("unchecked")
	private static final List<Class<? extends Item>> ELIXIRS = Arrays.asList(
			ElixirOfHoneyedHealing.class, ElixirOfAquaticRejuvenation.class,
			ElixirOfMight.class, ElixirOfDragonsBlood.class, ElixirOfIcyTouch.class,
			ElixirOfToxicEssence.class, ElixirOfArcaneArmor.class, ElixirOfFeatherFall.class );

	@SuppressWarnings("unchecked")
	private static final List<Class<? extends Item>> BREWS = Arrays.asList(
			UnstableBrew.class, CausticBrew.class, BlizzardBrew.class,
			ShockingBrew.class, InfernalBrew.class, AquaBrew.class );

	private final ScrollingListPane list;

	private WndCheatItems() {
		super();
		list = new ScrollingListPane();
		add( list );
	}

	public static WndCheatItems potions(){
		WndCheatItems w = new WndCheatItems();
		w.addSection( "regular", classes( Generator.Category.POTION.classes ) );
		w.addSection( "exotic", new ArrayList<>(ExoticPotion.regToExo.values()) );
		w.addSection( "elixirs", ELIXIRS );
		w.addSection( "brews", BREWS );
		w.finish();
		return w;
	}

	public static WndCheatItems scrolls(){
		WndCheatItems w = new WndCheatItems();
		w.addSection( "scrolls", classes( Generator.Category.SCROLL.classes ) );
		w.addSection( "exotic_scrolls", new ArrayList<>(ExoticScroll.regToExo.values()) );
		w.finish();
		return w;
	}

	private static ArrayList<Class<? extends Item>> classes( Class<?>[] arr ){
		ArrayList<Class<? extends Item>> result = new ArrayList<>();
		for (Class<?> c : arr) result.add( c.asSubclass(Item.class) );
		return result;
	}

	private void finish(){
		resize( WIDTH, Math.min( HEIGHT, (int)list.content().height() ) );
		list.setRect( 0, 0, width, height );
	}

	private void addSection( String titleKey, List<? extends Class<? extends Item>> classes ){
		list.addTitle( Messages.get(this, titleKey) );
		for (Class<? extends Item> cls : classes){
			Item display = displayItem( cls );
			ScrollingListPane.ListItem item = new ScrollingListPane.ListItem(
					new ItemSprite( display ), Messages.titleCase( display.name() ) ){
				@Override
				public boolean onClick( float x, float y ) {
					if (inside( x, y )) {
						ShatteredPixelDungeon.scene().addToFront( new WndItemInfo( WndCheatItems.this, cls ) );
						return true;
					}
					return false;
				}
			};
			list.addItem( item );
		}
	}

	//anonymes Item zur Anzeige: zeigt Name & Beschreibung, ohne die Sorte im Spiel zu identifizieren
	private static Item displayItem( Class<? extends Item> cls ){
		Item colored = Reflection.newInstance( cls );
		Item display = Reflection.newInstance( cls );
		if (display instanceof Potion) ((Potion) display).anonymize();
		if (display instanceof Scroll) ((Scroll) display).anonymize();
		display.image = colored.image;
		return display;
	}

	//gibt dem Helden das Item und führt sofort dessen Standard-Aktion aus (trinken / werfen / lesen)
	private static void activate( Class<? extends Item> cls ){
		Hero hero = Dungeon.hero;
		Item item = giveToHero( cls );
		if (item == null) return;

		Item stack = hero.belongings.getItem( cls );
		if (stack == null) stack = item;
		stack.execute( hero, stack.defaultAction() );
		Item.updateQuickslot();
	}

	private static Item giveToHero( Class<? extends Item> cls ){
		Item potion = Reflection.newInstance( cls );
		potion.identify();
		if (!potion.collect()){
			GLog.w( Messages.get(WndCheatItems.class, "full") );
			return null;
		}
		Item.updateQuickslot();
		return potion;
	}

	public static class WndItemInfo extends WndInfoItem {

		private static final int BTN_HEIGHT = 18;
		private static final int GAP = 2;

		public WndItemInfo( Window owner, Class<? extends Item> cls ){
			super( displayItem( cls ) );

			float y = height + GAP;

			RedButton use = new RedButton( Messages.get(WndCheatItems.class, "use"), 8 ){
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

			RedButton give = new RedButton( Messages.get(WndCheatItems.class, "give"), 8 ){
				@Override
				protected void onClick() {
					if (giveToHero( cls ) != null){
						GLog.p( Messages.get(WndCheatItems.class, "given", Messages.titleCase(displayItem(cls).name())) );
					}
				}
			};
			add( give );
			give.setRect( use.right() + GAP, y, width - use.right() - GAP, BTN_HEIGHT );

			resize( width, (int)(y + BTN_HEIGHT) );
		}
	}
}
