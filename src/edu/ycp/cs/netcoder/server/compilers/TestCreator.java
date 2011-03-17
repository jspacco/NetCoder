package edu.ycp.cs.netcoder.server.compilers;

import java.util.ArrayList;
import java.util.List;

import org.junit.runner.notification.Failure;

public class TestCreator
{
    private String functionName;
    private String body;
    private String className;
    private int numTests=0;
    private List<BeanShellTest> tests=new ArrayList<BeanShellTest>();
    
    public TestCreator(String className, String functionName, String body) {
        this.className=className;
        this.functionName=functionName;
        this.body=body;
    }
    
    public void addTest(Object input, Object output) {
        tests.add(new BeanShellTest(input, output, numTests));
        numTests++;
    }
    
    BeanShellTest getTestNum(int i) {
        return tests.get(i);
    }
    
    public String toString() {
        StringBuffer buf=new StringBuffer();
        buf.append("import org.junit.Test;\n");
        buf.append("import bsh.Interpreter;\n");
        buf.append("import static org.junit.Assert.assertEquals;\n");
        buf.append("import bsh.EvalError;\n");
        buf.append("public class "+className+" {\n");
        
        for (BeanShellTest t : tests) {
            buf.append("@Test\n");
            buf.append(t.toTestCase()+"\n");
        }
        buf.append("public static void main(String[] args) {\n");
        buf.append("org.junit.runner.JUnitCore.runClasses("+className+".class);\n");
        buf.append("}\n");

        buf.append("}\n");
        
        return buf.toString();
    }
    
    public int getNumTests() {
        return tests.size();
    }
    /**
     * @return the functionName
     */
    public String getFunctionName(){
        return functionName;
    }
    /**
     * @return the body
     */
    public String getBody(){
        return body;
    }
    /**
     * @return the className
     */
    public String getClassName(){
        return className;
    }

    class BeanShellTest {
        Object input;
        Object correctOutput;
        String testName;
        //XXX: store test results directly in with the inputs/outputs?
        //would need to know if the test executed, if it succeeded, 
        // and what message it got.
        boolean hasExecuted=false;
        Boolean success;
        Failure failure;
        
        
        BeanShellTest(Object input, Object correctOutput, int testNum) {
            this.input=input;
            this.correctOutput=correctOutput;
            this.testName="test"+testNum;
        }
        
        void setSuccess() {
            hasExecuted=true;
            success=true;
        }
        
        void setFailure(Failure failure) {
            hasExecuted=true;
            this.success=false;
            this.failure=failure;
        }
        
        public String toString() {
            //TODO: pretty-printing
            if (!hasExecuted) {
                return "Test case has not been executed";
            }
            if (success) {
                return getTestName()+" passed";
            }
            if (failure.getException()!=null) {
                return failure.getException().toString();
            }
            return failure.toString();
        }
        
        String getTestName() {
            return testName;
        }
        
        String inputAsString() {
            // TODO: handle array types
            return "\"input:<"+input+">\"";
        }
        
        String toTestCase() {
            return "public void "+testName+"() throws bsh.EvalError {\n"+
            "Interpreter bsh=new Interpreter();\n"+
                "assertEquals("+inputAsString()+", "+
                this.correctOutput+", bsh.eval(\""+body+"; "+
                    functionName+"("+this.input+")\"));\n}";
        }
    }
}
