package com.cherokeelessons.cll2ev1.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import com.cherokeelessons.cll2ev1.AbstractGame;
import com.cherokeelessons.cll2ev1.CLL2EV1;
import com.cherokeelessons.cll2ev1.models.CardData;
import com.cherokeelessons.deck.Deck;

public class LearningSession extends AbstractScreen implements Screen {
	private static final String IMAGES_OVERLAY = "images/overlay-border.png";
	private static final String IMAGES_BACKDROP = "images/white-backdrop.png";
	private final int session;
	private final Deck<CardData> masterDeck;
	private final Deck<CardData> activeDeck;
	private final Deck<CardData> discardsDeck = new Deck<CardData>();

	public LearningSession(AbstractGame game, int session, Deck<CardData> masterDeck, Deck<CardData> activeDeck) {
		super(game);
		this.session = session;
		this.masterDeck = masterDeck;
		this.activeDeck = activeDeck;
		setBackdrop(CLL2EV1.BACKDROP);
		setSkin(CLL2EV1.SKIN);
		Gdx.app.postRunnable(init);
	}
	
	protected Runnable init = new Runnable() {
		@Override
		public void run() {
			Table uiTable = new Table(skin);
			uiTable.setTouchable(Touchable.childrenOnly);
			uiTable.defaults().expandX();
			TextButton btnBack = new TextButton(CLL2EV1.BACKTEXT, skin);
			btnBack.getLabel().setFontScale(.7f);
			TextButton btnReplay = new TextButton("[AUDIO]", skin);
			btnReplay.getLabel().setFontScale(.7f);
			btnBack.pack();
			btnBack.addListener(onBack);
			uiTable.row();
			uiTable.add(btnBack).top().left();
			uiTable.add(btnReplay).right();
			
			Table gameTable = new Table(skin);
			gameTable.setFillParent(true);
			gameTable.defaults().expand().fill();
			gameTable.setTouchable(Touchable.childrenOnly);
			
			Table challengeTable = new Table(skin);
			challengeTable.row();
			challengeTable.add("ᏌᏊ ᏔᎵ ᏦᎢ ᏅᎩ ᎯᏍᎩ ᏑᏓᎵ ᎦᎵᏉᎩ!").expandX();
			
			Table answersTable = new Table(skin);
			answersTable.defaults().expand().fill().pad(4);
			Stack choice1 = getImageFor("card-data/images/04/osda_02.png");
			Stack choice2 = getImageFor("card-data/images/04/uyoi_02.png");
			answersTable.row();
			answersTable.add(choice1);
			answersTable.add(choice2);
			
			gameTable.row();
			gameTable.add(challengeTable).expand(true, false).fillX();
			gameTable.row();
			gameTable.add(answersTable);
			gameTable.row();
			gameTable.add(uiTable).expand(true, false).fillX();
			
			stage.addActor(gameTable);
			stage.setDebugAll(true);
			
			choice1.addListener(new ClickListener());
			choice2.addListener(new ClickListener());
			
			assets.load("card-data/audio/04/uyoi.mp3", Music.class);
			assets.finishLoadingAsset("card-data/audio/04/uyoi.mp3");
			Music audio = assets.get("card-data/audio/04/uyoi.mp3", Music.class);
			audio.play();
		}
	};
	
	private ClickListener onBack = new ClickListener() {
		@Override
		public void clicked(InputEvent event, float x, float y) {
			onBack();
		};
	};

	@Override
	protected boolean onBack() {
		userPause();
		pausedStage.clear();
		Dialog cancelSession = new Dialog("CANCEL SESSION?", skin) {
			@Override
			protected void result(Object object) {
				if ("YES".equals(object)) {
					game.previousScreen();
				}
				userResume();
			}
		};
		pausedStage.addActor(cancelSession);
		cancelSession.getTitleLabel().setAlignment(Align.center);
		cancelSession.button("ᎥᎥ - YES", "YES");
		cancelSession.button("ᎥᏝ - NO", "NO");
		cancelSession.getContentTable().row();
		cancelSession.text("Are you sure you want to cancel?");
		cancelSession.getContentTable().row();
		cancelSession.text("You will lose all of your");
		cancelSession.getContentTable().row();
		cancelSession.text("progress if you choose ᎥᎥ!");
		cancelSession.show(pausedStage);
		return true;
	}

	@Override
	protected boolean onMenu() {
//		userPauseToggle();
//		if (isUserPaused()) {
//			pausedStage.clear();
//			Dialog paused = new Dialog("PAUSED", skin) {
//				@Override
//				protected void result(Object object) {
//					userResume();
//				}
//			};
//			pausedStage.addActor(paused);
//			paused.getTitleLabel().setAlignment(Align.center);
//			paused.button("RESUME");
//			paused.setFillParent(true);
//			paused.show(pausedStage);
//		}
		return true;
	}

	protected void discardImageFor(String imageFile){
		assets.unload(IMAGES_BACKDROP);
		assets.unload(IMAGES_OVERLAY);
		assets.unload(imageFile);
	}
	protected Stack getImageFor(String imageFile) {
		if (!assets.isLoaded(IMAGES_BACKDROP)) {
			assets.load(IMAGES_BACKDROP, Texture.class);
			assets.finishLoadingAsset(IMAGES_BACKDROP);
		}
		if (!assets.isLoaded(IMAGES_OVERLAY)) {
			assets.load(IMAGES_OVERLAY, Texture.class);
			assets.finishLoadingAsset(IMAGES_OVERLAY);
		}
		if (!assets.isLoaded(imageFile)){
			assets.load(imageFile, Texture.class);
			assets.finishLoadingAsset(imageFile);
		}
		assets.finishLoading();
		Image backdrop = new Image(assets.get(IMAGES_BACKDROP, Texture.class));
		Image image = new Image(assets.get(imageFile, Texture.class));
		Image overlay = new Image(assets.get(IMAGES_OVERLAY, Texture.class));
		backdrop.setScaling(Scaling.fit);
		image.setScaling(Scaling.fit);
		overlay.setScaling(Scaling.fit);
		Stack choice1 = new Stack();
		choice1.add(backdrop);
		choice1.add(image);
		choice1.add(overlay);
		return choice1;
	}

}
