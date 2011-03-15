package edu.ycp.cs.netcoder.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class NetCoder_GWT2 implements EntryPoint {
	private HorizontalPanel topPanel;
	private HorizontalPanel bottomPanel;
	
	private static final boolean USE_ACE = true;
	
	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		if (USE_ACE) {
			startACE();
		} else {
			startCodeMirror();
		}
	}

	private void startACE() {
		/*
		topPanel = new HorizontalPanel();
		topPanel.setWidth("100%");
		
		topPanel.add(new Label("Top stuff!"));
		
		RootPanel.get("topstuff").add(topPanel);
		
		bottomPanel = new HorizontalPanel();
		bottomPanel.setWidth("100%");
		
		bottomPanel.add(new Label("Bottom stuff!"));
		
		RootPanel.get("bottomstuff").add(bottomPanel);
		*/
		
		topPanel = new HorizontalPanel();
		topPanel.add(new Label("Top stuff!"));
		
		HTML aceDiv = new HTML("<div id=\"editor\" style=\"height: 500px; width: 500px\">some text</div>");
		
		bottomPanel = new HorizontalPanel();
		bottomPanel.add(new Label("Bottom stuff"));
		
		/*
		HTML aceInit = new HTML(
			"    <script>\n" +
			//"	window.onload = function() {\n" +
			"	    var editor = ace.edit(\"editor\");\n" +
			"	    editor.setTheme(\"ace/theme/twilight\");\n" +
			"	    var JavaMode = require(\"ace/mode/java\").Mode;\n" +
			"		editor.getSession().setMode(new JavaMode());\n" +
			//"	};\n" +
			"	</script>\n"
		);
		*/
		
		RootPanel rootPanel = RootPanel.get();
		rootPanel.add(topPanel);
		rootPanel.add(aceDiv);
		rootPanel.add(bottomPanel);
		//rootPanel.add(aceInit);
		
		startEditor();
	}
	
	private native void startEditor() /*-{
		var editor = $wnd.ace.edit("editor");
		editor.setTheme("ace/theme/twilight");
		var JavaMode = $wnd.require("ace/mode/java").Mode;
		editor.getSession().setMode(new JavaMode());
	}-*/;

	private void startCodeMirror() {
		// Construct the UI
		VerticalPanel vPanel = new VerticalPanel();
		vPanel.setWidth("100%");
		
		// Top panel
		topPanel = new HorizontalPanel();
		topPanel.setWidth("100%");
		topPanel.add(new Label("Top stuff!"));
		
		// HTML widget which will contain the CodeMirror
		HTML codeMirrorDiv = new HTML(
				"<div>\n" +
				"<textarea id='code' name='code'></textarea>\n" +
				"</div>\n"
		);
		//codeMirrorDiv.setWidth("800px");
		
		// Bottom panel
		bottomPanel = new HorizontalPanel();
		bottomPanel.setWidth("100%");
		bottomPanel.add(new Label("Bottom stuff!"));

		// Add widgets to main layout panel
		vPanel.add(topPanel);
		vPanel.add(codeMirrorDiv);
		vPanel.add(bottomPanel);
		
		// Add main layout panel to RootPanel
		RootPanel.get().add(vPanel);
		
		// HTML widget containing the javascript to create/initialize the CodeMirror
		HTML codeMirrorInit = new HTML(
			   "<script>\n" +
			   "   var editor = CodeMirror.fromTextArea(document.getElementById(\"code\"), {\n" +
			   "     lineNumbers: true,\n" +
			   "     matchBrackets: true,\n" +
			   "     mode: \"text/x-csrc\"\n" +
			   "   });\n" +
			   " </script>\n"
		);
		
		// Add CodeMirror init script to RootPanel
		RootPanel.get().add(codeMirrorInit);
	}
}
