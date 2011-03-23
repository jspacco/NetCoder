package edu.ycp.cs.netcoder.client.logchange;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayString;

import edu.ycp.cs.netcoder.shared.logchange.Change;
import edu.ycp.cs.netcoder.shared.logchange.ChangeType;

/**
 * Convert ACE onChange events to {@link Change} objects.
 */
public class ChangeFromAceOnChangeEvent {
	/**
	 * Convert an ACE editor onChange event object into a {@link Change}
	 * object (which can be sent to the server in serialized form.)
	 * 
	 * @param obj an ACE editor onChange object
	 * @return Change object
	 */
	public native static Change convert(JavaScriptObject obj) /*-{
		var action = obj.data.action;
		if (action == "insertText" || action == "removeText") {
			return @edu.ycp.cs.netcoder.client.logchange.ChangeFromAceOnChangeEvent::convertFromString(Ljava/lang/String;IIIILjava/lang/String;)(
				action,
				obj.data.range.start.row,
				obj.data.range.start.column,
				obj.data.range.end.row,
				obj.data.range.end.column,
				obj.data.text
			);
		} else {
			return @edu.ycp.cs.netcoder.client.logchange.ChangeFromAceOnChangeEvent::convertFromLines(Ljava/lang/String;IIIILcom/google/gwt/core/client/JsArrayString;)(
				action,
				obj.data.range.start.row,
				obj.data.range.start.column,
				obj.data.range.end.row,
				obj.data.range.end.column,
				obj.data.lines
			);
		}
	}-*/;
	
	protected static Change convertFromString(String aceChangeType, int sr, int sc, int er, int ec, String text) {
		ChangeType type = ChangeType.fromAceChangeType(aceChangeType);
		return new Change(type, sr, sc, er, ec, System.currentTimeMillis(), text);
	}

	protected static Change convertFromLines(String aceChangeType, int sr, int sc, int er, int ec, JsArrayString lines) {
		ChangeType type = ChangeType.fromAceChangeType(aceChangeType);
		String[] lineArr = new String[lines.length()];
		for (int i = 0; i < lineArr.length; i++) {
			lineArr[i] = lines.get(i);
		}
		return new Change(type, sr, sc, er, ec, System.currentTimeMillis(), lineArr);
	}
}
