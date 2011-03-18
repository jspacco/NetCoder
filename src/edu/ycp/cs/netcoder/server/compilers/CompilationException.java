package edu.ycp.cs.netcoder.server.compilers;

public class CompilationException extends Exception
{
    private CompileResult compileResult;
    public CompilationException(CompileResult result) {
        super();
        this.compileResult=result;
    }
    
    public CompileResult getCompileResult() {
        return compileResult;
    }
}
