package edu.ycp.cs.netcoder.client;

import com.google.gwt.core.client.EntryPoint;
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
	private AceEditor editor;
	
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
		topPanel = new HorizontalPanel();
		topPanel.add(new Label("Top stuff!"));
		
		editor = new AceEditor();
		editor.setWidth("500px");
		editor.setHeight("500px");
		
		bottomPanel = new HorizontalPanel();
		bottomPanel.add(new Label("Bottom stuff"));
		
		RootPanel rootPanel = RootPanel.get();
		rootPanel.add(topPanel);
		rootPanel.add(editor);
		rootPanel.add(bottomPanel);
		
		editor.startEditor();
	}

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
