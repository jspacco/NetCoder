package edu.ycp.cs.netcoder.server.compilers;

import java.util.List;

import javax.persistence.EntityManager;

import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.Request;
import org.junit.runner.Result;

import edu.ycp.cs.netcoder.server.problems.TestCreator;
import edu.ycp.cs.netcoder.server.problems.TestRunner;
import edu.ycp.cs.netcoder.server.util.HibernateUtil;
import edu.ycp.cs.netcoder.shared.problems.Problem;
import edu.ycp.cs.netcoder.shared.testing.TestResult;

public class OtherTestCases
{
    public static String printMsg(String msg) {
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
    printMsg("Message in output")+
    "return x*x; " +
    "}";
    
    @Test
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

        Class<?> testClass=flyCompiler.loadClass(creator.getBinaryClassName());

        Result result=core.run(Request.method(testClass, "test1"));

        System.out.println(result);
    }
    //@Test

//    public void testSecurityManagerThreadInteractions() throws Exception
//    {
//        SecurityManager orig=System.getSecurityManager();
//        final SandboxBooleanContainer[] containers=new SandboxBooleanContainer[5];
//        for (int i=0; i<containers.length; i++) {
//            containers[i]=new SandboxBooleanContainer();
//        }
//        //final StudentCodeSecurityManager.SandboxBooleanContainer container=new StudentCodeSecurityManager.SandboxBooleanContainer();
//        Thread[] pool=new Thread[5];
//        for (int i=0; i<pool.length; i++) {
//            final int j=i;
//            pool[i]=new Thread() {
//                public void run() {
//                    try {
//                        method(containers[j]);
//                    } catch (ThreadDeath e) {
//                        System.out.println("Thread death, not where I want it..." +e);
//                    }
//
//                }
//            };
//            pool[i].start();
//        }
//        System.out.println("Security manager before start(): " +System.getSecurityManager());
//
//        try {
//            Thread.sleep(500);
//        } catch (Exception e) {
//            // shouldn't happen
//        }
//
//        System.out.println("My security manager before: " +System.getSecurityManager());
//
//        containers[0].disableSandbox();
//
//        pool[1].stop();
//
//        SecurityManager current=System.getSecurityManager();
//        System.out.println("My security manager is now: " +current);
//        assertEquals(orig, current);
//
//    }
}
