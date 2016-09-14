package com.libgdx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.cherokeelessons.cll2ev1.views.AbstractView;


public class ViewScreenPoweredBy extends AbstractView {
    	
	private final Array<Image> logo = new Array<Image>();
	private final Array<Texture> textures = new Array<Texture>();

	private Music music;
	private Runnable onDone;

	public ViewScreenPoweredBy(Stage stage) {
		super(stage);
	}
	
	public ViewScreenPoweredBy(Stage stage, Runnable onDone) {
		this(stage);
		this.onDone=onDone;
	}

	@Override
	public void dispose() {
		super.dispose();
		music.dispose();
		for (Texture t: textures) {
			t.dispose();
		}
	}

	@Override
	public void hide() {
		super.hide();
		getStage().clear();
		music.stop();
	}
	
	private final float tvSafePercent=.05f;
	private final Rectangle tvSafe = new Rectangle((int)(1280f*tvSafePercent), (int)(720f*tvSafePercent), (int)(1280f*(1f-2f*tvSafePercent)), (int)(720f*(1f-2f*tvSafePercent)));
	private Rectangle logoBox;
	private void init() {
		music = Gdx.audio.newMusic(Gdx.files.internal("libgdx/atmoseerie03.mp3"));
		music.setVolume(0f);
		for (int i = 0; i < 25; i++) {
			Texture texture = new Texture(Gdx.files.internal("libgdx/1080p_" + i + ".png"));
			texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
			textures.add(texture);
			Image image = new Image(texture);
			image.pack();
			logo.add(image);
		}

		int width = 0;
		int height = 0;
		for (int x = 0; x < 5; x++) {
			height = 0;
			Image img=null;
			for (int y = 0; y < 5; y++) {
				int z = 4 - y;
				int p = z * 5 + x;
				img = logo.get(p);
				img.setOrigin(0, 0);
				img.setPosition(width, height);
				height += img.getHeight();				
			}
			width += img.getWidth();
		}
		
		logoBox = new Rectangle(0, 0, width, height);		
		logoBox.fitInside(tvSafe);
		float scaleXY=logoBox.height/height;
		if (scaleXY>logoBox.width/width) scaleXY=logoBox.width/width;
		
		Group logoGroup=new Group();
		for (Image img: logo) {
			logoGroup.addActor(img);
		}
		
		logoGroup.getColor().a=0f;
		logoGroup.setOrigin(Align.center);
		logoGroup.setScale(scaleXY);
		logoGroup.setTransform(true);
		
		logoGroup.setPosition(logoBox.x, logoBox.y);
		
		logoGroup.addAction(Actions.parallel(getAlphaAction(), getVolumeAction(music)));
		
		getStage().addActor(logoGroup);
		music.play();
	}
	
	@Override
	public void show() {
		super.show();
		init();
		music.play();
	}
	
	private Action getAlphaAction(){
		SequenceAction sa = Actions.sequence();
		sa.addAction(Actions.delay(1f));
		sa.addAction(Actions.alpha(1f, 4f));
		sa.addAction(Actions.delay(4f));
		sa.addAction(Actions.alpha(0f, 2f));
		sa.addAction(Actions.delay(1f));
		return sa;
	}
	
	private Action getVolumeAction(Music music){
		SequenceAction sa = Actions.sequence();
		sa.addAction(Actions.delay(1f));
		sa.addAction(new MusicVolumeAction(music, .7f, 4f));
		sa.addAction(Actions.delay(4f));
		sa.addAction(new MusicVolumeAction(music, 0f, 2f));
		sa.addAction(Actions.delay(1f));
		if (onDone!=null) {
			sa.addAction(Actions.run(onDone));
		}
		return sa;
	}
	
}
