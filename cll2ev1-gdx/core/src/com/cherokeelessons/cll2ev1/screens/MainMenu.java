package com.cherokeelessons.cll2ev1.screens;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.cherokeelessons.cll2ev1.CLL2EV1;

public class MainMenu extends AbstractScreen {
	public MainMenu(CLL2EV1 game) {
		super(game);
		setSkin("skin/68/gdx-holo-freeserif-68.json");
		setClearColor(Color.WHITE);
		
		Table menu = new Table(skin);
		menu.setFillParent(true);
		menu.row();
		menu.add(new Label("Cherokee Language Lessons Vol. 1", skin));
		menu.row();
		menu.add(new TextButton("New Game - ᎢᏤ ᏗᏁᎶᏗᎢ", skin));
		menu.row();
		menu.add(new TextButton("High Scores - ᏬᏍᏓ ᏗᏎᏍᏗ", skin));
		menu.row();
		menu.add(new TextButton("Options - ᎠᏑᏰᏍᏗᎢ", skin));
		menu.row();
		menu.add(new TextButton("About - ᎢᎸᏢ", skin));
		menu.row();
		menu.add(new TextButton("Quit - ᎠᏑᎶᎪᏍᏗ", skin));
		menu.pack();
		
		stage.addActor(menu);
	}
}
