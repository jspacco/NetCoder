package edu.ycp.cs.netcoder.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
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
	
	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		// Construct the UI
		VerticalPanel vPanel = new VerticalPanel();
		
		// Top panel
		topPanel = new HorizontalPanel();
		topPanel.add(new Label("Top stuff!"));
		
		// HTML widget which will contain the CodeMirror
		HTML codeMirrorDiv = new HTML(
				"<div style=\"border-top: 1px solid black; border-bottom: 1px solid black;\">\n" +
				"<textarea id='code' name='code'></textarea>\n" +
				"</div>\n"
		);
		
		// Bottom panel
		bottomPanel = new HorizontalPanel();
		bottomPanel.add(new Label("Bottom stuff!"));
		
		// HTML widget containing the javascript to create/initialize the CodeMirror
		HTML codeMirrorInit = new HTML(
				"<script type=\"text/javascript\">\n" +
				"var cmElt = document.getElementById('code');\n" +
				"var myCodeMirror = CodeMirror(function(elt) {\n" + 
				"	  cmElt.parentNode.replaceChild(cmElt, elt);\n" +
				"}, {mode: \"x-csrc\"});\n" +
				"</script>\n"
		);
		
		vPanel.add(topPanel);
		vPanel.add(codeMirrorDiv);
		vPanel.add(bottomPanel);
		
		// Add to RootPanel
		RootPanel.get().add(vPanel);
		RootPanel.get().add(codeMirrorInit);
	}
}
