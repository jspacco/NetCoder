package edu.ycp.cs.netcoder.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.ycp.cs.netcoder.client.ace.AceEditor;
import edu.ycp.cs.netcoder.client.ace.AceEditorCallback;
import edu.ycp.cs.netcoder.client.ace.AceEditorMode;
import edu.ycp.cs.netcoder.client.hints.HintsWidget;
import edu.ycp.cs.netcoder.client.logchange.ChangeFromAceOnChangeEvent;
import edu.ycp.cs.netcoder.client.logchange.ChangeList;
import edu.ycp.cs.netcoder.client.status.EditorStatusWidget;
import edu.ycp.cs.netcoder.shared.logchange.Change;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class NetCoder_GWT2 implements EntryPoint, AceEditorCallback, ResizeHandler {
    private static final int APP_PANEL_HEIGHT_PX = 30;
    private static final int DESC_PANEL_HEIGHT_PX = 70;
	private static final int STATUS_PANEL_HEIGHT_PX = 30;
	private static final int BUTTON_PANEL_HEIGHT_PX = 40;
	
	private static final String PROBLEM_ID="problemId";
	
	private static final int NORTH_SOUTH_PANELS_HEIGHT_PX =
		APP_PANEL_HEIGHT_PX + DESC_PANEL_HEIGHT_PX + STATUS_PANEL_HEIGHT_PX + BUTTON_PANEL_HEIGHT_PX;
	
	private ChangeList changeList;

	private HorizontalPanel appPanel;
	private HorizontalPanel descPanel;
	private HorizontalPanel editorAndWidgetPanel;
	private HintsWidget hintsWidget;
	private VerticalPanel widgetPanel;
	private HorizontalPanel buttonPanel;
	private EditorStatusWidget statusWidget;
	private InlineLabel statusLabel;
	private InlineLabel descLabel;
	private AceEditor editor;
	private Timer flushPendingChangeEventsTimer;
	
	private LogCodeChangeServiceAsync logCodeChangeService;
	//private CompileServiceAsync compileService;
	private SubmitServiceAsync submitService;
	private LoadExerciseServiceAsync loadService;
	
	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		// Model (data) objects
		changeList = new ChangeList();
		
		// Id of the problem we're solving
		// currently this is a request parameter
		Integer problemId = getProblemId();
		    
		createServices();
		
		DockLayoutPanel mainPanel = new DockLayoutPanel(Unit.PX);
		
		// The app panel can be for logout button, menus, etc.
		appPanel = new HorizontalPanel();
		appPanel.add(new Label("Menus and logout button should go here"));
		mainPanel.addNorth(appPanel, APP_PANEL_HEIGHT_PX);
		
		// The description panel is for the problem description
		descPanel=new HorizontalPanel();
		descLabel = new InlineLabel();
		descPanel.add(descLabel);
		mainPanel.addNorth(descPanel, DESC_PANEL_HEIGHT_PX);
		// Load the problem (will update the description panel created above)
		loadExerciseDescription(problemId);
		
		// The editor (left) and widget panel (right) occupy the center location
		// in the DockLayoutPanel, and expand to fill space not occupied by
		// docked panels.
		editorAndWidgetPanel = new HorizontalPanel();
		editorAndWidgetPanel.setWidth("100%");
		
		// Button panel is for buttons
		buttonPanel = new HorizontalPanel();
//		Button compileButton = new Button("Compile");
//		compileButton.addClickHandler(new ClickHandler() {
//			@Override
//			public void onClick(ClickEvent event) {
//				compileCode();
//			}
//		});
//		buttonPanel.add(compileButton);
		Button submitButton=new Button("Submit");
		submitButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event){
                submitCode();
            }
        });
		buttonPanel.add(submitButton);
		mainPanel.addSouth(buttonPanel, BUTTON_PANEL_HEIGHT_PX);
		
		// Status panel - need to think more about what feedback to provide and how
		FlowPanel statusPanel = new FlowPanel();
		statusWidget = new EditorStatusWidget();
		changeList.addObserver(statusWidget);
		statusPanel.add(statusWidget);
		statusLabel = new InlineLabel();
		statusPanel.add(statusLabel);
		statusPanel.setWidth("100%");
		mainPanel.addSouth(statusPanel, STATUS_PANEL_HEIGHT_PX);
		
		// Code editor
		editor = new AceEditor();
		editor.setStylePrimaryName("NetCoderEditor");
		editor.setHeight("500px");
		
		// Widget panel: for things like hints, affect data collection, etc.
		widgetPanel = new VerticalPanel();
		hintsWidget = new HintsWidget();
		widgetPanel.add(hintsWidget);
		widgetPanel.add(new Label("Affect data collection!")); // TODO

		// Add the editor and widget panel so that it is a 80/20 split
		editorAndWidgetPanel.add(editor);
		editorAndWidgetPanel.setCellWidth(editor, "80%");
		editorAndWidgetPanel.add(widgetPanel);
		editorAndWidgetPanel.setCellWidth(widgetPanel, "20%");
		mainPanel.add(editorAndWidgetPanel);
		
		// Add the main panel to the window
		RootLayoutPanel.get().add(mainPanel);
		
		// Size the editor and widget panel to fill available space
		resize(Window.getClientWidth(), Window.getClientHeight());
		
		// Add window resize handler so that we can make editor and widget
		// panel expand vertically as necessary
		Window.addResizeHandler(this);

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
					Change[] changeBatch = changeList.beginTransmit();
					
					AsyncCallback<Boolean> callback = new AsyncCallback<Boolean>() {
						@Override
						public void onFailure(Throwable caught) {
							changeList.endTransmit(false);
							GWT.log("Failed to send change batch to server");
						}
						
						@Override
						public void onSuccess(Boolean result) {
							changeList.endTransmit(true);
						}
					};
					
					logCodeChangeService.logChange(changeBatch, callback);
				}
			}
		};
		flushPendingChangeEventsTimer.scheduleRepeating(1000);
	}
	
	private void createServices() {
	    // Create async service objects for communication with server
        logCodeChangeService = (LogCodeChangeServiceAsync) GWT.create(LogCodeChangeService.class);
        //compileService = (CompileServiceAsync) GWT.create(CompileService.class);
        submitService =(SubmitServiceAsync) GWT.create(SubmitService.class);
        loadService =(LoadExerciseServiceAsync) GWT.create(LoadExerciseService.class);
	}

	/**
	 * Handles onChange events from the editor.
	 */
	@Override
	public void invokeAceCallback(JavaScriptObject obj) {
		changeList.addChange(ChangeFromAceOnChangeEvent.convert(obj));
	}
	
	/**
	 * Send the current text in the editor to the server to be compiled.
	 */
