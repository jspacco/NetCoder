package edu.ycp.cs.netcoder.client.status;

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.ycp.cs.netcoder.shared.testing.TestResult;

public class ResultWidget extends VerticalPanel
{
    private Label title=new Label();
    private FlexTable table=new FlexTable();
    
    public ResultWidget() {
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
    }

    public void ensureCapacity(int numRows, int numCols) {
        for (int r=table.getRowCount(); r<numRows; r++) {
            table.insertRow(r);
            for (int c=0; c<numCols; c++) {
                table.addCell(r);
            }
        }
    }
    
    public void setResults(TestResult[] results) {
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
    }
    
    public void setMessage(String message) {
        title.setText(message);
    }
}
