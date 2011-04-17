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

import java.net.URL;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.List;

import org.junit.runner.JUnitCore;
import org.junit.runner.Request;
import org.junit.runner.Result;

import edu.ycp.cs.netcoder.server.compilers.CompilationException;
import edu.ycp.cs.netcoder.server.compilers.CompileResult;
import edu.ycp.cs.netcoder.server.compilers.OnTheFlyCompiler;
import edu.ycp.cs.netcoder.shared.testing.TestResult;

public class TestRunner
{
    public TestRunner() {}

    public List<TestResult> run(TestCreator creator)
    throws ClassNotFoundException, CompilationException
    {
        OnTheFlyCompiler flyCompiler=new OnTheFlyCompiler();

        CompileResult compileResult=flyCompiler.compile(
                creator.getBinaryClassName(),
                creator.toJUnitTestCase());

        // print source file
        System.out.println(creator.toJUnitTestCase());
        if (!compileResult.success) {
            // must receive a class that compiled or we throw exception
            throw new CompilationException(compileResult);
        }
        //System.out.println(creator.toString());
        
        // load class
        final Class testClass=flyCompiler.loadClass(creator.getBinaryClassName());
        
        // create a list of tasks to be executed
        List<IsolatedTask<TestResult>> tasks=new ArrayList<IsolatedTask<TestResult>>();
        for (int i=0; i<creator.getNumTests(); i++) {
            final TestCase test=creator.getTestNum(i);
            tasks.add(new IsolatedTask<TestResult>() {
                public TestResult execute() {
                    JUnitCore core=new JUnitCore();
                    Result result=core.run(Request.method(testClass, test.getJUnitTestCaseName()));
                    if (result.getFailureCount()>0) {
                        // failed
                        // XXX improve error message?
                        return new TestResult(false, 
                                "input:<"+test.inputAsString()+"> "+result.getFailures().get(0).getMessage().toString());
                    } else {
                        // succeeded
                        return new TestResult(true, 
                                "correct! "+test.inputAsString()+" output:<"+test.getCorrectOutput()+">");
                    }
                }
            });
        }
        
        KillableTaskManager<TestResult> pool=
            new KillableTaskManager<TestResult>(
                    tasks, 
                    2000,
                    new TestResult(false, "Timed out:  Check for an infinite loop or infinite recursion"));
        pool.run();
        
        return pool.getOutcomes();

//        for (int i=0; i<creator.getNumTests(); i++) {
//            //TODO Spawn each testcase in a separate thread using a threadpool
//            //TODO capture stdout/stderr to improve on codingbat
//            //TODO return a list of "junit outcomes"
//            BeanShellTest test=creator.getTestNum(i);
//            Result result=core.run(Request.method(testClass, test.getTestName()));
//            //Result result=JUnitCore.runClasses(testClass);
//
//            if (result.getFailureCount()>0) {
//                // failed
//                results.addTestResult(new TestResult(
//                        false, 
//                        test.inputAsString()+" "+result.getFailures().get(0).getMessage().toString()));
//                System.out.println(result.getFailures().get(0).getTrace());
//            } else {
//                // succeeded
//                results.addTestResult(new TestResult(true, 
//                        test.inputAsString()+" "+test.correctOutputAsString()));
//            }
//        }
//        return results;
    }
    
    

    private URL getWhereClassWasLoaded(Class cls){
        ProtectionDomain pDomain = cls.getProtectionDomain();
        CodeSource cSource = pDomain.getCodeSource();
        URL loc = cSource.getLocation();
        return loc;
    }
}
