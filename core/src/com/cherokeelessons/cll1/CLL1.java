package com.cherokeelessons.cll1;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.cherokeelessons.cll1.models.GameCard;
import com.cherokeelessons.cll1.screens.MainMenu;
import com.cherokeelessons.cll1.screens.ScreenPoweredBy;

public class CLL1 extends AbstractGame {
	public static final Vector2 WORLDSIZE = new Vector2(1280, 720);
	public static final String BACKDROP = "textures/parchment-seemless.png";
	public static final String SKIN = "skin/serif-68/gdx-holo-freeserif-68.json";
	public static final String CARDS_CSV = "card-data/cards.csv";
	public static final String ACTIVE_CARDS = "CardsInPlay.txt";
	public static final String BACKTEXT = "[BACK]";
	public static final String DECKSTATS = "DeckStats.json";
	public static final String QUIT = "[QUIT]";
	public static final String APP_NAME = "CLL1";
	public final List<GameCard> cards = new ArrayList<GameCard>();
	private ScreenPoweredBy poweredBy = null;
	private final Runnable onPoweredByDone = new Runnable() {
		@Override
		public void run() {
			setScreen(new MainMenu(CLL1.this));
		}
	};
	public boolean deckReady = false;

	@Override
	public void create() {
		super.create();
		poweredBy = new ScreenPoweredBy(CLL1.this, onPoweredByDone);
		addScreen(poweredBy);
		Gdx.input.setCatchBackKey(true);
		Gdx.input.setCatchMenuKey(true);
	}
}
