package com.cherokeelessons.cll2ev1.views;

import com.badlogic.gdx.Gdx;

import net.peakgames.libgdx.stagebuilder.core.AbstractGame;
import net.peakgames.libgdx.stagebuilder.core.AbstractScreen;

public abstract class CllScreen extends AbstractScreen {
	protected final String TAG = this.getClass().getSimpleName();
	protected void log(String message) {
		Gdx.app.log(TAG, message);
	}

	public CllScreen(AbstractGame game) {
		super(game, false);
	}
	
	@Override
	public boolean isResizable() {
		return false;
	}
	
	@Override
	public void resize(int newWidth, int newHeight) {
		super.resize(newWidth, newHeight);
		stage.getViewport().update(newWidth, newHeight, false);
	}
}
