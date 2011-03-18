package edu.ycp.cs.netcoder.server;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import edu.ycp.cs.netcoder.client.LogCodeChangeService;
import edu.ycp.cs.netcoder.server.logchange.ApplyChangeToTextDocument;
import edu.ycp.cs.netcoder.server.logchange.Change;
import edu.ycp.cs.netcoder.server.logchange.CompactChangeStringScanner;
import edu.ycp.cs.netcoder.server.logchange.TextDocument;

public class LogCodeChangeServiceImpl extends RemoteServiceServlet implements LogCodeChangeService {
	private static final long serialVersionUID = 1L;

	@Override
	public Boolean logChange(String s) {
		HttpServletRequest req = this.getThreadLocalRequest();
		HttpSession session = req.getSession();
		
		TextDocument doc = (TextDocument) session.getAttribute("doc");
		if (doc == null) {
			doc = new TextDocument();
			session.setAttribute("doc", doc);
		}
		
		System.out.println("Code change: " + s);

		CompactChangeStringScanner scanner = new CompactChangeStringScanner(s);
		ApplyChangeToTextDocument applicator = new ApplyChangeToTextDocument();
		while (scanner.hasNext()) {
			Change change = scanner.next();
			applicator.apply(change, doc);
		}
		System.out.println("Document is now:\n" + doc.getText());
		
		/*
		Change change = Change.fromCompactString(s);
		System.out.println(change);
		
		try {
			ApplyChangeToTextDocument applicator = new ApplyChangeToTextDocument();
			applicator.apply(change, doc);
			System.out.println("Document is now:\n" + doc.getText());
		} catch (Exception e) {
			System.out.println("Oops: could not apply change to document: " + e.getMessage());
		}
		*/
		
		return true;
	}
}
