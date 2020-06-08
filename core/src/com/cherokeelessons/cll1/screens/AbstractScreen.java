package com.cherokeelessons.cll1.screens;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.TextureLoader;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.TiledDrawable;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.cherokeelessons.cll1.AbstractGame;
import com.cherokeelessons.cll1.CLL1;

public abstract class AbstractScreen implements Screen, InputProcessor {
	public static class SyncAssetManager extends AssetManager {
		public synchronized <T> T loadAndGet(final String fileName, final Class<T> type) {
			if (!isLoaded(fileName)) {
				load(fileName, type);
				finishLoadingAsset(fileName);
			}
			return super.get(fileName, type);
		}

		public synchronized <T> T loadAndGet(final String fileName, final Class<T> type,
				final AssetLoaderParameters<T> parameter) {
			if (!isLoaded(fileName)) {
				load(fileName, type, parameter);
				finishLoadingAsset(fileName);
			}
			return super.get(fileName, type);
		}
	}

	protected final String TAG = this.getClass().getSimpleName();
	protected Music music;

	protected Skin skin;

	protected final Stage backStage;

	protected final Stage stage;
	protected final Stage frontStage;
	protected final Stage pausedStage;
	protected final InputMultiplexer inputMultiplexer;
	protected AbstractGame game;
	protected final AssetManager assets;
	private String backdropTextureFile = null;

	protected final Color clearColor = new Color(Color.BLACK);

	protected boolean isLoading = false;

	protected float totalElapsed = 0f;

	protected float currentElapsed = 0f;

	private boolean userPaused = false;

	private boolean systemPaused = false;

	private boolean wasMusicPlaying = false;

	public AbstractScreen(final AbstractGame game) {
		super();
		this.game = game;
		this.assets = new AssetManager();
		final TextureLoader textureLoader = new TextureLoader(new InternalFileHandleResolver()) {
			TextureParameter param = new TextureParameter();
			{
				param.magFilter = TextureFilter.Linear;
			}

			@Override
			public void loadAsync(final AssetManager manager, final String fileName, final FileHandle file,
					final TextureParameter parameter) {
				super.loadAsync(manager, fileName, file, parameter == null ? param : parameter);
			}

			@Override
			public Texture loadSync(final AssetManager manager, final String fileName, final FileHandle file,
					final TextureParameter parameter) {
				return super.loadSync(manager, fileName, file, parameter == null ? param : parameter);
			}
		};
		this.assets.setLoader(Texture.class, textureLoader);

		backStage = new Stage(new FillViewport(CLL1.WORLDSIZE.x, CLL1.WORLDSIZE.y));
		stage = new Stage(new FitViewport(CLL1.WORLDSIZE.x, CLL1.WORLDSIZE.y));
		frontStage = new Stage(new FitViewport(CLL1.WORLDSIZE.x, CLL1.WORLDSIZE.y));
		pausedStage = new Stage(new FitViewport(CLL1.WORLDSIZE.x, CLL1.WORLDSIZE.y));
		inputMultiplexer = new InputMultiplexer(this, frontStage, stage, backStage);
	}

	protected abstract void act(float delta);

	@Override
	public void dispose() {
		log("Dispose");
		assets.dispose();
	}

	@Override
	public void hide() {
		log("hide");
		Gdx.input.setInputProcessor(null);
		if (music != null) {
			wasMusicPlaying = music.isPlaying();
			music.pause();
		} else {
			wasMusicPlaying = false;
		}
	}

	public boolean isUserPaused() {
		return userPaused;
	}

	@Override
	public boolean keyDown(final int keycode) {
		switch (keycode) {
		case Keys.BACK:
			return onBack();
		case Keys.ESCAPE:
			return onBack();
		case Keys.MENU:
			return onMenu();
		case Keys.F1:
			return onMenu();
		}
		return false;
	}

	@Override
	public boolean keyTyped(final char character) {
		return false;
	}

	@Override
	public boolean keyUp(final int keycode) {
		return false;
	}

	protected void log(final String message) {
		Gdx.app.log(TAG, message);
	}

