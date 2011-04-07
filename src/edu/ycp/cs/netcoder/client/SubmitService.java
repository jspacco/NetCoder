package edu.ycp.cs.netcoder.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import edu.ycp.cs.netcoder.shared.testing.TestResult;

@RemoteServiceRelativePath("submit")
public interface SubmitService extends RemoteService {
    // FIXME: should return a TestResult and/or CompileResult
    // probably both
    public TestResult[] submit(int problemId, String programText);
}
