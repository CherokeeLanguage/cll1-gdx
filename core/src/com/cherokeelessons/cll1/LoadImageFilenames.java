package com.cherokeelessons.cll1;

import java.nio.charset.Charset;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.cherokeelessons.cll1.models.CardData;
import com.cherokeelessons.cll1.models.GameCard;

public class LoadImageFilenames implements Runnable {

	/**
	 * Eight-bit UCS Transformation Format
	 */
	public static final Charset UTF_8 = Charset.forName("UTF-8");

	private final boolean debug = false;
	private final FileHandle cardImageDir = Gdx.files.internal("card-data/images/");

	private final List<GameCard> cards;

	public LoadImageFilenames(final CLL1 game) {
		cards = game.cards;
	}

	private void log(final String message) {
		Gdx.app.log(this.getClass().getSimpleName(), message);
	}

	@Override
	public void run() {
		// each top level directory is a two-digit number that is the chapter
		// number in the csv file
		final String[] dirs = cardImageDir.child("0_dirs.txt").readString(UTF_8.name()).split("\n");
		for (final String dir : dirs) {
			final FileHandle subDir = cardImageDir.child(dir);
			final String[] imageFiles = subDir.child("0_files.txt").readString(UTF_8.name()).split("\n");
			for (final GameCard card : cards) {
				final CardData cd = card.getData();
				if (cd == null || cd.images == null) {
					continue;
				}
				String c = "" + cd.chapter;
				if (cd.chapter < 100) {
					c = "0" + c;
				}
				if (cd.chapter < 10) {
					c = "0" + c;
				}
				if (!c.equals(dir)) {
					continue;
				}
				final String[] imagePrefixes = cd.images.split(";\\s*");
				final String[] imageBlacklistSubstrings = cd.blacklistPic.split(";\\s*");
				for (final String imagePrefix : imagePrefixes) {
					nextImage: for (final String imageFile : imageFiles) {
						// skip not recogized image files
						if (!imageFile.endsWith(".png") && !imageFile.endsWith(".jpg")
								&& !imageFile.endsWith(".jpeg")) {
							continue nextImage;
						}
						// matches as correct, add it to the correct side of
						// things
						if (imageFile.startsWith(imagePrefix + ".")) {
							cd.addImageFile(subDir.child(imageFile).path());
							continue nextImage;
						}
						// matches as correct, add it to the correct side of
						// things
						if (imageFile.startsWith(imagePrefix + "_")) {
							cd.addImageFile(subDir.child(imageFile).path());
							continue nextImage;
						}
						// see if it is ok to add to the wrong pics list
						for (final String substring : imageBlacklistSubstrings) {
							if (substring == null || substring.trim().isEmpty()) {
								break;
							}
							if (imageFile.contains(substring)) {
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
			for (final GameCard card : cards) {
				final CardData data = card.getData();
				if (data.hasImageFiles() && data.hasWrongImageFiles()) {
					log("");
					log("CARD [c]: " + data.chapter + " - " + data.nextRandomImageFile() + " - "
							+ data.getImageFiles().size());
					log("CARD [w]: " + data.chapter + " - " + data.nextRandomWrongImageFile() + " - "
							+ data.getWrongImageFiles().size());
				}
			}
		}
	}

}
