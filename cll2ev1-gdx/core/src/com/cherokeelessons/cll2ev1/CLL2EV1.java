package com.cherokeelessons.cll2ev1;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.cherokeelessons.cll2ev1.views.MainMenu;
import com.libgdx.ScreenPoweredBy;

import net.peakgames.libgdx.stagebuilder.core.AbstractGame;
import net.peakgames.libgdx.stagebuilder.core.services.LocalizationService;

public class CLL2EV1 extends AbstractGame {

	public static final Rectangle ScreenSize = new Rectangle(0, 0, 1280, 720);
	private Batch batch;
	
	@Override
	public List<Vector2> getSupportedResolutions() {
		List<Vector2> supported = new ArrayList<Vector2>();
		supported.add(new Vector2(1280, 720));
		return supported;
	}

	@Override
	public LocalizationService getLocalizationService() {
		LocalizationService ls = new LocalizationService() {
			@Override
			public String getString(String s, Object... args) {
				return s;
			}
			
			@Override
			public String getString(String s) {
				return s;
			}
		};
		return ls;
	}

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
