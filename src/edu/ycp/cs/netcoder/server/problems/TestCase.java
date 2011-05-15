// NetCoder - a web-based pedagogical programming environment
// Copyright (C) 2011, Jaime Spacco <jspacco@knox.edu>
// Copyright (C) 2011, David H. Hovemeyer <dhovemey@ycp.edu>
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
    
    public String inputAsString() {
        // TODO: handle array types
        return input.toString();
    }
    
    /**
     * Convert the test case into a String representing a JUnit test case
     * method.  Includes a "sandboxing" security manager that student code 
     * cannot disable.
     * TODO Ensure student code cannot perform a field access
     *      or use reflection to get at the container variable inside
     *      the security manager.
     * @param className
     * @param functionName
     * @return
     */
    String toJUnitTestCase(String className, String functionName) {
        return "@Test\n"+
        "public void "+getJUnitTestCaseName()+"() throws Exception {\n"+
        //"SecurityManager originalSecurityManager=System.getSecurityManager();\n"+
        //"StudentCodeSecurityManager.SandboxBooleanContainer container=new StudentCodeSecurityManager.SandboxBooleanContainer();\n"+
        //"StudentCodeSecurityManager sman=new StudentCodeSecurityManager(container);\n"+
        //"container.enableSandbox();\n"+
        //"System.setSecurityManager(sman);\n"+
        "try {\n"+
        className+" theInstance=new "+className+"();\n"+
            "assertEquals(\"input:<"+input+">\", "+
            this.correctOutput+", theInstance."+functionName+"("+this.input+"));\n"+
            "} finally {\n"+
            //"     container.disableSandbox();\n"+
            //"     System.setSecurityManager(originalSecurityManager);\n"+
            "}\n"+
            "}";
        
    }
    
    String toBeanShellTestCase(String functionName, String body) {
        return "@Test\n"+
        "public void "+getJUnitTestCaseName()+"() throws Exception {\n"+
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
