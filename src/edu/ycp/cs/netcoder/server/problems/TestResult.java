package edu.ycp.cs.netcoder.server.problems;

import java.io.Serializable;

public class TestResult implements Serializable
{
    public static final long serialVersionUID=1L;
    //TODO: store outcomes (pass, fail, timeout)?
    
    public final boolean success;
    public final String message;
    
    public TestResult(boolean success, String message) {
        this.success=success;
        this.message=message;
    }
    
    public String toString() {
        return message;
    }
}
