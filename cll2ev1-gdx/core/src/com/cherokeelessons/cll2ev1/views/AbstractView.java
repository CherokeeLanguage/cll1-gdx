package com.cherokeelessons.cll2ev1.views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.github.czyzby.lml.parser.impl.AbstractLmlView;

public abstract class AbstractView extends AbstractLmlView {

	public AbstractView(Stage stage) {
		super(stage);
	}
	
	@Override
	public String getViewId() {
		return this.getClass().getSimpleName();
	}
	
	@Override
    public FileHandle getTemplateFile() {
    	return Gdx.files.internal("views/"+getViewId()+".xml");
    }

	@Override
	public void render() {
		getStage().act();
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        getStage().draw();
	}
	
	@Override
	public void render(float delta) {
		getStage().act(delta);
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        getStage().draw();
	}

}
