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
import edu.ycp.cs.netcoder.server.logchange.ApplyChangeToTextDocument;
import edu.ycp.cs.netcoder.server.logchange.TextDocument;
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

    	String text = doLoadCurrentText(problemId, session);
    	
    	// FIXME: this is only necessary because (for debugging purposes) LogCodeChangeServiceImpl expects to have the full document
    	TextDocument doc = new TextDocument();
    	doc.setText(text);
    	session.setAttribute("doc", doc);
    	
    	return text;
    }

	protected String doLoadCurrentText(int problemId, HttpSession session) {
		// Make sure user is authenticated
    	User user = (User) session.getAttribute("user");
    	if (user == null) {
    		throw new IllegalArgumentException("Not logged in!");
    	}

    	List<Change> result;
    	
    	// Find the most recent Change event for user on this problem.
    	EntityManager eman = HibernateUtil.getManager();
    	result = eman.createQuery(
    			"select c from Change c, Event e " +
    			"where c.eventId = e.id " +
    			"  and e.id = (select max(ee.id) from Change cc, Event ee " +
    			"              where cc.eventId = ee.id " +
    			"                and ee.userId = :userId " + ")"
    			, Change.class)
    			.setParameter("userId", user.getId())
    			.getResultList();


    	if (result.size() != 1) {
    		// Presumably, user has never worked on this problem.
    		System.out.println("No changes recorded for user " + user.getId() + ", problem " + problemId);
    		return "";
    	} else {
    		Change change = result.get(0);

    		// If the Change is a full text change, great.
    		if (change.getType() == ChangeType.FULL_TEXT) {
    			return change.getText();
    		}

    		// Otherwise, find the last full-text change (if any) and
    		// apply all later changes.
    		
    		// Find the most recent full-text change.
    		result = eman.createQuery(
        			"select c from Change c, Event e " +
        			"where c.eventId = e.id " +
        			"  and e.id = (select max(ee.id) from Change cc, Event ee " +
        			"              where cc.eventId = ee.id " +
        			"                and ee.userId = :userId " +
                	"                and cc.type = " + ChangeType.FULL_TEXT.ordinal() + ")"
        			, Change.class)
        			.setParameter("userId", user.getId())
        			.getResultList();
    		
    		// Text doc to accumulate changes.
    		TextDocument textDocument = new TextDocument();
    		
    		// Find the base revision (event id) that the deltas are relative to, if any.
    		int baseRev;
    		Change fullText = null;
    		if (result.size() == 1) {
    			// found a full-text change to use as a base revision
    			fullText = result.get(0);
    			textDocument.setText(fullText.getText());
    			baseRev = fullText.getEventId();
    		} else {
    			// no full-text change exists: base revision is implicitly the empty document
    			baseRev = -1;
    		}
    		
    		// Get all deltas that follow the base revision.
    		result = eman.createQuery(
    				"select c from Change c, Event e " +
    				"where c.eventId = e.id " + 
    				"  and e.id > :baseRev " +
    				"order by e.id asc",
    				Change.class)
    				.setParameter("baseRev", baseRev)
    				.getResultList();
    		
    		// Apply the deltas to the base revision.
    		try {
        		ApplyChangeToTextDocument applicator = new ApplyChangeToTextDocument();
	    		for (Change delta : result) {
	    			applicator.apply(delta, textDocument);
	    		}
	    		return textDocument.getText();
    		} catch (RuntimeException e) {
    			// FIXME: should do something smarter than this 
    			return fullText != null ? fullText.getText() : "";
    		}
    	}
	}
}
