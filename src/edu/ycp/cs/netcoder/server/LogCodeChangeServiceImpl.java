package edu.ycp.cs.netcoder.server;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import edu.ycp.cs.netcoder.client.LogCodeChangeService;
import edu.ycp.cs.netcoder.server.logchange.ApplyChangeToTextDocument;
import edu.ycp.cs.netcoder.server.logchange.TextDocument;
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

		ApplyChangeToTextDocument applicator = new ApplyChangeToTextDocument();
		for (Change change : changeList) {
			applicator.apply(change, doc);
		}
		System.out.println("Document is now:\n" + doc.getText());
		
		return true;
	}
}
