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

package edu.ycp.cs.netcoder.client.status;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;

import edu.ycp.cs.netcoder.client.Session;
import edu.ycp.cs.netcoder.shared.problems.Problem;
import edu.ycp.cs.netcoder.shared.util.Observable;
import edu.ycp.cs.netcoder.shared.util.Observer;

public class ProblemDescriptionWidget extends Composite implements Observer {
	private Session session;
	private Label problemDescriptionText;
	
	public ProblemDescriptionWidget(Session session) {
		this.session = session;
		session.addObserver(this);
		
		problemDescriptionText = new Label("Loading problem description...");
		
		initWidget(problemDescriptionText);
		
		this.setStyleName("NetCoderProblemDescription");
	}
	
	@Override
	public void update(Observable obj, Object hint) {
		Problem problem = session.get(Problem.class);
		if (problem != null) {
			problemDescriptionText.setText(problem.getDescription());
		}
	}

	public void setErrorText(String text) {
		problemDescriptionText.setText("Error: " + text);
	}
}
