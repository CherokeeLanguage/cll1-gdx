package com.cherokeelessons.cll1;

import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.Set;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Logger;
import com.cherokeelessons.cll1.models.CardData;
import com.cherokeelessons.cll1.models.GameCard;
import com.cherokeelessons.deck.CardUtils;
import com.cherokeelessons.deck.DeckStats;

public class LoadCards implements Runnable {
	private static final int CHAPTER = 0;
	private static final int SYLLABARY = 1;
	private static final int AUDIO = 2;
	private static final int ANSWER_PICS = 3;
	private static final int EXCLUDE_PICS = 4;
	private static final int ENGLISH_GLOSS = 5;
	@SuppressWarnings("unused")
	private static final int NOTES = 6;

	/**
     * Eight-bit UCS Transformation Format
     */
    public static final Charset UTF_8 = Charset.forName("UTF-8");
	
	private CLL1 game;

	private Logger log = new Logger(this.getClass().getSimpleName(), Logger.INFO);

	public LoadCards(CLL1 game) {
		this.game = game;
	}

	@Override
	public void run() {
		log.info("Loading cards from " + CLL1.CARDS_CSV);
		String tmpCards = Gdx.files.internal(CLL1.CARDS_CSV).readString(UTF_8.name());
		String[] tmpLines = tmpCards.split("\n");
		log.info("Loaded " + tmpLines.length + " records.");
		int activeChapter = 0;
		int recno=0;
		for (String tmpLine : tmpLines) {
			recno++;
			String[] tmpCard = tmpLine.split("\t", -1);
			if (tmpCard.length < 6) {
				continue;
			}
			if (tmpCard[CHAPTER].startsWith("#")) {
				continue;
			}
			if (!tmpCard[CHAPTER].trim().isEmpty()) {
				try {
					activeChapter = Integer.valueOf(tmpCard[CHAPTER]);
				} catch (NumberFormatException e) {
				}
			}
			if (tmpCard[SYLLABARY].trim().isEmpty()) {
				continue;
			}
			CardData data = new CardData();
			data.recno=recno;
			data.chapter = activeChapter;
			data.text = tmpCard[SYLLABARY].trim();
			data.audio = tmpCard[AUDIO].trim();
			data.images = tmpCard[ANSWER_PICS].trim();
			if (data.images.trim().isEmpty()) {
				data.images = data.audio;
			}
			data.blacklistPic = tmpCard[EXCLUDE_PICS].trim();
			data.setEnglishGloss(tmpCard[ENGLISH_GLOSS].trim());
			GameCard card = new GameCard();
			card.setData(data);
			game.cards.add(card);
		}
		log.info("Have " + game.cards.size() + " cards.");
		/**
		 * Scan for and report duplicate ids.
		 */
		Set<String> already = new HashSet<String>();
		for (GameCard card: game.cards) {
			if (already.contains(card.id())){
				log.error("DUPLICATE CARD ID: '"+card.id()+"' (Removing from deck...)");
				card.getMyDeck().remove(card);
			}
		}
		game.deckReady = true;
		
		/**
		 * debug dumps
		 */
		for (int session =0; session<DeckStats.FULLY_LEARNED_BOX; session++) {
			log.info("LEITNER BOX "+session+" IS "+CardUtils.getNextSessionIntervalDays(session)+" DAYS DELAY.");
		}
		for (GameCard card: game.cards) {
			CardData data = card.getData();
			log.info(data.chapter+"-"+data.recno+": "+data.text+" = "+data.getEnglishGloss());
		}
	}
}
