package edu.ycp.cs.netcoder.server.compilers;

import java.util.List;

import javax.persistence.EntityManager;

import org.junit.Test;

import edu.ycp.cs.netcoder.server.problems.TestCreator;
import edu.ycp.cs.netcoder.server.problems.TestResult;
import edu.ycp.cs.netcoder.server.problems.TestRunner;
import edu.ycp.cs.netcoder.server.util.HibernateUtil;
import edu.ycp.cs.netcoder.shared.problems.Problem;


public class BeanTestDriver
{
    @Test
    public void testCreatorNoDatabase()
    throws Exception
    {
        TestCreator creator=new TestCreator("edu.ycp.cs.netcoder.junit.online", 
                "Run", 
                "sq",
                "public int sq(int x) { "+
                    "if (x==10) throw new RuntimeException(); "+    
                    "if (x<0) return 17; "+
                    "return x*x; " +
                    "}");
        
        creator.addTest("test1", "5", "25");
        creator.addTest("test2", "10", "100");
        creator.addTest("test3", "-1", "1");
        
        //System.out.println(creator.toString());
        
        TestRunner runner=new TestRunner();
        List<TestResult> results=runner.run(creator);
        for (TestResult t : results) {
            System.out.println(t);
        }
    }
    
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
                "public int sq(int x) { "+
                    "if (x==10) throw new RuntimeException(); "+    
                    "if (x<0) return 17; "+
                    "return x*x; " +
                    "}");
        
        creator.loadTestCasesFromDB(problem.getProblemId(), eman);
        
        TestRunner runner=new TestRunner();
        List<TestResult> results=runner.run(creator);
        for (TestResult t : results) {
            System.out.println(t);
        }
    }
}
