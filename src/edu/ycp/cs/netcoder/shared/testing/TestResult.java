package edu.ycp.cs.netcoder.shared.testing;

import java.io.Serializable;
import java.security.AccessControlException;

import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

import com.google.gwt.user.client.rpc.IsSerializable;

import edu.ycp.cs.netcoder.server.problems.TestCase;

public class TestResult implements Serializable, IsSerializable
{
    public static final long serialVersionUID=1L;
    
    //TODO: Replace with enum?
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
    
    public TestResult(Result result, TestCase test) {
        if (result.getFailureCount()>0) {
            Failure failure=result.getFailures().get(0);
            Throwable t=failure.getException();
            if (t instanceof AssertionError) {
                // JUnit failure due to failed assertion
                this.outcome=FAILED_ASSERTION;
                this.message="input:<"+test.inputAsString()+"> "+failure.getMessage();
            } else if (t instanceof AccessControlException) {
                this.outcome=FAILED_BY_SECURITY_MANAGER;
                this.message="input:<"+test.inputAsString()+"> "+failure.getTrace();
            } else {
                // JUnit failure due to runtime exception in student code
                this.outcome=FAILED_WITH_EXCEPTION;
                this.message="input:<"+test.inputAsString()+"> "+"<expected "+
                    test.getCorrectOutput()+"> but instead exception raised: "+
                    failure.getTrace();
            }
        } else {
            // succeeded
            this.outcome=PASSED;
            this.message="correct! "+test.inputAsString()+" output:<"+test.getCorrectOutput()+">";
        }
    }

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
