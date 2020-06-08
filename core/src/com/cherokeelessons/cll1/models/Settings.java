package com.cherokeelessons.cll1.models;

public class Settings {
	public String name = "";
	public boolean muted = false;

	public Settings() {
	}

	public Settings(final Settings settings) {
		this.muted = settings.muted;
		this.name = settings.name;
	}
}