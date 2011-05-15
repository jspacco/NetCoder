package edu.ycp.cs.netcoder.shared.testing;

import java.io.Serializable;

import com.google.gwt.user.client.rpc.IsSerializable;

public class TestResult implements Serializable, IsSerializable
{
    public static final long serialVersionUID=1L;
    
    //TODO: Replace with enum?  
    //TODO: Add 6 separate methods for each possible outcome
    public static final String PASSED="passed";
    public static final String FAILED_ASSERTION="failed";
    public static final String FAILED_WITH_EXCEPTION="runtime_exception";
    public static final String FAILED_BY_SECURITY_MANAGER="security_exception";
    public static final String FAILED_FROM_TIMEOUT="timeout";
    public static final String INTERNAL_ERROR="interal_error";
    
    private String outcome;
    private String message;
    private String stdout;
    private String stderr;
    
    public TestResult(String outcome, String message) {
        this.outcome=outcome;
        this.message=message;
    }
    
    public TestResult(String outcome, 
            String message, 
            String stdout, 
            String stderr)
    {
        this(outcome,message);
        this.stdout=stdout;
        this.stderr=stderr;
    }
    
    public TestResult() {}

    public String toString() {
        return message;
    }

    public static TestResult[] error(String msg) {
        return new TestResult[] {new TestResult(INTERNAL_ERROR, msg)};
    }
    /**
     * @return the success
     */
    public String getOutcome() {
        return outcome;
    }
    /**
     * @param success the success to set
     */
    public void setOutcome(String outcome) {
        this.outcome= outcome;
    }
    /**
     * @return the message
     */
    public String getMessage() {
        return message;
    }
    /**
     * @param message the message to set
     */
    public void setMessage(String message) {
        this.message = message;
    }
    /**
     * @return the stdout
     */
    public String getStdout() {
        return stdout;
    }
    /**
     * @param stdout the stdout to set
     */
    public void setStdout(String stdout) {
        this.stdout = stdout;
    }
    /**
     * @return the stderr
     */
    public String getStderr() {
        return stderr;
    }
    /**
     * @param stderr the stderr to set
     */
    public void setStderr(String stderr) {
        this.stderr = stderr;
    }
}
