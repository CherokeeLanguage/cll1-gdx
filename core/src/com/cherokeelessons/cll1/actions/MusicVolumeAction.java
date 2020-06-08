package com.cherokeelessons.cll1.actions;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;

public class MusicVolumeAction extends TemporalAction {
	private Music music;

	float startVolume = 0;

	float endVolume = 1f;

	public MusicVolumeAction(final Music music, final float endVolume, final float duration) {
		this(music, endVolume, duration, Interpolation.linear);
	}

	public MusicVolumeAction(final Music music, final float endVolume, final float duration,
			final Interpolation interpolation) {
		super(duration, interpolation);
		this.music = music;
		this.endVolume = endVolume;
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
	public void reset() {
		super.reset();
		startVolume = 0f;
		endVolume = 0f;
		music = null;
	}

	@Override
	protected void update(final float percent) {
		if (this.music != null) {
			this.music.setVolume(percent * (endVolume - startVolume) + startVolume);
		}
	}
}