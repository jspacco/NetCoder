package edu.ycp.cs.netcoder.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;

public class TopBar extends Composite {
	public TopBar() {
		String urlBase = GWT.getModuleBaseURL();
		
		/*
		HTML html = new HTML(
				"<div class='NetCoderTopBar'>" +
				"NetCoder " +
				"<img alt='logo' src='" + urlBase + "/images/NetCoderLogoSmall.png' />" +
				"</div>");
		*/
		FlowPanel panel = new FlowPanel();
		panel.setWidth("100%");
		panel.setStyleName("NetCoderTopBar");
		HorizontalPanel hPanel = new HorizontalPanel();
		hPanel.add(new HTML("<span class='NetCoderProductName'>NetCoder</span>"));
		hPanel.add(new HTML("<img alt='logo' src='" + urlBase + "/images/NetCoderLogoSmall.png' />"));

		panel.add(hPanel);
		
		initWidget(panel);
	}
}
