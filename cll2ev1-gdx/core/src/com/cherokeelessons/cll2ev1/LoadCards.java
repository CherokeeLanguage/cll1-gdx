package com.cherokeelessons.cll2ev1;

import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Logger;
import com.cherokeelessons.cll2ev1.models.CardData;
import com.cherokeelessons.cll2ev1.models.GameCard;

public class LoadCards implements Runnable {
	private CLL2EV1 game;

	private Logger log = new Logger(this.getClass().getSimpleName(), Logger.INFO);

	public LoadCards(CLL2EV1 game) {
		this.game = game;
	}

	@Override
	public void run() {
		log.info("Loading cards from " + CLL2EV1.CARDS_CSV);
		String tmpCards = Gdx.files.internal(CLL2EV1.CARDS_CSV).readString(StandardCharsets.UTF_8.name());
		String[] tmpLines = tmpCards.split("\n");
		log.info("Loaded " + tmpLines.length + " records.");
		int activeChapter = 0;
		for (String tmpLine : tmpLines) {
			String[] tmpCard = tmpLine.split("\t", -1);
			if (tmpCard.length < 5) {
				continue;
			}
			if (tmpCard[0].startsWith("#")) {
				continue;
			}
			if (!tmpCard[0].trim().isEmpty()) {
				try {
					activeChapter = Integer.valueOf(tmpCard[0]);
				} catch (NumberFormatException e) {
				}
			}
			if (tmpCard[1].trim().isEmpty()) {
				continue;
			}
			CardData data = new CardData();
			data.chapter = activeChapter;
			data.text = tmpCard[1].trim();
			data.audio = tmpCard[2].trim();
			data.images = tmpCard[3].trim();
			if (data.images.trim().isEmpty()) {
				data.images = data.audio;
			}
			data.blacklistPic = tmpCard[4].trim();
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
	}
}
