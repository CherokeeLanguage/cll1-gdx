package com.cherokeelessons.cll2ev1;

import com.badlogic.gdx.Gdx;

public class DiscardIncompleteCards implements Runnable {
	private CLL2EV1 game;

	private void log(String message) {
		Gdx.app.log(this.getClass().getSimpleName(), message);
	}

	public DiscardIncompleteCards(CLL2EV1 game) {
		this.game=game;
	}

	@Override
	public void run() {
		
	}

}
