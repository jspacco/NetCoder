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

import com.google.gwt.user.client.ui.InlineHTML;

import edu.ycp.cs.netcoder.client.logchange.ChangeList;
import edu.ycp.cs.netcoder.shared.util.Publisher;
import edu.ycp.cs.netcoder.shared.util.Subscriber;
import edu.ycp.cs.netcoder.shared.util.SubscriptionRegistrar;

public class EditorStatusWidget extends InlineHTML implements Subscriber {
	private ChangeList changeList;

	public EditorStatusWidget(ChangeList changeList, SubscriptionRegistrar registrar) {
		super("XX");
		this.changeList = changeList;
		
		// subscribe to state change events
		changeList.subscribeToAll(ChangeList.State.values(), this, registrar);

		// set initial view contents
		syncView();
		
		getElement().setId("NetCoderEditorStatusWidget");
	}

	@Override
	public void eventOccurred(Object key, Publisher publisher, Object hint) {
		syncView();
	}

	private void syncView() {
		ChangeList.State state = changeList.getState();
		
		String styleName = "";
		
		switch (state) {
		case CLEAN:
			styleName = "NetCoderEditorStatusClean";
			break;
		case TRANSMISSION:
			styleName = "NetCoderEditorStatusTransmit";
			break;
		case UNSENT:
			styleName = changeList.isTransmitSuccess()
					? "NetCoderEditorStatusUnsent"
					: "NetCoderEditorStatusError";
			break;
		}
		
		//GWT.log("Setting style name to " + styleName);
		setStyleName(styleName);
	}
	
	@Override
	public void unsubscribeFromAll() {
		changeList.unsubscribeFromAll(this);
	}
}
