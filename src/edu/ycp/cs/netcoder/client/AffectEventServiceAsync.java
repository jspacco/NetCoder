package edu.ycp.cs.netcoder.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.ycp.cs.netcoder.shared.affect.AffectEvent;

public interface AffectEventServiceAsync {

	void recordAffectEvent(AffectEvent affectEvent, AsyncCallback<Void> callback);

}
