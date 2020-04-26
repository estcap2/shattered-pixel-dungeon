/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2019 Evan Debenham
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

package com.shatteredpixel.shatteredpixeldungeon.scenes;

import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Chrome;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.GamesInProgress;
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Blacksmith;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Ghost;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Imp;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Wandmaker;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.Potion;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.Ring;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.Scroll;
import com.shatteredpixel.shatteredpixeldungeon.journal.Journal;
import com.shatteredpixel.shatteredpixeldungeon.journal.Notes;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.secret.SecretRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.SpecialRoom;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.Archs;
import com.shatteredpixel.shatteredpixeldungeon.ui.ExitButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.GameLog;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.QuickSlotButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.utils.DungeonSeed;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndGameInProgress;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndLoadSave;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndStartGame;
import com.watabou.noosa.BitmapText;
import com.watabou.noosa.Camera;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.watabou.noosa.NinePatch;
import com.watabou.noosa.ui.Button;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;
import com.watabou.utils.FileUtils;
import com.watabou.utils.SparseArray;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

public class LoadSaveScene extends PixelScene {

	private static final String GAME_FOLDER = "game%d";
	private static final String GAME_FILE	= "game.dat";
	private static final String DEPTH_FILE	= "depth%d.dat";

	private static final int SLOT_WIDTH = 120;
	private static final int SLOT_HEIGHT = 30;
	private static int BTN_WIDTH = 50;
	private static int BTN_HEIGHT = 15;

	private static final String VERSION		= "version";
	private static final String SEED		= "seed";
	private static final String CHALLENGES	= "challenges";
	private static final String HERO		= "hero";
	private static final String GOLD		= "gold";
	private static final String DEPTH		= "depth";
	private static final String DROPPED     = "dropped%d";
	private static final String PORTED      = "ported%d";
	private static final String LEVEL		= "level";
	private static final String LIMDROPS    = "limited_drops";
	private static final String CHAPTERS	= "chapters";
	private static final String QUESTS		= "quests";
	private static final String BADGES		= "badges";

	@Override
	public void create() {
		super.create();

		uiCamera.visible = false;

		int w = Camera.main.width;
		int h = Camera.main.height;

		Archs archs = new Archs();
		archs.setSize( w, h );
		add( archs );

		RenderedTextBlock title = PixelScene.renderTextBlock( Messages.get(this, "title"), 9);
		title.hardlight(Window.TITLE_COLOR);
		title.setPos(
				(w - title.width()) / 2f,
				(20 - title.height()) / 2f
		);
		align(title);
		add(title);

		//TODO: multiple slots, details of savegames (using start screen style)

		RedButton btnSave1 = new RedButton( Messages.get(this, "save") ) {
			@Override
			protected void onClick() {
				//System.out.println("saveA");
				try{
					int slot = 1;
					save(slot);
				}catch (Exception e){
					ShatteredPixelDungeon.reportException( e );
				}
			}
		};

		RedButton btnLoad1 = new RedButton( Messages.get(this, "load") ) {
			@Override
			protected void onClick() {
				try {
					int slot = 1;
					load(slot);
				} catch (IOException e) {
					ShatteredPixelDungeon.reportException(e);
				}
			}
		};

		btnSave1.setRect(10, 100, BTN_WIDTH, BTN_HEIGHT);
		btnLoad1.setRect(80, 100, BTN_WIDTH, BTN_HEIGHT);

		add(btnLoad1);
		add(btnSave1);

		fadeIn();

	}

	/**
	 * CLick on Save State
	 * @param slot
	 * @throws IOException
	 */
	private void save(int slot) throws IOException {
		//TODO. ADD OVERWRITE WARNING
		saveSavegame(slot);
		InterlevelScene.mode = InterlevelScene.Mode.CONTINUE;
		ShatteredPixelDungeon.switchScene(InterlevelScene.class);
	}

	/**
	 * Save into slot
	 * @param slot
	 * @throws IOException
	 */
	private void saveSavegame(int slot) throws IOException{
		if (Dungeon.hero != null && Dungeon.hero.isAlive()) {
			Actor.fixTime();
			saveStateGame( slot );
			saveStateLevel( slot );
		}
	}

