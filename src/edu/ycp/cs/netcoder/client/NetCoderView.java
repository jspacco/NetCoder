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

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.LayoutPanel;

/**
 * Common superclass for all NetCoder views.
 * Handles creation of containing LayoutPanel and common UI elements
 * (such as the TopBar).
 */
public abstract class NetCoderView extends Composite {
	private Session session;
	
	private LayoutPanel layoutPanel;
	private TopBar topBar;
	
	/**
	 * Constructor.
	 * 
	 * @param session the Session object
	 */
	public NetCoderView(Session session) {
		this.session = session;
		this.layoutPanel = new LayoutPanel();
		
		this.topBar = new TopBar();
		layoutPanel.add(topBar);
		layoutPanel.setWidgetTopHeight(topBar, 0, Unit.PX, LayoutConstants.TOP_BAR_HEIGHT_PX, Unit.PX);
		topBar.setSession(session);
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
