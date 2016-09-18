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
			titleLabel.setFontScale(.75f);
			
			Table menu = new Table(skin);
			menu.setFillParent(true);
			menu.defaults().expand();
			TextButton btnBack = new TextButton(BACK, skin);
			btnBack.getLabel().setFontScale(.7f);
			btnBack.pack();
			btnBack.addListener(onBack);
			menu.row();
			menu.add(btnBack).left().fill(false).expand(false, false);
			
			menu.row();
			menu.add(titleLabel);
			for (int ix=0; ix<4; ix++) {
				String text = "["+(1+ix)+"] Completed 0%, Accuracy 0%" ;
				TextButton btnSession = new TextButton(text, skin);
				btnSession.getLabel().setFontScale(.75f);
				menu.row();
				menu.add(btnSession);
				btnSession.addListener(chooseSession(ix));
			}
			
			stage.addActor(menu);
		}
	};
	
	protected ClickListener chooseSession(final int session) {
		return new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {
				//TODO
			}
		};
	}
	
	private ClickListener onBack = new ClickListener() {
		@Override
		public void clicked(InputEvent event, float x, float y) {
			onBack();
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
