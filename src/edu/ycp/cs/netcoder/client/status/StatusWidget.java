package edu.ycp.cs.netcoder.client.status;

import com.google.gwt.user.client.ui.Label;

import edu.ycp.cs.netcoder.client.logchange.ChangeList;
import edu.ycp.cs.netcoder.client.util.Observable;
import edu.ycp.cs.netcoder.client.util.Observer;

public class StatusWidget extends Label implements Observer {
	private static final String NORMAL = "NetCoderStatusNormal";
	private static final String XMIT_FAILURE = "NetCoderStatusTransmitFailure";

	public StatusWidget() {
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
