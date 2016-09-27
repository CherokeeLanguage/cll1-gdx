package com.cherokeelessons.cll2ev1;

import java.nio.charset.StandardCharsets;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.cherokeelessons.cll2ev1.models.CardData;
import com.cherokeelessons.cll2ev1.models.GameCard;

public class LoadImageFilenames implements Runnable {

	private boolean debug = false;
	private static final String UTF_8 = StandardCharsets.UTF_8.name();
	private FileHandle cardImageDir = Gdx.files.internal("card-data/images/");

	private void log(String message) {
		Gdx.app.log(this.getClass().getSimpleName(), message);
	}

	private List<GameCard> cards;

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
			for (GameCard card : cards) {
				CardData cd = card.getData();
				if (cd == null || cd.images == null) {
					continue;
				}
				String c = ""+cd.chapter;
				if (cd.chapter<100) {
					c = "0"+c;
				}
				if (cd.chapter<10) {
					c = "0"+c;
				}
				if (!c.equals(dir)) {
					continue;
				}
				String[] imagePrefixes = cd.images.split(";\\s*");
				String[] imageBlacklistPrefixes = cd.blacklistPic.split(";\\s*");
				for (String imagePrefix : imagePrefixes) {
					nextImage: for (String imageFile : imageFiles) {
						//skip not recogized image files
						if (!imageFile.endsWith(".png") && !imageFile.endsWith(".jpg")
								&& !imageFile.endsWith(".jpeg")) {
							continue nextImage;
						}
						//matches as correct, add it to the correct side of things
						if (imageFile.startsWith(imagePrefix + ".")) {
							cd.addImageFile(subDir.child(imageFile).path());
							continue nextImage;
						}
						//matches as correct, add it to the correct side of things
						if (imageFile.startsWith(imagePrefix + "_")) {
							cd.addImageFile(subDir.child(imageFile).path());
							continue nextImage;
						}
						//see if it is ok to add to the wrong pics list
						for (String prefix: imageBlacklistPrefixes) {
							if (imageFile.startsWith(prefix+".")){
								continue nextImage;
							}
							if (imageFile.startsWith(prefix+"_")){
								continue nextImage;
							}
						}
						cd.addWrongImageFile(subDir.child(imageFile).path());
					}
				}
			}
		}
		if (debug) {
			log("=== DEBUG - CARD DATA IMAGE FILE ASSIGNMENTS:");
			for (GameCard card : cards) {
				CardData data = card.getData();
				if (data.hasImageFiles() && data.hasWrongImageFiles()) {
					log("");
					log("CARD [c]: " + data.chapter + " - " + data.nextRandomImageFile() + " - " + data.getImageFiles().size());
					log("CARD [w]: " + data.chapter + " - " + data.nextRandomWrongImageFile() + " - " + data.getWrongImageFiles().size());
				}
			}
		}
	}

}
