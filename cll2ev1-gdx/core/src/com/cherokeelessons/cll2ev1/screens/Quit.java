package com.cherokeelessons.cll2ev1.screens;

import com.badlogic.gdx.Gdx;
import com.cherokeelessons.cll2ev1.AbstractGame;
import com.cherokeelessons.cll2ev1.CLL2EV1;

public class Quit extends AbstractScreen {
	public Quit(AbstractGame game) {
		super(game);
		setBackdrop(CLL2EV1.BACKDROP);
	}
	
	@Override
	public void show() {
		super.show();
		Gdx.app.postRunnable(new Runnable() {
			public void run() {
				Gdx.app.exit();
			}
		});
	}

}
