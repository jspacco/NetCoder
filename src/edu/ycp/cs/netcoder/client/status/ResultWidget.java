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

import com.google.gwt.gen2.table.client.FixedWidthFlexTable;
import com.google.gwt.gen2.table.client.FixedWidthGrid;
import com.google.gwt.gen2.table.client.ScrollTable;
import com.google.gwt.gen2.table.client.SelectionGrid.SelectionPolicy;
import com.google.gwt.gen2.table.override.client.FlexTable.FlexCellFormatter;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.InlineLabel;

import edu.ycp.cs.netcoder.client.Session;
import edu.ycp.cs.netcoder.shared.testing.TestResult;
import edu.ycp.cs.netcoder.shared.util.Publisher;
import edu.ycp.cs.netcoder.shared.util.Subscriber;
import edu.ycp.cs.netcoder.shared.util.SubscriptionRegistrar;

/**
 * ResultWidget displays TestResults received from the server
 * following a submission.
 */
public class ResultWidget extends Composite implements Subscriber
{
	private Session session;
	private FixedWidthFlexTable headerTable;
	private FixedWidthGrid grid;
	private ScrollTable table;

	/**
	 * Constructor.
	 * The ResultWidget will listen for TestResult[] objects being
	 * added to the session, and update its contents appropriately.
	 * 
	 * @param session    the Session
	 * @param registrar  the SubscriptionRegistrar
	 */
	public ResultWidget(Session session, SubscriptionRegistrar registrar) {
		this.session = session;
		session.subscribe(Session.Event.ADDED_OBJECT, this, registrar);

		headerTable = new FixedWidthFlexTable();
		FlexCellFormatter formatter = headerTable.getFlexCellFormatter();
		headerTable.setHTML(0, 0, "Outcome");
		headerTable.setHTML(0, 1, "Message");
		headerTable.setHTML(0, 2, "Output");
		headerTable.setHTML(0, 3, "Error output");
		formatter.setColSpan(0, 0, 1);
		formatter.setColSpan(0, 1, 1);
		formatter.setColSpan(0, 2, 1);
		formatter.setColSpan(0, 3, 1);
		
		grid = new FixedWidthGrid();
		grid.setSelectionPolicy(SelectionPolicy.ONE_ROW);
		
		setColumnWidth(0, 100);
		setColumnWidth(1, 400);
		setColumnWidth(2, 100);
		setColumnWidth(3, 100);
		
		table = new ScrollTable(grid, headerTable);
		
		initWidget(table);
	}
	
	private void setColumnWidth(int col, int width) {
		headerTable.setColumnWidth(col, width);
		grid.setColumnWidth(col, width);
	}

	@Override
	public void eventOccurred(Object key, Publisher publisher, Object hint) {
		if (key == Session.Event.ADDED_OBJECT && hint.getClass() == new TestResult[0].getClass()) {
			setResults((TestResult[]) hint);
		}
	}
	
	@Override
	public void unsubscribeFromAll() {
		session.unsubscribeFromAll(this);
	}
	
	private static class OutcomeCell extends InlineLabel {
		public OutcomeCell(TestResult result) {
			super(result.getOutcome());
			setStyleName(result.getOutcome().equals(TestResult.PASSED) ? "NetCoderOutcomePassed" : "NetCoderOutcomeFailed");
		}
	}

	private void setResults(TestResult[] results) {
		grid.clear();
		grid.resize(results.length, 4);
		int row = 0;
		for (TestResult testResult : results) {
			grid.setWidget(row, 0, new OutcomeCell(testResult));
			grid.setWidget(row, 1, new InlineLabel(testResult.getMessage()));
			grid.setWidget(row, 2, new InlineLabel(testResult.getStdout()));
			grid.setWidget(row, 3, new InlineLabel(testResult.getStderr()));
			row++;
		}
	}
}
