package com.cherokeelessons.cll2ev1;

import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.cherokeelessons.cll2ev1.models.GameCard;

public class DiscardIncompleteCards implements Runnable {
	private boolean debug=true;
	private CLL2EV1 game;

	private void log(String message) {
		Gdx.app.log(this.getClass().getSimpleName(), message);
	}

	public DiscardIncompleteCards(CLL2EV1 game) {
		this.game=game;
	}

	@Override
	public void run() {
		int beforeSize = this.game.cards.size();
		Iterator<GameCard> icards = this.game.cards.iterator();
		while (icards.hasNext()) {
			GameCard card = icards.next();
			if (!card.getData().hasAudioFiles()) {
				icards.remove();
				continue;
			}
			if (!card.getData().hasImageFiles()) {
				icards.remove();
				continue;
			}
		}
		log("Removed "+(beforeSize-this.game.cards.size())+" cards.");
		log("Have "+this.game.cards.size()+" valid cards.");
		if (debug) {
			log("=== DEBUG VALID CARDS:");
			for (GameCard card: this.game.cards) {
				log(card.getData().sortKey());
			}
		}
	}

}
