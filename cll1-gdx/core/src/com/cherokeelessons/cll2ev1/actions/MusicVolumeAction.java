package com.cherokeelessons.cll2ev1.actions;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;

public class MusicVolumeAction extends TemporalAction {
	private Music music;

	public MusicVolumeAction(Music music, float endVolume, float duration) {
		this(music, endVolume, duration, Interpolation.linear);
	}

	public MusicVolumeAction(Music music, float endVolume, float duration, Interpolation interpolation) {
		super(duration, interpolation);
		this.music = music;
		this.endVolume = endVolume;
	}

	float startVolume = 0;
	float endVolume = 1f;

	@Override
	public void reset() {
		super.reset();
		startVolume = 0f;
		endVolume = 0f;
		music = null;
	}

	@Override
	protected void begin() {
		super.begin();
		if (this.music == null) {
			startVolume = 0f;
			return;
		}
		startVolume = this.music.getVolume();
	}

	@Override
	protected void end() {
		super.end();
	}

	@Override
	protected void update(float percent) {
		if (this.music != null) {
			this.music.setVolume(percent * (endVolume - startVolume) + startVolume);
		}
	}
}