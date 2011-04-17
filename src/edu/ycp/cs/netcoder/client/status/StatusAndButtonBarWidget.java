package edu.ycp.cs.netcoder.client.status;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;

import edu.ycp.cs.netcoder.client.Session;

public class StatusAndButtonBarWidget extends Composite {
	private Session session;
	private Runnable onSubmit;
	
	public StatusAndButtonBarWidget(Session session) {
		this.session = session;
		
		FlowPanel panel = new FlowPanel();
		
		panel.setStyleName("NetCoderStatusAndButtonBar");
		
		// TODO: status widget(s)
		
		final Button submitButton = new Button("Submit");
		submitButton.setStyleName("NetCoderSubmitButton");
		submitButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (onSubmit != null) {
					onSubmit.run();
				}
			}
		});
		panel.add(submitButton);
		
		initWidget(panel);
	}
	
	public void setOnSubmit(Runnable onSubmit) {
		this.onSubmit = onSubmit;
	}
}
