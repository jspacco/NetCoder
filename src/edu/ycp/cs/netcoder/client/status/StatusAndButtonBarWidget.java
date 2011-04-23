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

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

import edu.ycp.cs.netcoder.client.Session;
import edu.ycp.cs.netcoder.client.logchange.ChangeList;
import edu.ycp.cs.netcoder.shared.util.SubscriptionRegistrar;

public class StatusAndButtonBarWidget extends Composite {
//	private Runnable onSubmit;
	private FlowPanel leftPanel;
	private FlowPanel rightPanel;
	
	
	public StatusAndButtonBarWidget(Session session, SubscriptionRegistrar registrar) {
		FlowPanel panel = new FlowPanel();
		
		panel.setStyleName("NetCoderStatusAndButtonBar");
		
		leftPanel = new FlowPanel();
		
//		StatusMessageWidget statusMessageWidget = new StatusMessageWidget(session, registrar);
//		leftPanel.add(statusMessageWidget);
		
		this.rightPanel = new FlowPanel();
		rightPanel.setStyleName("NetCoderEditorStatusAndSubmitButton");
		
//		EditorStatusWidget editorStatusWidget = new EditorStatusWidget(session.get(ChangeList.class), registrar);
//		rightPanel.add(editorStatusWidget);
//		final Button submitButton = new Button("Submit");
//		submitButton.addClickHandler(new ClickHandler() {
//			@Override
//			public void onClick(ClickEvent event) {
//				if (onSubmit != null) {
//					onSubmit.run();
//				}
//			}
//		});
//		rightPanel.add(submitButton);
		
		panel.add(leftPanel);
		panel.add(rightPanel);
		
		initWidget(panel);
	}
	
//	public void setOnSubmit(Runnable onSubmit) {
//		this.onSubmit = onSubmit;
//	}
	
	public void addToLeftPanel(Widget w) {
		leftPanel.add(w);
	}
	
	public void addToRightPanel(Widget w) {
		rightPanel.add(w);
	}
}
