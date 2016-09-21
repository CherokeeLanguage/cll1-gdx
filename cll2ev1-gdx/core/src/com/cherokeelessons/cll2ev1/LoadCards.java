package com.cherokeelessons.cll2ev1;

import java.nio.charset.StandardCharsets;
import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.cherokeelessons.cll2ev1.models.CardData;
import com.cherokeelessons.deck.Card;

public class LoadCards implements Runnable {
	private CLL2EV1 game;

	private void log(String message) {
		Gdx.app.log(this.getClass().getSimpleName(), message);
	}

	public LoadCards(CLL2EV1 game) {
		this.game=game;
	}

	@Override
	public void run() {
		log("Loading cards from " + CLL2EV1.CARDS_CSV);
		String tmpCards = Gdx.files.internal(CLL2EV1.CARDS_CSV).readString(StandardCharsets.UTF_8.name());
		String[] tmpLines = tmpCards.split("\n");
		log("Loaded " + tmpLines.length + " records.");
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
			data.answerPic = tmpCard[3].trim();
			data.blacklistPic = tmpCard[4].trim();
			Card<CardData> card = new Card<CardData>();
			card.setData(data);
			CLL2EV1.cards.add(card);
		}
		log("Have " + CLL2EV1.cards.size() + " cards.");
		Iterator<Card<CardData>> icards = CLL2EV1.cards.iterator();
		while (icards.hasNext()) {
			CardData data = icards.next().getData();
			System.out.println(data.chapter + "] " + data.text);
		}
		game.deckReady=true;
	}
}
