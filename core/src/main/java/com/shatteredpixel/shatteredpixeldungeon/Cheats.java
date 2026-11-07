/*
 * Shattered Pixel Dungeon - Cheat-Mod
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.shatteredpixel.shatteredpixeldungeon;

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Hunger;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfHealing;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfUpgrade;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.watabou.utils.Bundle;

import java.util.ArrayList;

//Zentrale Stelle für alle Cheat-Funktionen und den gespeicherten Cheat-Zustand
public class Cheats {

	//wird mit dem Spielstand gespeichert
	public static boolean godMode = false;
	public static boolean alwaysHit = false;

	private static final String GOD_MODE   = "cheat_god_mode";
	private static final String ALWAYS_HIT = "cheat_always_hit";

	public static void reset(){
		godMode = false;
		alwaysHit = false;
	}

	public static void storeInBundle( Bundle bundle ){
		bundle.put( GOD_MODE, godMode );
		bundle.put( ALWAYS_HIT, alwaysHit );
	}

	public static void restoreFromBundle( Bundle bundle ){
		godMode = bundle.contains( GOD_MODE ) && bundle.getBoolean( GOD_MODE );
		alwaysHit = bundle.contains( ALWAYS_HIT ) && bundle.getBoolean( ALWAYS_HIT );
	}

	public static boolean godModeActive( Hero hero ){
		return godMode && hero != null && hero == Dungeon.hero;
	}

	public static void addStrength( int amount ){
		Dungeon.hero.STR += amount;
		Item.updateQuickslot();
	}

	public static void addGold( int amount ){
		Dungeon.gold += amount;
	}

	public static void addEnergy( int amount ){
		Dungeon.energy += amount;
	}

	public static void fullHeal(){
		Hero hero = Dungeon.hero;
		hero.HP = hero.HT;
		PotionOfHealing.cure( hero );
		Hunger hunger = hero.buff( Hunger.class );
		if (hunger != null) hunger.satisfy( Hunger.STARVING );
	}

	public static void levelUp(){
		Hero hero = Dungeon.hero;
		if (hero.lvl >= Hero.MAX_LEVEL) return;
		hero.earnExp( hero.maxExp() - hero.exp, Cheats.class );
	}

	public static void identifyAll(){
		ArrayList<Item> items = Dungeon.hero.belongings.getAllItems( Item.class );
		for (Item i : items){
			if (!i.isIdentified()) i.identify();
		}
		Item.updateQuickslot();
	}

	public static void giveUpgrades( int amount ){
		Item scroll = new ScrollOfUpgrade().quantity( amount );
		scroll.identify();
		if (!scroll.collect()){
			Dungeon.level.drop( scroll, Dungeon.hero.pos ).sprite.drop();
		}
	}

	public static void revealMap(){
		int length = Dungeon.level.length();
		for (int i = 0; i < length; i++){
			if (Dungeon.level.discoverable[i]){
				Dungeon.level.mapped[i] = true;
				int terr = Dungeon.level.map[i];
				if ((Terrain.flags[terr] & Terrain.SECRET) != 0){
					Dungeon.level.discover( i );
					GameScene.updateMap( i );
				}
			}
		}
		GameScene.updateFog();
	}
}
