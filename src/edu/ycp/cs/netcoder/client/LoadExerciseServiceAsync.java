package edu.ycp.cs.netcoder.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface LoadExerciseServiceAsync
{
    void load(int problemId, AsyncCallback<String> callback);

}
