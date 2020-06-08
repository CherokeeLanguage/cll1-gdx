package com.cherokeelessons.cll1.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.cherokeelessons.cll1.CLL1;
import com.cherokeelessons.cll1.DiscardIncompleteCards;
import com.cherokeelessons.cll1.LoadAudioFilenames;
import com.cherokeelessons.cll1.LoadCards;
import com.cherokeelessons.cll1.LoadImageFilenames;

public class MainMenu extends AbstractScreen {
	private static final String SKIN = CLL1.SKIN;
	private static final String QUIT = "Quit - ᎠᏑᎶᎪᏍᏗ";
	private static final String ABOUT = "About - ᎢᎸᏢ";
	private static final String OPTIONS = "Options - ᎠᏑᏰᏍᏗᎢ";
	// private static final String HIGH_SCORES = "High Scores - ᏬᏍᏓ ᏗᏎᏍᏗ";
	private static final String PRACTICE = "Practice - ᏣᎪᏅᏗ";
	private static final String TITLE = "Cherokee Language Lessons 1";

	private final ClickListener onNewGame = new ClickListener() {
		@Override
		public void clicked(final InputEvent event, final float x, final float y) {
			game.addScreen(new SelectSession(game));
		};
	};
	private final ClickListener onOptions = new ClickListener();
	private final ClickListener onAbout = new ClickListener() {
		@Override
		public void clicked(final InputEvent event, final float x, final float y) {
			game.addScreen(new About(game));
		}
	};
	private final ClickListener onQuit = new ClickListener() {
		@Override
		public void clicked(final InputEvent event, final float x, final float y) {
			game.setScreen(new Quit(game));
		};
	};

	protected Runnable howa = new Runnable() {
		@Override
		public void run() {
			assets.load("audio/howa.mp3", Sound.class);
			assets.finishLoadingAsset("audio/howa.mp3");
			assets.get("audio/howa.mp3", Sound.class).play(1f);
		}
	};

	protected Runnable init = new Runnable() {
		@Override
		public void run() {
			log("init");
			final Label titleLabel = new Label(TITLE, skin);
			final TextButton btnNewGame = new TextButton(PRACTICE, skin);
			final TextButton btnOptions = new TextButton(OPTIONS, skin);
			final TextButton btnAbout = new TextButton(ABOUT, skin);
			final TextButton btnQuit = new TextButton(QUIT, skin);

			btnNewGame.addListener(onNewGame);
			btnOptions.addListener(onOptions);
			btnAbout.addListener(onAbout);
			btnQuit.addListener(onQuit);

			final Table menu = new Table(skin);
			menu.setFillParent(true);
			menu.defaults().expand();
			menu.row();
			menu.add(titleLabel);
			menu.row();
			menu.add(btnNewGame);
			// menu.row();
			// menu.add(btnOptions);
			menu.row();
			menu.add(btnAbout);
			menu.row();
			menu.add(btnQuit);
			menu.pack();

			stage.addActor(menu);
		}
	};

	public MainMenu(final CLL1 game) {
		super(game);
		setSkin(SKIN);
		setBackdrop(CLL1.BACKDROP);
		Gdx.app.postRunnable(new LoadCards(game));
		Gdx.app.postRunnable(new LoadAudioFilenames(game));
		Gdx.app.postRunnable(new LoadImageFilenames(game));
		Gdx.app.postRunnable(new DiscardIncompleteCards(game));
		Gdx.app.postRunnable(init);
		Gdx.app.postRunnable(howa);
	}

	@Override
	protected void act(final float delta) {
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
