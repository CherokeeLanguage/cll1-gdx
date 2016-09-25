package com.cherokeelessons.cll2ev1.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.cherokeelessons.deck.ICardData;

public class CardData implements ICardData {
	
	public String audio;
	private List<String> audioFiles=new ArrayList<String>();
	public String blacklistPic;
	public int chapter;
	private List<String> imageFiles=new ArrayList<String>();
	private List<String> wrongImageFiles=new ArrayList<String>();
	public String images;
	private List<String> randomAudioFiles=new ArrayList<String>();
	private List<String> randomImageFiles=new ArrayList<String>();
	private List<String> randomWrongImageFiles=new ArrayList<String>();
	public String text;
	
	public void addAudioFile(String file){
		audioFiles.add(file);
	}
	
	public void addImageFile(String file){
		imageFiles.add(file);
	}
	
	public void addWrongImageFile(String file){
		wrongImageFiles.add(file);
	}
	@Override
	public CardData copy() {
		CardData copy = new CardData();
		copy.audio=audio;
		copy.audioFiles=new ArrayList<String>(audioFiles);
		copy.blacklistPic=blacklistPic;
		copy.chapter=chapter;
		copy.imageFiles=new ArrayList<String>(imageFiles);
		copy.wrongImageFiles=new ArrayList<String>(wrongImageFiles);
		copy.images=images;
		copy.randomAudioFiles=new ArrayList<String>(randomAudioFiles);
		copy.randomImageFiles=new ArrayList<String>(randomImageFiles);
		copy.randomWrongImageFiles=new ArrayList<String>(randomWrongImageFiles);
		copy.text=text;
		return copy;
	}
	public boolean hasAudioFiles(){
		return audioFiles.size()!=0;
	}
	public boolean hasImageFiles(){
		return imageFiles.size()!=0;
	}
	public boolean hasWrongImageFiles(){
		return wrongImageFiles.size()!=0;
	}
	
	@Override
	public String id() {
		StringBuilder sb = new StringBuilder();
		if (chapter<100) {
			sb.append("0");
		}
		if (chapter<10) {
			sb.append("0");
		}
		sb.append(chapter);
		sb.append("-");
		sb.append(text==null?"":text.trim());
		sb.append("-");
		sb.append(audio==null?"":audio.replaceAll(";.*", "").trim());
		return sb.toString();
	}
	
	public String nextRandomAudioFile(){
		if (!hasAudioFiles()) {
			return null;
		}
		if (randomAudioFiles.size()==0) {
			randomAudioFiles.addAll(audioFiles);
			Collections.shuffle(randomAudioFiles);
		}
		return randomAudioFiles.remove(0);
	}

	public String nextRandomImageFile(){
		if (!hasImageFiles()) {
			return null;
		}
		if (randomImageFiles.size()==0) {
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

	public String nextRandomWrongImageFile(){
		if (!hasWrongImageFiles()) {
			return null;
		}
		if (randomWrongImageFiles.size()==0) {
			randomWrongImageFiles.addAll(wrongImageFiles);
			Collections.shuffle(randomWrongImageFiles);
		}
		return randomWrongImageFiles.remove(0);
	}
	
	@Override
	public String sortKey() {
		StringBuilder sb = new StringBuilder();
		if (chapter<100) {
			sb.append("0");
		}
		if (chapter<10) {
			sb.append("0");
		}
		sb.append(chapter);
		sb.append("-");
		sb.append(text==null?"":text.trim());
		sb.append("-");
		sb.append(audio==null?"":audio.replaceAll(";.*", "").trim());
		return sb.toString();
	}
}
