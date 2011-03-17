package edu.ycp.cs.netcoder.server.logchange;

/**
 * Apply a Change object to a TextDocument.
 */
public class ApplyChangeToTextDocument {
	/**
	 * Apply a Change object to a TextDocument.
	 * 
	 * @param change a Change object
	 * @param doc    a TextDocument to which the Change should be applied
	 */
	public void apply(Change change, TextDocument doc) {
		String s, up;
		
		switch (change.getType()) {
		case INSERT_TEXT:
			if (change.getStartRow() != change.getEndRow() && !change.getText().equals("\n")) {
				throw new IllegalArgumentException("Multi-line INSERT_TEXT change? " + change);
			}
			if (change.getStartRow() == doc.getNumLines()) {
				doc.append("");
			}
			s = doc.getLine(change.getStartRow());
			up = s.substring(0, change.getStartColumn()) + change.getText() + s.substring(change.getStartColumn());
			changeLine(doc, change.getStartRow(), up);
			break;
		case REMOVE_TEXT:
			if (change.getStartRow() != change.getEndRow()) {
				throw new IllegalArgumentException("Multi-line REMOVE_TEXT change? " + change);
			}
			s = doc.getLine(change.getStartRow());
			up = s.substring(0, change.getStartColumn()) + s.substring(change.getEndColumn());
			doc.setLine(change.getStartRow(), up);
			break;
		case INSERT_LINES:
			for (int i = 0; i < change.getNumLines(); i++) {
				doc.insertLine(change.getStartRow() + i, change.getLine(i) + "\n");
			}
			break;
		default:
			throw new IllegalStateException("Not handled yet: " + change.getType());
		}
	}
	
	/**
	 * Change text at given line, inserting multiple lines as necessary
	 * if text has embedded newlines.
	 * 
	 * @param doc   the TextDocument
	 * @param index index of line to change
	 * @param text  text to put at given index
	 */
	private void changeLine(TextDocument doc, int index, String text) {
		int nl = text.indexOf('\n');

		if (nl < 0 || nl == text.length() - 1) {
			// Line either has no newline, or there is only one newline at
			// the end of the line
			doc.setLine(index, text);
			return;
		}
		
		// line contains embedded newlines: need to split
		doc.removeLine(index);
		boolean done = false;
		while (!done) {
			doc.insertLine(index, text.substring(0, nl+1));
			index++;
			text = text.substring(nl + 1);
			nl = text.indexOf('\n');
			done = (nl < 0);
		}
	}
}
