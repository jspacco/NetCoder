package edu.ycp.cs.netcoder.server.logchange;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Object representing a textual change.
 * The client sends these to the server so that we
 * can capture the user's edit history.
 */
public class Change {
	private final ChangeType type;
	private final int startRow, startColumn, endRow, endColumn;
	private final long timestamp;
	private List<String> text;

//	private static final Pattern META_PATTERN =
//		Pattern.compile("^(IT|RT|IL|RL)(\\d+),(\\d+),(\\d+),(\\d+),(\\d+)$");
//	
//	/**
//	 * Create Change from compact string sent by NetCoder client's LogCodeChangeService.
//	 * 
//	 * @param s the compact string
//	 * @return the Change object
//	 */
//	public static Change fromCompactString(String s) {
//		int semi = s.indexOf(';');
//		if (semi < 0) {
//			throw new IllegalArgumentException("Invalid change string: " + s);
//		}
//
//		String meta = s.substring(0, semi);
//		Matcher m = META_PATTERN.matcher(meta);
//		if (!m.matches()) {
//			throw new IllegalArgumentException("Invalid change string: " + s);
//		}
//		
//		ChangeType type = ChangeType.fromCompactString(m.group(1));
//		int startRow = Integer.parseInt(m.group(2));
//		int startColumn = Integer.parseInt(m.group(3));
//		int endRow = Integer.parseInt(m.group(4));
//		int endColumn = Integer.parseInt(m.group(5));
//		long timestamp = Long.parseLong(m.group(6));
//		
//		String jsonTextOrLines = s.substring(semi+1);
//
//		final List<String> text = new ArrayList<String>();
//		
//		ParseJSONCallback callback = new ParseJSONCallback() {
//			@Override
//			public void visitString(String s) {
//				text.add(s);
//			}
//		};
//		ParseJSON parser = new ParseJSON();
//		parser.parse(jsonTextOrLines, callback);
//
//		if (type == ChangeType.INSERT_TEXT || type == ChangeType.REMOVE_TEXT) {
//			return new Change(type, startRow, startColumn, endRow, endColumn, timestamp, text.get(0));
//		} else {
//			return new Change(type, startRow, startColumn, endRow, endColumn, timestamp, text);
//		}
//	}
	
	private Change(ChangeType type, int sr, int sc, int er, int ec, long ts) {
		this.type = type;
		this.startRow = sr;
		this.startColumn = sc;
		this.endRow = er;
		this.endColumn = ec;
		this.timestamp = ts;
	}
	
	public Change(ChangeType type, int sr, int sc, int er, int ec, long ts, String text) {
		this(type, sr, sc, er, ec, ts);
		this.text = Collections.singletonList(text);
	}
	
	public Change(ChangeType type, int sr, int sc, int er, int ec, long ts, List<String> textToAdopt) {
		this(type, sr, sc, er, ec, ts);
		this.text = textToAdopt;
	}
	
	public ChangeType getType() {
		return type;
	}
	
	/**
	 * Get single chunk of text (for INSERT_TEXT and REMOVE_TEXT events).
	 * 
	 * @return chunk of text inserted or removed
	 */
	public String getText() {
		assert text.size() == 1;
		return text.get(0);
	}

	/**
	 * Get given line (for INSERT_LINES and REMOVE_LINES) events.
	 * 
	 * @param index index of the inserted or removed line (0 for first).
	 * @return the inserted or removed line
	 */
	public String getLine(int index) {
		return text.get(index);
	}
	
	/**
	 * Get number of lines.
	 * Always 1 for INSERT_TEXT and REMOVE_TEXT events.
	 * Could be greater than 1 for INSERT_LINES and REMOVE_LINES events.
	 * 
	 * @return number of lines
	 */
	public int getNumLines() {
		return text.size();
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
	
	@Override
	public String toString() {
		return type + "," + startRow + "," + startColumn + "," + endRow + "," + endColumn + "," + timestamp + "," + text;
	}
}
