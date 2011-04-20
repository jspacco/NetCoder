// NetCoder - a web-based pedagogical programming environment
// Copyright (C) 2011, Jaime Spacco <jspacco@knox.edu>
// Copyright (C) 2011, David H. Hovemeyer <dhovemey@ycp.edu>
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU Affero General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU Affero General Public License for more details.
//
// You should have received a copy of the GNU Affero General Public License
// along with this program.  If not, see <http://www.gnu.org/licenses/>.

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
