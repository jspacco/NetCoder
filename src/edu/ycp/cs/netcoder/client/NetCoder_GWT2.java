package edu.ycp.cs.netcoder.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextArea;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class NetCoder_GWT2 implements EntryPoint {
	private HorizontalPanel topPanel;
	private HorizontalPanel bottomPanel;
	private AceEditor editor;
	
	private TextArea textArea;
	
	private LogCodeChangeServiceAsync logCodeChangeService;
	
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
		editor.setTheme("cobalt");
		editor.setFontSize("14px");
		editor.setMode(AceEditorMode.JAVA);
		editor.onChange(new AceEditorCallback() {
			@Override
			public void invoke(JavaScriptObject obj) {
				sendChangeToServer(obj);
			}
		});
		
		logCodeChangeService = (LogCodeChangeServiceAsync) GWT.create(LogCodeChangeService.class);
	}
	
	private native void sendChangeToServer(JavaScriptObject obj) /*-{
		var jsonText = JSON.stringify(obj);
		this.@edu.ycp.cs.netcoder.client.NetCoder_GWT2::sendStringifiedChangeToServer(Ljava/lang/String;)(jsonText);
	}-*/;
	
	private void sendStringifiedChangeToServer(String s) {
		AsyncCallback<Boolean> callback = new AsyncCallback<Boolean>() {
			@Override
			public void onFailure(Throwable caught) {
				textArea.setText(textArea.getText() + "could not log code change: " + caught.getMessage() + "\n");
			}
			
			@Override
			public void onSuccess(Boolean result) {
				// TODO: clear queue of change events
			}
		};
		
		logCodeChangeService.logChange(s, callback);
	}
}
