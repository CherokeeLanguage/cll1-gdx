package com.cherokeelessons.cll2ev1.screens;

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
import com.cherokeelessons.cll2ev1.AbstractGame;
import com.cherokeelessons.cll2ev1.CLL2EV1;

public abstract class AbstractScreen implements Screen, InputProcessor {
	protected final String TAG = this.getClass().getSimpleName();
	protected Music music;
	protected Skin skin;

	protected void log(String message) {
		Gdx.app.log(TAG, message);
	}

	protected void setSkin(String skinJson) {
		this.assets.load(skinJson, Skin.class);
		this.assets.finishLoadingAsset(skinJson);
		this.skin = this.assets.get(skinJson, Skin.class);
		for (BitmapFont bf: this.skin.getAll(BitmapFont.class).values()) {
			bf.getRegion().getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
			bf.setUseIntegerPositions(false);
		}
	}

	protected final Stage backStage;
	protected final Stage stage;
	protected final Stage frontStage;
	protected final Stage pausedStage;
	protected final InputMultiplexer inputMultiplexer;
	protected AbstractGame game;
	protected final AssetManager assets;

	public static class SyncAssetManager extends AssetManager {
		public synchronized <T> T loadAndGet(String fileName, Class<T> type) {
			if (!isLoaded(fileName)) {
				load(fileName, type);
				finishLoadingAsset(fileName);
			}
			return super.get(fileName, type);
		}
		public synchronized <T> T loadAndGet(String fileName, Class<T> type, AssetLoaderParameters<T> parameter) {
			if (!isLoaded(fileName)) {
				load(fileName, type, parameter);
				finishLoadingAsset(fileName);
			}
			return super.get(fileName, type);
		}
	}
	
	public AbstractScreen(AbstractGame game) {
		super();
		this.game = game;
		this.assets = new AssetManager();
		TextureLoader textureLoader = new TextureLoader(new InternalFileHandleResolver()){
			TextureParameter param = new TextureParameter();
			{
				param.magFilter=TextureFilter.Linear;
			}
			
			@Override
			public void loadAsync(AssetManager manager, String fileName, FileHandle file, TextureParameter parameter) {
				super.loadAsync(manager, fileName, file, parameter==null?param:parameter);
			}
			@Override
			public Texture loadSync(AssetManager manager, String fileName, FileHandle file,
					TextureParameter parameter) {
				return super.loadSync(manager, fileName, file, parameter==null?param:parameter);
			}
		};
		this.assets.setLoader(Texture.class, textureLoader);
		
		backStage = new Stage(new FillViewport(CLL2EV1.WORLDSIZE.x, CLL2EV1.WORLDSIZE.y));
		stage = new Stage(new FitViewport(CLL2EV1.WORLDSIZE.x, CLL2EV1.WORLDSIZE.y));
		frontStage = new Stage(new FitViewport(CLL2EV1.WORLDSIZE.x, CLL2EV1.WORLDSIZE.y));
		pausedStage = new Stage(new FitViewport(CLL2EV1.WORLDSIZE.x, CLL2EV1.WORLDSIZE.y));
		inputMultiplexer = new InputMultiplexer(this, frontStage, stage, backStage);
	}
	
	private String backdropTextureFile=null;
	public void setBackdrop(String textureFile) {
		if (backdropTextureFile!=null) {
			assets.unload(backdropTextureFile);
		}
		backdropTextureFile=textureFile;
		assets.load(textureFile, Texture.class);
		assets.finishLoadingAsset(textureFile);
		Texture texture=assets.get(textureFile, Texture.class);
		TiledDrawable tiled = new TiledDrawable(new TextureRegion(texture));
		Image img=new Image(tiled);
		img.setFillParent(true);
		backStage.addActor(img);
	}
	
	protected final Color clearColor=new Color(Color.BLACK);
	protected void setClearColor(Color color) {
		clearColor.set(color);
	}

	@Override
	public void resize(int newWidth, int newHeight) {
		log("Resize: " + newWidth + "x" + newHeight);
		backStage.getViewport().update(newWidth, newHeight);
		stage.getViewport().update(newWidth, newHeight);
		frontStage.getViewport().update(newWidth, newWidth);
		pausedStage.getViewport().update(newWidth, newHeight);
	}

	@Override
	public void show() {
		log("Show");
		Gdx.input.setInputProcessor(inputMultiplexer);
		if (wasMusicPlaying && music != null) {
			music.play();
		}
	}

	protected abstract void act(float delta);
	
	protected boolean isLoading = false;
	protected float totalElapsed=0f;
	protected float currentElapsed=0f;
	@Override
	public void render(float delta) {

		if (!systemPaused && !userPaused) {
			totalElapsed+=delta;
			currentElapsed+=delta;
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

	private boolean userPaused = false;
	public boolean isUserPaused() {
		return userPaused;
	}

	private boolean systemPaused = false;
	private boolean wasMusicPlaying = false;

	public void userPauseToggle(){
		if (userPaused) {
			userResume();
		} else {
			userPause();
		}
	}
	public void userPause(){
		userPaused=true;
		inputMultiplexer.removeProcessor(pausedStage);
		inputMultiplexer.addProcessor(0, pausedStage);
	}
	public void userResume(){
		userPaused=false;
		inputMultiplexer.removeProcessor(pausedStage);
	}
	
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
	public void resume() {
		systemPaused = false;
		log("Resume");
		Gdx.input.setInputProcessor(inputMultiplexer);
		if (wasMusicPlaying && music != null) {
			music.play();
		}
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

	@Override
	public void dispose() {
		log("Dispose");
		assets.dispose();
	}

	protected abstract boolean onBack();
	protected abstract boolean onMenu();
	
	@Override
	public boolean keyDown(int keycode) {
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
	public boolean keyUp(int keycode) {
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		return false;
	}
}
