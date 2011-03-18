package edu.ycp.cs.netcoder.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("submit")
public interface SubmitService extends RemoteService {
    // FIXME: should return a TestResult and/or CompileResult
    // probably both
    public String submit(String problemId, String programText);
}
