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

public class TestResult implements Serializable
{
    public static final long serialVersionUID=1L;
    //TODO: store outcomes (pass, fail, timeout)?
    
    public final boolean success;
    public final String message;
    
    public TestResult(boolean success, String message) {
        this.success=success;
        this.message=message;
    }
    
    public String toString() {
        return message;
    }
}
