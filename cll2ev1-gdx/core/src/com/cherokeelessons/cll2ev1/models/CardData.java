package com.cherokeelessons.cll2ev1.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.badlogic.gdx.files.FileHandle;
import com.cherokeelessons.deck.ICardData;

public class CardData implements ICardData {
	
	public String audio;
	private List<FileHandle> audioFiles=new ArrayList<FileHandle>();
	public String blacklistPic;
	public int chapter;
	private List<FileHandle> imageFiles=new ArrayList<FileHandle>();
	public String images;
	private List<FileHandle> randomAudioFiles=new ArrayList<FileHandle>();
	private List<FileHandle> randomImageFiles=new ArrayList<FileHandle>();
	public String text;
	
	public void addAudioFile(FileHandle file){
		audioFiles.add(file);
	}
	
	public void addImageFile(FileHandle file){
		imageFiles.add(file);
	}
	@Override
	public CardData copy() {
		CardData copy = new CardData();
		copy.audio=audio;
		copy.audioFiles=new ArrayList<FileHandle>(audioFiles);
		copy.blacklistPic=blacklistPic;
		copy.chapter=chapter;
		copy.imageFiles=new ArrayList<FileHandle>(imageFiles);
		copy.images=images;
		copy.randomAudioFiles=new ArrayList<FileHandle>(randomAudioFiles);
		copy.randomImageFiles=new ArrayList<FileHandle>(randomImageFiles);
		copy.text=text;
		return copy;
	}
	public boolean hasAudioFiles(){
		return audioFiles.size()!=0;
	}
	public boolean hasImageFiles(){
		return imageFiles.size()!=0;
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
	
	public FileHandle nextRandomAudioFile(){
		if (!hasAudioFiles()) {
			return null;
		}
		if (randomAudioFiles.size()==0) {
			randomAudioFiles.addAll(audioFiles);
			Collections.shuffle(randomAudioFiles);
		}
		return randomAudioFiles.remove(0);
	}

	public FileHandle nextRandomImageFile(){
		if (!hasImageFiles()) {
			return null;
		}
		if (randomImageFiles.size()==0) {
			randomImageFiles.addAll(imageFiles);
			Collections.shuffle(randomImageFiles);
		}
		return randomImageFiles.remove(0);
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
