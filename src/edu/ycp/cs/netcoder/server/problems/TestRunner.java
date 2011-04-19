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

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.net.URL;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.junit.runner.JUnitCore;
import org.junit.runner.Request;
import org.junit.runner.Result;

import edu.ycp.cs.netcoder.server.compilers.CompilationException;
import edu.ycp.cs.netcoder.server.compilers.CompileResult;
import edu.ycp.cs.netcoder.server.compilers.OnTheFlyCompiler;
import edu.ycp.cs.netcoder.shared.testing.TestResult;

public class TestRunner
{
    public static final long TIMEOUT_LIMIT=2000;
    private SecurityManager securityManager;
    
    public TestRunner() {
        // default to the current security manager
        this.securityManager=System.getSecurityManager();
    }
    public TestRunner(SecurityManager sm) {
        this.securityManager=sm;
    }
    
    /**
     * Run all test cases in the given TestCreator in a single thread.
     * WARNING:  This may hang forever if a test case has an infinite loop!
     * 
     * @param creator
     * @return List of TestResults from the test cases, or nothing for
     *  eternity if one of the test cases contained an infinite loop.
     * @throws ClassNotFoundException
     * @throws CompilationException
     */
    public List<TestResult> runInSingleThread(TestCreator creator)
    throws ClassNotFoundException, CompilationException
    {
        final Class<?> testClass = compileToClass(creator);

        
        // create a list of results
        List<TestResult> results=new LinkedList<TestResult>();
        
        // remember original stdOut/stdErr
        final PrintStream originalStdout=System.out;
        final PrintStream originalStderr=System.err;
        
        for (int i=0; i<creator.getNumTests(); i++) {
            final TestCase test=creator.getTestNum(i);
            JUnitCore core=new JUnitCore();
            
            // re-direct stdout/stderr
            ByteArrayOutputStream baosOut=new ByteArrayOutputStream();
            PrintStream out=new PrintStream(baosOut);
            
            ByteArrayOutputStream baosErr=new ByteArrayOutputStream();
            PrintStream err=new PrintStream(baosErr);
            
            System.setOut(out);
            System.setErr(err);
            
            // run tests
            Result result=core.run(Request.method(testClass, test.getJUnitTestCaseName()));
            
            // put stdout/stderr back
            System.setOut(originalStdout);
            System.setErr(originalStderr);
            
            TestResult testResult= TestResultFromJUnitResult.create(result, test);
            // put buffered stdout/stderr into test results
            testResult.setStdout(baosOut.toString());
            testResult.setStderr(baosErr.toString());
            results.add(testResult);
        }
        return results;
    }

    public List<TestResult> run(TestCreator creator)
    throws ClassNotFoundException, CompilationException
    {
        final Class testClass = compileToClass(creator);

        // create a list of tasks to be executed
        List<IsolatedTask<TestResult>> tasks=new ArrayList<IsolatedTask<TestResult>>();
        
        for (int i=0; i<creator.getNumTests(); i++) {
            final TestCase test=creator.getTestNum(i);
            tasks.add(new IsolatedTask<TestResult>() {
                public TestResult execute() {
                    //TODO Security Manager:  Probably need to restrict
                    //access to unwanted operations by everything
                    //located in the package where I'll compile
                    //all of the student code.
                    
                    JUnitCore core=new JUnitCore();
                    Result result=core.run(Request.method(testClass, test.getJUnitTestCaseName()));
                    
                    //TODO: Get exceptions and stack traces into TestResult
                    return TestResultFromJUnitResult.create(result, test);
                }
            });
        }
        
        KillableTaskManager<TestResult> pool=new KillableTaskManager<TestResult>(
                tasks, 
                TIMEOUT_LIMIT,
                this.securityManager,
                new KillableTaskManager.TimeoutHandler<TestResult>() {
                    @Override
                    public TestResult handleTimeout() {
                        return new TestResult(TestResult.FAILED_FROM_TIMEOUT, 
                                "Took too long!  Check for infinite loops, or recursion without a proper base case");
                    }
                });
        
        // run each task in a separate thread
        pool.run();
        
        //merge outcomes with their buffered inputs for stdout/stderr
        List<TestResult> outcomes=pool.getOutcomes();
        Map<Integer,String> stdout=pool.getBufferedStdout();
        Map<Integer,String> stderr=pool.getBufferedStderr();
        for (int i=0; i<outcomes.size(); i++) {
            TestResult t=outcomes.get(i);
            t.setStdout(stdout.get(i));
            t.setStderr(stderr.get(i));
        }
        
        return outcomes;
    }



    /**
     * @param creator
     * @return
     * @throws CompilationException
     * @throws ClassNotFoundException
     */
    public Class compileToClass(TestCreator creator)
            throws CompilationException, ClassNotFoundException {
        OnTheFlyCompiler flyCompiler=new OnTheFlyCompiler();

        CompileResult compileResult=flyCompiler.compile(
                creator.getBinaryClassName(),
                creator.toJUnitTestCase());

        //DEBUG: print source file
        //System.out.println(creator.toJUnitTestCase());
        if (!compileResult.success) {
            // must receive a class that compiled or we throw exception
            throw new CompilationException(compileResult);
        }
        //System.out.println(creator.toString());
        
        // load class
        final Class testClass=flyCompiler.loadClass(creator.getBinaryClassName());
        return testClass;
    }
    
    private interface TestResultCreator {
        TestResult createTimeout(PrintStreamToStringConverter out, PrintStreamToStringConverter err);
    }
    
    private static class PrintStreamToStringConverter {
        private PrintStream stream;
        private ByteArrayOutputStream baos;
        
        PrintStreamToStringConverter() {
            baos=new ByteArrayOutputStream();
            stream=new PrintStream(baos);
        }
        
        PrintStream getPrintStream() {
            return this.stream;
        }
        String getString() {
            stream.flush();
            stream.close();
            return baos.toString();
        }
    }

    private URL getWhereClassWasLoaded(Class cls){
        ProtectionDomain pDomain = cls.getProtectionDomain();
        CodeSource cSource = pDomain.getCodeSource();
        URL loc = cSource.getLocation();
        return loc;
    }
}
