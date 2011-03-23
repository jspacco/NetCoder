package edu.ycp.cs.netcoder.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import edu.ycp.cs.netcoder.shared.logchange.Change;

@RemoteServiceRelativePath("logChange")
public interface LogCodeChangeService extends RemoteService {
	public Boolean logChange(Change[] changeList);
}
