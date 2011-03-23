package edu.ycp.cs.netcoder.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.ycp.cs.netcoder.shared.logchange.Change;

public interface LogCodeChangeServiceAsync {
	public void logChange(Change[] changeList, AsyncCallback<Boolean> callback);
}
