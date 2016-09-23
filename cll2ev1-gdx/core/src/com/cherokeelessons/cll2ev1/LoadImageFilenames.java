package com.cherokeelessons.cll2ev1;

import java.nio.charset.StandardCharsets;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.cherokeelessons.cll2ev1.models.CardData;
import com.cherokeelessons.deck.Card;

public class LoadImageFilenames implements Runnable {

	private boolean debug = true;
	private static final String UTF_8 = StandardCharsets.UTF_8.name();
	private FileHandle cardImageDir = Gdx.files.internal("card-data/images/");

	private void log(String message) {
		Gdx.app.log(this.getClass().getSimpleName(), message);
	}

	private List<Card<CardData>> cards;

	public LoadImageFilenames(CLL2EV1 game) {
		cards = game.cards;
	}

	@Override
	public void run() {
		// each top level directory is a two-digit number that is the chapter
		// number in the csv file
		String[] dirs = cardImageDir.child("0_dirs.txt").readString(UTF_8).split("\n");
		for (String dir : dirs) {
			FileHandle subDir = cardImageDir.child(dir);
			String[] imageFiles = subDir.child("0_files.txt").readString(UTF_8).split("\n");
			for (Card<CardData> card : cards) {
				CardData cd = card.getData();
				if (cd == null || cd.images == null) {
					continue;
				}
				String c = cd.chapter < 10 ? "0" + cd.chapter : "" + cd.chapter;
				if (!c.equals(dir)) {
					continue;
				}
				String[] imagePrefixes = cd.images.split(";\\s*");
				for (String imagePrefix : imagePrefixes) {
					for (String imageFile : imageFiles) {
						if (!imageFile.endsWith(".png") && !imageFile.endsWith(".jpg")
								&& !imageFile.endsWith(".jpeg")) {
							continue;
						}
						if (imageFile.startsWith(imagePrefix + ".")) {
							cd.addImageFile(subDir.child(imageFile));
							continue;
						}
						if (imageFile.startsWith(imagePrefix + "_")) {
							cd.addImageFile(subDir.child(imageFile));
							continue;
						}
					}
				}
			}
		}
		if (debug) {
			log("=== DEBUG - CARD DATA IMAGE FILE ASSIGNMENTS:");
			for (Card<CardData> card : cards) {
				CardData data = card.getData();
				if (data.hasImageFiles()) {
					log("CARD: " + data.chapter + " - " + data.images + " - " + data.nextRandomImageFile().path());
				}
			}
		}
	}

}
