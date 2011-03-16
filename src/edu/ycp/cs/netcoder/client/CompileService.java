package edu.ycp.cs.netcoder.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("compile")
public interface CompileService extends RemoteService {
	// FIXME: should return a CompileResult object
	public Boolean compile(String programText);
}
