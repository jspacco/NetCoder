package edu.ycp.cs.netcoder.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import edu.ycp.cs.netcoder.client.CompileService;

public class CompileServiceImpl extends RemoteServiceServlet implements CompileService {
	private static final long serialVersionUID = 1L;

	@Override
	public Boolean compile(String programText) {
		System.out.println("Request to compile!");
		System.out.println("Code:");
		System.out.println(programText);
		return Boolean.TRUE;
	}
}
