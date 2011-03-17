package edu.ycp.cs.netcoder.server.compilers;

import org.junit.runner.JUnitCore;
import org.junit.runner.Request;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

import edu.ycp.cs.netcoder.server.compilers.TestCreator.BeanShellTest;

public class TestRunner
{
    private TestCreator creator;
    
    public TestRunner(TestCreator creator) {
        this.creator=creator;
    }
    
    public void run() 
    {
        OnTheFlyCompiler flyCompiler=new OnTheFlyCompiler();
        CompileResult compileResult=flyCompiler.compile(creator.getClassName(), creator.toString());
        //System.out.println("Compilation result: " +compileResult);
        if (!compileResult.success) {
            // Throw exception containing the diagnostics
            // XXX Create a new exception type for this?
            throw new RuntimeException(compileResult.toString());
        }
        try {
            JUnitCore core=new JUnitCore();
            Class testClass=flyCompiler.loadClass(creator.getClassName());

            for (int i=0; i<creator.getNumTests(); i++) {
                //TODO Spawn each testcase in a separate thread using a threadpool
                //TODO capture stdout/stderr to improve on codingbat
                //TODO return a list of "junit outcomes"
                BeanShellTest test=creator.getTestNum(i);
                Result result=core.run(Request.method(testClass, test.getTestName()));
                if (result.getFailureCount()>0) {
                    // failed
                    test.setFailure(result.getFailures().get(0));
                } else {
                    // succeeded
                    test.setSuccess();
                }
                System.out.println(test);
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
