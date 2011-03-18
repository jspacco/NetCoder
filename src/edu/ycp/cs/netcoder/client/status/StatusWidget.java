package edu.ycp.cs.netcoder.client.status;

import com.google.gwt.user.client.ui.Label;

import edu.ycp.cs.netcoder.client.logchange.ChangeList;
import edu.ycp.cs.netcoder.client.util.Observable;
import edu.ycp.cs.netcoder.client.util.Observer;

public class StatusWidget extends Label implements Observer {

	public StatusWidget() {
		setWidth("40px");
	}
	
	@Override
	public void update(Observable obj, Object hint) {
		ChangeList model = (ChangeList) obj;
		
		switch (model.getState()) {
		case CLEAN:
			setStylePrimaryName("NetCoder-Status-Clean");
			setText("-----");
			break;
			
		case TRANSMISSION:
			setStylePrimaryName("NetCoder-Status-Tranmission");
			setText("<--->");
			break;
			
		case UNSENT:
			setStylePrimaryName("NetCoder-Status-Unsent");
			setText("--*--");
			break;
		}
	}
}
