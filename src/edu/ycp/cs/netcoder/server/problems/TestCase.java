package edu.ycp.cs.netcoder.server.problems;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * Represents a single test case associated with a column.
 */
@Entity
@Table(name="testcases")
public class TestCase {
	@Id 
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="testcase_id")
	private int testCaseId;
	
	@Column(name="input")
	private String input;
	
	@Column(name="output")
	private String output;
	
	@Transient
	private Problem problem;
	
	public TestCase() {
	}
	
	public void setTestCaseId(int testCaseId) {
		this.testCaseId = testCaseId;
	}
	
	public int getTestCaseId() {
		return testCaseId;
	}
	
	public void setProblem(Problem problem) {
		this.problem = problem;
	}
	
	@ManyToOne
	@JoinColumn(name="problem_id")
	public Problem getProblem() {
		return problem;
	}
	
	public void setInput(String input) {
		this.input = input;
	}
	
	public String getInput() {
		return input;
	}
	
	public void setOutput(String output) {
		this.output = output;
	}
	
	public String getOutput() {
		return output;
	}
	
	@Override
	public String toString() {
		StringBuilder buf = new StringBuilder();
		buf.append(testCaseId);
		buf.append(",");
		buf.append(problem != null ? problem.getProblemId() : "<no problem id?>");
		buf.append(",");
		buf.append(input);
		buf.append(",");
		buf.append(output);
		return buf.toString();
	}
}
