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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.LayoutPanel;

import edu.ycp.cs.netcoder.shared.util.Publisher;
import edu.ycp.cs.netcoder.shared.util.Subscriber;
import edu.ycp.cs.netcoder.shared.util.SubscriptionRegistrar;

/**
 * Common superclass for all NetCoder views.
 * Handles creation of containing LayoutPanel and common UI elements
 * (such as the TopBar).  Also provides helper methods for
 * managing session data and event subscribers.
 */
public abstract class NetCoderView extends Composite {
	private List<Object> sessionObjectList;
	private DefaultSubscriptionRegistrar subscriptionRegistrar;

	private Session session;
	
	private LayoutPanel layoutPanel;
	private TopBar topBar;
	
	/**
	 * Constructor.
	 * 
	 * @param session the Session object
	 */
	public NetCoderView(Session session) {
		this.sessionObjectList = new ArrayList<Object>();
		this.subscriptionRegistrar = new DefaultSubscriptionRegistrar();
		
		this.session = session;
		this.layoutPanel = new LayoutPanel();
		
		this.topBar = new TopBar();
		layoutPanel.add(topBar);
		layoutPanel.setWidgetTopHeight(topBar, 0, Unit.PX, LayoutConstants.TOP_BAR_HEIGHT_PX, Unit.PX);
		topBar.setSession(session);
	}
	
	/**
	 * Add an object to the Session.
	 * 
	 * @param obj  object to add to the Session
	 */
	protected void addSessionObject(Object obj) {
		session.add(obj);
		sessionObjectList.add(obj);
	}
	
	/**
	 * Remove all objects added to the Session.
	 */
	protected void removeAllSessionObjects() {
		for (Object obj : sessionObjectList) {
			session.remove(obj.getClass());
		}
	}
	
	/**
	 * This method is called after a NetCoderView has been instantiated
	 * in the client web page.  Subclasses may override this to do any
	 * initialization that requires that the view is part of the DOM tree.
	 */
	public abstract void activate();
	
	/**
	 * This method is called just before a NetCoderView is removed
	 * from the client web page.  Subclasses may override this
	 * to do cleanup.
	 */
	public abstract void deactivate();
	
	/**
	 * @return the Session object
	 */
	public Session getSession() {
		return session;
	}
	
	/**
	 * Get the SubscriptionRegistrar which keeps track of subscribers
	 * for this view.
	 * 
	 * @return the SubscriptionRegistrar
	 */
	public SubscriptionRegistrar getSubscriptionRegistrar() {
		return subscriptionRegistrar;
	}
	
	/**
	 * @return the overall LayoutPanel which should contain all view UI elements 
	 */
	protected LayoutPanel getLayoutPanel() {
		return layoutPanel;
	}
	
	/**
	 * @return the view's TopBar
	 */
	public TopBar getTopBar() {
		return topBar;
	}
}
