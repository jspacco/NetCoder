package edu.ycp.cs.netcoder.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import edu.ycp.cs.netcoder.client.LogCodeChangeService;
import edu.ycp.cs.netcoder.server.logchange.Change;

public class LogCodeChangeServiceImpl extends RemoteServiceServlet implements LogCodeChangeService {
	private static final long serialVersionUID = 1L;

	@Override
	public Boolean logChange(String s) {
		//System.out.println("Code change: " + s);

		Change change = Change.fromCompactString(s);
		System.out.println(change);
		
		return true;
	}
}