//	protected void compileCode() {
//		AsyncCallback<Boolean> callback = new AsyncCallback<Boolean>() {
//			@Override
//			public void onFailure(Throwable caught) {
//				statusLabel.setText("Error sending submission to server for compilation");
//				GWT.log("compile failed", caught);
//			}
//
//			@Override
//			public void onSuccess(Boolean result) {
//				statusLabel.setText(result ? "Compile succeeded" : "Compile failed");
//			}
//		};
//		
//		compileService.compile(editor.getText(), callback);
//	}
	
	protected void loadExerciseDescription(int problemId) {
	    AsyncCallback<String> callback = new AsyncCallback<String>() {
            @Override
            public void onFailure(Throwable caught) {
                descLabel.setText("Error loading exercise");
                GWT.log("loading exercise description failed", caught);
            }

            @Override
            public void onSuccess(String result) {
                descLabel.setText(result);
            }
        };
        loadService.load(problemId, callback);
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
        int problemId=getProblemId();
        submitService.submit(problemId, editor.getText(), callback);
	}
	
	@Override
	public void onResize(ResizeEvent event) {
		resize(Window.getClientWidth(), Window.getClientHeight());
	}

	private int getProblemId() {
	    String problemIdStr=Window.Location.getParameter(PROBLEM_ID);
	    if (problemIdStr==null) {
	        return 0;
	    }
	    return Integer.parseInt(problemIdStr);
	}
	
	private void resize(int width, int height) {
		// Let the editor and widget panel take up all of the vertical
		// height not consumed by the north/south panels.
		int availHeight = (height - NORTH_SOUTH_PANELS_HEIGHT_PX) - 10;
		if (availHeight < 0) {
			availHeight = 0;
		}
		editor.setHeight(availHeight + "px");
	}
}
