package com.cherokeelessons.cll2ev1.screens;

import com.badlogic.gdx.Gdx;
import com.cherokeelessons.cll2ev1.CLL2EV1;

public class ValidateCards implements Runnable {
	private CLL2EV1 game;

	private void log(String message) {
		Gdx.app.log(this.getClass().getSimpleName(), message);
	}

	public ValidateCards(CLL2EV1 game) {
		this.game=game;
	}

	@Override
	public void run() {
		
	}

}