	private void loadSavegame(int slot) throws  IOException{
		Mob.clearHeldAllies();
		GameLog.wipe();
		loadStateGame( slot , true);
		if (Dungeon.depth == -1) {
			Dungeon.depth = Statistics.deepestFloor;
			Dungeon.switchLevel( loadStateLevel( slot ), -1 );
		} else {
			Level level = loadStateLevel( slot );
			Dungeon.switchLevel( level, Dungeon.hero.pos );
		}
	}

	/**
	 * Click on Load State
	 * @param slot
	 * @throws IOException
	 */
	private void load (int slot) throws IOException {
		Bundle bundle = FileUtils.bundleFromFile( stateFile( slot ) );
		long seed = bundle.contains( "seed" ) ? bundle.getLong( "seed" ) : DungeonSeed.randomSeed();
		if(Dungeon.seed != seed){
			ShatteredPixelDungeon.scene().add(new WndOptions(
					Messages.get(LoadSaveScene.class, "load_different_game_title"),
					Messages.get(LoadSaveScene.class, "load_different_game_body"),
					Messages.get(LoadSaveScene.class, "load_different_game_yes"),
					Messages.get(LoadSaveScene.class, "load_different_game_no") ) {
				@Override
				protected void onSelect( int index ){
					if (index == 0) {
						try {
							loadSavegame(slot);
							Dungeon.saveAll();
							InterlevelScene.mode = InterlevelScene.Mode.CONTINUE;
							ShatteredPixelDungeon.switchScene(InterlevelScene.class);
						} catch (IOException e) {
							ShatteredPixelDungeon.reportException(e);
						}
					}
				}
			} );
		} else {
			loadSavegame(slot);
			Dungeon.saveAll();
			InterlevelScene.mode = InterlevelScene.Mode.CONTINUE;
			ShatteredPixelDungeon.switchScene(InterlevelScene.class);
		}
	}

	private void saveStateGame( int save ) {
		try {
			Bundle bundle = new Bundle();

			int version = Game.versionCode;
			bundle.put( VERSION, version );
			bundle.put( SEED, Dungeon.seed );
			bundle.put( CHALLENGES, Dungeon.challenges );
			bundle.put( HERO, Dungeon.hero );
			bundle.put( GOLD, Dungeon.gold );
			bundle.put( DEPTH, Dungeon.depth );

			for (int d : Dungeon.droppedItems.keyArray()) {
				bundle.put(Messages.format(DROPPED, d), Dungeon.droppedItems.get(d));
			}

			for (int p :  Dungeon.portedItems.keyArray()){
				bundle.put(Messages.format(PORTED, p),  Dungeon.portedItems.get(p));
			}

			Dungeon.quickslot.storePlaceholders( bundle );

			Bundle limDrops = new Bundle();
			Dungeon.LimitedDrops.store( limDrops );
			bundle.put ( LIMDROPS, limDrops );

			int count = 0;
			int ids[] = new int[ Dungeon.chapters.size()];
			for (Integer id :  Dungeon.chapters) {
				ids[count++] = id;
			}
			bundle.put( CHAPTERS, ids );

			Bundle quests = new Bundle();
			Ghost.Quest.storeInBundle( quests );
			Wandmaker.Quest.storeInBundle( quests );
			Blacksmith.Quest.storeInBundle( quests );
			Imp.Quest.storeInBundle( quests );
			bundle.put( QUESTS, quests );

			SpecialRoom.storeRoomsInBundle( bundle );
			SecretRoom.storeRoomsInBundle( bundle );

			Statistics.storeInBundle( bundle );
			Notes.storeInBundle( bundle );
			Generator.storeInBundle( bundle );

			Scroll.save( bundle );
			Potion.save( bundle );
			Ring.save( bundle );

			Actor.storeNextID( bundle );

			Bundle badges = new Bundle();
			Badges.saveLocal( badges );
			bundle.put( BADGES, badges );

			FileUtils.bundleToFile( stateFile(save), bundle);

		} catch (IOException e) {
			GamesInProgress.setUnknown( save );
			ShatteredPixelDungeon.reportException(e);
		}
	}

	private void saveStateLevel( int save ) throws IOException {
		Bundle bundle = new Bundle();
		bundle.put( LEVEL, Dungeon.level );

		FileUtils.bundleToFile(stateDepthFile( save, Dungeon.depth), bundle);
	}

