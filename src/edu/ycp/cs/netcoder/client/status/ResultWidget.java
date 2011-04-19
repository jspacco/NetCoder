package edu.ycp.cs.netcoder.client.status;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.user.client.Element;

import edu.ycp.cs.netcoder.shared.testing.TestResult;

public class ResultWidget extends LayoutContainer
{
    private ListStore<TestResultModelData> store;
	private ColumnModel columnModel;
	private Grid<TestResultModelData> grid;

	public ResultWidget() {
    	/*
        this.add(title);
        title.setText("Test Results");
        
        this.add(table);
        table.insertRow(0);
        table.setBorderWidth(1);
        // table header
        table.insertCell(0, 0);
        table.setWidget(0, 0, new HTML("<th>outcome</th>"));
        table.insertCell(0, 1);
        table.setWidget(0, 1, new HTML("<th>message</th>"));
        table.insertCell(0, 2);
        table.setWidget(0, 2, new HTML("<th>stdout</th>"));
        table.insertCell(0, 3);
        table.setWidget(0, 3, new HTML("<th>stderr</th>"));
        */
    }
    
    // Ugh.
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
    		return null;
    	}
    }
    
    @Override
    protected void onRender(Element parent, int index) {
    	// TODO Auto-generated method stub
    	super.onRender(parent, index);
    	
    	//ContentPanel cp = new ContentPanel();
    	//cp.setLayout(new FitLayout());
    	//cp.setSize(600,300);
    	
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
    
    // XXX
    public void setGridSize(String width, String height) {
    	grid.setSize(width, height);
    }

    /*
    public void ensureCapacity(int numRows, int numCols) {
        for (int r=table.getRowCount(); r<numRows; r++) {
            table.insertRow(r);
            for (int c=0; c<numCols; c++) {
                table.addCell(r);
            }
        }
    }
    */
    
    public void setResults(TestResult[] results) {
    	//MessageBox.info("Test results", "Setting the results!!!", null);

    	store.removeAll();
    	
    	List<TestResultModelData> list = new ArrayList<ResultWidget.TestResultModelData>();
    	for (TestResult testResult : results) {
    		list.add(new TestResultModelData(testResult));
    	}
    	
    	store.add(list);
    	
    	grid.reconfigure(store, columnModel);
    	//MessageBox.info("Test results", "Added " + list.size() + " test results", null);
    	
    	/*
        System.out.println("Is this being called?");
        title.setText("Results received: "+results.length);
        table.setTitle("Table title");
        // may not be necessary to ensure capacity
        //ensureCapacity(results.length+1, 4);
        
        for (int i=0; i<results.length; i++) {
            TestResult t=results[i];
            // result / outcome / stdout / stderr
            // leave room for the header row
            table.setWidget(i+1, 0, new Label(t.getOutcome()));
            table.setWidget(i+1, 1, new Label(t.getMessage()));
            table.setWidget(i+1, 2, new Label("stdout"));
            table.setWidget(i+1, 3, new Label("stderr"));
        }
        table.setVisible(true);
        */
    }
    
//    public void setMessage(String message) {
//        title.setText(message);
//    }
}
