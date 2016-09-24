package com.cherokeelessons.cll2ev1;

import java.nio.charset.StandardCharsets;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.cherokeelessons.cll2ev1.models.CardData;
import com.cherokeelessons.cll2ev1.models.GameCard;

/**
 * Build up master list of audio files used by the cards with directory names.
 * Then update the cards with this data. <br/>
 * This code ASSUMES that the 0_dirs.txt and 0_files.txt are in place and
 * up-to-date.
 */

public class LoadAudioFilenames implements Runnable {
	private boolean debug=true;
	private static final String UTF_8 = StandardCharsets.UTF_8.name();
	private FileHandle cardAudioDir = Gdx.files.internal("card-data/audio/");
	
	private void log(String message) {
		Gdx.app.log(this.getClass().getSimpleName(), message);
	}

	private List<GameCard> cards;

	public LoadAudioFilenames(CLL2EV1 game) {
		cards = game.cards;
	}
	
	@Override
	public void run() {
		// each top level directory is a two-digit number that is the chapter
		// number in the csv file
		String[] dirs = cardAudioDir.child("0_dirs.txt").readString(UTF_8)
				.split("\n");
		for (String dir: dirs) {
			FileHandle subDir = cardAudioDir.child(dir);
			String[] audioFiles = subDir.child("0_files.txt").readString(UTF_8).split("\n");
			for (GameCard card: cards) {
				CardData cd = card.getData();
				if (cd==null || cd.audio==null) {
					continue;
				}
				String c = cd.chapter<10?"0"+cd.chapter:""+cd.chapter;
				if (!c.equals(dir)){
					continue;
				}
				String[] audioPrefixes = cd.audio.split(";\\s*");
				for (String audioPrefix: audioPrefixes) {
					for (String audioFile: audioFiles) {
						if (!audioFile.endsWith(".mp3")){
							continue;
						}
						if (audioFile.startsWith(audioPrefix+".")){
							cd.addAudioFile(subDir.child(audioFile));
							continue;
						}
						if (audioFile.startsWith(audioPrefix+"_")){
							cd.addAudioFile(subDir.child(audioFile));
							continue;
						}
					}
				}
			}
		}
		if (debug) {
			log("=== DEBUG - CARD DATA AUDIO FILE ASSIGNMENTS:");
			for (GameCard card: cards) {
				CardData data = card.getData();
				if (data.hasAudioFiles()){
					log("CARD: "+data.chapter+" - "+data.audio+" - "+ data.nextRandomAudioFile().path());
				}
			}
		}
	}

}
