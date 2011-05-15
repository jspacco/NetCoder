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
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;

import edu.ycp.cs.netcoder.shared.problems.User;

/**
 * Top bar widget.
 * Shows product name and logo, and if the Session has a User
 * object, shows the username and a logout button.
 */
public class TopBar extends Composite {
//	private Session session;
	
	private FlowPanel hPanel;
	private FlowPanel hPanel2;
	private InlineLabel loggedInAsLabel;
	
	private Runnable logoutHandler;
	
	public TopBar() {
		String urlBase = GWT.getModuleBaseURL();
		
		FlowPanel panel = new FlowPanel();
		panel.setStyleName("NetCoderTopBar");
		this.hPanel = new FlowPanel();
		InlineLabel productName = new InlineLabel("NetCoder");
		productName.setStyleName("NetCoderProductName");
		hPanel.add(productName);
		Image logoImg = new Image(urlBase + "/images/NetCoderLogoSmall.png");
		logoImg.setAltText("[logo]");
		logoImg.setStyleName("NetCoderLogo");
		hPanel.add(logoImg);
		Image agplImg = new Image(urlBase + "/images/agplv3-88x31.png");
		agplImg.setAltText("[agpl logo]");
		agplImg.setStyleName("NetCoderAGPLLogo");
		hPanel.add(agplImg);

		hPanel2 = new FlowPanel();
		hPanel2.setStyleName("NetCoderUsernameAndLogout");
		loggedInAsLabel = new InlineLabel();
		loggedInAsLabel.setStyleName("NetCoderLoggedInAs");
		hPanel2.add(loggedInAsLabel);
		
		panel.add(hPanel);
		panel.add(hPanel2);
		
		initWidget(panel);
	}

	public void setSession(Session session) {
//		this.session = session;
		User user = session.get(User.class);
		if (user != null) {
			loggedInAsLabel.setText("Logged in as " + user.getUserName());
			Button logoutButton = new Button("Log out");
			logoutButton.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					if (logoutHandler != null) {
						logoutHandler.run();
					}
				}
			});
			hPanel2.add(new InlineLabel("    "));
			hPanel2.add(logoutButton);
		}
	}
	
	public void setLogoutHandler(Runnable logoutHandler) {
		this.logoutHandler = logoutHandler;
	}
}
