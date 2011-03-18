package edu.ycp.cs.netcoder.server.compilers;

import java.io.Serializable;

public class TestResult implements Serializable
{
    public static final long serialVersionUID=1L;
    
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
