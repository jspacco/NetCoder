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

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootLayoutPanel;

import edu.ycp.cs.netcoder.shared.util.Publisher;
import edu.ycp.cs.netcoder.shared.util.Subscriber;
import edu.ycp.cs.netcoder.shared.util.SubscriptionRegistrar;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class NetCoder_GWT2 implements EntryPoint, Subscriber, ResizeHandler {
	// Client session data.
	private Session session;
	
	// Subscription registrar
	private SubscriptionRegistrar subscriptionRegistrar;

	// Currently-active NetCoderView.
	private NetCoderView currentView;
	
	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		// Create session
		session = new Session();
		session.add(new WindowResizeNotifier());
		
		// Create a SubscriptionRegistrar
		subscriptionRegistrar = new DefaultSubscriptionRegistrar();
		
		// Observe Session changes
		session.subscribe(Session.Event.LOGIN, this, subscriptionRegistrar);
		session.subscribe(Session.Event.LOGOUT, this, subscriptionRegistrar);
		
		// Get window ResizeEvents so we can publish them to views
		Window.addResizeHandler(this);
		
		changeView(new LoginView(session));
	}
	
	@Override
	public void onResize(ResizeEvent event) {
		// Publish window ResizeEvent
		session.get(WindowResizeNotifier.class).notifySubscribers(WindowResizeNotifier.WINDOW_RESIZED, event);
	}
	
	public void changeView(NetCoderView view) {
		if (currentView != null) {
			currentView.deactivate();
			RootLayoutPanel.get().remove(currentView);
		}
		RootLayoutPanel.get().add(view);
		view.activate();
		currentView = view;
	}
	
	@Override
	public void eventOccurred(Object key, Publisher publisher, Object hint) {
		if (key == Session.Event.LOGIN) {
//			changeView(new DevelopmentView(session));
			changeView(new CourseAndProblemView(session));
		} else if (key == Session.Event.LOGOUT) {
			changeView(new LoginView(session));
		}
	}
	
	@Override
	public void unsubscribeFromAll() {
		session.unsubscribeFromAll(this);
	}
}
