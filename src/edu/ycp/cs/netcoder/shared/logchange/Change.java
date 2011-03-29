package edu.ycp.cs.netcoder.shared.logchange;

import java.util.Arrays;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Object representing a textual change.
 * The client sends these to the server so that we
 * can capture the user's edit history.
 */
@Entity
@Table(name="changes")
public class Change implements IsSerializable {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    @Column(name="id")
    private long id;
    @Column(name="user_id")
    private long userId;
    @Column(name="problem_id")
    private long problemId;

    @Column(name="type")
    private String type;
    @Column(name="start_row")
    private int startRow;
    @Column(name="start_col")
	private int startColumn;
    @Column(name="end_row")
	private int endRow;
    @Column(name="end_col")
	private int endColumn;
    @Column(name="timestamp")
	private long timestamp;
    @Column(name="text")
	private String text;
	

	// Zero-arg constructor - required for serialization
    // also required for persistence
	public Change() {
	}
	
	private Change(ChangeType type, int sr, int sc, int er, int ec, long ts) {
		this.type = type.toString();
		this.startRow = sr;
		this.startColumn = sc;
		this.endRow = er;
		this.endColumn = ec;
		this.timestamp = ts;
	}
	
	public Change(ChangeType type, int sr, int sc, int er, int ec, long ts, String text) {
		this(type, sr, sc, er, ec, ts);
		//this.text = Collections.singletonList(text);
		this.text=text;
	}
	
	public Change(ChangeType type, int sr, int sc, int er, int ec, long ts, String[] textToAdopt) {
		this(type, sr, sc, er, ec, ts);
		StringBuffer buf=new StringBuffer();
		for (int i=0; i<textToAdopt.length-1; i++) {
		    buf.append(textToAdopt[i]);
		    buf.append("\n");
		}
		buf.append(textToAdopt[textToAdopt.length-1]);
		this.text = buf.toString();
	}
	
	public ChangeType getType() {
		return ChangeType.valueOf(type);
	}
	
	/**
	 * Get single chunk of text (for INSERT_TEXT and REMOVE_TEXT events).
	 * 
	 * @return chunk of text inserted or removed
	 */
	public String getText() {
		//assert text.length == 1;
		//return text[0];
	    return text;
	}

	/**
	 * Get given line (for INSERT_LINES and REMOVE_LINES) events.
	 * 
	 * @param index index of the inserted or removed line (0 for first).
	 * @return the inserted or removed line
	 */
	public String getLine(int index) {
		return text.split("\n")[index];
	}
	
	/**
	 * Get number of lines.
	 * Always 1 for INSERT_TEXT and REMOVE_TEXT events.
	 * Could be greater than 1 for INSERT_LINES and REMOVE_LINES events.
	 * 
	 * @return number of lines
	 */
	public int getNumLines() {
	    if (type.equals(ChangeType.INSERT_TEXT.toString()) ||
	            type.equals(ChangeType.REMOVE_TEXT.toString()))
	    {
	        return 1;
	    }
		return text.split("\n").length;
	}
	
	/**
	 * @return start row of change
	 */
	public int getStartRow() {
		return startRow;
	}

	/**
	 * @return start column of change
	 */
	public int getStartColumn() {
		return startColumn;
	}
	
	/**
	 * @return end row of change
	 */
	public int getEndRow() {
		return endRow;
	}
	
	/**
	 * @return end column of change
	 */
	public int getEndColumn() {
		return endColumn;
	}
	
	/**
	 * @return timestamp of change (milliseconds since epoch), as reported by client
	 */
	public long getTimestamp() {
		return timestamp;
	}
	
	/**
     * @return the id
     */
    public long getId()
    {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(long id)
    {
        this.id = id;
    }

    /**
     * @return the userId
     */
    public long getUserId()
    {
        return userId;
    }

    /**
     * @param userId the userId to set
     */
    public void setUserId(long userId)
    {
        this.userId = userId;
    }

    /**
     * @return the problemId
     */
    public long getProblemId()
    {
        return problemId;
    }

    /**
     * @param problemId the problemId to set
     */
    public void setProblemId(long problemId)
    {
        this.problemId = problemId;
    }
    /**
     * @param type the type to set
     */
    public void setType(String type){
        this.type = type;
    }
    /**
     * @param startRow the startRow to set
     */
    public void setStartRow(int startRow)    {
        this.startRow = startRow;
    }
    /**
     * @param startColumn the startColumn to set
     */
    public void setStartColumn(int startColumn){
        this.startColumn = startColumn;
    }
    /**
     * @param endRow the endRow to set
     */
    public void setEndRow(int endRow){
        this.endRow = endRow;
    }
    /**
     * @param endColumn the endColumn to set
     */
    public void setEndColumn(int endColumn){
        this.endColumn = endColumn;
    }
    /**
     * @param timestamp the timestamp to set
     */
    public void setTimestamp(long timestamp){
        this.timestamp = timestamp;
    }
    /**
     * @param text the text to set
     */
    public void setText(String text){
        this.text = text;
    }
    @Override
	public String toString() {
		return type + "," + startRow + "," + startColumn + "," + endRow + "," + endColumn + "," + timestamp + "," + Arrays.asList(text);
	}
}
