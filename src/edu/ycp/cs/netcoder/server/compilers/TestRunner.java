package edu.ycp.cs.netcoder.server.compilers;

import java.net.URL;
import java.security.CodeSource;
import java.security.ProtectionDomain;

import org.junit.runner.JUnitCore;
import org.junit.runner.Request;
import org.junit.runner.Result;

import edu.ycp.cs.netcoder.server.compilers.TestCreator.BeanShellTest;

public class TestRunner
{
    public TestRunner() {}

    public TestResultCollection run(TestCreator creator)
    throws ClassNotFoundException
    {
        OnTheFlyCompiler flyCompiler=new OnTheFlyCompiler();

        CompileResult compileResult=flyCompiler.compile(
                creator.getBinaryClassName(),
                creator.toString());

        TestResultCollection results=new TestResultCollection(compileResult);
        // print source file
        //System.out.println(creator.toString2());
        System.out.println(creator.toString());
        if (!compileResult.success) {
            // Should never happen
            throw new IllegalStateException("Cannot compile class: " +creator.toString());
        }
        //System.out.println(creator.toString());
        JUnitCore core=new JUnitCore();
        Class testClass=flyCompiler.loadClass(creator.getBinaryClassName());
        
        /*
        Class testClass=flyCompiler.loadClass(creator.getBinaryClassName());
        System.out.println("\nsanity check: "+
                flyCompiler.loadClass("org.junit.Test").equals(org.junit.Test.class)+"\n"); 

        System.out.println(testClass+" was loaded from " +getWhereClassWasLoaded(testClass));
        System.out.println(org.junit.Test.class+" was loaded from " +getWhereClassWasLoaded(org.junit.Test.class));
        System.out.println(org.junit.runner.JUnitCore.class+" was loaded from " +getWhereClassWasLoaded(org.junit.Test.class));
        System.out.println(junit.framework.TestCase.class+" was loaded from " +getWhereClassWasLoaded(org.junit.Test.class));

        System.out.println("Test was loaded by: " +org.junit.Test.class.getClassLoader());
        System.out.println(creator.getBinaryClassName()+" was loaded by: " +testClass.getClassLoader());
        System.out.println("Test case was loaded by: " +junit.framework.TestCase.class.getClassLoader());

        for (Method m : testClass.getDeclaredMethods()) {
            System.out.println(m);
            System.out.println("Do we have the right annotation? "+m.isAnnotationPresent(org.junit.Test.class));
        }
         */
        for (int i=0; i<creator.getNumTests(); i++) {
            //TODO Spawn each testcase in a separate thread using a threadpool
            //TODO capture stdout/stderr to improve on codingbat
            //TODO return a list of "junit outcomes"
            BeanShellTest test=creator.getTestNum(i);
            Result result=core.run(Request.method(testClass, test.getTestName()));
            //Result result=JUnitCore.runClasses(testClass);

            if (result.getFailureCount()>0) {
                // failed
                results.addTestResult(new TestResult(
                        false, 
                        test.inputAsString()+" "+result.getFailures().get(0).getMessage().toString()));
                System.out.println(result.getFailures().get(0).getTrace());
            } else {
                // succeeded
                results.addTestResult(new TestResult(true, 
                        test.inputAsString()+" "+test.correctOutputAsString()));
            }
        }
        return results;
    }

    private URL getWhereClassWasLoaded(Class cls){
        ProtectionDomain pDomain = cls.getProtectionDomain();
        CodeSource cSource = pDomain.getCodeSource();
        URL loc = cSource.getLocation();
        return loc;
    }
}
