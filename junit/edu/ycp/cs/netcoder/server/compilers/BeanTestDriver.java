package edu.ycp.cs.netcoder.server.compilers;

import java.util.List;

import javax.persistence.EntityManager;

import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.Request;
import org.junit.runner.Result;

import static org.junit.Assert.*;

import edu.ycp.cs.netcoder.server.problems.StudentCodeSecurityManager;
import edu.ycp.cs.netcoder.server.problems.TestCreator;
import edu.ycp.cs.netcoder.server.problems.TestRunner;
import edu.ycp.cs.netcoder.server.util.HibernateUtil;
import edu.ycp.cs.netcoder.shared.problems.Problem;
import edu.ycp.cs.netcoder.shared.testing.TestResult;

public class BeanTestDriver
{
    private String printMsg(String msg) {
        return "System.out.println(\""+msg+"\");";
    }
    String code="public int sq(int x) { "+
    "if (x==1) {"+printMsg("return 17")+" return 17; }"+
    "if (x==2) {"+printMsg("throw exception")+" throw new RuntimeException(); }"+    
    "if (x==3) {"+printMsg("Infinite loop!")+" while (true);}"+
    "if (x==4) {"+printMsg("Try to spawn thread")+" new Thread() {public void run() {while(true);}}.start();}"+
    "if (x==5) {"+printMsg("Try to call System.exit(1)")+" System.exit(1);}"+
    "if (x==6) { return (int)Math.pow(x, 2); }"+
    "if (x==7) {System.setSecurityManager(null);}"+
    //"if (x==8) {StudentCodeSecurityManager sman=(StudentCodeSecurityManager)System.getSecurityManager(); sman.disableSandbox();}"+
    printMsg("Message in output")+
    "return x*x; " +
    "}";
    
    private TestResult executeMultiThreaded(String input, String output) 
    throws Exception
    {
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
        return result;
    }
    
    
    private TestResult executeSingleThreaded(String input, String output) 
    throws Exception
    {
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
    public void testStudentCodeCannotDisableSecurityManager()
    throws Exception
    {
        TestResult t=executeMultiThreaded("8", "64");
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
    public void testTimeoutInfiniteLoopMultiThreaded()
    throws Exception
    {
        TestResult t=executeMultiThreaded("3", "9");
        assertEquals(TestResult.FAILED_FROM_TIMEOUT, t.getOutcome());
    }
    
    @Test
    public void testThreadSpawnAttempLoopMultiThreaded()
    throws Exception
    {
        TestResult t=executeMultiThreaded("4", "16");
        assertEquals(TestResult.FAILED_BY_SECURITY_MANAGER, t.getOutcome());
    }
    
    @Test
    public void testSystemExitSingleThread()
    throws Exception
    {
        TestResult t=executeSingleThreaded("5", "25");
        assertEquals(TestResult.FAILED_BY_SECURITY_MANAGER, t.getOutcome());
    }
    
    @Test
    public void testSystemExit()
    throws Exception
    {
        TestResult t=executeMultiThreaded("5", "25");
        
        assertEquals(TestResult.FAILED_BY_SECURITY_MANAGER, t.getOutcome());
    }
    
    
    //@Test
    public void testJUnitCore()
    throws Exception
    {
        TestCreator creator=new TestCreator("edu.ycp.cs.netcoder.junit.online", 
                "Run", 
                "sq",
                code
                );
        creator.addTest("test2", "2", "2");
        
        JUnitCore core=new JUnitCore();
        
        OnTheFlyCompiler flyCompiler=new OnTheFlyCompiler();
        CompileResult compileResult=flyCompiler.compile(
                creator.getBinaryClassName(),
                creator.toJUnitTestCase());
        
        if (!compileResult.success) {
            throw new Exception("Can't compile");
        }
        
        Class testClass=flyCompiler.loadClass(creator.getBinaryClassName());
        
        Result result=core.run(Request.method(testClass, "test1"));
        
        System.out.println(result);
    }
    
    //@Test
    public void testCreatorWithDatabase()
    throws Exception
    {
        EntityManager eman=HibernateUtil.getManager();
        Problem problem=
            eman.createQuery("select p from Problem p where p.problemId = :id", 
                    Problem.class).setParameter("id", 1).getSingleResult();

        TestCreator creator=new TestCreator("edu.ycp.cs.netcoder.junit.online", 
                "Run"+System.currentTimeMillis(), 
                problem.getTestName(),
                code);
        
        creator.loadTestCasesFromDB(problem.getProblemId(), eman);
        TestRunner runner=new TestRunner();
        List<TestResult> results=runner.run(creator);
        for (TestResult t : results) {
            System.out.println(t);
        }
    }
    
    @Test
    public void testSecurityManagerThreadInteractions() throws Exception
    {
        SecurityManager orig=System.getSecurityManager();
        Thread t=new Thread() {
            public void run() {
                SecurityManager mine=System.getSecurityManager();
                StudentCodeSecurityManager.SandboxBooleanContainer container=new StudentCodeSecurityManager.SandboxBooleanContainer();
                StudentCodeSecurityManager sman=new StudentCodeSecurityManager(container);
                container.enableSandbox();
                try {
                    //while (true) if (2<1) break;
                    Thread.sleep(50000);
                } catch (InterruptedException e) {
                    System.out.println("Interrupted: "+e);
                } catch (ThreadDeath e) {
                    System.out.println(e);
                }
                //sman.disableSandbox();
                //System.setSecurityManager(mine);
            }
        };
        t.start();
        
        try {
            Thread.sleep(1000);
        } catch (Exception e) {
            // shouldn't happen
        }
        
        t.stop();
        SecurityManager current=System.getSecurityManager();
        System.out.println("My security manager is now: " +current);
        assertEquals(orig, current);
        
    }
}
