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

package edu.ycp.cs.netcoder.server.compilers;

import java.io.Serializable;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaFileObject;

public class CompileResult implements Serializable
{
    public static final long serialVersionUID=1;
    //TODO: May have to convert Diagnotics into something properly serializable
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
