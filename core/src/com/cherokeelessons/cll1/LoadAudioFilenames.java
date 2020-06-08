package com.cherokeelessons.cll1;

import java.nio.charset.Charset;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.cherokeelessons.cll1.models.CardData;
import com.cherokeelessons.cll1.models.GameCard;

/**
 * Build up master list of audio files used by the cards with directory names.
 * Then update the cards with this data. <br/>
 * This code ASSUMES that the 0_dirs.txt and 0_files.txt are in place and
 * up-to-date.
 */

public class LoadAudioFilenames implements Runnable {

	/**
	 * Eight-bit UCS Transformation Format
	 */
	public static final Charset UTF_8 = Charset.forName("UTF-8");

	private final boolean debug = false;
	private final FileHandle cardAudioDir = Gdx.files.internal("card-data/audio/");

	private final List<GameCard> cards;

	public LoadAudioFilenames(final CLL1 game) {
		cards = game.cards;
	}

	private void log(final String message) {
		Gdx.app.log(this.getClass().getSimpleName(), message);
	}

	@Override
	public void run() {
		// each top level directory is a two-digit number that is the chapter
		// number in the csv file
		final String[] dirs = cardAudioDir.child("0_dirs.txt").readString(UTF_8.name()).split("\n");
		for (final String dir : dirs) {
			final FileHandle subDir = cardAudioDir.child(dir);
			final String[] audioFiles = subDir.child("0_files.txt").readString(UTF_8.name()).split("\n");
			for (final GameCard card : cards) {
				final CardData cd = card.getData();
				if (cd == null || cd.audio == null) {
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
				final String[] audioPrefixes = cd.audio.split(";\\s*");
				for (final String audioPrefix : audioPrefixes) {
					for (final String audioFile : audioFiles) {
						if (!audioFile.endsWith(".mp3")) {
							continue;
						}
						if (audioFile.startsWith(audioPrefix + ".")) {
							cd.addAudioFile(subDir.child(audioFile).path());
							continue;
						}
						if (audioFile.startsWith(audioPrefix + "_")) {
							cd.addAudioFile(subDir.child(audioFile).path());
							continue;
						}
					}
				}
			}
		}
		if (debug) {
			log("=== DEBUG - CARD DATA AUDIO FILE ASSIGNMENTS:");
			for (final GameCard card : cards) {
				final CardData data = card.getData();
				if (data.hasAudioFiles()) {
					log("CARD: " + data.chapter + " - " + data.audio + " - " + data.nextRandomAudioFile());
				}
			}
		}
	}

}
