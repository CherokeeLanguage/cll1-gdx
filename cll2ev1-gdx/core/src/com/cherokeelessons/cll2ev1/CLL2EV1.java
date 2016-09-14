package com.cherokeelessons.cll2ev1;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.cherokeelessons.cll2ev1.views.ViewScreenPoweredBy;
import com.github.czyzby.kiwi.util.gdx.asset.Disposables;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.util.LmlApplicationListener;
import com.github.czyzby.lml.vis.util.VisLml;
import com.kotcrab.vis.ui.VisUI;

public class CLL2EV1 extends LmlApplicationListener {

	public static final Rectangle ScreenSize = new Rectangle(0, 0, 1280, 720);
	private Batch batch;

	@Override
	public void create() {
		batch = new SpriteBatch();
		super.create();
		setView(new ViewScreenPoweredBy(newStage(), new Runnable() {
			public void run() {
				Gdx.app.exit();
			}
		}));
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
		Disposables.gracefullyDisposeOf(batch);
		VisUI.dispose(); // Disposing of default VisUI skin.
	}

	@Override
	protected LmlParser createParser() {
		return VisLml.parser().build();
	}
}
