// NetCoder - a web-based pedagogical programming environment
// Copyright (C) 2011, Jaime Spacco
// Copyright (C) 2011, David H. Hovemeyer
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
import edu.ycp.cs.netcoder.client.util.Observable;
import edu.ycp.cs.netcoder.client.util.Observer;

public class EditorStatusWidget extends InlineLabel implements Observer {
	private static final String NORMAL = "NetCoderEditorStatusNormal";
	private static final String XMIT_FAILURE = "NetCoderEditorStatusTransmitFailure";

	public EditorStatusWidget() {
		setText("---");
		setStylePrimaryName(NORMAL);
	}
	
	@Override
	public void update(Observable obj, Object hint) {
		ChangeList model = (ChangeList) obj;
		
		switch (model.getState()) {
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
		
		if (model.isTransmitSuccess()) {
			setStylePrimaryName(NORMAL);
		} else {
			setStylePrimaryName(XMIT_FAILURE);
		}
	}
}
