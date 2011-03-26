package edu.ycp.cs.netcoder.client.affect;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.TabLayoutPanel;

public class AffectWidget extends TabLayoutPanel {
	public static String[] EMOTIONS = {
		"BORED",
		"CONFUSED",
		"DELIGHTED",
		"NEUTRAL",
		"FOCUSED",
		"OTHER",
		"FRUSTRATED"
	};

	public AffectWidget() {
		super(0.0, Unit.PX);
		
		add(new HTML("tab 1"), "[tab1]");
		add(new HTML("tab 2"), "[tab2]");
		add(new HTML("tab 3"), "[tab3]");
	}

}
