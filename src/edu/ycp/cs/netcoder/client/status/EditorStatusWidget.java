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

import com.google.gwt.user.client.ui.InlineLabel;

import edu.ycp.cs.netcoder.client.logchange.ChangeList;
import edu.ycp.cs.netcoder.shared.util.Publisher;
import edu.ycp.cs.netcoder.shared.util.Subscriber;
import edu.ycp.cs.netcoder.shared.util.SubscriptionRegistrar;

public class EditorStatusWidget extends InlineLabel implements Subscriber {
	private static final String NORMAL = "NetCoderEditorStatusNormal";
	private static final String XMIT_FAILURE = "NetCoderEditorStatusTransmitFailure";
	
	private ChangeList changeList;

	public EditorStatusWidget(ChangeList changeList, SubscriptionRegistrar registrar) {
		this.changeList = changeList;
		
		// subscribe to state change events
		for (ChangeList.State state : ChangeList.State.values()) {
			changeList.subscribe(state, this, registrar);
		}
		
		setText("---");
		setStylePrimaryName(NORMAL);
	}

	@Override
	public void eventOccurred(Object key, Publisher publisher, Object hint) {
		ChangeList.State state = (ChangeList.State) key;
		
		switch (state) {
		case CLEAN:
			setText("---");
			break;
		case TRANSMISSION:
			setText("<->");
			break;
		case UNSENT:
			setText("-*-");
			break;
		}
		
		if (changeList.isTransmitSuccess()) {
			setStylePrimaryName(NORMAL);
		} else {
			setStylePrimaryName(XMIT_FAILURE);
		}
	}
	
	@Override
	public void unsubscribeFromAll() {
		changeList.unsubscribeFromAll(this);
	}
}
