package edu.ycp.cs.netcoder.shared.affect;

import com.google.gwt.user.client.rpc.IsSerializable;

public class AffectData implements IsSerializable {
	private String emotion;
	private String otherEmotion;
	private int boredomLevel;
	
	public AffectData() {
		
	}
	
	public void setEmotion(Emotion emotion) {
		this.emotion = emotion.toString();
	}
	
	public Emotion getEmotion() {
		return Emotion.valueOf(emotion);
	}
	
	public void setOtherEmotion(String otherEmotion) {
		this.otherEmotion = otherEmotion;
	}
	
	public String getOtherEmotion() {
		return otherEmotion;
	}
	
	public void setBoredomLevel(int boredomLevel) {
		this.boredomLevel = boredomLevel;
	}
	
	public int getBoredomLevel() {
		return boredomLevel;
	}
}
