package com.cherokeelessons.cll2ev1.screens;

import java.nio.charset.StandardCharsets;
import java.util.ListIterator;

import javax.swing.text.LayeredHighlighter;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import com.cherokeelessons.cll2ev1.AbstractGame;
import com.cherokeelessons.cll2ev1.CLL2EV1;
import com.cherokeelessons.cll2ev1.models.CardData;
import com.cherokeelessons.deck.CardStats;
import com.cherokeelessons.deck.Deck;
import com.cherokeelessons.deck.DeckStats;
import com.cherokeelessons.deck.ICard;

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

		challengeText = new Label("", skin);
		choice1 = new Stack();
		choice2 = new Stack();

		Gdx.app.postRunnable(init);
	}

	private Table uiTable;
	private Table gameTable;
	private Table challengeTable;
	private Table answersTable;

	private Music challengeAudio;

	private final Label challengeText;
	private final Stack choice1;
	private final Stack choice2;

	protected Runnable init = new Runnable() {
		@Override
		public void run() {
			//clamp leitner box values
			for (ICard<CardData> card : activeDeck.getCards()) {
				CardStats cardStats = card.getCardStats();
				card.setLeitnerBox(Math.max(card.getLeitnerBox(), 0));
			}
			//reset basic statistics and scoring for active cards
			for (ICard<CardData> card : activeDeck.getCards()) {
				CardStats cardStats = card.getCardStats();
				card.resetStats();
				card.resetTriesRemaining();
			}
			//dec next session counter for active cards
			for (ICard<CardData> card : activeDeck.getCards()) {
				CardStats cardStats = card.getCardStats();
				card.setNextSessionShow(Math.max(card.getNextSessionShow()-1, 0));
			}
			//go ahead and move to the discards deck any cards in the active
			//deck that are scheduled for later sessions
			ListIterator<ICard<CardData>> li = activeDeck.cardsIterator(); 
			while (li.hasNext()) {
				ICard<CardData> card = li.next();
				if (card.getNextSessionShow()>0) {
					li.remove(); //remove before add!
					discardsDeck.add(card);
					continue;
				}
			}
			uiTable = new Table(skin);
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

			gameTable = new Table(skin);
			gameTable.setFillParent(true);
			gameTable.defaults().expand().fill();
			gameTable.setTouchable(Touchable.childrenOnly);

			challengeTable = new Table(skin);
			challengeTable.row();
			challengeText.setText("ᎤᏲᎢ");
			challengeTable.add(challengeText).expandX();

			answersTable = new Table(skin);
			answersTable.defaults().expand().fill().pad(4);

			choice1.clearChildren();
			for (Image img : getImageFor("card-data/images/04/osda_01.png")) {
				choice1.addActor(img);
			}
			;

			choice2.clearChildren();
			for (Image img : getImageFor("card-data/images/04/uyoi_03.png")) {
				choice2.addActor(img);
			}
			;

			stage.addAction(Actions.sequence(Actions.delay(5), Actions.run(new Runnable() {
				@Override
				public void run() {
					challengeText.setText("ᎤᏲᎢ");
					choice2.clearChildren();
					for (Image img : getImageFor("card-data/images/04/osda_03.png")) {
						choice2.addActor(img);
					}
					choice1.clearChildren();
					for (Image img : getImageFor("card-data/images/04/uyoi_01.png")) {
						choice1.addActor(img);
					}
					assets.load("card-data/audio/04/osda.mp3", Music.class);
					assets.finishLoadingAsset("card-data/audio/04/osda.mp3");
					challengeAudio = assets.get("card-data/audio/04/osda.mp3", Music.class);
					challengeAudio.play();

				}
			})));

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

			choice1.addListener(new ClickListener());
			choice2.addListener(new ClickListener());

			assets.load("card-data/audio/04/uyoi.mp3", Music.class);
			assets.finishLoadingAsset("card-data/audio/04/uyoi.mp3");
			challengeAudio = assets.get("card-data/audio/04/uyoi.mp3", Music.class);
			challengeAudio.play();
			
			if (activeDeck.size()==0) {
				firstTime();
			}
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
	
	protected void firstTime() {
		userPause();
		pausedStage.clear();
		Dialog firstTimeNotice = new Dialog("HOW THIS WORKS.", skin) {
			@Override
			protected void result(Object object) {
				userResume();
			}
		};
		pausedStage.addActor(firstTimeNotice);
		firstTimeNotice.setFillParent(true);
		firstTimeNotice.getTitleLabel().setAlignment(Align.center);
		firstTimeNotice.button("ᎰᏩ");
		
		firstTimeNotice.getContentTable().row();
		Table noticeTable = new Table(skin);
		noticeTable.defaults().expand().fill();
		firstTimeNotice.getContentTable().add(noticeTable).expand().fill();
		String txt = Gdx.files.internal("text/how-this-works.txt").readString(StandardCharsets.UTF_8.name());
		Label message = new Label(txt, skin);
		message.setFontScale(.85f);
		message.setWrap(true);
		noticeTable.add(message);
		firstTimeNotice.show(pausedStage);
	}

	@Override
	protected boolean onMenu() {
		return false;
	}

	protected void discardImageFor(String imageFile) {
		assets.unload(IMAGES_BACKDROP);
		assets.unload(IMAGES_OVERLAY);
		assets.unload(imageFile);
	}

	protected Image[] getImageFor(String imageFile) {
		if (!assets.isLoaded(IMAGES_BACKDROP)) {
			assets.load(IMAGES_BACKDROP, Texture.class);
			assets.finishLoadingAsset(IMAGES_BACKDROP);
		}
		if (!assets.isLoaded(IMAGES_OVERLAY)) {
			assets.load(IMAGES_OVERLAY, Texture.class);
			assets.finishLoadingAsset(IMAGES_OVERLAY);
		}
		if (!assets.isLoaded(imageFile)) {
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
		choice1.addActor(backdrop);
		choice1.addActor(image);
		choice1.addActor(overlay);
		return new Image[] { backdrop, image, overlay };// choice1;
	}

}
