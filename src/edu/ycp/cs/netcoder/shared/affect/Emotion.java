package edu.ycp.cs.netcoder.shared.affect;

/**
 * Emotion values for affect data collection.
 */
public enum Emotion {
	BORED,
	CONFUSED,
	DELIGHTED,
	NEUTRAL,
	FOCUSED,
	OTHER,
	FRUSTRATED;
	
	/**
	 * @return a "nice" string suitable for presentation in the UI
	 */
	public String toNiceString() {
		String s = toString();
		return s.charAt(0) + s.substring(1).toLowerCase();
	}
}
