// NetCoder - a web-based pedagogical programming environment
// Copyright (C) 2011, Jaime Spacco <jspacco@knox.edu>
// Copyright (C) 2011, David H. Hovemeyer <dhovemey@ycp.edu>
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU Affero General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU Affero General Public License for more details.
//
// You should have received a copy of the GNU Affero General Public License
// along with this program.  If not, see <http://www.gnu.org/licenses/>.

package edu.ycp.cs.netcoder.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.LayoutPanel;

import edu.ycp.cs.dh.acegwt.client.ace.AceEditor;
import edu.ycp.cs.dh.acegwt.client.ace.AceEditorCallback;
import edu.ycp.cs.dh.acegwt.client.ace.AceEditorMode;
import edu.ycp.cs.dh.acegwt.client.ace.AceEditorTheme;
import edu.ycp.cs.netcoder.client.logchange.ChangeFromAceOnChangeEvent;
import edu.ycp.cs.netcoder.client.logchange.ChangeList;
import edu.ycp.cs.netcoder.client.status.ProblemDescriptionWidget;
import edu.ycp.cs.netcoder.client.status.ResultWidget;
import edu.ycp.cs.netcoder.client.status.StatusAndButtonBarWidget;
import edu.ycp.cs.netcoder.shared.affect.AffectEvent;
import edu.ycp.cs.netcoder.shared.logchange.Change;
import edu.ycp.cs.netcoder.shared.logchange.ChangeType;
import edu.ycp.cs.netcoder.shared.problems.Problem;
import edu.ycp.cs.netcoder.shared.problems.User;
import edu.ycp.cs.netcoder.shared.testing.TestResult;
import edu.ycp.cs.netcoder.shared.util.Observable;
import edu.ycp.cs.netcoder.shared.util.Observer;
import edu.ycp.cs.netcoder.shared.util.Publisher;
import edu.ycp.cs.netcoder.shared.util.Subscriber;

/**
 * View for working on a problem: code editor, submit button, feedback, etc.
 */
public class DevelopmentView extends NetCoderView implements Subscriber, ResizeHandler {
	private static final int PROBLEM_ID = 0; // FIXME
	
	private enum Mode {
		/** Loading problem and current text - editing not allowed. */
		LOADING,
		
		/** Normal state - user is allowed to edit the program text. */
		EDITING,
		
		/**
		 * Submit in progress.
		 * Editing disallowed until server response is received.
		 */
		SUBMIT_IN_PROGRESS,
		
		/**
		 * Logging out.
		 */
		LOGOUT,
	}
	
	// UI mode
	private Mode mode;
	private boolean textLoaded;
	
	/*
	// Model objects added to the session.
	private Object[] sessionObjects;
	*/
	
	// Widgets
	private ProblemDescriptionWidget problemDescription;
	private AceEditor editor;
	private ResultWidget resultWidget;
	private Timer flushPendingChangeEventsTimer;
	
	// RPC services.
	private LoginServiceAsync loginService = GWT.create(LoginService.class);
	private LogCodeChangeServiceAsync logCodeChangeService = GWT.create(LogCodeChangeService.class);
	private SubmitServiceAsync submitService = GWT.create(SubmitService.class);
	private LoadExerciseServiceAsync loadService = GWT.create(LoadExerciseService.class);
	private AffectEventServiceAsync affectEventService = GWT.create(AffectEventService.class);
	
