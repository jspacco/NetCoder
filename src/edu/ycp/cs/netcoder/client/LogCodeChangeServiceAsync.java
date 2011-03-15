package edu.ycp.cs.netcoder.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface LogCodeChangeServiceAsync {
	public void logChange(String s, AsyncCallback<Boolean> callback);
}
