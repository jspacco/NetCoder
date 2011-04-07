package edu.ycp.cs.netcoder.shared.testing;

import java.io.Serializable;

import com.google.gwt.user.client.rpc.IsSerializable;

public class TestResult implements Serializable, IsSerializable
{
    public static final long serialVersionUID=1L;
    //TODO: store outcomes (pass, fail, timeout)?
    //TODO: store stdout and stderr from running the code
    
    public boolean success;
    public String message;
    
    public TestResult(boolean success, String message) {
        this.success=success;
        this.message=message;
    }
    
    public TestResult() {}
    
    public String toString() {
        return message;
    }
    
    public static TestResult[] error(String msg) {
        return new TestResult[] {new TestResult(false, msg)};
    }
    
}
