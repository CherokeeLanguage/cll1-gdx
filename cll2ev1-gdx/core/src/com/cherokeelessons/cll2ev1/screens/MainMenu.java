package com.cherokeelessons.cll2ev1.screens;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.cherokeelessons.cll2ev1.CLL2EV1;

public class MainMenu extends AbstractScreen {
	private static final String QUIT = "Quit - ᎠᏑᎶᎪᏍᏗ";
	private static final String ABOUT = "About - ᎢᎸᏢ";
	private static final String OPTIONS = "Options - ᎠᏑᏰᏍᏗᎢ";
	private static final String HIGH_SCORES = "High Scores - ᏬᏍᏓ ᏗᏎᏍᏗ";
	private static final String NEW_PRACTICE = "New Practice - ᎢᏤ ᎤᎪᏅᏗ";
	private static final String TITLE = "Cherokee Language Lessons Vol. 1";

	public MainMenu(CLL2EV1 game) {
		super(game);
		setSkin("skin/68/gdx-holo-freeserif-68.json");
		setClearColor(Color.WHITE);
		
		Label titleLabel = new Label(TITLE, skin);
		TextButton btnNewGame = new TextButton(NEW_PRACTICE, skin);
		TextButton btnHighScores = new TextButton(HIGH_SCORES, skin);
		TextButton btnOptions = new TextButton(OPTIONS, skin);
		TextButton btnAbout = new TextButton(ABOUT, skin);
		TextButton btnQuit = new TextButton(QUIT, skin);
		
		Table menu = new Table(skin);
		menu.setFillParent(true);
		menu.row();
		menu.add(titleLabel);
		menu.row();
		menu.add(btnNewGame);
		menu.row();
		menu.add(btnHighScores);
		menu.row();
		menu.add(btnOptions);
		menu.row();
		menu.add(btnAbout);
		menu.row();
		menu.add(btnQuit);
		menu.pack();
		
		stage.addActor(menu);
	}
}
