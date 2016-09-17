package com.cherokeelessons.cll2ev1.screens;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.cherokeelessons.cll2ev1.CLL2EV1;

public class MainMenu extends AbstractScreen {
	public MainMenu(CLL2EV1 game) {
		super(game);
		setSkin("skin/68/gdx-holo-freeserif-68.json");
		setClearColor(Color.WHITE);
		
		Table menu = new Table(skin);
		menu.add(new TextButton("Menu Item", skin));
		menu.pack();
		
		stage.addActor(menu);
	}
}
