package edu.ycp.cs.netcoder.server.compilers;

import java.net.URL;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.List;

import org.junit.runner.JUnitCore;
import org.junit.runner.Request;
import org.junit.runner.Result;

import edu.ycp.cs.netcoder.server.compilers.TestCreator.BeanShellTest;

public class TestRunner
{
    public TestRunner() {}

    public List<TestResult> run(TestCreator creator)
    throws ClassNotFoundException, CompilationException
    {
        OnTheFlyCompiler flyCompiler=new OnTheFlyCompiler();

        CompileResult compileResult=flyCompiler.compile(
                creator.getBinaryClassName(),
                creator.toString());

        // print source file
        //System.out.println(creator.toString2());
        System.out.println(creator.toString());
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
            final BeanShellTest test=creator.getTestNum(i);
            tasks.add(new IsolatedTask<TestResult>() {
                public TestResult execute() {
                    JUnitCore core=new JUnitCore();
                    Result result=core.run(Request.method(testClass, test.getTestName()));
                    if (result.getFailureCount()>0) {
                        // failed
                        // XXX improve error message?
                        return new TestResult(false, 
                                "input:<"+test.inputAsString()+"> "+result.getFailures().get(0).getMessage().toString());
                    } else {
                        // succeeded
                        return new TestResult(true, 
                                "correct! "+test.inputAsString()+" output:<"+test.correctOutput+">");
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
