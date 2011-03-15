package edu.ycp.cs.netcoder.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("logChange")
public interface LogCodeChangeService extends RemoteService {
	public Boolean logChange(String s);
}
