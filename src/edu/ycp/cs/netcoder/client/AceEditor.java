package edu.ycp.cs.netcoder.client;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.ui.HTML;

/**
 * A GWT widget for the Ajax.org Code Editor (ACE).
 * 
 * @see <a href="http://ace.ajax.org/">Ajax.org Code Editor</a>
 */
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
		editor = startEditorImpl(elementId);
	}

	private native JavaScriptObject startEditorImpl(String elementId) /*-{
		var editor = $wnd.ace.edit(elementId);
		return editor;
	}-*/;
//	editor.setTheme("ace/theme/twilight");
//	var JavaMode = $wnd.require("ace/mode/java").Mode;
//	editor.getSession().setMode(new JavaMode());
	
	/**
	 * Set the theme.
	 * 
	 * @param themeName the theme name (e.g., "twilight")
	 */
	public void setTheme(String themeName) {
		setThemeImpl(editor, "ace/theme/" + themeName);
	}
	
	private native void setThemeImpl(JavaScriptObject editor, String themeName) /*-{
		editor.setTheme(themeName);
	}-*/;

	/**
	 * Set the mode.
	 * 
	 * @param mode the mode (one of the values in the
	 *             {@link AceEditorMode} enumeration)
	 */
	public void setMode(AceEditorMode mode) {
		setModeImpl(editor, "ace/mode/" + mode.getName());
	}
	
	public native void setModeImpl(JavaScriptObject editor, String modeName) /*-{
		var TheMode = $wnd.require(modeName).Mode;
		editor.getSession().setMode(new TheMode());
	}-*/;
}
