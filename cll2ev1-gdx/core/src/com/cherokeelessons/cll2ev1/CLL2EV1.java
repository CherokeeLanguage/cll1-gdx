package com.cherokeelessons.cll2ev1;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.cherokeelessons.cll2ev1.views.MainMenu;
import com.libgdx.ScreenPoweredBy;

import net.peakgames.libgdx.stagebuilder.core.AbstractGame;
import net.peakgames.libgdx.stagebuilder.core.assets.ResolutionHelper;
import net.peakgames.libgdx.stagebuilder.core.services.LocalizationService;

public class CLL2EV1 extends AbstractGame {
	
	public static final Vector2 worldSize = new Vector2(1280, 720);
	
	@Override
	public ResolutionHelper getResolutionHelper() {
		return super.getResolutionHelper();
	}
	
	@Override
	public List<Vector2> getSupportedResolutions() {
		List<Vector2> supported = new ArrayList<Vector2>();
		supported.add(new Vector2(worldSize));
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
			log("onPoweredByDone");
			replaceTopScreen(new MainMenu(CLL2EV1.this));
		}
	};
	
	@Override
	public void create() {
		initialize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), (int)worldSize.x, (int)worldSize.y);
		poweredBy = new ScreenPoweredBy(CLL2EV1.this, onPoweredByDone);
		addScreen(poweredBy);
	}

	protected final String TAG = this.getClass().getSimpleName();
	protected void log(String message) {
		Gdx.app.log(TAG, message);
	}
}
