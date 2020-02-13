package com.cherokeelessons.cll1.screens;

import com.cherokeelessons.cll1.AbstractGame;
import com.cherokeelessons.cll1.CLL1;

public class Practice extends AbstractScreen {
	public Practice(AbstractGame game) {
		super(game);
		setBackdrop(CLL1.BACKDROP);
		setSkin(CLL1.SKIN);
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
	protected void act(float delta) {
		// TODO Auto-generated method stub

	}
}
