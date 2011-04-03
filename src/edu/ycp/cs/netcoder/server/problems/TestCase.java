package edu.ycp.cs.netcoder.server.problems;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="test_cases")
public class TestCase
{
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    @Column(name="test_case_id")
    private int testCaseId;
    @Column(name="problem_id")
    private int problemId;
    @Column(name="test_case_name")
    private String testCaseName;
    @Column(name="input")
    private String input;
    @Column(name="output")
    private String correctOutput;
    
    
    
    // for Hibernate
    public TestCase() {}
    
    public TestCase(String testCaseName, String in, String out) {
        this.testCaseName=testCaseName;
        this.input=in;
        this.correctOutput=out;
    }
    
    public String getJUnitTestCaseName() {
        return "test"+getTestCaseName();
    }
    
    String inputAsString() {
        // TODO: handle array types
        return input.toString();
    }
    
    String toJUnitTestCase(String className, String functionName) {
        return "public void "+getJUnitTestCaseName()+"() throws Exception {\n"+
        className+" theInstance=new "+className+"();\n"+
            "assertEquals(\"input:<"+input+">\", "+
            this.correctOutput+", theInstance."+functionName+"("+this.input+"));\n}";
    }
    
    String toBeanShellTestCase(String functionName, String body) {
        return "public void "+getJUnitTestCaseName()+"() throws Exception {\n"+
        "Interpreter bsh=new Interpreter();\n"+
            "assertEquals(\"input:<"+input+">\", "+
            this.correctOutput+", bsh.eval(\""+body+"; "+
                functionName+"("+this.input+")\"));\n}";
    }
    /**
     * @return the problemId
     */
    public int getProblemId() {
        return problemId;
    }
    /**
     * @param problemId the problemId to set
     */
    public void setProblemId(int problemId) {
        this.problemId = problemId;
    }
    /**
     * @return the testCaseName
     */
    public String getTestCaseName() {
        return testCaseName;
    }
    /**
     * @param testCaseName the testCaseName to set
     */
    public void setTestCaseName(String testCaseName) {
        this.testCaseName = testCaseName;
    }
    /**
     * @param input the input to set
     */
    public void setInput(String input) {
        this.input = input;
    }
    /**
     * @param correctOutput the correctOutput to set
     */
    public void setCorrectOutput(String correctOutput) {
        this.correctOutput = correctOutput;
    }
    /**
     * @return the input
     */
    public String getInput() {
        return input;
    }
    /**
     * @return the correctOutput
     */
    public String getCorrectOutput() {
        return correctOutput;
    }

    /**
     * @return the testCaseId
     */
    public int getTestCaseId() {
        return testCaseId;
    }

    /**
     * @param testCaseId the testCaseId to set
     */
    public void setTestCaseId(int testCaseId) {
        this.testCaseId = testCaseId;
    }
}
