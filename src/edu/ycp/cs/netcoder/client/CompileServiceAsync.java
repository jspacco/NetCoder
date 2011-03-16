package edu.ycp.cs.netcoder.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface CompileServiceAsync {
	void compile(String programText, AsyncCallback<Boolean> callback);
}
