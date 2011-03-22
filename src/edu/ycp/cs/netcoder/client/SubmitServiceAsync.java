package edu.ycp.cs.netcoder.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface SubmitServiceAsync
{
    void submit(int problemId, String programText, AsyncCallback<String> callback);
}
