package edu.ycp.cs.netcoder.server;

import javax.servlet.http.HttpSession;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import edu.ycp.cs.netcoder.shared.problems.NetCoderAuthenticationException;
import edu.ycp.cs.netcoder.shared.problems.User;

/**
 * Superclass for NetCoder RPC service implementations.
 * Has utility methods for authentication.
 */
public abstract class NetCoderServiceImpl extends RemoteServiceServlet {
	private static final long serialVersionUID = 1L;
	
	/**
	 * Check whether or not the client is authenticated.
	 * 
	 * @return the authenticated User object
	 * @throws NetCoderAuthenticationException if the client is not authenticated
	 */
	protected User checkClientIsAuthenticated() throws NetCoderAuthenticationException {
		HttpSession session = getThreadLocalRequest().getSession();
		User user = (User) session.getAttribute("user");
		if (user == null) {
			throw new NetCoderAuthenticationException();
		}
		return user;
	}

	/**
	 * Check whether or not the client is authenticated as a given user.
	 * 
	 * @param userId id of the user we want to know whether the client is authenticated as
	 * @return the authenticated User object
	 * @throws NetCoderAuthenticationException if the client is not authenticated
	 *                                         as the given user
	 */
	protected User checkClientIsAuthenticatedAs(int userId) throws NetCoderAuthenticationException {
		HttpSession session = getThreadLocalRequest().getSession();
		User user = (User) session.getAttribute("user");
		if (user == null || user.getId() != userId) {
			throw new NetCoderAuthenticationException();
		}
		return user;
	}
}
