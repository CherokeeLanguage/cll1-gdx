package com.cherokeelessons.cll2ev1.screens;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.cherokeelessons.cll2ev1.CLL2EV1;

public class MainMenu extends AbstractScreen {
	private static final String SKIN = CLL2EV1.SKIN;
	private static final String QUIT = "Quit - ᎠᏑᎶᎪᏍᏗ";
	private static final String ABOUT = "About - ᎢᎸᏢ";
	private static final String OPTIONS = "Options - ᎠᏑᏰᏍᏗᎢ";
	//private static final String HIGH_SCORES = "High Scores - ᏬᏍᏓ ᏗᏎᏍᏗ";
	private static final String PRACTICE = "Practice - ᏣᎪᏅᏗ";
	private static final String TITLE = "Cherokee Language Lessons Vol. 1";

	private ClickListener onNewGame = new ClickListener();
	private ClickListener onOptions = new ClickListener();
	private ClickListener onAbout = new ClickListener(){
		@Override
		public void clicked(InputEvent event, float x, float y) {
			game.addScreen(new About(game));
		}
	};
	private ClickListener onQuit = new ClickListener() {
		public void clicked(InputEvent event, float x, float y) {
			game.setScreen(new Quit(game));
		};
	};
	
	public MainMenu(CLL2EV1 game) {
		super(game);
		setSkin(SKIN);

		Label titleLabel = new Label(TITLE, skin);
		TextButton btnNewGame = new TextButton(PRACTICE, skin);
		TextButton btnOptions = new TextButton(OPTIONS, skin);
		TextButton btnAbout = new TextButton(ABOUT, skin);
		TextButton btnQuit = new TextButton(QUIT, skin);

		btnNewGame.addListener(onNewGame);
		btnOptions.addListener(onOptions);
		btnAbout.addListener(onAbout);
		btnQuit.addListener(onQuit);
		
		btnNewGame.setProgrammaticChangeEvents(false);
		btnOptions.setProgrammaticChangeEvents(false);
		btnAbout.setProgrammaticChangeEvents(false);
		btnQuit.setProgrammaticChangeEvents(false);

		Table menu = new Table(skin);
		menu.setFillParent(true);
		menu.defaults().expand();
		menu.row();
		menu.add(titleLabel);
		menu.row();
		menu.add(btnNewGame);
		menu.row();
		menu.add(btnOptions);
		menu.row();
		menu.add(btnAbout);
		menu.row();
		menu.add(btnQuit);
		menu.pack();

		setBackdrop(CLL2EV1.BACKDROP);

		stage.addActor(menu);
	}

	@Override
	protected boolean onBack() {
		return false;
	}

	@Override
	protected boolean onMenu() {
		return false;
	}
}
