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
		
		/*
		// HTML widget which will contain the CodeMirror
		HTML codeMirrorDiv = new HTML("<div id='code'/>");
		*/
		
		// Bottom panel
		bottomPanel = new HorizontalPanel();
		bottomPanel.add(new Label("Bottom stuff!"));
		
		/*
		// HTML widget containg the CodeMirror script includes
		String baseURL = GWT.getModuleBaseURL();
		HTML codeMirrorInclude = new HTML(
				"<script src=\"" + baseURL + "/lib/codemirror.js\"></script>\n" +
				"<script src=\"" + baseURL + "/mode/clike/clike.js\"></script>\n"
		);
		*/
		
		/*
		// HTML widget containing the javascript to create/initialize the CodeMirror
		HTML codeMirrorInit = new HTML(
				"<script>" +
				"var cmElt = document.getElementById('code');\n" +
				"var myCodeMirror = CodeMirror(function(elt) {\n" + 
				"	  cmElt.parentNode.replaceChild(cmElt, elt);\n" +
				"}, {mode: \"clike\"});\n" +
				"</script>"
		);
		*/
		
		vPanel.add(topPanel);
		//vPanel.add(codeMirrorDiv);
		vPanel.add(bottomPanel);
		
		// Add to RootPanel
		//RootPanel.get().add(codeMirrorInclude);
		RootPanel.get().add(vPanel);
		//RootPanel.get().add(codeMirrorInit);
	}
}
