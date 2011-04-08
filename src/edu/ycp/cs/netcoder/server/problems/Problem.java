// NetCoder - a web-based pedagogical idea
// Copyright (C) 2011, Jaime Spacco
// Copyright (C) 2011, David H. Hovemeyer
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