	private void loadStateGame( int save, boolean fullLoad ) throws IOException {

		Bundle bundle = FileUtils.bundleFromFile( stateFile( save ) );

		Dungeon.version = bundle.getInt( VERSION );

		Dungeon.seed = bundle.contains( SEED ) ? bundle.getLong( SEED ) : DungeonSeed.randomSeed();

		Actor.restoreNextID( bundle );

		Dungeon.quickslot.reset();
		QuickSlotButton.reset();

		Dungeon.challenges = bundle.getInt( CHALLENGES );

		Dungeon.level = null;
		Dungeon.depth = -1;

		Scroll.restore( bundle );
		Potion.restore( bundle );
		Ring.restore( bundle );

		Dungeon.quickslot.restorePlaceholders( bundle );

		if (fullLoad) {

			Dungeon.LimitedDrops.restore( bundle.getBundle(LIMDROPS) );

			Dungeon.chapters = new HashSet<>();
			int ids[] = bundle.getIntArray( CHAPTERS );
			if (ids != null) {
				for (int id : ids) {
					Dungeon.chapters.add( id );
				}
			}

			Bundle quests = bundle.getBundle( QUESTS );
			if (!quests.isNull()) {
				Ghost.Quest.restoreFromBundle( quests );
				Wandmaker.Quest.restoreFromBundle( quests );
				Blacksmith.Quest.restoreFromBundle( quests );
				Imp.Quest.restoreFromBundle( quests );
			} else {
				Ghost.Quest.reset();
				Wandmaker.Quest.reset();
				Blacksmith.Quest.reset();
				Imp.Quest.reset();
			}

			SpecialRoom.restoreRoomsFromBundle(bundle);
			SecretRoom.restoreRoomsFromBundle(bundle);
		}

		Bundle badges = bundle.getBundle(BADGES);
		if (!badges.isNull()) {
			Badges.loadLocal( badges );
		} else {
			Badges.reset();
		}

		Notes.restoreFromBundle( bundle );

		Dungeon.hero = null;
		Dungeon.hero = (Hero)bundle.get( HERO );

		//pre-0.7.0 saves, back when alchemy had a window which could store items
		if (bundle.contains("alchemy_inputs")){
			for (Bundlable item : bundle.getCollection("alchemy_inputs")){

				//try to add normally, force-add otherwise.
				if (!((Item)item).collect(Dungeon.hero.belongings.backpack)){
					Dungeon.hero.belongings.backpack.items.add((Item)item);
				}
			}
		}

		Dungeon.gold = bundle.getInt( GOLD );
		Dungeon.depth = bundle.getInt( DEPTH );

		Statistics.restoreFromBundle( bundle );
		Generator.restoreFromBundle( bundle );

		Dungeon.droppedItems = new SparseArray<>();
		Dungeon.portedItems = new SparseArray<>();
		for (int i=1; i <= 26; i++) {

			//dropped items
			ArrayList<Item> items = new ArrayList<>();
			if (bundle.contains(Messages.format( DROPPED, i )))
				for (Bundlable b : bundle.getCollection( Messages.format( DROPPED, i ) ) ) {
					items.add( (Item)b );
				}
			if (!items.isEmpty()) {
				Dungeon.droppedItems.put( i, items );
			}

			//ported items
			items = new ArrayList<>();
			if (bundle.contains(Messages.format( PORTED, i )))
				for (Bundlable b : bundle.getCollection( Messages.format( PORTED, i ) ) ) {
					items.add( (Item)b );
				}
			if (!items.isEmpty()) {
				Dungeon.portedItems.put( i, items );
			}
		}
	}

	private static Level loadStateLevel( int save ) throws IOException {

		Dungeon.level = null;
		Actor.clear();

		Bundle bundle = FileUtils.bundleFromFile( stateDepthFile( save, Dungeon.depth)) ;

		Level level = (Level)bundle.get( LEVEL );

		if (level == null){
			throw new IOException();
		} else {
			return level;
		}
	}

	private static String stateDepthFile( int slot, int depth ) {
		return stateFolder(slot) + "/" + Messages.format(DEPTH_FILE, depth);
	}

	private String stateFile(int slot){
		return stateFolder(slot) + "/" + GAME_FILE;
	}

	private static String stateFolder( int slot ){
		return GamesInProgress.gameFolder(GamesInProgress.curSlot) + "/savestate" + slot;
	}

	@Override
	protected void onBackPressed() {
		InterlevelScene.mode = InterlevelScene.Mode.CONTINUE;
		ShatteredPixelDungeon.switchScene(InterlevelScene.class);
	}
}
