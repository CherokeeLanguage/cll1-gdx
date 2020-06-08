package com.cherokeelessons.cll1;

import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.cherokeelessons.cll1.models.GameCard;

public class DiscardIncompleteCards implements Runnable {
	private final boolean debug = true;
	private final CLL1 game;

	public DiscardIncompleteCards(final CLL1 game) {
		this.game = game;
	}

	private void log(final String message) {
		Gdx.app.log(this.getClass().getSimpleName(), message);
	}

	@Override
	public void run() {
		final int beforeSize = this.game.cards.size();
		final Iterator<GameCard> icards = this.game.cards.iterator();
		while (icards.hasNext()) {
			final GameCard card = icards.next();
			if (!card.getData().hasAudioFiles()) {
				icards.remove();
				if (debug) {
					log("- Missing Audio: " + card.id());
				}
				continue;
			}
			if (!card.getData().hasImageFiles()) {
				icards.remove();
				if (debug) {
					log("- Missing Correct Images: " + card.id());
				}
				continue;
			}
			if (!card.getData().hasWrongImageFiles()) {
				icards.remove();
				if (debug) {
					log("- Missing Wrong Images: " + card.id());
				}
				continue;
			}
		}
		log("Removed " + (beforeSize - this.game.cards.size()) + " cards.");
		log("Have " + this.game.cards.size() + " valid cards.");
	}

}
