package edu.ycp.cs.netcoder.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import edu.ycp.cs.netcoder.client.LoginService;
import edu.ycp.cs.netcoder.shared.problems.User;

public class LoginServiceImpl extends RemoteServiceServlet implements LoginService {
	private static final long serialVersionUID = 1L;

	@Override
	public User login(String userName, String password) {
		// TODO: implement by searching for matching user record in database
		
		if (userName.equals("user") && password.equals("abc")) {
			User user = new User();
			user.setId(0);
			user.setUserName(userName);
			// Don't return the password in the User object
			return user;
		} else {
			return null;
		}
	}
}
