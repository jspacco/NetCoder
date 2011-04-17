package edu.ycp.cs.netcoder.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;

import edu.ycp.cs.netcoder.shared.problems.User;

public class LoginView extends Composite {
	private Session session;
	
	private TextBox userNameTextBox;
	private PasswordTextBox passwordTextBox;
	private Label loginStatusLabel;
	
	private LoginServiceAsync loginService = GWT.create(LoginService.class);

	public LoginView(Session session) {
		this.session = session;

		LayoutPanel loginViewPanel = new LayoutPanel();
		
		FlowPanel panel = new FlowPanel();
		panel.add(new Label("Username:"));
		userNameTextBox = new TextBox();
		userNameTextBox.setWidth("20em");
		panel.add(userNameTextBox);
		panel.add(new Label("Password:"));
		passwordTextBox = new PasswordTextBox();
		passwordTextBox.setWidth("20em");
		panel.add(passwordTextBox);

		userNameTextBox.addKeyPressHandler(new KeyPressHandler() {
			@Override
			public void onKeyPress(KeyPressEvent event) {
				if (event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER) {
					passwordTextBox.setFocus(true);
				}
			}
		});
		passwordTextBox.addKeyPressHandler(new KeyPressHandler() {
			@Override
			public void onKeyPress(KeyPressEvent event) {
				if (event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER) {
					attemptLogin();
				}
			}
		});

		FlowPanel panel2 = new FlowPanel();
		Button loginButton = new Button("Log in");
		
		loginButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				attemptLogin();
			}
		});
		
		panel2.add(loginButton);
		FlowPanel loginBox = new FlowPanel();
		loginBox.setStyleName("NetCoderRoundBox");
		loginBox.add(panel);
		loginBox.add(panel2);
		
		loginStatusLabel = new Label("");
		loginStatusLabel.setStyleName("NetCoderLoginStatus");
		loginBox.add(loginStatusLabel);
		
		loginViewPanel.add(loginBox);
		loginViewPanel.setWidgetRightWidth(loginBox, 2, Unit.EM, 23, Unit.EM);
		loginViewPanel.setWidgetTopHeight(loginBox, LayoutConstants.TOP_BAR_HEIGHT_PX, Unit.PX, 13, Unit.EM);
		
		initWidget(loginViewPanel);
	}

	protected void attemptLogin() {
		AsyncCallback<User> callback = new AsyncCallback<User>() {
			@Override
			public void onFailure(Throwable caught) {
				GWT.log("Couldn't communicate with login service: " + caught.getMessage());
				loginStatusLabel.setText("Could not communicate with server. Are you connected " +
						"to the network?");
			}
			
			public void onSuccess(User result) {
				if (result == null) {
					loginStatusLabel.setText("Could not log in. Check your username and password.");
				} else {
					// Set the user object in the session!
					session.add(result);
					
					Window.alert("Successful login!");
				}
			}
		};
		
		loginStatusLabel.setText("Logging in...");
		loginService.login(userNameTextBox.getText(), passwordTextBox.getText(), callback);
	}
}
