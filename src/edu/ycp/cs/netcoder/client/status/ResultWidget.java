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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.google.gwt.user.client.Element;

import edu.ycp.cs.netcoder.client.Session;
import edu.ycp.cs.netcoder.shared.testing.TestResult;
import edu.ycp.cs.netcoder.shared.util.Publisher;
import edu.ycp.cs.netcoder.shared.util.Subscriber;
import edu.ycp.cs.netcoder.shared.util.SubscriptionRegistrar;

/**
 * ResultWidget displays TestResults received from the server
 * following a submission.
 */
public class ResultWidget extends LayoutContainer implements Subscriber
{
	private Session session;
	private ListStore<TestResultModelData> store;
	private ColumnModel columnModel;
	private Grid<TestResultModelData> grid;

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

	/**
	 * Ext-GWT requires model classes to implement its ModelData interface.
	 * All I can say is: isn't the idea to make your model classes
	 * NOT depend on your UI?  Feh.
	 */
	static class TestResultModelData implements ModelData {
		private Map<String, Object> properties;

		public TestResultModelData(TestResult testResult) {
			this.properties = new HashMap<String, Object>();
			properties.put("outcome", testResult.getOutcome());
			properties.put("message", testResult.getMessage());
			properties.put("stdout", testResult.getStdout());
			properties.put("stderr", testResult.getStderr());
		}

		@SuppressWarnings("unchecked")
		@Override
		public <X> X get(String property) {
			return (X) properties.get(property);
		}

		@Override
		public Map<String, Object> getProperties() {
			return properties;
		}

		@Override
		public Collection<String> getPropertyNames() {
			return properties.keySet();
		}

		@SuppressWarnings("unchecked")
		@Override
		public <X> X remove(String property) {
			return (X) properties.remove(property);
		}

		public <X extends Object> X set(String property, X value) {
			throw new UnsupportedOperationException();
		}
	}

	@Override
	protected void onRender(Element parent, int index) {
		// TODO Auto-generated method stub
		super.onRender(parent, index);

		store = new ListStore<ResultWidget.TestResultModelData>();

		TestResult tr = new TestResult(TestResult.PASSED, "Woo hoo?", "stdout text", "stderr text");
		store.add(new TestResultModelData(tr));
		store.commitChanges();

		GridCellRenderer<TestResultModelData> outcomeRenderer = new GridCellRenderer<ResultWidget.TestResultModelData>() {
			@Override
			public Object render(TestResultModelData model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<TestResultModelData> store,
					Grid<TestResultModelData> grid) {
				String outcome = model.get(property);

				boolean passed = outcome.equals(TestResult.PASSED);

				return "<span style='color: " + (passed ? "green" : "red") + ";'> " + outcome + "</span>";
			}
		};

		GridCellRenderer<TestResultModelData> otherStringRenderer = new GridCellRenderer<ResultWidget.TestResultModelData>() {
			@Override
			public Object render(TestResultModelData model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<TestResultModelData> store,
					Grid<TestResultModelData> grid) {
				return model.get(property);
			}
		};

		List<ColumnConfig> columnList = new ArrayList<ColumnConfig>();
		ColumnConfig column;

		column = new ColumnConfig();
		column.setId("outcome");
		column.setHeader("Outcome");
		column.setWidth(100);
		column.setRenderer(outcomeRenderer);
		columnList.add(column);

		column = new ColumnConfig();
		column.setId("message");
		column.setHeader("Message");
		column.setWidth(400);
		column.setRenderer(otherStringRenderer);
		columnList.add(column);

		column = new ColumnConfig();
		column.setId("stdout");
		column.setHeader("Output");
		column.setWidth(100);
		column.setRenderer(otherStringRenderer);
		columnList.add(column);

		column = new ColumnConfig();
		column.setId("stderr");
		column.setHeader("Error output");
		column.setWidth(100);
		column.setRenderer(otherStringRenderer);
		columnList.add(column);

		columnModel = new ColumnModel(columnList);

		grid = new Grid<ResultWidget.TestResultModelData>(store, columnModel);

		add(grid);
	}

	/**
	 * I can't figure out how to get an Ext-GWT Grid to
	 * set its own vertical size correctly, so this is a hack
	 * to work around that.
	 * 
	 * @param width  width to set the Grid to
	 * @param height height to set the Grid to
	 */
	public void setGridSize(String width, String height) {
		grid.setSize(width, height);
	}

	private void setResults(TestResult[] results) {
		if (grid == null) {
			return; // UI is not initialized yet
		}
		
		store.removeAll();

		List<TestResultModelData> list = new ArrayList<ResultWidget.TestResultModelData>();
		for (TestResult testResult : results) {
			list.add(new TestResultModelData(testResult));
		}

		store.add(list);

		grid.reconfigure(store, columnModel);
	}
}
