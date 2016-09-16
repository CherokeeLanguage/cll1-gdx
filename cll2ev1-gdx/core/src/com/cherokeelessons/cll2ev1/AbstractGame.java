package com.cherokeelessons.cll2ev1;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;

public abstract class AbstractGame extends Game {
	protected final String TAG = this.getClass().getSimpleName();
	protected void log(String message) {
		Gdx.app.log(TAG, message);
	}
	protected List<Screen> screens = new ArrayList<Screen>();

	@Override
	public void setScreen(Screen screen) {
		int activeScreen = screens.size() - 1;
		screens.add(screen);
		super.setScreen(screen);
		if (activeScreen > -1) {
			final Screen forDisposal = screens.remove(activeScreen);
			Gdx.app.postRunnable(new Runnable() {
				@Override
				public void run() {
					forDisposal.dispose();
				}
			});
		}
	}

	public void addScreen(Screen screen) {
		screens.add(screen);
		super.setScreen(screen);
	}
	
	public AssetManager assets;
	@Override
	public void create() {
		assets = new AssetManager();		
	}
	
	@Override
	public void dispose() {
		log("Dispose");
		super.dispose();
		for (Screen screen: screens) {
			screen.dispose();
		}
		assets.dispose();
	}
}
