// NetCoder - a web-based pedagogical programming environment
// Copyright (C) 2011, Jaime Spacco
// Copyright (C) 2011, David H. Hovemeyer
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU Affero General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU Affero General Public License for more details.
//
// You should have received a copy of the GNU Affero General Public License
// along with this program.  If not, see <http://www.gnu.org/licenses/>.

package edu.ycp.cs.netcoder.server;

import java.util.List;

import javax.persistence.EntityManager;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import edu.ycp.cs.netcoder.client.SubmitService;
import edu.ycp.cs.netcoder.server.compilers.CompilationException;
import edu.ycp.cs.netcoder.server.compilers.CompileResult;
import edu.ycp.cs.netcoder.server.compilers.OnTheFlyCompiler;
import edu.ycp.cs.netcoder.server.problems.Problem;
import edu.ycp.cs.netcoder.server.problems.TestCreator;
import edu.ycp.cs.netcoder.server.problems.TestRunner;
import edu.ycp.cs.netcoder.server.util.HibernateUtil;
import edu.ycp.cs.netcoder.shared.testing.TestResult;

public class SubmitServiceImpl extends RemoteServiceServlet implements SubmitService {
    public static final long serialVersionUID=1L;
    
    /* (non-Javadoc)
     * @see edu.ycp.cs.netcoder.client.SubmitService#submit(java.lang.String, java.lang.String)
     */
    @Override
    public TestResult[] submit(int problemId, String programText)
    {
        // TODO return type should be either a CompileResult or TestResults
        // TODO use problemID to look up the problem in filesystem/DB
        System.out.println("problemId: " +problemId);
        EntityManager eman=HibernateUtil.getManager();
        Problem problem=eman.createQuery("select p from Problem p where p.id = :id", 
                Problem.class).setParameter("id", problemId).
                getSingleResult();
        if (problem==null) {
            return TestResult.error("Cannot find problem with id "+problemId);
        }
        TestCreator creator=new TestCreator("edu.ycp.cs.netcoder.server.junit.online",
                "RunDude", 
                problem.getTestName(),
                programText.replace("\n", " "));
        
        // Make sure the code snippet compiles
        OnTheFlyCompiler compiler=new OnTheFlyCompiler();
        CompileResult result=compiler.compile(creator.getBinaryClassName(), creator.toClass());
        if (!result.success) {
            // May be necessary to convert into a better error message
            // So that we can give feedback to the user
            return TestResult.error(result.toString());
        }
        
        System.out.println("programText: "+programText);
        // Load tests out of DB
        creator.loadTestCasesFromDB(problem.getProblemId(), eman);
        
        //System.out.println(creator.toString());
        
        TestRunner runner=new TestRunner();
        try {
            List<TestResult> results=runner.run(creator);
            return results.toArray(new TestResult[results.size()]);
            /*
            StringBuffer buf=new StringBuffer();
            for (TestResult r : results) {
                buf.append(r+"<br>\n");
            }
            return buf.toString();
            */
        } catch (CompilationException e) {
            return TestResult.error(e.getCompileResult().toString());
        } catch (ClassNotFoundException e) {
            return TestResult.error(e.toString());
        } 
        // TODO pass/return results to test results panel to update display
    }
}