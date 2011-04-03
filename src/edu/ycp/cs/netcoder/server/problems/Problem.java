package edu.ycp.cs.netcoder.server.problems;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;


@Entity
@Table(name="problems")
public class Problem
{
	@Id 
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="problem_id")
	private Integer problemId;

	@Column(name="testname")
	private String testName;

	@Column(name="description")
	private String description;

	@Transient
	private List<TestCase> testCases;

	public String toString() {
		return getProblemId()+" testName: "+getTestName()+" "+getDescription();
	}

	/**
	 * @return the id
	 */
	public Integer getProblemId(){
		return problemId;
	}
	/**
	 * @param id the id to set
	 */
	public void setProblemId(Integer id){
		this.problemId = id;
	}
	/**
	 * @return the testName
	 */
	public String getTestName(){
		return testName;
	}
	/**
	 * @param testName the testName to set
	 */
	public void setTestName(String testName){
		this.testName = testName;
	}
	/**
	 * @return the description
	 */
	public String getDescription(){
		return description;
	}
	/**
	 * @param description the description to set
	 */
	public void setDescription(String description){
		this.description = description;
	}

	/**
	 * Get list of test cases for this problem.
	 * 
	 * @return list of test cases
	 */
	@OneToMany(mappedBy="problem", targetEntity=TestCase.class, fetch=FetchType.EAGER, cascade=CascadeType.ALL)
	public List<TestCase> getTestCases() {
		return testCases;
	}  

	/**
	 * Set the list of test cases for this problem.
	 * 
	 * @param testCases list of test cases
	 */
	public void setTestCases(List<TestCase> testCases) {
		this.testCases = testCases;
	}
}
