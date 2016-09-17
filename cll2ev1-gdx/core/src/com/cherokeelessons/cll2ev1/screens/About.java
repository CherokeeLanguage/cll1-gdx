package com.cherokeelessons.cll2ev1.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.cherokeelessons.cll2ev1.AbstractGame;
import com.cherokeelessons.cll2ev1.CLL2EV1;

public class About extends AbstractScreen {

	private static final String CHANGELOG_TXT = "text/changelog.txt";
	private static final String ABOUT_TXT = "text/about.txt";

	public About(AbstractGame game) {
		super(game);
		setSkin(CLL2EV1.SKIN);
		setBackdrop(CLL2EV1.BACKDROP);
		Gdx.app.postRunnable(init);
	}

	protected Runnable init=new Runnable(){
		@Override
		public void run() {
			Table container = new Table(skin);
			container.setFillParent(true);
			container.defaults().expand();
			
			container.row();
			TextButton btnBack = new TextButton("[BACK]", skin);
			btnBack.getLabel().setFontScale(.65f);
			btnBack.pack();
			
			container.add(btnBack).left().fill(false).expand(false, false);
			
			btnBack.addListener(new ClickListener(){
				public boolean touchDown(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y, int pointer, int button) {
					return onBack();
				}
			});
			
			Table scrollTable = new Table(skin);
			ScrollPane scroller = new ScrollPane(scrollTable, skin);
			scroller.setFadeScrollBars(false);
			
			String text = Gdx.files.internal(ABOUT_TXT).readString("UTF-8");		
			text+="\n\n";
			text+="===========\n";
			text+="CHANGELOG\n";
			text+="===========\n";
			text+="\n\n";
			
			text += Gdx.files.internal(CHANGELOG_TXT).readString("UTF-8");

			Label label = new Label(text, skin);
			label.setWrap(true);
			label.setFontScale(0.65f);
			
			scrollTable.row();
			scrollTable.add(label).expand().fill().left().padLeft(20).padRight(20);
			
			container.row();
			container.add(scroller).expand().fill();
			
			stage.addActor(container);
			stage.setKeyboardFocus(scroller);
			stage.setScrollFocus(scroller);
		}
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
