package com.cherokeelessons.cll1.screens;

import com.badlogic.gdx.Gdx;
import com.cherokeelessons.cll1.AbstractGame;
import com.cherokeelessons.cll1.CLL1;

public class Quit extends AbstractScreen {
	public Quit(final AbstractGame game) {
		super(game);
		setBackdrop(CLL1.BACKDROP);
	}

	@Override
	protected void act(final float delta) {
		// TODO Auto-generated method stub

	}

	@Override
	protected boolean onBack() {
		return false;
	}

	@Override
	protected boolean onMenu() {
		return false;
	}

	@Override
	public void show() {
		super.show();
		Gdx.app.postRunnable(new Runnable() {
			@Override
			public void run() {
				Gdx.app.exit();
			}
		});
	}

}
