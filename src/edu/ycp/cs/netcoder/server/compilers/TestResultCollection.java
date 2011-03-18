package edu.ycp.cs.netcoder.server.compilers;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import javax.tools.Diagnostic;

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
