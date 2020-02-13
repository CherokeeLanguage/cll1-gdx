package com.cherokeelessons.cll1.desktop;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.Color;
import com.cherokeelessons.cll1.CLL1;

public class DesktopLauncher {
	public static void main(String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		
		config.width = (int) (2732 / 2.5);
		config.height = (int) (2048 / 2.5);
		
		config.allowSoftwareMode = true;
		config.backgroundFPS = 15;
		config.foregroundFPS = 30;
		config.initialBackgroundColor = Color.BLACK;
		config.resizable = true;
		config.title = "CLL1 - Cherokee Language Lessons 1";
		config.useHDPI = true;
		config.vSyncEnabled = false;
		config.x = -1;
		config.y = -1;
		/**
		 * Adds a window icon. Icons are tried in the order added, the first one
		 * that works is used. Typically three icons should be provided: 128x128
		 * (for Mac), 32x32 (for Windows and Linux), and 16x16 (for Windows).
		 */
		config.addIcon("icons/icon-128.png", FileType.Internal);
		config.addIcon("icons/icon-64.png", FileType.Internal);
		config.addIcon("icons/icon-32.png", FileType.Internal);
		config.addIcon("icons/icon-16.png", FileType.Internal);
		new LwjglApplication(new CLL1(), config);
	}
}
