package edu.ycp.cs.netcoder.server.compilers;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.List;

import org.junit.Test;

import edu.ycp.cs.netcoder.server.problems.StudentCodeSecurityManager;
import org.junit.runner.JUnitCore;
import org.junit.runner.Request;
import org.junit.runner.Result;

import edu.ycp.cs.netcoder.server.problems.TestCreator;
import edu.ycp.cs.netcoder.server.problems.TestRunner;
import edu.ycp.cs.netcoder.shared.testing.TestResult;

public class BeanTestDriver
{
    public static String printMsg(String msg) {
        return "System.out.println(\""+msg+"\");";
    }
    static String code="public int sq(int x) { "+
    "if (x==1) {"+printMsg("return 17")+" return 17; }"+
    "if (x==2) {"+printMsg("throw exception")+" throw new RuntimeException(); }"+    
    "if (x==3) {"+printMsg("Infinite loop!")+" while (true);}"+
    "if (x==4) {"+printMsg("Try to spawn thread")+" new Thread() {public void run() {while(true);}}.start();}"+
    "if (x==5) {"+printMsg("Try to call System.exit(1)")+" System.exit(1);}"+
    "if (x==6) { return (int)Math.pow(x, 2); }"+
    "if (x==7) {System.setSecurityManager(null);}"+
    printMsg("Message in output")+
    "return x*x; " +
    "}";

    private static TestResult executeMultiThreaded(String input, String output) 
    throws Exception
    {
        // Set the security manager
        StudentCodeSecurityManager sman=new StudentCodeSecurityManager();
        System.setSecurityManager(sman);
        try {
            TestCreator creator=new TestCreator("edu.ycp.cs.netcoder.junit.online", 
                    "Run", 
                    "sq",
                    code
            );
            creator.addTest("test1", input, output);

            TestRunner runner=new TestRunner();
            List<TestResult> results=runner.run(creator);
            TestResult result=results.get(0);
            System.out.println(result.getStdout());
            System.err.println(result.getStderr());
            // Undo security manager
            System.setSecurityManager(null);
            return result;
        } finally {
            System.setSecurityManager(null);
        }
    }


    private static TestResult executeSingleThreaded(String input, String output) 
    throws Exception
    {
        StudentCodeSecurityManager sman=new StudentCodeSecurityManager();
        System.setSecurityManager(sman);
        try {
            TestCreator creator=new TestCreator("edu.ycp.cs.netcoder.junit.online", 
                    "Run", 
                    "sq",
                    code
            );
            creator.addTest("test1", input, output);

            TestRunner runner=new TestRunner();
            List<TestResult> results=runner.runInSingleThread(creator);
            TestResult result=results.get(0);
            System.out.println(result.getStdout());
            System.err.println(result.getStderr());
            return result;
        } finally {
            System.setSecurityManager(null);
        }
    }

    @Test
    public void testFailedAssertionMultiThread()
    throws Exception
    {
        TestResult t=executeMultiThreaded("1", "1");
        assertEquals(TestResult.FAILED_ASSERTION, t.getOutcome());
    }

    @Test
    public void testFailedAssertionSingelThread()
    throws Exception
    {
        TestResult t=executeSingleThreaded("1", "1");
        assertEquals(TestResult.FAILED_ASSERTION, t.getOutcome());
    }

    @Test
    public void testRuntimeExceptionSingleThread()
    throws Exception
    {
        TestResult t=executeSingleThreaded("2", "4");
        assertEquals(TestResult.FAILED_WITH_EXCEPTION, t.getOutcome());
    }

    @Test
    public void testRuntimeExceptionMultiThreaded()
    throws Exception
    {
        TestResult t=executeMultiThreaded("2", "4");
        assertEquals(TestResult.FAILED_WITH_EXCEPTION, t.getOutcome());
    }

    @Test
    public void testStudentCodeCannotSetSecurityManager()
    throws Exception
    {
        TestResult t=executeMultiThreaded("7", "49");
        assertEquals(TestResult.FAILED_BY_SECURITY_MANAGER, t.getOutcome());
    }

    @Test
    public void testPassedSingleThread()
    throws Exception
    {
        TestResult t=executeSingleThreaded("12", "144");
        assertEquals(TestResult.PASSED, t.getOutcome());
    }

    @Test
    public void testPassedMultiThread()
    throws Exception
    {
        TestResult t=executeMultiThreaded("12", "144");
        assertEquals(TestResult.PASSED, t.getOutcome());
    }

    @Test
    public void testThreadSpawnAttempLoopMultiThreaded()
    throws Exception
    {
        TestResult t=executeMultiThreaded("4", "16");
        assertEquals(TestResult.FAILED_BY_SECURITY_MANAGER, t.getOutcome());
    }

    
//    //Cannot actually test this!
//    //SecurityManager blocks actions based on the thread group of
//    //the currently executing thread.
//    @Test
//    public void testSystemExitSingleThread()
//    throws Exception
//    {
//        TestResult t=executeSingleThreaded("5", "25");
//        assertEquals(TestResult.FAILED_BY_SECURITY_MANAGER, t.getOutcome());
//    }

    @Test
    public void testSystemExitThreaded()
    throws Exception
    {
        TestResult t=executeMultiThreaded("5", "25");

        assertEquals(TestResult.FAILED_BY_SECURITY_MANAGER, t.getOutcome());
    }


    @Test
    public void testTimeoutInfiniteLoopMultiThreaded()
    throws Exception
    {
        TestResult t=executeMultiThreaded("3", "9");
        assertEquals(TestResult.FAILED_FROM_TIMEOUT, t.getOutcome());
    }
    
    public static void main(String[] args) throws Exception
    {
        //BeanTestDriver d=new BeanTestDriver();
        //d.testSystemExitSingleThread();
        //System.out.println("OK");
    }
}