	public DevelopmentView(Session session) {
		super(session);
		
		/*
		// Add ChangeList and AffectEvent to session
		sessionObjects = new Object[]{ new ChangeList(), new AffectEvent() };
		for (Object obj : sessionObjects) {
			getSession().add(obj);
		}
		*/
		addSessionObject(new ChangeList());
		addSessionObject(new AffectEvent());
		
		// Add logout handler.
		// The goal is to completely purge session data on both server
		// and client when the user logs out.
		getTopBar().setLogoutHandler(new Runnable() {
			@Override
			public void run() {
				AsyncCallback<Void> callback = new AsyncCallback<Void>() {
					@Override
					public void onFailure(Throwable caught) {
						GWT.log("Could not log out?", caught);
						
						// well, at least we tried
						clearSessionData();
					}
					
					@Override
					public void onSuccess(Void result) {
						// server has purged the session
						clearSessionData();
					}

					protected void clearSessionData() {
						// Clear the User object from the session.
						getSession().remove(User.class);
						
						// Publish the LOGOUT event.
						getSession().notifySubscribers(Session.Event.LOGOUT, null);
					}
				};
				
				loginService.logout(callback);
			}
		});

		// Observe ChangeList state.
		// We do this so that we know when the local editor contents are
		// up to date with the text on the server.
		session.get(ChangeList.class).subscribe(ChangeList.State.CLEAN, this, getSubscriptionRegistrar());
		
		// User won't be allowed to edit until the problem (and previous editor contents, if any)
		// are loaded.
		mode = Mode.LOADING;
		textLoaded = false;
		
		// The overall UI is build in a LayoutPanel (which the parent class creates)
		LayoutPanel layoutPanel = getLayoutPanel();
		
		// Add problem description widget
		problemDescription = new ProblemDescriptionWidget(session, getSubscriptionRegistrar());
		layoutPanel.add(problemDescription);
		layoutPanel.setWidgetTopHeight(
				problemDescription,
				LayoutConstants.TOP_BAR_HEIGHT_PX, Unit.PX,
				LayoutConstants.PROBLEM_DESC_HEIGHT_PX, Unit.PX);
		
		// Add AceEditor widget
		editor = new AceEditor();
		editor.setStyleName("NetCoderEditor");
		layoutPanel.add(editor);
		layoutPanel.setWidgetTopHeight(editor,
				LayoutConstants.TOP_BAR_HEIGHT_PX + LayoutConstants.PROBLEM_DESC_HEIGHT_PX, Unit.PX,
				200, Unit.PX);

		// Add the status and button bar widget
		StatusAndButtonBarWidget statusAndButtonBarWidget = new StatusAndButtonBarWidget(session);
		layoutPanel.add(statusAndButtonBarWidget);
		layoutPanel.setWidgetBottomHeight(
				statusAndButtonBarWidget,
				LayoutConstants.RESULTS_PANEL_HEIGHT_PX, Unit.PX,
				LayoutConstants.STATUS_AND_BUTTON_BAR_HEIGHT_PX, Unit.PX);
		statusAndButtonBarWidget.setOnSubmit(new Runnable() {
			@Override
			public void run() {
				submitCode();
			}
		});
		
		// Add the ResultWidget
		resultWidget = new ResultWidget();
		layoutPanel.add(resultWidget);
		layoutPanel.setWidgetBottomHeight(
				resultWidget,
				0, Unit.PX,
				LayoutConstants.RESULTS_PANEL_HEIGHT_PX, Unit.PX);
		
		// UI is now complete
		initWidget(layoutPanel);
		
		// Initiate loading of the problem and current editor text.
		loadProblemAndCurrentText();
		
		// Create timer to flush unsent change events periodically.
		this.flushPendingChangeEventsTimer = new Timer() {
			@Override
			public void run() {
				final ChangeList changeList = getSession().get(ChangeList.class);
				
				if (changeList == null) {
					// paranoia
					return;
				}
				
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

	/**
	 * Load the problem and current editor text.
	 * The current editor text is (hopefully) whatever the user
	 * had in his/her editor the last time they were logged in.
	 */
	protected void loadProblemAndCurrentText() {
		// Load the problem.
		loadService.load(PROBLEM_ID, new AsyncCallback<Problem>() {
			@Override
			public void onSuccess(Problem result) {
				if (result != null) {
					getSession().add(result);
					onProblemLoaded();
				} else {
					loadProblemFailed();
				}
			}
			
			@Override
			public void onFailure(Throwable caught) {
				GWT.log("Could not load problem", caught);
				loadProblemFailed();
			}
		});
		
		// Load current text.
		loadService.loadCurrentText(PROBLEM_ID, new AsyncCallback<String>() {
			@Override
			public void onFailure(Throwable caught) {
				GWT.log("Could not load current text", caught);
				loadCurrentTextFailed();
			}
			
			public void onSuccess(String result) {
				onCurrentTextLoaded(result);
			}
		});
	}

	/**
	 * Called when the problem has been loaded.
	 */
	protected void onProblemLoaded() {
		// If the current editor text has been loaded,
		// then it is ok to start editing.
		if (textLoaded == true) {
			startEditing();
		}
	}
	
	/**
	 * Called when the current text has been retrieved from the server.
	 * 
	 * @param text the current text to load into the editor
	 */
	protected void onCurrentTextLoaded(String text) {
		editor.setText(text);
		textLoaded = true;
		
		// If the problem has been loaded, then it is ok to start editing.
		if (getSession().get(Problem.class) != null) {
			startEditing();
		}
	}

	protected void startEditing() {
		editor.setReadOnly(false);
		mode = Mode.EDITING;
	}

	protected void loadProblemFailed() {
		// TODO - improve
		problemDescription.setErrorText("Could not load problem description");
	}
	
	protected void loadCurrentTextFailed() {
		// TODO - improve
		problemDescription.setErrorText("Could not load text for problem");
	}

	@Override
	public void activate() {
		editor.startEditor();
		editor.setReadOnly(true); // until a Problem is loaded
		editor.setTheme(AceEditorTheme.ECLIPSE);
		editor.setFontSize("14px");
		editor.setMode(AceEditorMode.JAVA);
		editor.addOnChangeHandler(new AceEditorCallback() {
			@Override
			public void invokeAceCallback(JavaScriptObject obj) {
				// Important: don't send the change to the server unless the
				// initial editor contents has been loaded.  Otherwise,
				// the setting of the initial editor contents will get sent
				// to the server as a change, which is obviously not what
				// we want.
				if (!textLoaded) {
					return;
				}
				
				// Convert ACE onChange event object to a Change object,
				// and add it to the session's ChangeList
				User user = getSession().get(User.class);
				Problem problem = getSession().get(Problem.class);
				Change change = ChangeFromAceOnChangeEvent.convert(obj, user.getId(), problem.getProblemId());
				getSession().get(ChangeList.class).addChange(change);
			}
		});
		
		// make the editor the correct height
		doResize();
	}
	
	@Override
	public void deactivate() {
		// Turn off the flush pending events timer
		flushPendingChangeEventsTimer.cancel();

		// Unsubscribe all event subscribers
		getSubscriptionRegistrar().unsubscribeAllEventSubscribers();

		// Clear all local session data
		removeAllSessionObjects();
	}

	protected void submitCode() {
		// If the problem has not been loaded yet,
		// then there is nothing to do.
		if (getSession().get(Problem.class) == null) {
			return;
		}
		
		// Set the editor to read-only!
		// We don't want any edits until the results have
		// come back from the server.
		editor.setReadOnly(true);
		
		// Create a Change representing the full text of the document,
		// and schedule it for transmission to the server.
		Change fullText = new Change(
				ChangeType.FULL_TEXT,
				0, 0, 0, 0, // ignored
				System.currentTimeMillis(),
				getSession().get(User.class).getId(),
				getSession().get(Problem.class).getProblemId(),
				editor.getText());
		getSession().get(ChangeList.class).addChange(fullText);
		
		// Set the mode to SUBMIT_IN_PROGRESS, indicating that we are
		// waiting for the full text to be uploaded to the server.
		mode = Mode.SUBMIT_IN_PROGRESS;
	}
	
	/*
	@Override
	public void update(Observable obj, Object hint) {
		ChangeList changeList = getSession().get(ChangeList.class);
		if (obj == changeList) {
			if (mode == Mode.SUBMIT_IN_PROGRESS && changeList.getState() == ChangeList.State.CLEAN) {
				// Full text of submission has arrived at server,
				// and because the editor is read-only, we know that the
				// local text is in-sync.  So, submit the code!
				
				AsyncCallback<TestResult[]> callback = new AsyncCallback<TestResult[]>() {
					@Override
					public void onFailure(Throwable caught) {
						final String msg = "Error sending submission to server for compilation"; 
						resultWidget.setMessage(msg);
						GWT.log(msg, caught);
						// TODO: should set editor back to read/write?
					}

					@Override
					public void onSuccess(TestResult[] results) {
						// Great, got results back from server!
						resultWidget.setResults(results);
						
						// Can resume editing now
						startEditing();
					}
				};
				
				// Send editor text to server. 
				int problemId = getSession().get(Problem.class).getProblemId();
				submitService.submit(problemId, editor.getText(), callback);
			}
		}
	}
	*/
	
	@Override
	public void eventOccurred(Object key, Publisher publisher, Object hint) {
		if (key == ChangeList.State.CLEAN && mode == Mode.SUBMIT_IN_PROGRESS) {
			// Full text of submission has arrived at server,
			// and because the editor is read-only, we know that the
			// local text is in-sync.  So, submit the code!
			
			AsyncCallback<TestResult[]> callback = new AsyncCallback<TestResult[]>() {
				@Override
				public void onFailure(Throwable caught) {
					final String msg = "Error sending submission to server for compilation"; 
					resultWidget.setMessage(msg);
					GWT.log(msg, caught);
					// TODO: should set editor back to read/write?
				}

				@Override
				public void onSuccess(TestResult[] results) {
					// Great, got results back from server!
					resultWidget.setResults(results);
					
					// Can resume editing now
					startEditing();
				}
			};
			
			// Send editor text to server. 
			int problemId = getSession().get(Problem.class).getProblemId();
			submitService.submit(problemId, editor.getText(), callback);
		}
	}
	
	@Override
	public void unsubscribeFromAll() {
		getSession().get(ChangeList.class).unsubscribeFromAll(this);
	}
	
	@Override
	public void onResize(ResizeEvent event) {
		doResize();
	}

	protected void doResize() {
		int height = Window.getClientHeight();
		
		int availableForEditor = height -
				(LayoutConstants.TOP_BAR_HEIGHT_PX +
				 LayoutConstants.PROBLEM_DESC_HEIGHT_PX +
				 LayoutConstants.STATUS_AND_BUTTON_BAR_HEIGHT_PX +
				 LayoutConstants.RESULTS_PANEL_HEIGHT_PX);
		
		if (availableForEditor < 0) {
			availableForEditor = 0;
		}
		
		getLayoutPanel().setWidgetTopHeight(
				editor,
				LayoutConstants.TOP_BAR_HEIGHT_PX + LayoutConstants.PROBLEM_DESC_HEIGHT_PX, Unit.PX,
				availableForEditor, Unit.PX);
	}

//	protected void flushAllChanges() {
//	}
	
//	private static final int APP_PANEL_HEIGHT_PX = 30;
//	private static final int DESC_PANEL_HEIGHT_PX = 70;
//	private static final int STATUS_PANEL_HEIGHT_PX = 30;
//	private static final int BUTTON_PANEL_HEIGHT_PX = 40;
//
//	private static final String PROBLEM_ID="problemId";
//
//	private static final int NORTH_SOUTH_PANELS_HEIGHT_PX =
//		APP_PANEL_HEIGHT_PX + DESC_PANEL_HEIGHT_PX + STATUS_PANEL_HEIGHT_PX + BUTTON_PANEL_HEIGHT_PX;
//
//	private static final int FAKE_USER_ID = 1; // FIXME
//
//	// Data (model) objects.
//	private Session session;
//
//	// UI widgets.
//	private HorizontalPanel appPanel;
//	private HorizontalPanel descPanel;
//	private HorizontalPanel editorAndWidgetPanel;
//	private HintsWidget hintsWidget;
//	private AffectWidget affectWidget;
//	private VerticalPanel widgetPanel;
//	private HorizontalPanel buttonPanel;
//	private EditorStatusWidget statusWidget;
//	//private InlineLabel statusLabel;
//	private ResultWidget resultWidget;
//	private InlineLabel descLabel;
//	private AceEditor editor;
//	private Timer flushPendingChangeEventsTimer;
//
//	// RPC services.
//	private LogCodeChangeServiceAsync logCodeChangeService;
//	private SubmitServiceAsync submitService;
//	private LoadExerciseServiceAsync loadService;
//	private AffectEventServiceAsync affectEventService;
//
//	/**
//	 * This is the entry point method.
//	 * @param session 
//	 */
//	public DevelopmentView(Session session) {
//		this.session = session;
//		session.get(AffectEvent.class).addObserver(this); // when complete, send to server
//
//		// Id of the problem we're solving
//		// currently this is a request parameter
//		Integer problemId = getProblemId();
//
//		createServices();
//
//		DockLayoutPanel mainPanel = new DockLayoutPanel(Unit.PX);
//
//		// The app panel can be for logout button, menus, etc.
//		appPanel = new HorizontalPanel();
//		appPanel.add(new Label("Menus and logout button should go here"));
//		mainPanel.addNorth(appPanel, APP_PANEL_HEIGHT_PX);
//
//		// The description panel is for the problem description
//		descPanel=new HorizontalPanel();
//		descLabel = new InlineLabel();
//		descPanel.add(descLabel);
//		mainPanel.addNorth(descPanel, DESC_PANEL_HEIGHT_PX);
//		// Load the problem (will update the description panel created above)
//		loadExerciseDescription(problemId);
//
//		// The editor (left) and widget panel (right) occupy the center location
//		// in the DockLayoutPanel, and expand to fill space not occupied by
//		// docked panels.
//		editorAndWidgetPanel = new HorizontalPanel();
//		editorAndWidgetPanel.setWidth("100%");
//
//		// Button panel is for buttons
//		buttonPanel = new HorizontalPanel();
//		Button submitButton=new Button("Submit");
//		submitButton.addClickHandler(new ClickHandler() {
//			@Override
//			public void onClick(ClickEvent event){
//				submitCode();
//			}
//		});
//		buttonPanel.add(submitButton);
//		mainPanel.addSouth(buttonPanel, BUTTON_PANEL_HEIGHT_PX);
//
//		// Status panel - need to think more about what feedback to provide and how
//		HorizontalPanel statusPanel = new HorizontalPanel();
//		statusWidget = new EditorStatusWidget();
//		session.get(ChangeList.class).addObserver(statusWidget);
//		statusPanel.add(statusWidget);
//
//		resultWidget=new ResultWidget();
//		statusPanel.add(resultWidget);
//		statusPanel.setWidth("100%");
//
//		VerticalPanel shimAndStatusPanel = new VerticalPanel();
//		shimAndStatusPanel.add(new HTML("<div style='height: 6px;'></div>"));
//		shimAndStatusPanel.add(statusPanel);
//		mainPanel.addSouth(shimAndStatusPanel, STATUS_PANEL_HEIGHT_PX);
//
//		// Code editor
//		editor = new AceEditor();
//		editor.setStylePrimaryName("NetCoderEditor");
//		editor.setHeight("500px");
//
//		// Widget panel: for things like hints, affect data collection, etc.
//		widgetPanel = new VerticalPanel();
//		widgetPanel.setWidth("100%");
//		hintsWidget = new HintsWidget();
//		hintsWidget.setWidth("100%");
//		widgetPanel.add(hintsWidget);
//		//widgetPanel.add(new HTML("<div style='height: 6px; width: 0px;'></div>")); // hack
//		affectWidget = new AffectWidget(session.get(AffectEvent.class));
//		affectWidget.setWidth("100%");
//		affectWidget.setHeight("300px");
//		widgetPanel.add(affectWidget);
//		// another try to get results into here...
//		resultWidget = new ResultWidget();
//		widgetPanel.add(resultWidget);
//
//		// Add the editor and widget panel so that it is a 80/20 split
//		editorAndWidgetPanel.add(editor);
//		editorAndWidgetPanel.setCellWidth(editor, "80%");
//		editorAndWidgetPanel.add(widgetPanel);
//		editorAndWidgetPanel.setCellWidth(widgetPanel, "20%");
//		mainPanel.add(editorAndWidgetPanel);
//
//		// Add the main panel to the window
//		//RootLayoutPanel.get().add(mainPanel);
//		initWidget(mainPanel);
//
//		//		// Size the editor and widget panel to fill available space
//		//		resize(Window.getClientWidth(), Window.getClientHeight());
//
//		// Add window resize handler so that we can make editor and widget
//		// panel expand vertically as necessary
//		Window.addResizeHandler(this);
//
//		//startEditor();
//
//		// create timer to flush unsent change events periodically
//		flushPendingChangeEventsTimer = new Timer() {
//			@Override
//			public void run() {
//				final ChangeList changeList = DevelopmentView.this.session.get(ChangeList.class);
//				if (changeList.getState() == ChangeList.State.UNSENT) {
//					Change[] changeBatch = changeList.beginTransmit();
//
//					AsyncCallback<Boolean> callback = new AsyncCallback<Boolean>() {
//						@Override
//						public void onFailure(Throwable caught) {
//							changeList.endTransmit(false);
//							GWT.log("Failed to send change batch to server");
//						}
//
//						@Override
//						public void onSuccess(Boolean result) {
//							changeList.endTransmit(true);
//						}
//					};
//
//					logCodeChangeService.logChange(changeBatch, callback);
//				}
//			}
//		};
//		flushPendingChangeEventsTimer.scheduleRepeating(1000);
//	}
//
//	public void startEditor() {
//		// fire up the ACE editor
//		editor.startEditor();
//		editor.setReadOnly(true); // until a Problem is loaded
//		editor.setTheme(AceEditorTheme.ECLIPSE);
//		editor.setFontSize("14px");
//		editor.setMode(AceEditorMode.JAVA);
//		editor.addOnChangeHandler(this);
//	}
//
//	private void createServices() {
//		// Create async service objects for communication with server
//		logCodeChangeService = (LogCodeChangeServiceAsync) GWT.create(LogCodeChangeService.class);
//		//compileService = (CompileServiceAsync) GWT.create(CompileService.class);
//		submitService =(SubmitServiceAsync) GWT.create(SubmitService.class);
//		loadService =(LoadExerciseServiceAsync) GWT.create(LoadExerciseService.class);
//		affectEventService = (AffectEventServiceAsync) GWT.create(AffectEventService.class);
//	}
//
//	/**
//	 * Handles onChange events from the editor.
//	 */
//	@Override
//	public void invokeAceCallback(JavaScriptObject obj) {
//		ChangeList changeList = session.get(ChangeList.class);
//		Problem problem = session.get(Problem.class);
//		changeList.addChange(ChangeFromAceOnChangeEvent.convert(obj, FAKE_USER_ID, problem.getProblemId()));
//	}
//
//	/**
//	 * Send the current text in the editor to the server to be compiled.
//	 */
//	//	protected void compileCode() {
//	//		AsyncCallback<Boolean> callback = new AsyncCallback<Boolean>() {
//	//			@Override
//	//			public void onFailure(Throwable caught) {
//	//				statusLabel.setText("Error sending submission to server for compilation");
//	//				GWT.log("compile failed", caught);
//	//			}
//	//
//	//			@Override
//	//			public void onSuccess(Boolean result) {
//	//				statusLabel.setText(result ? "Compile succeeded" : "Compile failed");
//	//			}
//	//		};
//	//		
//	//		compileService.compile(editor.getText(), callback);
//	//	}
//
//	protected void loadExerciseDescription(int problemId) {
//		AsyncCallback<Problem> callback = new AsyncCallback<Problem>() {
//			@Override
//			public void onFailure(Throwable caught) {
//				descLabel.setText("Error loading exercise");
//				GWT.log("loading exercise description failed", caught);
//			}
//
//			@Override
//			public void onSuccess(Problem result) {
//				setProblem(result);
//			}
//		};
//		loadService.load(problemId, callback);
//	}
//
//	protected void submitCode() {
//		AsyncCallback<TestResult[]> callback = new AsyncCallback<TestResult[]>() {
//			@Override
//			public void onFailure(Throwable caught) {
//				resultWidget.setMessage("Error sending submission to server for compilation");
//				GWT.log("compile failed", caught);
//			}
//
//			@Override
//			public void onSuccess(TestResult[] results) {
//				resultWidget.setResults(results);
//			}
//		};
//		int problemId=getProblemId();
//		submitService.submit(problemId, editor.getText(), callback);
//	}
//
//	@Override
//	public void onResize(ResizeEvent event) {
//		resize(Window.getClientWidth(), Window.getClientHeight());
//	}
//
//	private int getProblemId() {
//		String problemIdStr=Window.Location.getParameter(PROBLEM_ID);
//		if (problemIdStr==null) {
//			return 0;
//		}
//		return Integer.parseInt(problemIdStr);
//	}
//
//	private void resize(int width, int height) {
//		// Let the editor and widget panel take up all of the vertical
//		// height not consumed by the north/south panels.
//		int availHeight = (height - NORTH_SOUTH_PANELS_HEIGHT_PX) - 10;
//		if (availHeight < 0) {
//			availHeight = 0;
//		}
//		editor.setHeight(availHeight + "px");
//	}
//
//	protected void setProblem(Problem result) {
//		//this.problem = result;
//		this.session.add(result);
//		this.descLabel.setText(result.getDescription());
//		this.editor.setReadOnly(false);
//	}
//
//	// FIXME: is there a better way to do this?
//	// (maybe send completed AffectEvent using time, same as Change events)
//	private boolean sendingAffectData = false;
//
//	@Override
//	public void update(Observable obj, Object hint) {
//		final AffectEvent affectEvent = session.get(AffectEvent.class);
//		if (obj == affectEvent && !sendingAffectData && affectEvent.isComplete()) {
//			GWT.log("Sending affect data");
//
//			sendingAffectData = true;
//
//			AsyncCallback<Void> callback = new AsyncCallback<Void>() {
//				@Override
//				public void onFailure(Throwable caught) {
//					GWT.log("Could not store affect event: " + caught.getMessage());
//					sendingAffectData = false;
//				}
//
//				@Override
//				public void onSuccess(Void result) {
//					GWT.log("Affect data recorded successfully");
//					// Yay!
//					affectEvent.clear();
//					sendingAffectData = false;
//				}
//			};
//
//			// Fill in event details.
//			Problem problem = session.get(Problem.class);
//			affectEvent.createEvent(FAKE_USER_ID, problem.getProblemId(), System.currentTimeMillis());
//
//			// Send to server.
//			affectEventService.recordAffectEvent(affectEvent, callback);
//		}
//	}

}
