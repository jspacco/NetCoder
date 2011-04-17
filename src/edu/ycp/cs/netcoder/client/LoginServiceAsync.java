package edu.ycp.cs.netcoder.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.ycp.cs.netcoder.shared.problems.User;

public interface LoginServiceAsync {

	void login(String userName, String password, AsyncCallback<User> callback);

}
