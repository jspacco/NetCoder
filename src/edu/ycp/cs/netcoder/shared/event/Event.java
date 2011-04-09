package edu.ycp.cs.netcoder.shared.event;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * "Superclass" for event types.
 * Records common information (timestamp, user id, problem id) about each
 * event, and has a link field (data_id) to a corresponding row in
 * another table with additional information about the specific event.
 */
@Entity
@Table(name="events")
public class Event implements IsSerializable {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="id")
	private int id;

	@Column(name="data_id")
	private int dataId;
	
	@Column(name="user_id")
	private int userId;
	
	@Column(name="problem_id")
	private int problemId;

	@Column(name="type")
	private int type;

	@Column(name="timestamp")
	private long timestamp;

	public Event() {

	}
	
	public Event(int id, int dataId, EventType type, long timestamp) {
		this.id = id;
		this.dataId = dataId;
		this.type = type.ordinal();
		this.timestamp = timestamp;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}
	
	public int getDataId() {
		return dataId;
	}
	
	public void setUserId(int userId) {
		this.userId = userId;
	}
	
	public int getUserId() {
		return userId;
	}
	
	public void setProblemId(int problemId) {
		this.problemId = problemId;
	}
	
	public int getProblemId() {
		return problemId;
	}
	
	public void setDataId(int dataId) {
		this.dataId = dataId;
	}

	public void setType(int type) {
		this.type = type;
	}

	public void setType(EventType type) {
		this.type = type.ordinal();
	}

	public int getType() {
		return type;
	}

	public EventType getEventType() {
		return EventType.values()[type];
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public long getTimestamp() {
		return timestamp;
	}
}
