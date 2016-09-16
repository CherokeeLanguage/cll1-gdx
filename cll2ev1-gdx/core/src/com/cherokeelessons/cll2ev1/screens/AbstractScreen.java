package com.cherokeelessons.cll2ev1.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.cherokeelessons.cll2ev1.AbstractGame;
import com.cherokeelessons.cll2ev1.CLL2EV1;

public abstract class AbstractScreen implements Screen, InputProcessor {
	protected final String TAG = this.getClass().getSimpleName();
	protected Music music;
	protected void log(String message) {
		Gdx.app.log(TAG, message);
	}

	protected final Stage backStage;
	protected final Stage stage;
	protected final Stage frontStage;
	protected final InputMultiplexer inputMultiplexer;
	protected AbstractGame game;
	protected final AssetManager assets;
	
	public AbstractScreen(AbstractGame game) {
		super();
		this.game = game;
		this.assets = new AssetManager();
		
		backStage = new Stage(new FitViewport(CLL2EV1.WORLDSIZE.x, CLL2EV1.WORLDSIZE.y));
		stage = new Stage(new FitViewport(CLL2EV1.WORLDSIZE.x, CLL2EV1.WORLDSIZE.y));
		frontStage = new Stage(new FitViewport(CLL2EV1.WORLDSIZE.x, CLL2EV1.WORLDSIZE.y));
		
		inputMultiplexer = new InputMultiplexer(this, frontStage, stage, backStage);
	}

	@Override
	public void resize(int newWidth, int newHeight) {
		log("Resize: " + newWidth + "x" + newHeight);
		stage.getViewport().update(newWidth, newHeight);
	}

	@Override
	public void show() {
		log("Show");
		Gdx.input.setInputProcessor(inputMultiplexer);
		if (wasMusicPlaying && music!=null) {
			music.play();
		}
	}

	protected boolean isLoading=false;
	@Override
	public void render(float delta) {
		
		if (!systemPaused) {
			backStage.act(delta);
			stage.act(delta);
			frontStage.act(delta);
		}
		
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		
		backStage.draw();
		stage.draw();
		frontStage.draw();
		
		isLoading=assets.update(30);
	}

	boolean systemPaused=false;
	boolean wasMusicPlaying=false;
	@Override
	public void pause() {
		systemPaused=true;
		log("Pause");
		Gdx.input.setInputProcessor(null);
		if (music!=null) {
			wasMusicPlaying=music.isPlaying();
			music.pause();
		} else {
			wasMusicPlaying=false;
		}
	}

	@Override
	public void resume() {
		systemPaused=false;
		log("Resume");
		Gdx.input.setInputProcessor(inputMultiplexer);
		if (wasMusicPlaying && music!=null) {
			music.play();
		}
	}

	@Override
	public void hide() {
		log("hide");
		Gdx.input.setInputProcessor(null);
		if (music!=null) {
			wasMusicPlaying=music.isPlaying();
			music.pause();
		} else {
			wasMusicPlaying=false;
		}
	}

	@Override
	public void dispose() {
		log("Dispose");
		assets.dispose();
	}

	@Override
	public boolean keyDown (int keycode){
		return false;
	}

	@Override
	public boolean keyUp (int keycode) {
		return false;
	}

	@Override
	public boolean keyTyped (char character){
		return false;
	}

	@Override
	public boolean touchDown (int screenX, int screenY, int pointer, int button){
		return false;
	}

	@Override
	public boolean touchUp (int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchDragged (int screenX, int screenY, int pointer){
		return false;
	}

	@Override
	public boolean mouseMoved (int screenX, int screenY){
		return false;
	}

	@Override
	public boolean scrolled (int amount){
		return false;
	}
}
