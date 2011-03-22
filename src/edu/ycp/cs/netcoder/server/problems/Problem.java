package edu.ycp.cs.netcoder.server.problems;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;


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
}
