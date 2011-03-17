package edu.ycp.cs.netcoder.server.compilers;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaFileObject;

public class CompileResult
{
    public final DiagnosticCollector<JavaFileObject> diagnostics;
    public final boolean success;
    
    public CompileResult(boolean success,
            DiagnosticCollector<JavaFileObject> diagnostics)
    {
        this.diagnostics=diagnostics;
        this.success=success;
    }
    
    public String toString() {
        if (success) {
            return "success";
        }
        StringBuffer buf=new StringBuffer();
        buf.append("failure\n");
        for (Diagnostic diagnostic : diagnostics.getDiagnostics()) {
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
}
