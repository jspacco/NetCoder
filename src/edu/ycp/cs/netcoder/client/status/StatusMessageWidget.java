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

package edu.ycp.cs.netcoder.client.status;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.InlineLabel;

import edu.ycp.cs.netcoder.client.Session;
import edu.ycp.cs.netcoder.client.StatusMessage;
import edu.ycp.cs.netcoder.shared.util.Publisher;
import edu.ycp.cs.netcoder.shared.util.Subscriber;
import edu.ycp.cs.netcoder.shared.util.SubscriptionRegistrar;

public class StatusMessageWidget extends Composite implements Subscriber {
	private Session session;
	private InlineLabel label;
	
	public StatusMessageWidget(Session session, SubscriptionRegistrar registrar) {
		this.session = session;
		session.subscribe(Session.Event.ADDED_OBJECT, this, registrar);
		
		label = new InlineLabel("");
		
		// set initial contents
		setContents(StatusMessage.Category.INFORMATION, "");
		
		initWidget(label);
	}
	
	@Override
	public void eventOccurred(Object key, Publisher publisher, Object hint) {
		if (key == Session.Event.ADDED_OBJECT && hint.getClass() == StatusMessage.class){
			StatusMessage statusMessage = (StatusMessage) hint;
	
			StatusMessage.Category category = statusMessage.getCategory();
			String message = statusMessage.getMessage();
			
			setContents(category, message);
		}
	}
	
	@Override
	public void unsubscribeFromAll() {
		session.unsubscribeFromAll(this);
	}

	private void setContents(StatusMessage.Category category, String message) {
		label.setText(message);
		String styleName = category == StatusMessage.Category.INFORMATION
			? "NetCoderStatusInformation"
			: "NetCoderStatusError";
		label.setStyleName(styleName);
	}
}
