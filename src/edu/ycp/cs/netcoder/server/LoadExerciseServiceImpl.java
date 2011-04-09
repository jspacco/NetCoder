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

package edu.ycp.cs.netcoder.server;

import java.util.List;

import javax.persistence.EntityManager;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import edu.ycp.cs.netcoder.client.LoadExerciseService;
import edu.ycp.cs.netcoder.server.util.HibernateUtil;
import edu.ycp.cs.netcoder.shared.problems.Problem;

public class LoadExerciseServiceImpl extends RemoteServiceServlet implements LoadExerciseService 
{
    private static final long serialVersionUID = 1L;
    
    public Problem load(int problemId) {
//        if (problemId<=0) {
//            return "Cannot find problem with id "+problemId;
//        }
        EntityManager eman=HibernateUtil.getManager();
        List<Problem> problems=eman.createQuery("select p from Problem p where p.id = :id", 
                Problem.class).setParameter("id", problemId).
                getResultList();
        if (problems.size()==0) {
            return null;//"Cannot find problem with id "+problemId;
        }
        return problems.get(0); //.getDescription();
    }
}
