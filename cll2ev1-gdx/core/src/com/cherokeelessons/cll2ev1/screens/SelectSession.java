package com.cherokeelessons.cll2ev1.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.cherokeelessons.cll2ev1.AbstractGame;
import com.cherokeelessons.cll2ev1.CLL2EV1;

public class SelectSession extends AbstractScreen {

	protected static final String TITLE = "Please choose session:";
	private static final String BACK = "[BACK]";

	public SelectSession(AbstractGame game) {
		super(game);
		setBackdrop(CLL2EV1.BACKDROP);
		setSkin(CLL2EV1.SKIN);
		Gdx.app.postRunnable(init);
	}
	
	protected Runnable init = new Runnable() {
		@Override
		public void run() {
			Label titleLabel = new Label(TITLE, skin);
			
			Table menu = new Table(skin);
			menu.setFillParent(true);
			menu.defaults().expand();
			menu.row();
			menu.add(titleLabel);
			for (int ix=0; ix<4; ix++) {
				TextButton btnSession = new TextButton("SESSION: "+(1+ix), skin);
				menu.row();
				menu.add(btnSession);
			}
			TextButton btnBack = new TextButton(BACK, skin);
			btnBack.addListener(onQuit);
			menu.row();
			menu.add(btnBack);
			
			stage.addActor(menu);
		}
	};
	
	private ClickListener onQuit = new ClickListener() {
		public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
			return onBack();
		};
	};

	@Override
	protected boolean onBack() {
		game.previousScreen();
		return true;
	}

	@Override
	protected boolean onMenu() {
		return false;
	}

}
