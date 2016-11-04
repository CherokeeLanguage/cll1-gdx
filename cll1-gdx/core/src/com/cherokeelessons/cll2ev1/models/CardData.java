package com.cherokeelessons.cll2ev1.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.cherokeelessons.deck.ICardData;

public class CardData implements ICardData {
	public static final int MAX_TRIES = 5;
	public static final int SORT_KEY_LENGTH = 7;
	public String audio;
	private List<String> audioFiles = new ArrayList<String>();
	public String blacklistPic;
	public int chapter;
	private List<String> imageFiles = new ArrayList<String>();
	private List<String> wrongImageFiles = new ArrayList<String>();
	public String images;
	private List<String> randomAudioFiles = new ArrayList<String>();
	private List<String> randomImageFiles = new ArrayList<String>();
	private List<String> randomWrongImageFiles = new ArrayList<String>();
	public String text;
	protected String englishGloss;

	public String getEnglishGloss() {
		return englishGloss;
	}

	public void setEnglishGloss(String englishGloss) {
		this.englishGloss = englishGloss;
	}

	public void addAudioFile(String file) {
		audioFiles.add(file);
	}

	public void addImageFile(String file) {
		imageFiles.add(file);
	}

	public void addWrongImageFile(String file) {
		wrongImageFiles.add(file);
	}

	@Override
	public CardData copy() {
		CardData copy = new CardData();
		copy.audio = audio;
		copy.audioFiles = new ArrayList<String>(audioFiles);
		copy.blacklistPic = blacklistPic;
		copy.chapter = chapter;
		copy.imageFiles = new ArrayList<String>(imageFiles);
		copy.wrongImageFiles = new ArrayList<String>(wrongImageFiles);
		copy.images = images;
		copy.randomAudioFiles = new ArrayList<String>(randomAudioFiles);
		copy.randomImageFiles = new ArrayList<String>(randomImageFiles);
		copy.randomWrongImageFiles = new ArrayList<String>(randomWrongImageFiles);
		copy.text = text;
		copy.setEnglishGloss(getEnglishGloss());
		return copy;
	}

	public boolean hasAudioFiles() {
		return audioFiles.size() != 0;
	}

	public boolean hasImageFiles() {
		return imageFiles.size() != 0;
	}

	public boolean hasWrongImageFiles() {
		return wrongImageFiles.size() != 0;
	}

	/**
	 * {@inheritDoc} <br>
	 * This implementation uses the pattern: 'NNN-TEXT-AUDIO'. Where the first
	 * NNN is the chapter number. The TEXT is the challenge text. And AUDIO is
	 * the audio filename prefix.<br>
	 */
	@Override
	public String id() {
		StringBuilder sb = new StringBuilder();
		if (chapter < 100) {
			sb.append("0");
		}
		if (chapter < 10) {
			sb.append("0");
		}
		sb.append(chapter);
		sb.append("-");
		sb.append(text == null ? "" : text.trim());
		sb.append("-");
		sb.append(audio == null ? "" : audio.replaceAll(";.*", "").trim());
		return sb.toString();
	}

	public String nextRandomAudioFile() {
		if (!hasAudioFiles()) {
			return null;
		}
		if (randomAudioFiles.size() == 0) {
			randomAudioFiles.addAll(audioFiles);
			Collections.shuffle(randomAudioFiles);
		}
		return randomAudioFiles.remove(0);
	}

	public String nextRandomImageFile() {
		if (!hasImageFiles()) {
			return null;
		}
		if (randomImageFiles.size() == 0) {
			randomImageFiles.addAll(imageFiles);
			Collections.shuffle(randomImageFiles);
		}
		return randomImageFiles.remove(0);
	}

	public List<String> getWrongImageFiles() {
		return wrongImageFiles;
	}

	public List<String> getImageFiles() {
		return imageFiles;
	}

	public List<String> getRandomAudioFiles() {
		return randomAudioFiles;
	}

	public List<String> getRandomImageFiles() {
		return randomImageFiles;
	}

	public List<String> getRandomWrongImageFiles() {
		return randomWrongImageFiles;
	}

	public String nextRandomWrongImageFile() {
		if (!hasWrongImageFiles()) {
			return null;
		}
		if (randomWrongImageFiles.size() == 0) {
			randomWrongImageFiles.addAll(wrongImageFiles);
			Collections.shuffle(randomWrongImageFiles);
		}
		return randomWrongImageFiles.remove(0);
	}

	/**
	 * {@inheritDoc} <br>
	 * This implementation uses the pattern: 'NNN-NNN-TEXT-AUDIO'. Where the
	 * first NNN is the chapter number. The second NNN is the length of the
	 * challenge text. The TEXT is the challenge text. And AUDIO is the audio
	 * filename prefix. The recommended sort key length is either 7 or 9 for
	 * sorted shuffling.
	 */
	@Override
	public String sortKey() {
		StringBuilder sb = new StringBuilder();
		if (chapter < 100) {
			sb.append("0");
		}
		if (chapter < 10) {
			sb.append("0");
		}
		sb.append(chapter);
		sb.append("-");
		int len = text.replaceAll("[^Ꭰ-Ᏼ]", "").length();
		if (len < 100) {
			sb.append("0");
		}
		if (len < 10) {
			sb.append("0");
		}
		sb.append(len);
		sb.append("-");
		sb.append(text == null ? "" : text.trim());
		sb.append("-");
		sb.append(audio == null ? "" : audio.replaceAll(";.*", "").trim());
		return sb.toString();
	}

	/**
	 * Only uses {@link #chapter}, {@link #text}, and {@link #audio} for
	 * hashCode generation. <br>
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((audio == null) ? 0 : audio.hashCode());
		result = prime * result + chapter;
		result = prime * result + ((text == null) ? 0 : text.hashCode());
		return result;
	}

	/**
	 * Only uses {@link #chapter}, {@link #text}, and {@link #audio} for
	 * comparison. <br>
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof CardData)) {
			return false;
		}
		CardData other = (CardData) obj;
		if (audio == null) {
			if (other.audio != null) {
				return false;
			}
		} else if (!audio.equals(other.audio)) {
			return false;
		}
		if (chapter != other.chapter) {
			return false;
		}
		if (text == null) {
			if (other.text != null) {
				return false;
			}
		} else if (!text.equals(other.text)) {
			return false;
		}
		return true;
	}

}
