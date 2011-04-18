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

package edu.ycp.cs.netcoder.server;

import java.util.List;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpSession;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import edu.ycp.cs.netcoder.client.LoadExerciseService;
import edu.ycp.cs.netcoder.server.util.HibernateUtil;
import edu.ycp.cs.netcoder.shared.logchange.Change;
import edu.ycp.cs.netcoder.shared.logchange.ChangeType;
import edu.ycp.cs.netcoder.shared.problems.Problem;
import edu.ycp.cs.netcoder.shared.problems.User;

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
    
    @Override
    public String loadCurrentText(int problemId) {
    	
    	HttpSession session = getThreadLocalRequest().getSession();
    	
    	User user = (User) session.getAttribute("user");
    	if (user == null) {
    		throw new IllegalArgumentException("Not logged in!");
    	}
    	
    	// Find the most recent full-text Change event.
    	// Assuming the user logged out successfully,
    	// it should be present and up to date.
        EntityManager eman = HibernateUtil.getManager();
        List<Change> result = eman.createQuery(
        		"select c from Change c, Event e " +
        		"where c.eventId = e.id " +
        		"  and e.id = (select max(ee.id) from Change cc, Event ee " +
        		"              where cc.eventId = ee.id " +
        		"                and ee.userId = :userId " +
        		"                and cc.type = " + ChangeType.FULL_TEXT.ordinal() + ")"
        		, Change.class)
        		.setParameter("userId", user.getId())
        		.getResultList();
        
        // FIXME: Need to make sure that the full-text is the latest Change!
        // If it isn't, then we need to recover the text based on the last full-text
        // change and any deltas that follow it.
        
        if (result.size() != 1) {
        	System.out.println("Could not find most recent full-text for user " + user.getId() + ", problem " + problemId);
        	return "";
        } else {
        	Change fullText = result.get(0);
        	return fullText.getText();
        }
    }
}
