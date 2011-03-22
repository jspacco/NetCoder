package edu.ycp.cs.netcoder.server;

import java.util.List;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import edu.ycp.cs.netcoder.client.SubmitService;
import edu.ycp.cs.netcoder.server.compilers.CompilationException;
import edu.ycp.cs.netcoder.server.compilers.CompileResult;
import edu.ycp.cs.netcoder.server.compilers.OnTheFlyCompiler;
import edu.ycp.cs.netcoder.server.compilers.TestCreator;
import edu.ycp.cs.netcoder.server.compilers.TestResult;
import edu.ycp.cs.netcoder.server.compilers.TestRunner;

public class SubmitServiceImpl extends RemoteServiceServlet implements SubmitService {
    public static final long serialVersionUID=1L;
    
    /* (non-Javadoc)
     * @see edu.ycp.cs.netcoder.client.SubmitService#submit(java.lang.String, java.lang.String)
     */
    @Override
    public String submit(int problemId, String programText)
    {
        // TODO return type should be either a CompileResult or TestResults
        // TODO use problemID to look up the problem in filesystem/DB
        System.out.println("problemId: " +problemId);
        TestCreator creator=new TestCreator("edu.ycp.cs.netcoder.server.junit.online",
                "RunDude", 
                "sq", 
                programText.replace("\n", " "));
        
        // Make sure the code snippet compiles
        OnTheFlyCompiler compiler=new OnTheFlyCompiler();
        CompileResult result=compiler.compile(creator.getBinaryClassName(), creator.toClass());
        if (!result.success) {
            // May be necessary to convert into a better error message
            return result.toString();
        }
        
        System.out.println("programText: "+programText);
        creator.addTest(5, 25);
        creator.addTest(9, 81);
        creator.addTest(10, 100);
        creator.addTest(-1, 1);
        
        //System.out.println(creator.toString());
        
        TestRunner runner=new TestRunner();
        try {
            List<TestResult> results=runner.run(creator);
            StringBuffer buf=new StringBuffer();
            for (TestResult r : results) {
                buf.append(r+"<br>\n");
            }
            return buf.toString();
        } catch (CompilationException e) {
            return e.getCompileResult().toString();
        } catch (ClassNotFoundException e) {
            return e.toString();
        } 
        // TODO pass/return results to test results panel to update display
    }
}