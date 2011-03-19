package edu.ycp.cs.netcoder.client;

import java.util.List;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.ycp.cs.netcoder.client.ace.AceEditor;
import edu.ycp.cs.netcoder.client.ace.AceEditorCallback;
import edu.ycp.cs.netcoder.client.ace.AceEditorMode;
import edu.ycp.cs.netcoder.client.hints.HintsWidget;
import edu.ycp.cs.netcoder.client.logchange.ChangeList;
import edu.ycp.cs.netcoder.client.status.StatusWidget;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class NetCoder_GWT2 implements EntryPoint, AceEditorCallback {
	private ChangeList changeList;

	private HorizontalPanel appPanel;
	private HorizontalPanel editorAndWidgetPanel;
	private AceEditor editor;
	private HintsWidget hintsWidget;
	private VerticalPanel widgetPanel;
	private HorizontalPanel buttonPanel;
	private Label statusLabel;
	private StatusWidget statusWidget;
	private Timer flushPendingChangeEventsTimer;
	
	private LogCodeChangeServiceAsync logCodeChangeService;
	private CompileServiceAsync compileService;
	private SubmitServiceAsync submitService;
	
	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		// Model (data) objects
		changeList = new ChangeList();

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
		hintsWidget = new HintsWidget();
		widgetPanel.add(hintsWidget);
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
		
		Button submitButton=new Button("Submit");
		submitButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event){
                submitCode();
            }
        });
		buttonPanel.add(submitButton);
		
		// Status label - need to think more about what feedback to provide and how
		FlowPanel statusPanel = new FlowPanel();
		statusWidget = new StatusWidget();
		changeList.addObserver(statusWidget);
		statusPanel.add(statusWidget);
		statusLabel = new Label();
		statusPanel.add(statusLabel);
		statusPanel.setWidth("100%");
		
		// Build the UI
		RootPanel rootPanel = RootPanel.get();
		rootPanel.add(appPanel);
		rootPanel.add(editorAndWidgetPanel);
		rootPanel.add(statusPanel);
		rootPanel.add(buttonPanel);

		// fire up the ACE editor
		editor.startEditor();
		editor.setTheme("eclipse");
		editor.setFontSize("14px");
		editor.setMode(AceEditorMode.JAVA);
		editor.addOnChangeHandler(this);
		
		// create timer to flush unsent change events periodically
		flushPendingChangeEventsTimer = new Timer() {
			@Override
			public void run() {
				if (changeList.getState() == ChangeList.State.UNSENT) {
					String changeBatch = changeList.beginTransmit();
					
					AsyncCallback<Boolean> callback = new AsyncCallback<Boolean>() {
						@Override
						public void onFailure(Throwable caught) {
							changeList.endTrasnmit(false);
							GWT.log("Failed to send change batch to server");
						}
						
						@Override
						public void onSuccess(Boolean result) {
							changeList.endTrasnmit(true);
						}
					};
					
					logCodeChangeService.logChange(changeBatch, callback);
				}
			}
		};
		flushPendingChangeEventsTimer.scheduleRepeating(1000);
		
		// Create async service objects for communication with server
		logCodeChangeService = (LogCodeChangeServiceAsync) GWT.create(LogCodeChangeService.class);
		compileService = (CompileServiceAsync) GWT.create(CompileService.class);
		submitService =(SubmitServiceAsync) GWT.create(SubmitService.class);
		
		// Make a javascript map to help compactify ACE onChange event data
		makeOnChangeCompactificationMap();
	}
	
	private native void makeOnChangeCompactificationMap() /*-{
		$wnd.netCoderCompactify = {"insertText" : "IT", "insertLines" : "IL", "removeText" : "RT", "removeLines" : "RL"};
	}-*/;

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
		// Create a compact text representation of the change event object
		var compactChangeString = "";
		var type = obj.type;
		if (type != "change") {
			compactChangeString = "UnknownType" + type;
		} else {
			var action = obj.data.action;
			var compactAction = $wnd.netCoderCompactify[action];
			var compactRange = obj.data.range.start.row + "," + obj.data.range.start.column + "," + obj.data.range.end.row + "," + obj.data.range.end.column;
			
			var textOrLines = "";
			if (compactAction == "IT" || compactAction == "RT") {
				textOrLines = JSON.stringify(obj.data.text);
			} else if (compactAction == "IL" || compactAction == "RL") {
				textOrLines = JSON.stringify(obj.data.lines);
			}
			
			compactChangeString = compactAction + compactRange + "," + new Date().getTime() + ";" + textOrLines;
		}
		
		this.@edu.ycp.cs.netcoder.client.NetCoder_GWT2::sendStringifiedChangeToServer(Ljava/lang/String;)(compactChangeString);
	}-*/;
	
	/**
	 * Send a code change to the server.
	 * 
	 * @param changeEvent a JSON-stringified ACE onChange event object
	 */
	private void sendStringifiedChangeToServer(String changeEvent) {
		changeList.addChange(changeEvent); // will get sent eventually based on timer events
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
	
	protected void submitCode() {
	    AsyncCallback<String> callback = new AsyncCallback<String>() {
            @Override
            public void onFailure(Throwable caught) {
                statusLabel.setText("Error sending submission to server for compilation");
                GWT.log("compile failed", caught);
            }

            @Override
            public void onSuccess(String result) {
                statusLabel.setText(result);
            }
        };
        String problemId = com.google.gwt.user.client.Window.Location.getParameter("problemId");
        // XXX Probably needs only the problem's unique ID
        submitService.submit(problemId, editor.getText(), callback);
	}
}
