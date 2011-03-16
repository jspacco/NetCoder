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
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class NetCoder_GWT2 implements EntryPoint, AceEditorCallback {
	private HorizontalPanel appPanel;
	private HorizontalPanel editorAndWidgetPanel;
	private AceEditor editor;
	private VerticalPanel widgetPanel;
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
		
		// The editor (left) and widget panel (right) occupy most of the vertical space
		// TODO: make it expand vertically when window resizes
		
		editorAndWidgetPanel = new HorizontalPanel();
		editorAndWidgetPanel.setWidth("100%");
		
		// Code editor
		editor = new AceEditor();
		editor.setStylePrimaryName("NetCoderEditor");
		
		// Widget panel: for things like hints, affect data collection, etc.
		widgetPanel = new VerticalPanel();
		widgetPanel.add(new Label("Hints should go here!"));   // TODO
		widgetPanel.add(new Label("Affect data collection!")); // TODO

		// Add the editor and widget panel so that it is a 70/30 split
		editorAndWidgetPanel.add(editor);
		editorAndWidgetPanel.setCellWidth(editor, "70%");
		editorAndWidgetPanel.add(widgetPanel);
		editorAndWidgetPanel.setCellWidth(widgetPanel, "30%");
		
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
		
		// Build the UI
		RootPanel rootPanel = RootPanel.get();
		rootPanel.add(appPanel);
		rootPanel.add(editorAndWidgetPanel);
		rootPanel.add(buttonPanel);
		rootPanel.add(statusPanel);

		// fire up the ACE editor
		editor.startEditor();
		editor.setTheme("eclipse");
		editor.setFontSize("14px");
		editor.setMode(AceEditorMode.JAVA);
		editor.addOnChangeHandler(this);
		
		// Create async service objects for communication with server
		logCodeChangeService = (LogCodeChangeServiceAsync) GWT.create(LogCodeChangeService.class);
		compileService = (CompileServiceAsync) GWT.create(CompileService.class);
	}

	/**
	 * Handles onChange events from the editor.
	 */
	@Override
	public void invokeAceCallback(JavaScriptObject obj) {
		sendChangeToServer(obj);
	}

	/**
	 * Send an ACE onChange event to the server.
	 * 
	 * @param obj an ACE onChange event object
	 */
	private native void sendChangeToServer(JavaScriptObject obj) /*-{
		var jsonText = $wnd.JSON.stringify(obj);
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
				statusLabel.setText("Error sending submission to server for compilation");
				GWT.log("compile failed", caught);
			}

			@Override
			public void onSuccess(Boolean result) {
				statusLabel.setText(result ? "Compile succeeded" : "Compile failed");
			}
		};
		
		compileService.compile(editor.getText(), callback);
	}
}
