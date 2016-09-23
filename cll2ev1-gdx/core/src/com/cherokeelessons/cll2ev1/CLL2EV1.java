package com.cherokeelessons.cll2ev1;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.cherokeelessons.cll2ev1.models.CardData;
import com.cherokeelessons.cll2ev1.screens.MainMenu;
import com.cherokeelessons.cll2ev1.screens.ScreenPoweredBy;
import com.cherokeelessons.deck.Card;

public class CLL2EV1 extends AbstractGame {
	public final List<Card<CardData>> cards=new ArrayList<Card<CardData>>();
	public static final Vector2 WORLDSIZE = new Vector2(1280, 720);
	public static final String BACKDROP = "textures/parchment-seemless.png";
	public static final String SKIN = "skin/serif-68/gdx-holo-freeserif-68.json";
	public static final String CARDS_CSV = "card-data/cards.csv";
	private ScreenPoweredBy poweredBy = null;
	private Runnable onPoweredByDone = new Runnable() {
		public void run() {
			setScreen(new MainMenu(CLL2EV1.this));
		}
	};
	public boolean deckReady=false;

	@Override
	public void create() {
		super.create();
		poweredBy = new ScreenPoweredBy(CLL2EV1.this, onPoweredByDone);
		addScreen(poweredBy);
		Gdx.input.setCatchBackKey(true);
		Gdx.input.setCatchMenuKey(true);
	}
}
