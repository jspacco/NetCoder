package edu.ycp.cs.netcoder.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface SubmitServiceAsync
{
    void submit(String problemId, String programText, AsyncCallback<String> callback);
}
