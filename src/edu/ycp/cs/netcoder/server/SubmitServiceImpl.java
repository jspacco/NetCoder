package edu.ycp.cs.netcoder.server;

import javax.tools.Diagnostic;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import edu.ycp.cs.netcoder.client.SubmitService;
import edu.ycp.cs.netcoder.server.compilers.CompileResult;
import edu.ycp.cs.netcoder.server.compilers.OnTheFlyCompiler;
import edu.ycp.cs.netcoder.server.compilers.TestCreator;
import edu.ycp.cs.netcoder.server.compilers.TestResultCollection;
import edu.ycp.cs.netcoder.server.compilers.TestRunner;

public class SubmitServiceImpl extends RemoteServiceServlet implements SubmitService {
    public static final long serialVersionUID=1L;
    
    /* (non-Javadoc)
     * @see edu.ycp.cs.netcoder.client.SubmitService#submit(java.lang.String, java.lang.String)
     */
    @Override
    public String submit(String problemId, String programText)
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
        //creator.addTest(3, 9);
        //creator.addTest(50, 250);
        //creator.addTest(10, 100);
        //creator.addTest(-1, 1);
        
        //System.out.println(creator.toString());
        
        TestRunner runner=new TestRunner();
        try {
            TestResultCollection results=runner.run(creator);
            return results.toString().replaceAll("\n", "<br>\n");
        } catch (ClassNotFoundException e) {
            return e.toString();
        }
        // TODO pass/return results to test results panel to update display
    }
}