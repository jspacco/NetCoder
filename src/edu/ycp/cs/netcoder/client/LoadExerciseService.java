package edu.ycp.cs.netcoder.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("loadExercise")
public interface LoadExerciseService extends RemoteService {
    // FIXME: should return a TestResult and/or CompileResult
    // probably both
    public String load(int problemId);
}
