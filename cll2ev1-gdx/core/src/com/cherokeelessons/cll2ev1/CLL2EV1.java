package com.cherokeelessons.cll2ev1;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.libgdx.ScreenPoweredBy;

public class CLL2EV1 extends Game {
	SpriteBatch batch;
	Texture img;
	
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
