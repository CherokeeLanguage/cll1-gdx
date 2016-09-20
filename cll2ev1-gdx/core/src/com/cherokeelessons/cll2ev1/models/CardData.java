package com.cherokeelessons.cll2ev1.models;

import com.cherokeelessons.deck.ICardData;

public class CardData implements ICardData {
	
	public int chapter;
	public String text;
	public String audio;
	public String answerPic;
	public String blacklistPic;

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
		sb.append(audio==null?"":audio.trim());
		sb.append("-");
		sb.append(answerPic==null?"":answerPic.trim());
		sb.append("-");
		sb.append(blacklistPic==null?"":blacklistPic);
		return sb.toString();
	}

}
