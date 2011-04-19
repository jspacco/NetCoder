package edu.ycp.cs.netcoder.server.problems;

import java.security.AccessControlException;

import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

import edu.ycp.cs.netcoder.shared.testing.TestResult;

public abstract class TestResultFromJUnitResult {
    
    public static TestResult create(Result result, TestCase test) {
    	TestResult testResult = new TestResult();
    	
        if (result.getFailureCount()>0) {
            Failure failure=result.getFailures().get(0);
            Throwable t=failure.getException();
            if (t instanceof AssertionError) {
                // JUnit failure due to failed assertion
                testResult.setOutcome(TestResult.FAILED_ASSERTION);
                testResult.setMessage("input:<"+test.inputAsString()+"> "+failure.getMessage());
            } else if (t instanceof AccessControlException) {
                testResult.setOutcome(TestResult.FAILED_BY_SECURITY_MANAGER);
                testResult.setMessage("input:<"+test.inputAsString()+"> "+failure.getTrace());
            } else {
                // JUnit failure due to runtime exception in student code
                testResult.setOutcome(TestResult.FAILED_WITH_EXCEPTION);
                testResult.setMessage("input:<"+test.inputAsString()+"> "+"<expected "+
                    test.getCorrectOutput()+"> but instead exception raised: "+
                    failure.getTrace());
            }
        } else {
            // succeeded
            testResult.setOutcome(TestResult.PASSED);
            testResult.setMessage("correct! "+test.inputAsString()+" output:<"+test.getCorrectOutput()+">");
        }
        
        return testResult;
    }

}
