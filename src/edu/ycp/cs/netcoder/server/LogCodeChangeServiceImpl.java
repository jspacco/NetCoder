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

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import edu.ycp.cs.netcoder.client.LogCodeChangeService;
import edu.ycp.cs.netcoder.server.logchange.ApplyChangeToTextDocument;
import edu.ycp.cs.netcoder.server.logchange.TextDocument;
import edu.ycp.cs.netcoder.server.util.HibernateUtil;
import edu.ycp.cs.netcoder.shared.event.Event;
import edu.ycp.cs.netcoder.shared.logchange.Change;

public class LogCodeChangeServiceImpl extends RemoteServiceServlet implements LogCodeChangeService {
	private static final long serialVersionUID = 1L;

	@Override
	public Boolean logChange(Change[] changeList) {
		HttpServletRequest req = this.getThreadLocalRequest();
		HttpSession session = req.getSession();
		
		TextDocument doc = (TextDocument) session.getAttribute("doc");
		if (doc == null) {
			doc = new TextDocument();
			session.setAttribute("doc", doc);
		}

		EntityManager eman=HibernateUtil.getManager();
		
		ApplyChangeToTextDocument applicator = new ApplyChangeToTextDocument();
		eman.getTransaction().begin();
		for (Change change : changeList) {
			applicator.apply(change, doc);
			
			// Insert the generic Event object
			Event event = change.getEvent();
			eman.persist(event);
			
			// Link the Change object to the Event, and insert it
			change.setEventId(event.getId());
			eman.persist(change);
		}
		eman.getTransaction().commit();
		System.out.println("Document is now:\n" + doc.getText());
		
		return true;
	}
}
