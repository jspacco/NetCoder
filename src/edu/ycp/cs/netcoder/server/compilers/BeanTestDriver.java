package edu.ycp.cs.netcoder.server.compilers;

import java.util.List;


public class BeanTestDriver
{
    public static void main(String[] args)
    throws Exception
    {
        TestCreator creator=new TestCreator("edu.ycp.cs.netcoder.junit.online", "Run", 
                "sq", 
                "public int sq(int x) { "+
                    "if (x==10) throw new RuntimeException(); "+    
                    "if (x<0) return 17; "+
                    //"if (x==9) while(true); "+
                    "return x*x; " +
                    "}");
        creator.addTest(5, 25);
        creator.addTest(3, 9);
        creator.addTest(9, 81);
        creator.addTest(10, 100);
        creator.addTest(-1, 1);
        
        //System.out.println(creator.toString());
        
        TestRunner runner=new TestRunner();
        List<TestResult> results=runner.run(creator);
        for (TestResult t : results) {
            System.out.println(t);
        }
    }
}
