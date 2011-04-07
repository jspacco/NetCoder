package edu.ycp.cs.netcoder.client.status;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.ycp.cs.netcoder.shared.testing.TestResult;

public class ResultWidget extends VerticalPanel
{
    private Label title=new Label();
    private FlexTable table=new FlexTable();
    
    public void setResults(TestResult[] results) {
        title.setText("Results received: "+results.length);
        for (int i=0; i<results.length; i++) {
            TestResult t=results[i];
            // result / outcome / stdout / stderr
            table.setText(i, 0, t.success+"");
            table.setText(i, 1, t.message);
            table.setText(i, 2, "stdout");
            table.setText(i, 3, "stderr");
        }
    }
    
    public void setMessage(String message) {
        title.setText(message);
    }
    
    public ResultWidget() {
        add(title);
        title.setText("Test Results");
        
        add(table);
        table.setTitle("Test Results");
        table.setWidth("95%");
    }
}
