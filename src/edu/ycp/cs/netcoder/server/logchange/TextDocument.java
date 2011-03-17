package edu.ycp.cs.netcoder.server.logchange;

import java.util.ArrayList;
import java.util.List;

/**
 * Simple text document class; treats document as sequence of lines.
 */
public class TextDocument {
	private List<String> lineList;

	/**
	 * Constructor: initialize empty text document.
	 */
	public TextDocument() {
		lineList = new ArrayList<String>();
	}

	/**
	 * Append a line to the document.
	 * 
	 * @param line line of text to append
	 */
	public void append(String line) {
		lineList.add(line);
	}
	
	/**
	 * @return number of lines of text in document
	 */
	public int getNumLines() {
		return lineList.size();
	}
	
	/**
	 * Get line at given index (0 for first line).
	 * 
	 * @param index index of line (0 for first line)
	 * @return line of text
	 */
	public String getLine(int index) {
		return lineList.get(index);
	}
	
	/**
	 * Replace line at given index (0 for first line).
	 * 
	 * @param index index of line (0 for first line)
	 * @param line text value to set as new value of line
	 */
	public void setLine(int index, String line) {
		lineList.set(index, line);
	}

	/**
	 * @return complete text of document as string
	 */
	public String getText() {
		StringBuilder buf = new StringBuilder();
		
		for (String s : lineList) {
			buf.append(s);
		}
		
		return buf.toString();
	}
}
