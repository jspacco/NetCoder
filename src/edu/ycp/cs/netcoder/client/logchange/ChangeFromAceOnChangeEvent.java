// NetCoder - a web-based pedagogical programming environment
// Copyright (C) 2011, Jaime Spacco <jspacco@knox.edu>
// Copyright (C) 2011, David H. Hovemeyer <dhovemey@ycp.edu>
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU Affero General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU Affero General Public License for more details.
//
// You should have received a copy of the GNU Affero General Public License
// along with this program.  If not, see <http://www.gnu.org/licenses/>.

package edu.ycp.cs.netcoder.client.logchange;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayString;

import edu.ycp.cs.netcoder.shared.logchange.Change;
import edu.ycp.cs.netcoder.shared.logchange.ChangeType;

/**
 * Convert ACE onChange events to {@link Change} objects.
 */
public class ChangeFromAceOnChangeEvent {
	
	private static final Map<String, ChangeType> aceChangeTypeToChangeTypeMap =
		new HashMap<String, ChangeType>();
	static {
		aceChangeTypeToChangeTypeMap.put("insertText", ChangeType.INSERT_TEXT);
		aceChangeTypeToChangeTypeMap.put("removeText", ChangeType.REMOVE_TEXT);
		aceChangeTypeToChangeTypeMap.put("insertLines", ChangeType.INSERT_LINES);
		aceChangeTypeToChangeTypeMap.put("removeLines", ChangeType.REMOVE_LINES);
	}
	
	public static ChangeType fromAceChangeType(String aceChangeType) {
		return aceChangeTypeToChangeTypeMap.get(aceChangeType);
	}

	/**
	 * Convert an ACE editor onChange event object into a {@link Change}
	 * object (which can be sent to the server in serialized form.)
	 * 
	 * @param obj an ACE editor onChange object
	 * @return Change object
	 */
	public native static Change convert(JavaScriptObject obj, int userId, int problemId) /*-{
		var action = obj.data.action;
		if (action == "insertText" || action == "removeText") {
			return @edu.ycp.cs.netcoder.client.logchange.ChangeFromAceOnChangeEvent::convertFromString(Ljava/lang/String;IIIILjava/lang/String;II)(
				action,
				obj.data.range.start.row,
				obj.data.range.start.column,
				obj.data.range.end.row,
				obj.data.range.end.column,
				obj.data.text,
				userId,
				problemId
			);
		} else {
			return @edu.ycp.cs.netcoder.client.logchange.ChangeFromAceOnChangeEvent::convertFromLines(Ljava/lang/String;IIIILcom/google/gwt/core/client/JsArrayString;II)(
				action,
				obj.data.range.start.row,
				obj.data.range.start.column,
				obj.data.range.end.row,
				obj.data.range.end.column,
				obj.data.lines,
				userId,
				problemId
			);
		}
	}-*/;
	
	protected static Change convertFromString(String aceChangeType, int sr, int sc, int er, int ec, String text, int userId, int problemId) {
		ChangeType type = fromAceChangeType(aceChangeType);
		return new Change(type, sr, sc, er, ec, System.currentTimeMillis(), userId, problemId, text);
	}

	protected static Change convertFromLines(String aceChangeType, int sr, int sc, int er, int ec, JsArrayString lines, int userId, int problemId) {
		ChangeType type = fromAceChangeType(aceChangeType);
		String[] lineArr = new String[lines.length()];
		for (int i = 0; i < lineArr.length; i++) {
			lineArr[i] = lines.get(i);
		}
		return new Change(type, sr, sc, er, ec, System.currentTimeMillis(), userId, problemId, lineArr);
	}
}
