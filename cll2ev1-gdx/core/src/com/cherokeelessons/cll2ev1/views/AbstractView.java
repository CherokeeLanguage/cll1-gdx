package com.cherokeelessons.cll2ev1.views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;

public abstract class AbstractView extends ScreenAdapter {
	
	protected Stage stage;

	protected void log(String message) {
		Gdx.app.log(this.getClass().getSimpleName(), message);
	}

	public AbstractView(Stage stage) {
		this.stage=stage;
	}
	
	@Override
	public void render(float delta) {
		stage.act(delta);
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.draw();
	}

}
