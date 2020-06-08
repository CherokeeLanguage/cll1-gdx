package com.cherokeelessons.cll1.screens;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;
import java.util.Set;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Music.OnCompletionListener;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Scaling;
import com.cherokeelessons.cll1.AbstractGame;
import com.cherokeelessons.cll1.CLL1;
import com.cherokeelessons.cll1.models.CardData;
import com.cherokeelessons.deck.CardStats;
import com.cherokeelessons.deck.CardUtils;
import com.cherokeelessons.deck.Deck;
import com.cherokeelessons.deck.DeckStats;
import com.cherokeelessons.deck.ICard;
import com.cherokeelessons.util.SlotFolder;

public class LearningSession extends AbstractScreen implements Screen {

	private static final long DAY_ms = 24l * 60l * 60l * 1000l;

	/**
	 * Eight-bit UCS Transformation Format
	 */
	public static final Charset UTF_8 = Charset.forName("UTF-8");

	private static final float PIX_MAGIC_WIDTH_NEW_CARDS_PERCENT = .375f;
	private static final String DING = "audio/ding.mp3";
	private static final String BUZZER = "audio/buzzer2.mp3";
	private static final String XMARK = "images/2716_white.png";
	private static final String CHECKMARK = "images/2714_white.png";
	private static final String IMAGES_OVERLAY = "images/overlay-border.png";
	private static final String IMAGES_BACKDROP = "images/white-backdrop.png";
	private final int session;
	private final Deck<CardData> masterDeck;
	private final Deck<CardData> activeDeck;
	private final Deck<CardData> discardsDeck = new Deck<CardData>();
	private final Deck<CardData> completedDeck = new Deck<CardData>();

	private final float maxTime_secs = 5.0f * 60f;

	private final Texture checkmark;

	private final Texture xmark;

	private final Image imgCheckmark;

