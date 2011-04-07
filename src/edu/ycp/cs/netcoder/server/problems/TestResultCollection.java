package edu.ycp.cs.netcoder.server.problems;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import javax.tools.Diagnostic;

import edu.ycp.cs.netcoder.server.compilers.CompileResult;
import edu.ycp.cs.netcoder.shared.testing.TestResult;

public class TestResultCollection implements Serializable
{
    public static final long serialVersionUID=1;
    private CompileResult compileResult;
    private List<TestResult> results=new LinkedList<TestResult>();
    
    public TestResultCollection(CompileResult result) {
        this.compileResult=result;
    }
    
    public synchronized void addTestResult(TestResult result) {
        this.results.add(result);
    }
    public void addAll(List<TestResult> outcomes){
        this.results.addAll(outcomes);
    }
    
    public String toString() {
        if (!compileResult.success) {
            StringBuffer buf=new StringBuffer();
            for (Diagnostic diagnostic : compileResult.diagnostics.getDiagnostics()) {
                buf.append(diagnostic.getKind()+": "+diagnostic.getMessage(null)+"\n");
                /*
                System.out.println("code: "+diagnostic.getCode());
                System.out.println("kind: "+diagnostic.getKind());
                System.out.println("pos: "+diagnostic.getPosition());
                System.out.println("startpos: "+diagnostic.getStartPosition());
                System.out.println("endpos: "+diagnostic.getEndPosition());
                System.out.println("source: "+diagnostic.getSource());
                System.out.println("message: "+diagnostic.getMessage(null));
                 */
            }
            return buf.toString();
        }
        StringBuffer buf=new StringBuffer();
        for (TestResult r : results) {
            buf.append(r+"\n");
        }
        return buf.toString();
    }
}
