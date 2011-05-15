// NetCoder - a web-based pedagogical programming environment
// Copyright (C) 2011, Jaime Spacco <jspacco@knox.edu>
// Copyright (C) 2011, David H. Hovemeyer <dhovemey@ycp.edu>
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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.google.gwt.user.client.rpc.IsSerializable;

import edu.ycp.cs.netcoder.shared.event.Event;
import edu.ycp.cs.netcoder.shared.event.EventType;
import edu.ycp.cs.netcoder.shared.util.Publisher;

/**
 * Data for an affect data collection event.
 */
@Entity
@Table(name="affect_events")
public class AffectEvent extends Publisher implements IsSerializable {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="id")
	private int id;
	
	@Column(name="event_id")
	private int eventId;
	
	@Column(name="emotion")
	private int emotion;
	
	@Column(name="other_emotion")
	private String otherEmotion;
	
	@Column(name="emotion_level")
	private int emotionLevel;
	
	@Transient
	private Event event;
	
	@Transient
	private boolean complete;
	
	/**
	 * State change events published to subscribers.
	 */
	public enum State {
		/** The AffectEvent changed in some way. Hint is null. */
		DATA_MODIFIED,
		
		/** The AffectEvent is now complete. Hint is null. */
		COMPLETE;
	}

	/**
	 * Constructor for empty (unintialized) object.
	 */
	public AffectEvent() {
	}
	
	private void dataModified() {
		notifySubscribers(State.DATA_MODIFIED, null);
	}
	
	/**
	 * Create the Event object for this AffectData object.
	 * 
	 * @param userId     the user id
	 * @param problemId  the problem id
	 * @param timestamp  the timestamp
	 */
	public void createEvent(int userId, int problemId, long timestamp) {
		event = new Event(userId, problemId, EventType.AFFECT_DATA, timestamp);
		dataModified();
	}
	
	/**
	 * @return the Event object (null if none created yet)
	 */
	public Event getEvent() {
		return event;
	}
	
	/**
	 * Set the event object.
	 * 
	 * @param event the event object to set
	 */
	public void setEvent(Event event) {
		this.event = event;
		dataModified();
	}
	
	/**
	 * Set unique id.
	 * 
	 * @param id the unique id to set
	 */
	public void setId(int id) {
		this.id = id;
		dataModified();
	}
	
	/**
	 * Get the unique id.
	 * 
	 * @return the unique id
	 */
	public int getId() {
		return id;
	}
	
	/**
	 * Set unique id of the corresponding Event object.
	 * 
	 * @param eventId unique id of the corresponding Event object
	 */
	public void setEventId(int eventId) {
		this.eventId = eventId;
		dataModified();
	}
	
	/**
	 * Get unique id of corresponding Event object.
	 * 
	 * @return unique id of corresponding Event object
	 */
	public int getEventId() {
		return eventId;
	}
	
	/**
	 * Set the emotion value.
	 * 
	 * @param emotion the emotion value to set
	 */
	public void setEmotion(Emotion emotion) {
		this.emotion = emotion.ordinal();
		dataModified();
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
		dataModified();
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
		dataModified();
	}
	
	/**
	 * @return level of emotion on Likert scale (0 if not set)
	 */
	public int getEmotionLevel() {
		return emotionLevel;
	}
	
	/**
	 * Mark this AffectEvent object as being complete.
	 * 
	 * @param complete true if complete, false if not complete yet
	 */
	public void setComplete(boolean complete) {
		this.complete = complete;
		notifySubscribers(State.COMPLETE, null);
	}

	/**
	 * Return whether or not the AffectData is complete.
	 * 
	 * @return true if complete, false otherwise
	 */
	public boolean isComplete() {
		return complete;
	}

	/**
	 * Clear to default state.
	 */
	public void clear() {
		id = 0;
		eventId = 0;
		emotion = 0;
		otherEmotion = null;
		event = null;
		complete = false;
	}
}
