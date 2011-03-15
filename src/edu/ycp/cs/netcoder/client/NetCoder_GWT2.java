package edu.ycp.cs.netcoder.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.HasHorizontalAlignment.HorizontalAlignmentConstant;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class NetCoder_GWT2 implements EntryPoint {
	private HorizontalPanel topPanel;
	private HorizontalPanel bottomPanel;
	private AceEditor editor;
	
	private TextArea textArea;
	
	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		topPanel = new HorizontalPanel();
		topPanel.add(new Label("Top stuff!"));
		
		editor = new AceEditor();
		editor.setStylePrimaryName("NetCoderEditor");
		
		bottomPanel = new HorizontalPanel();
		bottomPanel.add(new Label("Bottom stuff"));
		
		textArea = new TextArea();
		textArea.setWidth("100%");
		textArea.setHeight("200px");
		
		RootPanel rootPanel = RootPanel.get();
		rootPanel.add(topPanel);
		rootPanel.add(editor);
		rootPanel.add(bottomPanel);
		rootPanel.add(textArea);
		
		editor.startEditor();
		editor.setTheme("twilight");
		editor.setMode(AceEditorMode.JAVA);
		editor.onChange(new AceEditorCallback() {
			@Override
			public void invoke(JavaScriptObject obj) {
				textArea.setText(textArea.getText() + "change!\n");
			}
		});
	}
}
