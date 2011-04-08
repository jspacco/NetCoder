// NetCoder - a web-based pedagogical idea
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

package edu.ycp.cs.netcoder.server.problems;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

public class TestCreator
{
    private String functionName;
    private String body;
    private String packageName;
    private String className;
    private List<TestCase> tests=new ArrayList<TestCase>();
    
    public TestCreator(String packageName, String className, String functionName, String body) {
        this.className=className;
        this.functionName=functionName;
        this.body=body;
    }
    
    public void addTest(String testCaseName, String in, String out) {
        tests.add(new TestCase(testCaseName, in, out));
    }
    
    TestCase getTestNum(int i) {
        return tests.get(i);
    }
    
    public String toJUnitTestCase() {
        StringBuffer buf=new StringBuffer();
        if (packageName!=null) {
            buf.append("package "+packageName+";");
        }
        buf.append("import org.junit.Test;\n");
        buf.append("import org.junit.runner.JUnitCore;\n");
        buf.append("import static org.junit.Assert.assertEquals;\n");
        
        buf.append("public class "+className+" {\n");
        //TODO: Create inner class for the 
        String classToBeTested="StudentTest";
        
        buf.append("private static class "+classToBeTested+" {\n");
        buf.append(body+"\n");
        buf.append("}\n");
        for (TestCase t : tests) {
            buf.append("@Test\n");
            buf.append(t.toJUnitTestCase(classToBeTested, functionName)+"\n");
        }
        buf.append("}\n");
        
        return buf.toString();
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
        
        for (TestCase t : tests) {
            buf.append("@Test\n");
            buf.append(t.toBeanShellTestCase(functionName, body)+"\n");
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

    public void loadTestCasesFromDB(Integer problemId, EntityManager eman) {
        List<TestCase> testCases=eman.createQuery(
                " select t from TestCase t where t.problemId = :problemId ", 
                TestCase.class).
                setParameter("problemId", problemId).getResultList();
        this.tests.addAll(testCases);
    }
}
