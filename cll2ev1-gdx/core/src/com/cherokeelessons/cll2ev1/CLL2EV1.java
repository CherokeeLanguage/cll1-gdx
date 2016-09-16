package com.cherokeelessons.cll2ev1;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.cherokeelessons.cll2ev1.views.MainMenu;
import com.cherokeelessons.cll2ev1.views.ScreenPoweredBy;

public class CLL2EV1 extends AbstractGame {
	
	public static final Vector2 worldSize = new Vector2(1280, 720);
	
	private ScreenPoweredBy poweredBy = null;
	private Runnable onPoweredByDone = new Runnable() {
		public void run() {
			log("onPoweredByDone");
			setScreen(new MainMenu(CLL2EV1.this));
		}
	};
	
	@Override
	public void create() {
		poweredBy = new ScreenPoweredBy(CLL2EV1.this, onPoweredByDone);
		addScreen(poweredBy);
	}

	protected final String TAG = this.getClass().getSimpleName();
	protected void log(String message) {
		Gdx.app.log(TAG, message);
	}
}
