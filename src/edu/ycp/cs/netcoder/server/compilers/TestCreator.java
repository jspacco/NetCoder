package edu.ycp.cs.netcoder.server.compilers;

import java.util.ArrayList;
import java.util.List;

public class TestCreator
{
    private String functionName;
    private String body;
    private String packageName;
    private String className;
    private int numTests=0;
    private List<BeanShellTest> tests=new ArrayList<BeanShellTest>();
    
    public TestCreator(String packageName, String className, String functionName, String body) {
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
        if (packageName!=null) {
            buf.append("package "+packageName+";");
        }
        buf.append("import org.junit.Test;\n");
        buf.append("import org.junit.runner.JUnitCore;\n");
        //buf.append("import junit.framework.TestCase;\n");
        buf.append("import bsh.Interpreter;\n");
        buf.append("import static org.junit.Assert.assertEquals;\n");
        buf.append("import bsh.EvalError;\n");
        buf.append("public class "+className+" {\n");
        
        for (BeanShellTest t : tests) {
            buf.append("@Test\n");
            buf.append(t.toTestCase()+"\n");
        }

        buf.append("}\n");
        
        return buf.toString();
    }
    
    public String toClass() {
        StringBuffer buf=new StringBuffer();
        if (packageName!=null) {
            buf.append("package "+packageName+";");
        }
        buf.append("public class "+className+" {\n");
        buf.append(body);
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
    public String getBinaryClassName(){
        if (packageName!=null) {
            return packageName+"."+className;
        }
        return className;
    }

    class BeanShellTest {
        Object input;
        Object correctOutput;
        String testName;
        
        
        BeanShellTest(Object input, Object correctOutput, int testNum) {
            this.input=input;
            this.correctOutput=correctOutput;
            this.testName="test"+testNum;
        }
        
        String getTestName() {
            return testName;
        }
        
        String inputAsString() {
            // TODO: handle array types
            return "\"input:<"+input+">\"";
        }
        String correctOutputAsString()
        {
            return "\"correct:<"+correctOutput+">\"";
        }
        
        String toTestCase() {
            return "public void "+testName+"() throws Exception {\n"+
            "Interpreter bsh=new Interpreter();\n"+
                "assertEquals("+inputAsString()+", "+
                this.correctOutput+", bsh.eval(\""+body+"; "+
                    functionName+"("+this.input+")\"));\n}";
        }
    }
}
