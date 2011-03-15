package edu.ycp.cs.netcoder.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class NetCoder_GWT2 implements EntryPoint {
	private HorizontalPanel topPanel;
	private HorizontalPanel bottomPanel;
	private AceEditor editor;
	
	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
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
		editor.setTheme("twilight");
		editor.setMode(AceEditorMode.JAVA);
	}
}
