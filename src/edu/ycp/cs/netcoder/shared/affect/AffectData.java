package edu.ycp.cs.netcoder.shared.affect;

import com.google.gwt.user.client.rpc.IsSerializable;

public class AffectData implements IsSerializable {
	private String emotion;
	
	public AffectData() {
		
	}
	
	public void setEmotion(Emotion emotion) {
		this.emotion = emotion.toString();
	}
	
	public Emotion getEmotion() {
		return Emotion.valueOf(emotion);
	}
}
