package com.cherokeelessons.cll2ev1;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.cherokeelessons.cll2ev1.views.MainMenu;
import com.libgdx.ScreenPoweredBy;

public class CLL2EV1 extends Game {

	public static final Rectangle ScreenSize = new Rectangle(0, 0, 1280, 720);
	private Batch batch;

	private ScreenPoweredBy poweredBy = null;
	private Runnable onPoweredByDone = new Runnable() {
		public void run() {
			setScreen(new MainMenu(newStage()));
			poweredBy.dispose();
		}
	};

	@Override
	public void create() {
		batch = new SpriteBatch();
		poweredBy = new ScreenPoweredBy(newStage(), onPoweredByDone);
		setScreen(poweredBy);
	}

	/** @return application's only {@link Batch}. */
	public Batch getBatch() {
		return batch;
	}

	public static CLL2EV1 get() {
		return (CLL2EV1) Gdx.app.getApplicationListener();
	}

	/** @return a new customized {@link Stage} instance. */
	public static Stage newStage() {
		return new Stage(new FitViewport(ScreenSize.width, ScreenSize.height), CLL2EV1.get().getBatch());
	}

	@Override
	public void dispose() {
		super.dispose();
		batch.dispose();
	}
}
