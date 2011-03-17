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
	 * Insert a line in a text document,
	 * pushing lines at or below index down one line.
	 * 
	 * @param index where to insert the line
	 * @param line line of text to insert
	 */
	public void insertLine(int index, String line) {
		lineList.add(index, line);
	}

	/**
	 * Remove line at given index.
	 * Lines below are moved up.
	 * 
	 * @param index index of line to remove
	 */
	public void removeLine(int index) {
		lineList.remove(index);
	}

	/**
	 * @return complete text of document as string
	 */
	public String getText() {
		StringBuilder buf = new StringBuilder();
		
		boolean lineNoNL = false;
		
		for (String s : lineList) {
			if (lineNoNL) {
				buf.append("[WTF?]");
			}
			
			buf.append(">>");
			if (s.endsWith("\n")) {
				buf.append(s.substring(0, s.length() - 1));
				buf.append("<<");
				buf.append("\n");
			} else if (s.contains("\n")) {
				int nl = s.indexOf('\n');
				buf.append(s.substring(0, nl));
				buf.append("!\n");
				buf.append(s.substring(nl+1));
				buf.append("<<\n");
			} else {
				buf.append(s);
				lineNoNL = true;
			}
		}
		
		return buf.toString();
	}
}
