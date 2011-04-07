package edu.ycp.cs.netcoder.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.ycp.cs.netcoder.shared.testing.TestResult;

public interface SubmitServiceAsync
{
    void submit(int problemId, String programText, AsyncCallback<TestResult[]> callback);
}
