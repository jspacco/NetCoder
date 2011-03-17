package edu.ycp.cs.netcoder.server.compilers;

import org.junit.runner.JUnitCore;
import org.junit.runner.Request;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;


public class BeanTestDriver
{
    public static void main(String[] args)
    {
        TestCreator creator=new TestCreator("RunTest", 
                "sq", 
                "public int sq(int x) { "+
                    "if (x==10) throw new RuntimeException(); "+    
                    "if (x<0) return 17; "+
                    "return x*x; " +
                    "}");
        creator.addTest(5, 25);
        creator.addTest(3, 9);
        creator.addTest(10, 100);
        creator.addTest(-1, 1);
        
        //System.out.println(creator.toString());
        
        TestRunner runner=new TestRunner(creator);
        runner.run();
    }
}
