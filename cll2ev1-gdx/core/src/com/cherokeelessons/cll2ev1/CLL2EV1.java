package com.cherokeelessons.cll2ev1;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.libgdx.ScreenPoweredBy;

public class CLL2EV1 extends Game {
	@Override
	public void create () {
		ScreenPoweredBy by = new ScreenPoweredBy(new Runnable() {
			public void run() {
				Gdx.app.exit();
			}
		});
		this.setScreen(by);
	}
}
