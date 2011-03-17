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
			doc.setLine(change.getStartRow(), up);
			break;
		case REMOVE_TEXT:
			if (change.getStartRow() != change.getEndRow()) {
				throw new IllegalArgumentException("Multi-line REMOVE_TEXT change? " + change);
			}
			s = doc.getLine(change.getStartRow());
			up = s.substring(0, change.getStartColumn()) + s.substring(change.getEndColumn());
			doc.setLine(change.getStartRow(), up);
			break;
		default:
			throw new IllegalStateException("Not handled yet: " + change.getType());
		}
	}
}
