package com.cherokeelessons.cll2ev1.models;

public class Settings {
	public String name = "";
	public boolean muted = false;

	public Settings() {
	}

	public Settings(Settings settings) {
		this.muted = settings.muted;
		this.name = settings.name;
	}
}