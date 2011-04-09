// NetCoder - a web-based pedagogical programming environment
// Copyright (C) 2011, Jaime Spacco
// Copyright (C) 2011, David H. Hovemeyer
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU Affero General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU Affero General Public License for more details.
//
// You should have received a copy of the GNU Affero General Public License
// along with this program.  If not, see <http://www.gnu.org/licenses/>.

package edu.ycp.cs.netcoder.shared.affect;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Data for an affect data collection event.
 * TODO: persistence.
 */
public class AffectData implements IsSerializable {
	private int emotion;
	private String otherEmotion;
	private int emotionLevel;

	/**
	 * Constructor for empty (unintialized) object.
	 */
	public AffectData() {
	}
	
	/**
	 * Set the emotion value.
	 * 
	 * @param emotion the emotion value to set
	 */
	public void setEmotion(Emotion emotion) {
		this.emotion = emotion.ordinal();
	}
	
	/**
	 * @return the emotion value (null if none set)
	 */
	public Emotion getEmotion() {
		return Emotion.values()[emotion];
	}
	
	/**
	 * Set a user-defined emotion value.
	 * This should be set only if the previous call to
	 * <code>setEmotion</code> set the value <code>Emotion.OTHER</code>.
	 * 
	 * @param otherEmotion user-defined emotion value
	 */
	public void setOtherEmotion(String otherEmotion) {
		this.otherEmotion = otherEmotion;
	}
	
	/**
	 * @return user-defined emotion value (null if none set)
	 */
	public String getOtherEmotion() {
		return otherEmotion;
	}
	
	/**
	 * Set the level of emotion as a Likert scale (1 - 5).
	 * This should only be called if the previous call to
	 * <code>setEmotion</code> set the value to something
	 * other than <code>Emotion.OTHER</code>.
	 * 
	 * @param emotionLevel level of emotion on Likert scale 
	 */
	public void setEmotionLevel(int emotionLevel) {
		this.emotionLevel = emotionLevel;
	}
	
	/**
	 * @return level of emotion on Likert scale (0 if not set)
	 */
	public int getEmotionLevel() {
		return emotionLevel;
	}
}
