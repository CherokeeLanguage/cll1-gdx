package com.cherokeelessons.cll2ev1.screens;

import java.nio.charset.StandardCharsets;
import java.util.Collections;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.cherokeelessons.cll2ev1.CLL2EV1;
import com.cherokeelessons.cll2ev1.models.CardData;
import com.cherokeelessons.deck.Card;

public class MainMenu extends AbstractScreen {
	private static final String SKIN = CLL2EV1.SKIN;
	private static final String QUIT = "Quit - ᎠᏑᎶᎪᏍᏗ";
	private static final String ABOUT = "About - ᎢᎸᏢ";
	private static final String OPTIONS = "Options - ᎠᏑᏰᏍᏗᎢ";
	// private static final String HIGH_SCORES = "High Scores - ᏬᏍᏓ ᏗᏎᏍᏗ";
	private static final String PRACTICE = "Practice - ᏣᎪᏅᏗ";
	private static final String TITLE = "Cherokee Language Lessons Vol. 1";

	private ClickListener onNewGame = new ClickListener() {
		@Override
		public void clicked(InputEvent event, float x, float y) {
			game.addScreen(new SelectSession(game));
			Collections.shuffle(null);
		};
	};
	private ClickListener onOptions = new ClickListener();
	private ClickListener onAbout = new ClickListener() {
		@Override
		public void clicked(InputEvent event, float x, float y) {
			game.addScreen(new About(game));
		}
	};
	private ClickListener onQuit = new ClickListener() {
		@Override
		public void clicked(InputEvent event, float x, float y) {
			game.setScreen(new Quit(game));
		};
	};

	public MainMenu(CLL2EV1 game) {
		super(game);
		setSkin(SKIN);
		setBackdrop(CLL2EV1.BACKDROP);
		Gdx.app.postRunnable(loadDeck);
	}
	
	protected Runnable loadDeck = new Runnable() {
		@Override
		public void run() {
			Gdx.app.postRunnable(init);
			log("Loading deck from "+CLL2EV1.CARDS_CSV);
			String tmpCards = Gdx.files.internal(CLL2EV1.CARDS_CSV).readString(StandardCharsets.UTF_8.name());
			String[] tmpLines = tmpCards.split("\n");
			log("Loaded "+tmpLines.length+" records.");
			int activeChapter=0;
			for (String tmpLine: tmpLines) {
				String[] tmpCard = tmpLine.split("\t", -1);
				if (tmpCard.length<5){
					continue;
				}
				if (tmpCard[0].startsWith("#")){
					continue;
				}
				if (!tmpCard[0].trim().isEmpty()) {
					try {
						activeChapter=Integer.valueOf(tmpCard[0]);
					} catch (NumberFormatException e) {
					}
				}
				if (tmpCard[1].trim().isEmpty()) {
					continue;
				}
				CardData data = new CardData();
				data.chapter=activeChapter;
				data.text=tmpCard[1].trim();
				data.audio=tmpCard[2].trim();
				data.answerPic=tmpCard[3].trim();
				data.blacklistPic=tmpCard[4].trim();
				Card<CardData> card = new Card<CardData>();
				card.setData(data);
				CLL2EV1.deck.add(card);
			}
			log("Deck built. Have "+CLL2EV1.deck.size()+" cards.");
			CLL2EV1.deck.shuffle();
			CLL2EV1.deck.sort(3);
			while (CLL2EV1.deck.hasNext()){
				CardData data = CLL2EV1.deck.next().getData();
				System.out.println(data.chapter+"] "+data.text);
			}
		}
	};
	
	protected Runnable init = new Runnable() {
		@Override
		public void run() {
			Label titleLabel = new Label(TITLE, skin);
			TextButton btnNewGame = new TextButton(PRACTICE, skin);
			TextButton btnOptions = new TextButton(OPTIONS, skin);
			TextButton btnAbout = new TextButton(ABOUT, skin);
			TextButton btnQuit = new TextButton(QUIT, skin);
			
			btnNewGame.addListener(onNewGame);
			btnOptions.addListener(onOptions);
			btnAbout.addListener(onAbout);
			btnQuit.addListener(onQuit);
			
			Table menu = new Table(skin);
			menu.setFillParent(true);
			menu.defaults().expand();
			menu.row();
			menu.add(titleLabel);
			menu.row();
			menu.add(btnNewGame);
			menu.row();
			menu.add(btnOptions);
			menu.row();
			menu.add(btnAbout);
			menu.row();
			menu.add(btnQuit);
			menu.pack();
			
			stage.addActor(menu);				
		}
	};

	@Override
	protected boolean onBack() {
		return false;
	}

	@Override
	protected boolean onMenu() {
		return false;
	}
}
