package edu.ycp.cs.netcoder.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class NetCoder_GWT2 implements EntryPoint {
	private HorizontalPanel appPanel;
	private AceEditor editor;
	private HorizontalPanel buttonPanel;
	private Label statusLabel;
	
	private LogCodeChangeServiceAsync logCodeChangeService;
	private CompileServiceAsync compileService;
	
	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		// The app panel can be for logout button, menus, etc.
		appPanel = new HorizontalPanel();
		appPanel.add(new Label("Menus and logout button should go here"));
		
		// The editor occupies most of the vertical space
		// TODO: make it expand vertically when window resizes
		editor = new AceEditor();
		editor.setStylePrimaryName("NetCoderEditor");
		
		// Button panel is for buttons
		buttonPanel = new HorizontalPanel();
		Button compileButton = new Button("Compile");
		compileButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				compileCode();
			}
		});
		buttonPanel.add(compileButton);
		
		// Status label - need to think more about what feedback to provide and how
		FlowPanel statusPanel = new FlowPanel();
		statusLabel = new Label();
		statusPanel.add(statusLabel);
		statusPanel.setWidth("100%");
		
		RootPanel rootPanel = RootPanel.get();
		rootPanel.add(appPanel);
		rootPanel.add(editor);
		rootPanel.add(buttonPanel);
		rootPanel.add(statusPanel);
		
		editor.startEditor();
		editor.setTheme("eclipse");
		editor.setFontSize("14px");
		editor.setMode(AceEditorMode.JAVA);
		editor.onChange(new AceEditorCallback() {
			@Override
			public void invoke(JavaScriptObject obj) {
				// TODO: queue the change events so that they can be sent in batches
				sendChangeToServer(obj);
			}
		});
		
		logCodeChangeService = (LogCodeChangeServiceAsync) GWT.create(LogCodeChangeService.class);
		compileService = (CompileServiceAsync) GWT.create(CompileService.class);
	}

	/**
	 * Send an ACE onChange event to the server.
	 * 
	 * @param obj an ACE onChange event object
	 */
	protected native void sendChangeToServer(JavaScriptObject obj) /*-{
		var jsonText = JSON.stringify(obj);
		this.@edu.ycp.cs.netcoder.client.NetCoder_GWT2::sendStringifiedChangeToServer(Ljava/lang/String;)(jsonText);
	}-*/;
	
	/**
	 * Send a code change to the server.
	 * 
	 * @param changeEvent a JSON-stringified ACE onChange event object
	 */
	private void sendStringifiedChangeToServer(String changeEvent) {
		AsyncCallback<Boolean> callback = new AsyncCallback<Boolean>() {
			@Override
			public void onFailure(Throwable caught) {
				GWT.log("send code change failed", caught);
			}
			
			@Override
			public void onSuccess(Boolean result) {
				// TODO: clear queue of change events
			}
		};
		
		logCodeChangeService.logChange(changeEvent, callback);
	}
	
	/**
	 * Send the current text in the editor to the server to be compiled.
	 */
	protected void compileCode() {
		AsyncCallback<Boolean> callback = new AsyncCallback<Boolean>() {
			@Override
			public void onFailure(Throwable caught) {
				statusLabel.setText("Compile failed");
				GWT.log("compile failed", caught);
			}

			@Override
			public void onSuccess(Boolean result) {
				statusLabel.setText("Compile succeeded");
			}
		};
		
		compileService.compile(editor.getText(), callback);
	}
}