	private final Image imgXmark;
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
			initUi();
		}
	};
	protected Runnable firstPlay = new Runnable() {
		@Override
		public void run() {
			if (activeDeck.size() == 0 && completedDeck.size() == 0 && discardsDeck.size() == 0) {
				stage.addAction(actionFirstTime());
			} else {
				stage.addAction(actionLoadNextChallengeQuick());
			}
		}
	};
	// private float sinceLastDeckShuffle_elapsed = 0f;
	private String previousCardId = null;
	private String activeAudioFile;

	private String activeImageFile1;

	private String activeImageFile2;
	private int correct = 1;
	// private ICard<CardData> activeCard;
	private CardData activeCardData;
	private CardStats activeCardStats;
	private final Runnable runLoadNextChallenge = new Runnable() {
		@Override
		public void run() {
			if (challengeAudio != null && challengeAudio.isPlaying()) {
				challengeAudio.setOnCompletionListener(new OnCompletionListener() {
					@Override
					public void onCompletion(final Music music) {
						music.setOnCompletionListener(null);
						stage.addAction(actionLoadNextChallengeQuick());
					}
				});
				return;
			}
			// 5-minute check
			if (totalElapsed > maxTime_secs) {
				log("=== SESSION TIME UP!");
				pausedStage.addAction(actionShowCompletedDialog());
				userPause();
				return;
			}
			loadNextChallenge();
		}

	};
	private final Random coinTosser = new Random();
	private final ClickListener onBack = new ClickListener() {
		@Override
		public void clicked(final InputEvent event, final float x, final float y) {
			onBack();
		};
	};

	private final RefCounts refCounts = new RefCounts();

	private final Runnable replayAudio = new Runnable() {
		@Override
		public void run() {
			if (challengeAudio != null && !challengeAudio.isPlaying()) {
				challengeAudio.play();
			}
		}
	};

	private final ClickListener playAudioChallenge = new ClickListener() {
		@Override
		public void clicked(final InputEvent event, final float x, final float y) {
			Gdx.app.postRunnable(replayAudio);
		}
	};

	private final ClickListener showEnglish = new ClickListener() {
		@Override
		public void clicked(final InputEvent event, final float x, final float y) {
			activeCardStats.setCorrect(false);
			showNewCardWithAudio();
		}
	};

	protected TextButton btnHelp;

	protected TextButton btnAudio;

	protected final Label lblCountdown;

	private final ClickListener maybe1 = new ClickListener() {
		@Override
		public boolean touchDown(final InputEvent event, final float x, final float y, final int pointer,
				final int button) {
			stage.addAction(actionLoadNextChallengeDelayed());
			choice1.setTouchable(Touchable.disabled);
			choice2.setTouchable(Touchable.disabled);
			// update card with total time it has been on display
			activeCardStats.setTotalShownTime(activeCardStats.getTotalShownTime() + currentElapsed);
			if (correct == 1) {
				choice1.addActor(imgCheckmark);
				ding();
			} else {
				choice1.addActor(imgXmark);
				if (activeCardStats.isCorrect()) {
					activeCardStats.pimsleurSlotDec();
					activeCardStats.triesRemainingInc();
					activeCardStats.setCorrect(false);
				}
				buzz();
			}
			return true;
		};
	};

	private final ClickListener maybe2 = new ClickListener() {
		@Override
		public boolean touchDown(final InputEvent event, final float x, final float y, final int pointer,
				final int button) {
			stage.addAction(actionLoadNextChallengeDelayed());
			choice1.setTouchable(Touchable.disabled);
			choice2.setTouchable(Touchable.disabled);
			// update card with total time it has been on display
			activeCardStats.setTotalShownTime(activeCardStats.getTotalShownTime() + currentElapsed);
			if (correct == 2) {
				choice2.addActor(imgCheckmark);
				ding();
			} else {
				choice2.addActor(imgXmark);
				if (activeCardStats.isCorrect()) {
					activeCardStats.pimsleurSlotDec();
					activeCardStats.triesRemainingInc();
					activeCardStats.setCorrect(false);
				}
				buzz();
			}
			return true;
		};
	};

	private Sound ding;

	private Sound buzz;

	public LearningSession(final AbstractGame game, final int session, final Deck<CardData> masterDeck,
			final Deck<CardData> activeDeck) {
		super(game);
		this.session = session;
		this.masterDeck = masterDeck;
		this.activeDeck = activeDeck;
		setBackdrop(CLL1.BACKDROP);
		setSkin(CLL1.SKIN);

		log("Session: " + session);
		log("Master Deck Size: " + masterDeck.size());
		log("Active Deck Size: " + activeDeck.size());
		log("First Time: " + (activeDeck.size() == 0));

		stage.addAction(actionUpdateTimeLeft());

		lblCountdown = new Label("0:00", skin);
		lblCountdown.setFontScale(.75f);

		challengeText = new Label("", skin);
		choice1 = new Stack();
		choice2 = new Stack();

		choice1.setTouchable(Touchable.childrenOnly);
		choice2.setTouchable(Touchable.childrenOnly);

		Gdx.app.postRunnable(init1);
		Gdx.app.postRunnable(init2);
		Gdx.app.postRunnable(firstPlay);

		assets.load(CHECKMARK, Texture.class);
		assets.load(XMARK, Texture.class);
		assets.finishLoadingAsset(CHECKMARK);
		assets.finishLoadingAsset(XMARK);
		checkmark = assets.get(CHECKMARK, Texture.class);
		xmark = assets.get(XMARK, Texture.class);
		imgCheckmark = new Image(checkmark);
		imgCheckmark.setScaling(Scaling.fit);
		imgCheckmark.setColor(new Color(Color.FOREST));
		imgCheckmark.getColor().a = .75f;
		imgXmark = new Image(xmark);
		imgXmark.setScaling(Scaling.fit);
		imgXmark.setColor(new Color(Color.FIREBRICK));
		imgXmark.getColor().a = .75f;

		assets.load(BUZZER, Sound.class);
		assets.load(DING, Sound.class);
	}

	@Override
	protected void act(final float delta) {
		// sinceLastDeckShuffle_elapsed += delta;
	}

	private Action actionFirstTime() {
		return Actions.run(new Runnable() {
			@Override
			public void run() {
				firstTime();
			}
		});
	}

	private Action actionLoadNextChallengeDelayed() {
		return Actions.sequence(Actions.delay(1.5f), Actions.run(runLoadNextChallenge));
	}

	private Action actionLoadNextChallengeQuick() {
		return Actions.sequence(Actions.delay(.1f), Actions.run(runLoadNextChallenge));
	}

	private Action actionSaveActiveDeck(final Button howa) {
		return Actions.run(new Runnable() {
			@Override
			public void run() {
				saveActiveDeck();
				howa.setDisabled(false);
			}
		});
	}

	private Action actionShowCompletedDialog() {
		return Actions.run(new Runnable() {
			@Override
			public void run() {
				endSessionCleanup();
				showFinalStats();
			}
		});
	}

	private Action actionUpdateTimeLeft() {
		return Actions.sequence(Actions.delay(.15f), Actions.run(new Runnable() {
			@Override
			public void run() {
				stage.addAction(actionUpdateTimeLeft());
				updateTimeleft();
			}
		}));
	}

	private void addCardFromMasterDeck() {
		/**
		 * We don't have any active cards, add one from the master deck. Being sure to
		 * set all stats to "never shown/correct".
		 */
		log("Getting new card from master deck ...");
		masterDeck.shuffleThenSortIntoPrefixedGroups(CardData.SORT_KEY_LENGTH);
		if (masterDeck.size() != 0) {
			final ICard<CardData> topCard = masterDeck.topCard();
			topCard.resetStats();
			topCard.resetTriesRemaining(CardData.MAX_TRIES);
			topCard.getCardStats().setPimsleurSlot(0);
			topCard.getCardStats().setNewCard(true);
			activeDeck.add(topCard);
		}
	}

	private void addCardsFromCompletedDeck() {
		/**
		 * We still don't have any active cards, grab some from the completed deck.
		 * Being sure to set all stats to "never shown/correct". This is like an
		 * emergency don't crash me measure.
		 */
		log("Retrieving old cards from completed deck ignoring next session time...");
		completedDeck.shuffleThenSortByNextSession();
		for (int count = 0; count < 3; count++) {
			if (completedDeck.size() != 0) {
				final ICard<CardData> topCard = completedDeck.topCard();
				topCard.resetStats();
				topCard.resetTriesRemaining(CardData.MAX_TRIES);
				topCard.getCardStats().setPimsleurSlot(0);
				activeDeck.add(topCard);
			}
		}
	}

	private void addFromDiscards(int maxCount) {
		// put all discards into the active deck w/o looking at show next times
		log("Moving ALL discards into active deck ...");
		discardsDeck.shuffleThenSortByShowAgainDelay();
		while (discardsDeck.hasCards() && --maxCount > 0) {
			activeDeck.add(discardsDeck.topCard());
		}
	}

	private void addThisSessionCardsFromCompletedDeck() {
		/**
		 * We still don't have any active cards, grab some from the completed deck.
		 * Being sure to set all stats to "never shown/correct". This is like an
		 * emergency don't crash me measure.
		 */
		log("Retrieving cards from completed deck that should be shown this session...");
		completedDeck.shuffleThenSortByNextSession();
		for (int count = 0; count < 3; count++) {
			if (completedDeck.size() != 0) {
				final ICard<CardData> topCard = completedDeck.topCard();
				if (topCard.getCardStats().getNextSessionShow() > 0) {
					// no cards ready for showing
					break;
				}
				topCard.resetStats();
				topCard.resetTriesRemaining(CardData.MAX_TRIES);
				topCard.getCardStats().setPimsleurSlot(0);
				activeDeck.add(topCard);
			}
		}
	}

	private void buzz() {
		if (buzz == null) {
			if (!assets.isLoaded(BUZZER)) {
				assets.load(BUZZER, Sound.class);
				assets.finishLoadingAsset(BUZZER);
			}
			buzz = assets.get(BUZZER, Sound.class);
		}
		buzz.play(.6f);
	}

	/**
	 * Move to "completed" any cards that don't have tries left taking care to
	 * update Leitner boxes as needed.
	 */
	private void completedCardsCheck() {
		log("Scanning discards for newly completed cards... ");
		final ListIterator<ICard<CardData>> iDiscards = discardsDeck.cardsIterator();
		while (iDiscards.hasNext()) {
			final ICard<CardData> card = iDiscards.next();
			final CardStats cardStats = card.getCardStats();
			if (cardStats.getTriesRemaining() > 0) {
				continue;
			}
			log("=== Completed card: " + card.id());
			if (cardStats.isCorrect()) {
				// if correct, the card should get moved to the next
				// leitner box
				cardStats.leitnerBoxInc();
			} else {
				// if wrong, it should get moved to the previous
				// leitner box
				cardStats.leitnerBoxDec();
			}
			cardStats.setNextSessionShow(CardUtils.getNextSessionIntervalDays(cardStats.getLeitnerBox()));
			iDiscards.remove(); // remove then add
			completedDeck.add(card);
			log("- Moved to Leitner box: " + cardStats.getLeitnerBox());
			log("- Sessions to show again: " + cardStats.getNextSessionShow());
		}
	}

	private void ding() {
		if (ding == null) {
			if (!assets.isLoaded(DING)) {
				assets.load(DING, Sound.class);
				assets.finishLoadingAsset(DING);
			}
			ding = assets.get(DING, Sound.class);
		}
		ding.play(.4f);
	}

	protected void discardImageFor(final String imageFile) {
		if (imageFile == null) {
			return;
		}
		refCounts.dec(IMAGES_BACKDROP);
		if (assets.isLoaded(IMAGES_BACKDROP) && refCounts.get(IMAGES_BACKDROP) < 1) {
			assets.unload(IMAGES_BACKDROP);
		}
		refCounts.dec(IMAGES_OVERLAY);
		if (assets.isLoaded(IMAGES_OVERLAY) && refCounts.get(IMAGES_OVERLAY) < 1) {
			assets.unload(IMAGES_OVERLAY);
		}
		if (imageFile != null) {
			refCounts.dec(imageFile);
			if (assets.isLoaded(imageFile) && refCounts.get(imageFile) < 1) {
				assets.unload(imageFile);
			}
		}
	}

	private void discardsReadyForShowCheck() {
		/**
		 * Go ahead and recycle all of the close to next show time discards.
		 */
		log("Scanning discards for cards to put back into immediate play... ");
		discardsDeck.shuffleThenSortByShowAgainDelay();
		log("Next show time for discards is: " + discardsDeck.getNextShowTime());
		while (discardsDeck.hasCards() && discardsDeck.getNextShowTime() < 5000l) {
			activeDeck.add(discardsDeck.topCard());
		}
	}

	private void endSessionCleanup() {
		log("End Session Cleanup");
		log("- Active Deck: " + activeDeck.size());
		log("- Discards Deck: " + discardsDeck.size());
		log("- Completed Deck: " + completedDeck.size());
		/*
		 * Check and see how many discards can be marked as "completed". <br> Those that
		 * can be marked are those with shown >= triesRemaining
		 */
		final ListIterator<ICard<CardData>> idiscard = discardsDeck.cardsIterator();
		while (idiscard.hasNext()) {
			final ICard<CardData> card = idiscard.next();
			final CardStats cardStats = card.getCardStats();
			if (cardStats.getShown() < cardStats.getTriesRemaining()) {
				continue;
			}
			log("Marking " + card.getData().text + " as completed this session.");
			if (cardStats.isCorrect()) {
				// if correct, the card should get moved to the next
				// leitner box
				cardStats.leitnerBoxInc();
			} else {
				// if wrong, it should get moved to the previous
				// leitner box
				cardStats.leitnerBoxDec();
			}
			cardStats.setNextSessionShow(CardUtils.getNextSessionIntervalDays(cardStats.getLeitnerBox()));
			idiscard.remove(); // remove before add
			completedDeck.add(card);
			log("- Moved to Leitner box: " + cardStats.getLeitnerBox());
			log("- Sessions to show again: " + cardStats.getNextSessionShow());
		}
		/*
		 * Combine discards and active together. These were in play at session end.
		 */
		log("  combining discards and active deck");
		while (discardsDeck.hasCards()) {
			activeDeck.add(discardsDeck.topCard());
		}
		log("- Active Deck: " + activeDeck.size());
		log("- Discards Deck: " + discardsDeck.size());
		log("- Completed Deck: " + completedDeck.size());
		/*
		 * Add in completed so all cards are now in the activedeck.
		 */
		log("  combining completed and active deck");
		while (completedDeck.hasCards()) {
			activeDeck.add(completedDeck.topCard());
		}
		log("- Active Deck: " + activeDeck.size());
		log("- Discards Deck: " + discardsDeck.size());
		log("- Completed Deck: " + completedDeck.size());
	}

	protected void firstTime() {
		log("SHOWING FIRST TIME DIALOG");
		userPause();
		pausedStage.getRoot().clearChildren();
		final Dialog firstTimeNotice = new Dialog("HOW THIS WORKS", skin) {
			@Override
			protected void result(final Object object) {
				userResume();
				stage.addAction(actionLoadNextChallengeQuick());
			}
		};
		firstTimeNotice.setModal(true);
		firstTimeNotice.setFillParent(true);
		firstTimeNotice.getTitleLabel().setAlignment(Align.center);
		firstTimeNotice.button("ᎰᏩ");

		final Table contentTable = firstTimeNotice.getContentTable();
		contentTable.row();
		final Table noticeTable = new Table(skin);
		noticeTable.defaults().expand().fill();
		contentTable.add(noticeTable).expand().fill();
		final String txt = Gdx.files.internal("text/how-this-works.txt").readString(UTF_8.name());
		final Label message = new Label(txt, skin);
		message.setFontScale(.8f);
		message.setWrap(true);
		noticeTable.add(message);
		firstTimeNotice.show(pausedStage);
	}

	protected Image[] getImageFor(final String imageFile) {
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
		final Image backdrop = new Image(assets.get(IMAGES_BACKDROP, Texture.class));
		refCounts.inc(IMAGES_BACKDROP);
		final Image image = new Image(assets.get(imageFile, Texture.class));
		refCounts.inc(imageFile);
		final Image overlay = new Image(assets.get(IMAGES_OVERLAY, Texture.class));
		refCounts.inc(IMAGES_OVERLAY);
		backdrop.setScaling(Scaling.fit);
		image.setScaling(Scaling.fit);
		overlay.setScaling(Scaling.fit);
		return new Image[] { backdrop, image, overlay };// choice1;
	}

	protected ICard<CardData> getNextCard() {
		log("getNextCard");
		/**
		 * Time marches on... for all cards in play
		 */
		activeDeck.updateTimeBy((long) (currentElapsed * 1000f));
		discardsDeck.updateTimeBy((long) (currentElapsed * 1000f));

		if (activeDeck.hasCards()) {
			log("Have " + activeDeck.size() + " cards left... ");
			// always re-shuffle deck each play
			activeDeck.shuffleThenSortByShowAgainDelay();

			final ICard<CardData> card = activeDeck.topCard();
			final CardStats cs = card.getCardStats();
			// check to try and avoid repeating same card sequentially
			if (card.id().equals(previousCardId)) {
				log("Maybe not repeating same card twice in a row...");
				// infinite loop prevention
				previousCardId = "" + System.currentTimeMillis();
				cs.setShowAgainDelay_ms(CardUtils.getNextInterval(cs.getPimsleurSlot()));
				cs.pimsleurSlotInc();
				discardsDeck.add(card);
				return getNextCard();
			}
			previousCardId = card.id();
			// go ahead and assign it to the discards deck
			discardsDeck.add(card);
			// reduce tries left count
			log("For card: " + card.getData().text);
			log("- Tries remaining: " + cs.getTriesRemaining());
			cs.triesRemainingDec();
			// update next show time delay based on current Pimsleur slot #
			cs.setShowAgainDelay_ms(CardUtils.getNextInterval(cs.getPimsleurSlot()));
			// move to next Pimsleur slot
			cs.pimsleurSlotInc();
			// inc count of how many times card has been displayed.
			cs.setShown(cs.getShown() + 1);
			// return this now "active" card for challenging with
			return card;
		}

		completedCardsCheck();
		discardsReadyForShowCheck();

		// ok, if we have active cards, do getNextCard
		log("Now have " + activeDeck.size() + " cards in play ...");
		if (activeDeck.hasCards()) {
			return getNextCard();
		}

		// see if any other cards were scheduled for this session
		addThisSessionCardsFromCompletedDeck();
		log("Now have " + activeDeck.size() + " cards in play ...");
		if (activeDeck.hasCards()) {
			return getNextCard();
		}

		addCardFromMasterDeck();
		// ok, if we have active cards, do getNextCard
		log("Now have " + activeDeck.size() + " cards in play ...");
		if (activeDeck.hasCards()) {
			return getNextCard();
		}

		addCardsFromCompletedDeck();
		// ok, if we have active cards, do getNextCard
		log("Now have " + activeDeck.size() + " cards in play ...");
		if (activeDeck.hasCards()) {
			return getNextCard();
		}

		addFromDiscards(3);

		// ok, if we have active cards, do getNextCard
		log("Now have " + activeDeck.size() + " cards in play ...");
		if (activeDeck.hasCards()) {
			return getNextCard();
		}

		// No cards found in any deck! Bad things are happening!
		return null;
	}

	private void initUi() {
		final TextButton btnQuit = new TextButton(CLL1.QUIT, skin);
		btnQuit.getLabel().setFontScale(.7f);
		btnQuit.addListener(onBack);

		btnHelp = new TextButton("[?]", skin);
		btnHelp.getLabel().setFontScale(.7f);
		btnHelp.addListener(showEnglish);

		btnAudio = new TextButton("[AUDIO]", skin);
		btnAudio.getLabel().setFontScale(.7f);
		btnAudio.addListener(playAudioChallenge);

		final Table uiRight = new Table(skin);
		uiRight.add(btnAudio);
		uiRight.add(btnHelp);

		uiTable = new Table(skin);
		uiTable.setTouchable(Touchable.childrenOnly);
		uiTable.defaults().top();
		uiTable.row();
		uiTable.add(btnQuit).left();
		uiTable.add(lblCountdown).expandX().center();
		uiTable.add(uiRight).right();

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

	private boolean isHeads() {
		return coinTosser.nextBoolean();
	}

	private void loadNewChallengeAudio(final String newActiveAudioFile) {
		Gdx.app.postRunnable(new Runnable() {
			@Override
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

	private void loadNewChallengeImages(final CardData cdata) {
		Gdx.app.postRunnable(new Runnable() {
			@Override
			public void run() {
				challengeText.setText(cdata.text);

				choice1.clear();
				choice2.clear();
				discardImageFor(activeImageFile1);
				discardImageFor(activeImageFile2);

				final Set<String> previousImages = new HashSet<String>();
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
				for (final Image img : getImageFor(activeImageFile1)) {
					choice1.add(img);
					img.addListener(maybe1);
				}
				for (final Image img : getImageFor(activeImageFile2)) {
					choice2.add(img);
					img.addListener(maybe2);
				}
			}
		});
	}

	private void loadNextChallenge() {
		ICard<CardData> card;
		card = getNextCard();
		// activeCard = card;
		activeCardData = card.getData();
		activeCardStats = card.getCardStats();
		loadNewChallengeImages(activeCardData);
		loadNewChallengeAudio(activeCardData.nextRandomAudioFile());
		// reset for each card being displayed
		currentElapsed = 0f;
		if (activeCardStats.isNewCard()) {
			activeCardStats.setNewCard(false);
			showNewCardWithAudio();
		}
	}

	@Override
	protected boolean onBack() {
		userPause();
		pausedStage.getRoot().clearChildren();
		final Dialog cancelSession = new Dialog("[PAUSED] CANCEL SESSION?", skin) {
			@Override
			protected void result(final Object object) {
				if ("YES".equals(object)) {
					game.previousScreen();
				}
				userResume();
			}
		};
		cancelSession.setModal(true);
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
		return false;
	}

	private void prepDecks() {
		// ensure no previously in-play shows up as new
		for (final ICard<CardData> card : activeDeck.getCards()) {
			card.getCardStats().setNewCard(false);
		}
		// clamp leitner box values
		for (final ICard<CardData> card : activeDeck.getCards()) {
			final CardStats cardStats = card.getCardStats();
			cardStats.setLeitnerBox(Math.max(cardStats.getLeitnerBox(), 0));
		}
		// reset basic statistics and scoring and tries remaining
		for (final ICard<CardData> card : activeDeck.getCards()) {
			// CardStats cardStats = card.getCardStats();
			card.resetStats();
			card.resetTriesRemaining(CardData.MAX_TRIES);
			card.getCardStats().setPimsleurSlot(0);
		}
		// dec next session counter for active cards
		for (final ICard<CardData> card : activeDeck.getCards()) {
			final CardStats cardStats = card.getCardStats();
			cardStats.setNextSessionShow(Math.max(cardStats.getNextSessionShow() - 1, 0));
		}
		// go ahead and move to the completed deck any cards in the active
		// deck that are scheduled for later sessions
		final ListIterator<ICard<CardData>> li = activeDeck.cardsIterator();
		while (li.hasNext()) {
			final ICard<CardData> card = li.next();
			final CardStats cardStats = card.getCardStats();
			if (cardStats.getNextSessionShow() > 0) {
				li.remove(); // remove before add!
				completedDeck.add(card);
				continue;
			}
		}
		// shuffle the remaining active cards
		activeDeck.shuffleThenSortByShowAgainDelay();
		// move more than three starting cards into the completed deck to
		// prevent overload
		while (activeDeck.size() > 3) {
			completedDeck.add(activeDeck.getCards().get(activeDeck.size() - 1));
		}
	}

	@Override
	public void render(final float delta) {
		super.render(delta);
	}

	private void saveActiveDeck() {
		final Json json = new Json();
		json.setUsePrototypes(false);
		final FileHandle slot = SlotFolder.getSlotFolder(session);
		slot.mkdirs();

		final FileHandle fh_activeCards_tmp = slot.child(CLL1.ACTIVE_CARDS + ".tmp");
		final FileHandle fh_activeCards = slot.child(CLL1.ACTIVE_CARDS);
		activeDeck.shuffleThenSortByNextSession();
		final StringBuilder sbActiveCards = new StringBuilder();
		for (final ICard<CardData> card : activeDeck.getCards()) {
			sbActiveCards.append(card.id());
			sbActiveCards.append("\t");
			sbActiveCards.append(json.toJson(card.getCardStats()));
			sbActiveCards.append("\n");
		}
		fh_activeCards_tmp.writeString(sbActiveCards.toString(), false, UTF_8.name());
		fh_activeCards_tmp.moveTo(fh_activeCards);

		final FileHandle fh_deckstats_tmp = slot.child(CLL1.DECKSTATS + ".tmp");
		final FileHandle fh_deckstats = slot.child(CLL1.DECKSTATS);
		final DeckStats deckStats = DeckStats.calculateStats(activeDeck);
		deckStats.lastrun = System.currentTimeMillis();
		if (masterDeck.size() > 0) {
			deckStats.nextrun = System.currentTimeMillis() + DAY_ms / 4;
		} else {
			int nextSession = 365; // no more than a year out
			for (final ICard<CardData> card : activeDeck.getCards()) {
				nextSession = Math.min(card.getCardStats().getNextSessionShow(), nextSession);
			}
			deckStats.nextrun = System.currentTimeMillis() + DAY_ms * nextSession;
		}
		json.toJson(deckStats, fh_deckstats_tmp);
		fh_deckstats_tmp.moveTo(fh_deckstats);
	}

	private void showFinalStats() {
		log("SHOWING FINAL STATS");
		userPause();
		pausedStage.getRoot().clearChildren();
		final Dialog finalStats = new Dialog(CLL1.APP_NAME + " FINAL STATS", skin) {
			@Override
			protected void result(final Object object) {
				userResume();
				game.previousScreen();
			}
		};
		finalStats.setModal(true);
		finalStats.setFillParent(true);
		finalStats.getTitleLabel().setAlignment(Align.center);
		final TextButton howa = new TextButton("ᎰᏩ", skin);
		howa.setDisabled(true);
		finalStats.button(howa);

		final DeckStats stats = DeckStats.calculateStats(activeDeck);
		final StringBuilder txt = new StringBuilder();
		txt.append("LEVEL: ");
		txt.append(stats.level.getEnglish());
		txt.append("\n");
		txt.append("Active cards: ");
		txt.append(stats.activeCards);
		txt.append("\n");
		txt.append("Score: ");
		txt.append(stats.lastScore);
		txt.append("\n");
		txt.append("Proficiency: ");
		txt.append(stats.proficiency);
		txt.append("%");

		int chapter = 0;
		for (final ICard<CardData> card : activeDeck.getCards()) {
			chapter = Math.max(chapter, card.getData().chapter);
		}
		txt.append("\n");
		txt.append("Book Chapter: ");
		txt.append(chapter / 10);

		final Table contentTable = finalStats.getContentTable();
		contentTable.row();
		final Table noticeTable = new Table(skin);
		noticeTable.defaults().expand().fill();
		contentTable.add(noticeTable).expand().fill();
		final Label message = new Label(txt.toString(), skin);
		message.setAlignment(Align.center);
		message.setWrap(true);
		message.setFontScale(1.2f);
		noticeTable.add(message);
		finalStats.show(pausedStage);

		pausedStage.addAction(actionSaveActiveDeck(howa));
	}

	private void showNewCardWithAudio() {
		log("SHOWING NEW CARD");
		userPause();
		pausedStage.getRoot().clearChildren();
		final List<String> newCardImageFiles = new ArrayList<String>(activeCardData.getImageFiles());
		Collections.shuffle(newCardImageFiles);
		final Dialog newCardDialog = new Dialog("ᎢᏤ ᎠᏘᏗ", skin) {
			@Override
			protected void result(final Object object) {
				if ("[AUDIO]".equals(object)) {
					Gdx.app.postRunnable(replayAudio);
					cancel();
					return;
				}
				if (challengeAudio != null) {
					if (challengeAudio.isPlaying()) {
						challengeAudio.setOnCompletionListener(new OnCompletionListener() {
							@Override
							public void onCompletion(final Music music) {
								music.setOnCompletionListener(null);
								Gdx.app.postRunnable(replayAudio);
							}
						});
					} else {
						Gdx.app.postRunnable(replayAudio);
					}
				}
				for (final String newCardImageFile : newCardImageFiles) {
					discardImageFor(newCardImageFile);
				}
				userResume();
			}
		};
		newCardDialog.setModal(true);
		newCardDialog.setFillParent(true);
		newCardDialog.setKeepWithinStage(true);
		newCardDialog.getTitleLabel().setAlignment(Align.center);

		final Table contentTable = newCardDialog.getContentTable();

		// TEXT above pix
		final Table tblText = new Table(skin);
		tblText.defaults().expand().fill();
		final Label text = new Label(activeCardData.text, skin);
		if (activeCardData.text.length() < 12) {
			text.setFontScale(1.5f);
		} else {
			text.setFontScale(1f);
		}
		text.setWrap(true);
		text.setAlignment(Align.center);
		tblText.add(text);
		contentTable.row();
		contentTable.add(tblText).expandX().fill().center();

		// English gloss below Syllabary.
		if (!activeCardData.getEnglishGloss().isEmpty()) {
			final Table tblGloss = new Table(skin);
			tblGloss.defaults().expand().fill();
			final Label gloss = new Label(activeCardData.getEnglishGloss(), skin);
			gloss.setFontScale(.85f);
			gloss.setColor(Color.FIREBRICK);
			gloss.setWrap(true);
			gloss.setAlignment(Align.center);
			tblGloss.add(gloss);
			contentTable.row();
			contentTable.add(tblGloss).expandX().fill().center();
		}

		// PIX below text
		final Table tblPix = new Table(skin);
		tblPix.defaults().space(4).fill().expand().uniform();

		final float picMaxWidth = pausedStage.getWidth() * PIX_MAGIC_WIDTH_NEW_CARDS_PERCENT;

		final Stack[] pictures = new Stack[newCardImageFiles.size()];
		for (int i = 0; i < newCardImageFiles.size(); i++) {
			pictures[i] = new Stack();
			for (final Image img : getImageFor(newCardImageFiles.get(i))) {
				pictures[i].add(img);
			}
			final Cell<Stack> cell = tblPix.add(pictures[i]);
			cell.width(picMaxWidth);
		}

		final ScrollPane scroller = new ScrollPane(tblPix, skin);
		scroller.setFadeScrollBars(false);
		scroller.setForceScroll(false, false);
		scroller.setOverscroll(true, false);
		scroller.addAction(Actions.sequence(Actions.delay(.25f), Actions.run(new Runnable() {
			@Override
			public void run() {
				scroller.setScrollPercentX(.5f);
			}
		})));

		contentTable.row();
		contentTable.add(scroller).fill().expand();

		// Ꮎ buttons

		final TextButton howa = new TextButton("ᎰᏩ", skin);
		newCardDialog.button(howa, "ᎰᏩ");

		final TextButton audio = new TextButton("[AUDIO]", skin);
		newCardDialog.button(audio, "[AUDIO]");

		// recno
		final Label recno = new Label("(" + activeCardData.chapter + "-" + activeCardData.recno + ")", skin);
		recno.setAlignment(Align.right);
		recno.setFontScale(0.6f);
		final Table buttonTable = newCardDialog.getButtonTable();
		buttonTable.getParent().addActor(recno);
		recno.setPosition(0, buttonTable.getY() + 10);

		newCardDialog.pack();
		newCardDialog.show(pausedStage);
	}

	private void updateTimeleft() {
		float timeleft_secs = maxTime_secs - totalElapsed;
		if (timeleft_secs < 0f) {
			timeleft_secs = 0f;
		}
		final int minutes = (int) (timeleft_secs / 60f);
		final int seconds = (int) timeleft_secs - 60 * minutes;
		final String tmp = minutes + ":" + (seconds < 10 ? "0" : "") + seconds;
		lblCountdown.setText(tmp);
		lblCountdown.pack();
	}

}
