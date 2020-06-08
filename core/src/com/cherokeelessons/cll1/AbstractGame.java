package com.cherokeelessons.cll1;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;

public abstract class AbstractGame extends Game {

	protected final Runnable audioRunner = new Runnable() {
		@Override
		public void run() {

		}
	};
	protected final String TAG = this.getClass().getSimpleName();

	protected List<Screen> screens = new ArrayList<Screen>();

	protected Preferences prefs;

	/**
	 * Adds this screen to the top of the "deck" of screens.<br/>
	 * Replaces current screen (if any) with a new screen.<br/>
	 * Current screen (if any) {@link Screen#hide()} is called.<br/>
	 * New screen {@link Screen#show()} is called.<br/>
	 */
	public void addScreen(final Screen screen) {
		screens.add(screen);
		super.setScreen(screen);
	}

	@Override
	public void create() {
		prefs = Gdx.app.getPreferences(this.getClass().getName());
	}

	@Override
	public void dispose() {
		log("Dispose");
		super.dispose();
		for (final Screen screen : screens) {
			screen.dispose();
		}
	}

	public Preferences getPrefs() {
		return prefs;
	}

	protected void log(final String message) {
		Gdx.app.log(TAG, message);
	}

	/**
	 * Discards current screen and displays previous screen.<br/>
	 * Has no effect if there is no previous screen to go back to.
	 */
	public void previousScreen() {
		if (screens.size() < 2) {
			return;
		}
		// remove current screen from "deck"
		final Screen forRemoval = screens.remove(screens.size() - 1);
		// hide it
		forRemoval.hide();
		// show new "top" screen
		super.setScreen(screens.get(screens.size() - 1));
		// schedule for dispose the no longer shown screen
		Gdx.app.postRunnable(new Runnable() {
			@Override
			public void run() {
				forRemoval.dispose();
			}
		});
	}

	public void setPrefs(final Preferences prefs) {
		this.prefs = prefs;
	}

	/**
	 * Replaces the top of the "deck" of screens with this screen.<br/>
	 * Current screen (if any) {@link Screen#hide()} is called.<br/>
	 * New screen {@link Screen#show()} is called.<br/>
	 * Previous screen (if any) {@link Screen#dispose()} is called.
	 */
	@Override
	public void setScreen(final Screen screen) {
		final int activeScreen = screens.size() - 1;
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
}
