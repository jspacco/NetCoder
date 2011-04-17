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
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
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
		
		TopBar topBar = new TopBar();
		loginViewPanel.add(topBar);
		loginViewPanel.setWidgetTopHeight(topBar, 0, Unit.PX, LayoutConstants.TOP_BAR_HEIGHT_PX, Unit.PX);
		
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
		loginViewPanel.setWidgetTopHeight(loginBox, LayoutConstants.TOP_BAR_HEIGHT_PX + 10, Unit.PX, 13, Unit.EM);
		
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
					// This will cause the entry point code to switch
					// views.
					session.add(result);
				}
			}
		};
		
		loginStatusLabel.setText("Logging in...");
		loginService.login(userNameTextBox.getText(), passwordTextBox.getText(), callback);
	}
}
