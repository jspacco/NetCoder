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