package com.cherokeelessons.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

public class SlotFolder {

	public static final String base = "CLL2EV1";

	public static FileHandle getSlotFolder(int slot) {
		return getFolder("/slots/" + slot);
	}

	public static FileHandle getFolder(String child) {
		final FileHandle p0;
		String path0 = base;
		switch (Gdx.app.getType()) {
		case Android:
			p0 = Gdx.files.local(path0);
			break;
		case Applet:
			p0 = Gdx.files.external(path0);
			break;
		case Desktop:
			p0 = Gdx.files.external(path0);
			break;
		case HeadlessDesktop:
			p0 = Gdx.files.external(path0);
			break;
		case WebGL:
			p0 = Gdx.files.external(path0);
			break;
		case iOS:
			p0 = Gdx.files.local(path0);
			break;
		default:
			p0 = Gdx.files.external(path0);
		}
		p0.child(child).mkdirs();
		return p0.child(child);
	}

	public static FileHandle getDeckSlot() {
		return getFolder("deck");
	}
}
