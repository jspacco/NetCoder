package edu.ycp.cs.netcoder.client;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.ui.HTML;

public class AceEditor extends HTML {
	// Used to generate unique element ids for Ace widgets.
	private static int nextId = 0;
	
	private String elementId;
	private JavaScriptObject editor;
	
	/**
	 * Constructor.
	 */
	public AceEditor() {
		elementId = "_aceGWT" + nextId;
		nextId++;
		setHTML("<div style=\"width: 100%; height: 100%;\" id=\"" + elementId + "\"></div>");
	}
	
	/**
	 * Call this method to start the editor.
	 * Make sure that the widget has been attached to the page
	 * before calling this method.
	 */
	public void startEditor() {
		editor = startEditor(elementId);
	}

	private native JavaScriptObject startEditor(String elementId) /*-{
		var editor = $wnd.ace.edit(elementId);
		editor.setTheme("ace/theme/twilight");
		var JavaMode = $wnd.require("ace/mode/java").Mode;
		editor.getSession().setMode(new JavaMode());
		return editor;
	}-*/;
}
