package edu.ycp.cs.netcoder.server.compilers;

import java.util.List;

import javax.persistence.EntityManager;

import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.Request;
import org.junit.runner.Result;

import static org.junit.Assert.*;

import edu.ycp.cs.netcoder.server.problems.StudentSandboxSecurityManager;
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
        
        TestRunner runner=new TestRunner(new StudentSandboxSecurityManager());
        List<TestResult> results=runner.run(creator);
        return results.get(0);
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
        return results.get(0);
    }
//    
    
    @Test
    public void testFailedAssertion()
    throws Exception
    {
        //TestResult t=executeMultiThreaded("1", "1");
        TestResult t=executeSingleThreaded("1", "1");
        assertEquals(TestResult.FAILED_ASSERTION, t.getOutcome());
    }
    
    @Test
    public void testRuntimeException()
    throws Exception
    {
        TestResult t=executeMultiThreaded("2", "4");
        assertEquals(TestResult.FAILED_WITH_EXCEPTION, t.getOutcome());
    }
    
    @Test
    public void testPassed()
    throws Exception
    {
        TestResult t=executeMultiThreaded("12", "144");
        assertEquals(TestResult.PASSED, t.getOutcome());
    }
    
    @Test
    public void testTimeoutInfiniteLoop()
    throws Exception
    {
        TestResult t=executeMultiThreaded("3", "9");
        assertEquals(TestResult.FAILED_FROM_TIMEOUT, t.getOutcome());
    }
    
    @Test
    public void testSystemExit()
    throws Exception
    {
        TestResult t=executeMultiThreaded("4", "16");
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
    
//  @Test
//  public void testCreatorNoDatabase()
//  throws Exception
//  {
//      TestCreator creator=new TestCreator("edu.ycp.cs.netcoder.junit.online", 
//              "Run", 
//              "sq",
//              code
//              );
//
//      creator.addTest("test1", "1", "1");
//      creator.addTest("test2", "2", "2");
//      creator.addTest("test3", "3", "9");
//      creator.addTest("test4", "4", "16");
//      creator.addTest("test5", "5", "25");
//
//      //System.out.println(creator.toString());
//      
//      TestRunner runner=new TestRunner();
//      List<TestResult> results=runner.run(creator);
//      for (TestResult t : results) {
//          //TODO assertions for each test case
//          System.out.println(t.getStdout());
//      }
//  }
    
//    @Test
//    public void testStdoutWithThreads() {
//        ByteArrayOutputStream baos=new ByteArrayOutputStream();
//        PrintStream origOut=System.out;
//        
//        PrintStream fakeOut=new PrintStream(baos);
//        System.setOut(fakeOut);
//        
//        for (int i=0; i<5; i++) {
//            Thread t=new Thread(i+"") {
//                public void run() {
//                    for (int i=0; i<10; i++) {
//                        System.out.println(getName()+" "+i);
//                    }
//                }
//            };
//            t.start();
//        }
//        
//        Thread t3=new Thread("foo") {
//            public void run() {
//                try {
//                    Thread.sleep(1000);
//                } catch (InterruptedException e) {
//                    // Ignore
//                }
//                for (int i=0; i<10; i++) {
//                    System.out.println(getName()+" "+i);
//                }
//            }
//        };
//        t3.start();
//        
//        for (int i=0; i<5; i++) {
//            Thread t2=new Thread(i+"") {
//                public void run() {
//                    for (int i=0; i<10; i++) {
//                        System.out.println(getName()+" "+i);
//                    }
//                }
//            };
//            t2.start();
//        }
//        System.setOut(origOut);
//        fakeOut.flush();
//        fakeOut.close();
//        System.out.println(new String(baos.toByteArray()));
//        
//        try {
//            Thread.sleep(3000);
//        } catch (InterruptedException e) {
//            // ignore
//        }
//        System.out.println(new String(baos.toByteArray()));
//    }
}
