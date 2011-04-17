package edu.ycp.cs.netcoder.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import edu.ycp.cs.netcoder.shared.problems.User;

@RemoteServiceRelativePath("login")
public interface LoginService extends RemoteService {
	public User login(String userName, String password);
}
