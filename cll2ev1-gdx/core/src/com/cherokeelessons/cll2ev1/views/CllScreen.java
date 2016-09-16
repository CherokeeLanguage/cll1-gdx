package com.cherokeelessons.cll2ev1.views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.cherokeelessons.cll2ev1.AbstractGame;
import com.cherokeelessons.cll2ev1.CLL2EV1;

public abstract class CllScreen implements Screen {
	protected final String TAG = this.getClass().getSimpleName();

	protected void log(String message) {
		Gdx.app.log(TAG, message);
	}

	protected final Stage stage;
	protected AbstractGame game;

	public CllScreen(AbstractGame game) {
		super();
		this.game = game;
		stage = new Stage(new FitViewport(CLL2EV1.worldSize.x, CLL2EV1.worldSize.y));
	}

	@Override
	public void resize(int newWidth, int newHeight) {
		log("Resize: " + newWidth + "x" + newHeight);
		stage.getViewport().update(newWidth, newHeight);
	}

	@Override
	public void show() {
		log("Show");
		Gdx.input.setInputProcessor(stage);
	}

	@Override
	public void render(float delta) {
		stage.act(delta);
		
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		stage.draw();
	}

	@Override
	public void pause() {
		log("Pause");
		Gdx.input.setInputProcessor(null);
	}

	@Override
	public void resume() {
		log("Resume");
		Gdx.input.setInputProcessor(stage);
	}

	@Override
	public void hide() {
		log("hide");
		Gdx.input.setInputProcessor(null);
	}

	@Override
	public void dispose() {
		log("Dispose");
	}

}
