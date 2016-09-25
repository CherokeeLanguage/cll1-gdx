package com.cherokeelessons.cll2ev1.screens;

import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.ListIterator;
import java.util.Random;
import java.util.Set;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
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
import com.cherokeelessons.deck.CardUtils;
import com.cherokeelessons.deck.Deck;
import com.cherokeelessons.deck.ICard;

public class LearningSession extends AbstractScreen implements Screen {
	private static final String XMARK = "images/2716_white.png";
	private static final String CHECKMARK = "images/2714_white.png";
	private static final String IMAGES_OVERLAY = "images/overlay-border.png";
	private static final String IMAGES_BACKDROP = "images/white-backdrop.png";
	private final int session;
	private final Deck<CardData> masterDeck;
	private final Deck<CardData> activeDeck;
	private final Deck<CardData> discardsDeck = new Deck<CardData>();
	private final Deck<CardData> completedDeck = new Deck<CardData>();

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
		
		choice1.setTouchable(Touchable.childrenOnly);
		choice2.setTouchable(Touchable.childrenOnly);

		Gdx.app.postRunnable(init1);
		Gdx.app.postRunnable(init2);
		
		assets.load(CHECKMARK, Texture.class);
		assets.load(XMARK, Texture.class);
		assets.finishLoadingAsset(CHECKMARK);
		assets.finishLoadingAsset(XMARK);
		checkmark = assets.get(CHECKMARK, Texture.class);
		xmark=assets.get(XMARK, Texture.class);
		imgCheckmark=new Image(checkmark);
		imgCheckmark.setScaling(Scaling.fit);
		imgCheckmark.setColor(new Color(Color.FOREST));
		imgCheckmark.getColor().a=.75f;
		imgXmark=new Image(xmark);
		imgXmark.setScaling(Scaling.fit);
		imgXmark.setColor(new Color(Color.FIREBRICK));
		imgXmark.getColor().a=.75f;
	}
	
	private Texture checkmark;
	private Texture xmark;
	
	private Image imgCheckmark;
	private Image imgXmark;

	private Table uiTable;
	private Table gameTable;
	private Table challengeTable;
	private Table answersTable;

	private Music challengeAudio;

	private final Label challengeText;
	private final Stack choice1;
	private final Stack choice2;

	protected Runnable init1 = new Runnable() {
		@Override
		public void run() {
			prepDecks();
		}
	};
	protected Runnable init2 = new Runnable() {
		@Override
		public void run() {
			prepUi();
			if (activeDeck.size() == 0) {
				firstTime();
			}
		}
	};

	private boolean loadChallenge = true;
	private float sinceLastNextCard_elapsed = 0f;
	private String previousCardId = null;
	private String activeAudioFile;
	private String activeImageFile1;
	private String activeImageFile2;
	private int correct = 1;

	@Override
	protected void act(float delta) {
		sinceLastNextCard_elapsed += delta;

		// 5-minute check
		if (totalElapsed > 5f * 60f) {
			
		}

		if (loadChallenge) {
			loadNextChallenge();
			loadChallenge = false;
		}
	}

	private void loadNextChallenge() {
		ICard<CardData> card;
		while (true) {
			card = getNextCard();
			CardStats cardStats = card.getCardStats();
			// we just saw this card - leave discarded and grab the next one
			if (card.id().equals(previousCardId)) {
				// set to null in case only one card is left (infinite loop
				// prevention)
				previousCardId = null;
				continue;
			}
			// go ahead and set the delay interval till the next show time
			cardStats.pimsleurSlotInc();
			int pimsleurSlot = cardStats.getPimsleurSlot();
			cardStats.setShowAgainDelay_ms(CardUtils.getNextInterval(pimsleurSlot));
			previousCardId = card.id();
			break;
		}
		CardData cdata = card.getData();
		loadNewChallengeImages(cdata);
		loadNewChallengeAudio(cdata.nextRandomAudioFile());
		stage.addAction(Actions.sequence(Actions.delay(3), Actions.run(new Runnable() {
			public void run() {
				if (correct==1) {
					maybe1.touchDown(null, 0, 0, 0, 0);
				} else {
					maybe2.touchDown(null, 0, 0, 0, 0);
				}
			}
		})));
		stage.addAction(Actions.sequence(Actions.delay(4), Actions.run(new Runnable() {
			public void run() {
				loadChallenge=true;
			}
		})));
	}

	private void loadNewChallengeImages(final CardData cdata) {
		Gdx.app.postRunnable(new Runnable() {
			@Override
			public void run() {
				challengeText.setText(cdata.text);
				
				choice1.clear();
				choice2.clear();
				discardImageFor(activeImageFile1);
				discardImageFor(activeImageFile2);

				Set<String> previousImages = new HashSet<String>();
				previousImages.add(activeImageFile1);
				previousImages.add(activeImageFile2);
				if (isHeads()) {
					correct = 1;
					activeImageFile1 = cdata.nextRandomImageFile();
					if (previousImages.contains(activeImageFile1)) {
						activeImageFile1 = cdata.nextRandomImageFile();
					}
					activeImageFile2 = cdata.nextRandomWrongImageFile();
					if (previousImages.contains(activeImageFile2)) {
						activeImageFile2 = cdata.nextRandomWrongImageFile();
					}
				} else {
					correct = 2;
					activeImageFile1 = cdata.nextRandomWrongImageFile();
					if (previousImages.contains(activeImageFile1)) {
						activeImageFile1 = cdata.nextRandomWrongImageFile();
					}
					activeImageFile2 = cdata.nextRandomImageFile();
					if (previousImages.contains(activeImageFile2)) {
						activeImageFile2 = cdata.nextRandomImageFile();
					}
				}

				choice1.clear();
				choice2.clear();
				choice1.setTouchable(Touchable.childrenOnly);
				choice2.setTouchable(Touchable.childrenOnly);
				for (Image img : getImageFor(activeImageFile1)) {
					choice1.add(img);
					img.addListener(maybe1);
				}
				for (Image img : getImageFor(activeImageFile2)) {
					choice2.add(img);
					img.addListener(maybe2);
				}
			}
		});
	}

	private void loadNewChallengeAudio(final String newActiveAudioFile) {
		Gdx.app.postRunnable(new Runnable() {
			public void run() {
				if (challengeAudio != null) {
					challengeAudio.stop();
				}
				if (activeAudioFile != null) {
					if (assets.isLoaded(activeAudioFile)) {
						assets.unload(activeAudioFile);
					}
				}
				assets.load(newActiveAudioFile, Music.class);
				assets.finishLoadingAsset(newActiveAudioFile);
				challengeAudio = assets.get(newActiveAudioFile, Music.class);
				challengeAudio.play();
				activeAudioFile = newActiveAudioFile;
			}
		});
	}

	private Random coinTosser = new Random();

	private boolean isHeads() {
		return coinTosser.nextBoolean();
	}

	protected ICard<CardData> getNextCard() {
		if (activeDeck.hasCards()) {
			ICard<CardData> card = activeDeck.topCard();
			// go ahead and assign it to the discards deck
			discardsDeck.add(card);
			// return this now "active" card for challenging with
			return card;
		}

		discardsDeck.updateTimeBy((long) (sinceLastNextCard_elapsed * 1000f));
		sinceLastNextCard_elapsed = 0f;
		/**
		 * move to "completed" any cards that don't have tries left
		 */
		ListIterator<ICard<CardData>> iDiscards = discardsDeck.cardsIterator();
		while (iDiscards.hasNext()) {
			ICard<CardData> card = iDiscards.next();
			CardStats cardStats = card.getCardStats();
			if (cardStats.getTriesRemaining() > 0) {
				continue;
			}
			// each completed correct card should it get moved to the next
			// leitner box
			if (cardStats.isCorrect()) {
				cardStats.leitnerBoxInc();
				cardStats.setNextSessionShow(CardUtils.getNextSessionIntervalDays(cardStats.getLeitnerBox()));
				iDiscards.remove(); // remove then add
				completedDeck.add(card);
				log("Bumped: " + card.id());
				continue;
			}
			// each completed wrong card should it get moved to the previous
			// leitner box
			cardStats.leitnerBoxDec();
			cardStats.setNextSessionShow(CardUtils.getNextSessionIntervalDays(cardStats.getLeitnerBox()));
			iDiscards.remove(); // remove then add
			completedDeck.add(card);
		}
		/**
		 * Find all cards in active session ready for display by time
		 */
		discardsDeck.shuffleThenSortByShowAgainDelay();
		iDiscards = discardsDeck.cardsIterator();
		while (iDiscards.hasNext()) {
			ICard<CardData> next = iDiscards.next();
			if (next.getCardStats().getShowAgainDelay_ms() > 0) {
				continue;
			}
			iDiscards.remove(); // remove then add
			activeDeck.add(next);
		}
		/**
		 * If we don't have any active cards, then first see about adding up to
		 * three card from the master deck.
		 */
		masterDeck.shuffleThenSortIntoGroups(3);
		if (activeDeck.size() == 0) {
			for (int count = 0; count < 3; count++) {
				if (masterDeck.size() != 0) {
					activeDeck.add(masterDeck.topCard());
				}
			}
		}
		/**
		 * If we still don't have any active cards, then grab some from the
		 * completed deck.
		 */
		completedDeck.shuffleThenSortByShowAgainDelay();
		if (activeDeck.size() == 0) {
			for (int count = 0; count < 3; count++) {
				if (completedDeck.size() != 0) {
					activeDeck.add(completedDeck.topCard());
				}
			}
		}
		activeDeck.shuffleThenSortByShowAgainDelay();
		// ok, grab first card off of deck
		ICard<CardData> card = activeDeck.topCard();
		// go ahead and assign it to the discards deck
		discardsDeck.add(card);
		// return this now "active" card for challenging with
		return card;
	}

	@Override
	public void render(float delta) {
		super.render(delta);
	}

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
		if (assets.isLoaded(IMAGES_BACKDROP)) {
			assets.unload(IMAGES_BACKDROP);
		}
		if (assets.isLoaded(IMAGES_OVERLAY)) {
			assets.unload(IMAGES_OVERLAY);
		}
		if (assets.isLoaded(imageFile)) {
			assets.unload(imageFile);
		}
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

	private void prepDecks() {
		// clamp leitner box values
		for (ICard<CardData> card : activeDeck.getCards()) {
			CardStats cardStats = card.getCardStats();
			cardStats.setLeitnerBox(Math.max(cardStats.getLeitnerBox(), 0));
		}
		// reset basic statistics and scoring for active cards
		for (ICard<CardData> card : activeDeck.getCards()) {
			CardStats cardStats = card.getCardStats();
			card.resetStats();
			card.resetTriesRemaining();
		}
		// dec next session counter for active cards
		for (ICard<CardData> card : activeDeck.getCards()) {
			CardStats cardStats = card.getCardStats();
			cardStats.setNextSessionShow(Math.max(cardStats.getNextSessionShow() - 1, 0));
		}
		// go ahead and move to the discards deck any cards in the active
		// deck that are scheduled for later sessions
		ListIterator<ICard<CardData>> li = activeDeck.cardsIterator();
		while (li.hasNext()) {
			ICard<CardData> card = li.next();
			CardStats cardStats = card.getCardStats();
			if (cardStats.getNextSessionShow() > 0) {
				li.remove(); // remove before add!
				discardsDeck.add(card);
				continue;
			}
		}
		// shuffle the remaining active cards
		activeDeck.shuffleThenSortByShowAgainDelay();
	}

	private ClickListener playAudioChallenge = new ClickListener() {
		public void clicked(InputEvent event, float x, float y) {
			if (challengeAudio != null && !challengeAudio.isPlaying()) {
				challengeAudio.play();
			}
		}
	};

	protected TextButton btnReplay;

	private void prepUi() {
		uiTable = new Table(skin);
		uiTable.setTouchable(Touchable.childrenOnly);
		uiTable.defaults().expandX();
		TextButton btnBack = new TextButton(CLL2EV1.BACKTEXT, skin);
		btnBack.getLabel().setFontScale(.7f);
		btnReplay = new TextButton("[AUDIO]", skin);
		btnReplay.getLabel().setFontScale(.7f);
		btnReplay.addListener(playAudioChallenge);
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
		challengeText.setText("...");
		challengeTable.add(challengeText).expandX();

		answersTable = new Table(skin);
		answersTable.defaults().expand().fill().pad(4);

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
	}
	
	private ClickListener maybe1 = new ClickListener() {
		public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
			if (correct==1) {
				choice1.addActor(imgCheckmark);
			} else {
				choice1.addActor(imgXmark);
			}
			choice1.setTouchable(Touchable.disabled);
			choice2.setTouchable(Touchable.disabled);
			return true;
		};
	};
	private ClickListener maybe2 = new ClickListener(){
		public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
			if (correct==2) {
				choice2.addActor(imgCheckmark);
			} else {
				choice2.addActor(imgXmark);
			}
			choice1.setTouchable(Touchable.disabled);
			choice2.setTouchable(Touchable.disabled);
			return true;
		};
	};

}
