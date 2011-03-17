package edu.ycp.cs.netcoder.server.logchange;

import java.util.HashMap;
import java.util.Map;

/**
 * Type of textual change.
 */
public enum ChangeType {
	/** Insertion of text within a particular line */
	INSERT_TEXT,
	
	/** Removal of text within a particular line. */
	REMOVE_TEXT,
	
	/** Insertion of one or more lines. */
	INSERT_LINES,
	
	/** Removal of one or more lines. */
	REMOVE_LINES;
	
	private static final Map<String, ChangeType> compactStringToChangeTypeMap = new HashMap<String, ChangeType>();
	static {
		compactStringToChangeTypeMap.put("IT", INSERT_TEXT);
		compactStringToChangeTypeMap.put("RT", REMOVE_TEXT);
		compactStringToChangeTypeMap.put("IL", INSERT_LINES);
		compactStringToChangeTypeMap.put("RL", REMOVE_LINES);
	}

	/**
	 * Get change type from "compact string" (as sent by NetCoder client.)
	 * 
	 * @param s compact string representing change type.
	 * @return the ChangeType
	 */
	public static ChangeType fromCompactString(String s) {
		ChangeType changeType = compactStringToChangeTypeMap.get(s);
		if (changeType == null) {
			throw new IllegalArgumentException("Invalid compact change type string: " + s);
		}
		return changeType;
	}
}
