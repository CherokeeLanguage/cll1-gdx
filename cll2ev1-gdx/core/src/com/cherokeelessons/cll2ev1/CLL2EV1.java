package com.cherokeelessons.cll2ev1;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.math.Vector2;
import com.cherokeelessons.cll2ev1.views.MainMenu;
import com.cherokeelessons.cll2ev1.views.ScreenPoweredBy;

public class CLL2EV1 extends AbstractGame {
	public AssetManager assets;
	public static final Vector2 worldSize = new Vector2(1280, 720);
	private ScreenPoweredBy poweredBy = null;
	private Runnable onPoweredByDone = new Runnable() {
		public void run() {
			setScreen(new MainMenu(CLL2EV1.this));
		}
	};

	@Override
	public void create() {
		super.create();
		poweredBy = new ScreenPoweredBy(CLL2EV1.this, onPoweredByDone);
		addScreen(poweredBy);
	}
}