	@Override
	public boolean mouseMoved(final int screenX, final int screenY) {
		return false;
	}

	protected abstract boolean onBack();

	protected abstract boolean onMenu();

	@Override
	public void pause() {
		if (!Gdx.app.getType().equals(ApplicationType.Desktop)) {
			systemPaused = true;
		}
		log("Pause");
		Gdx.input.setInputProcessor(null);
		if (music != null) {
			if (!Gdx.app.getType().equals(ApplicationType.Desktop)) {
				wasMusicPlaying = music.isPlaying();
				music.pause();
			}
		} else {
			wasMusicPlaying = false;
		}
	}

	@Override
	public void render(final float delta) {

		if (!systemPaused && !userPaused) {
			totalElapsed += delta;
			currentElapsed += delta;
			act(delta);
			backStage.act(delta);
			stage.act(delta);
			frontStage.act(delta);
		}
		if (userPaused) {
			pausedStage.act(delta);
		}

		Gdx.gl.glClearColor(clearColor.r, clearColor.g, clearColor.b, clearColor.a);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		backStage.getViewport().apply();
		backStage.draw();
		stage.getViewport().apply();
		stage.draw();
		frontStage.getViewport().apply();
		frontStage.draw();
		if (userPaused) {
			pausedStage.getViewport().apply();
			pausedStage.draw();
		}
		isLoading = assets.update(30);
	}

	@Override
	public void resize(final int newWidth, final int newHeight) {
		log("Resize: " + newWidth + "x" + newHeight);
		backStage.getViewport().update(newWidth, newHeight);
		stage.getViewport().update(newWidth, newHeight);
		frontStage.getViewport().update(newWidth, newWidth);
		pausedStage.getViewport().update(newWidth, newHeight);
	}

	@Override
	public void resume() {
		systemPaused = false;
		log("Resume");
		Gdx.input.setInputProcessor(inputMultiplexer);
		if (wasMusicPlaying && music != null) {
			music.play();
		}
	}

	@Override
	public boolean scrolled(final int amount) {
		return false;
	}

	public void setBackdrop(final String textureFile) {
		if (backdropTextureFile != null) {
			assets.unload(backdropTextureFile);
		}
		backdropTextureFile = textureFile;
		assets.load(textureFile, Texture.class);
		assets.finishLoadingAsset(textureFile);
		final Texture texture = assets.get(textureFile, Texture.class);
		final TiledDrawable tiled = new TiledDrawable(new TextureRegion(texture));
		final Image img = new Image(tiled);
		img.setFillParent(true);
		backStage.addActor(img);
	}

	protected void setClearColor(final Color color) {
		clearColor.set(color);
	}

	protected void setSkin(final String skinJson) {
		this.assets.load(skinJson, Skin.class);
		this.assets.finishLoadingAsset(skinJson);
		this.skin = this.assets.get(skinJson, Skin.class);
		for (final BitmapFont bf : this.skin.getAll(BitmapFont.class).values()) {
			bf.getRegion().getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
			bf.setUseIntegerPositions(false);
		}
	}

	@Override
	public void show() {
		log("Show");
		Gdx.input.setInputProcessor(inputMultiplexer);
		if (wasMusicPlaying && music != null) {
			music.play();
		}
	}

	@Override
	public boolean touchDown(final int screenX, final int screenY, final int pointer, final int button) {
		return false;
	}

	@Override
	public boolean touchDragged(final int screenX, final int screenY, final int pointer) {
		return false;
	}

	@Override
	public boolean touchUp(final int screenX, final int screenY, final int pointer, final int button) {
		return false;
	}

	public void userPause() {
		userPaused = true;
		inputMultiplexer.removeProcessor(pausedStage);
		inputMultiplexer.addProcessor(0, pausedStage);
	}

	public void userPauseToggle() {
		if (userPaused) {
			userResume();
		} else {
			userPause();
		}
	}

	public void userResume() {
		userPaused = false;
		inputMultiplexer.removeProcessor(pausedStage);
	}
}
