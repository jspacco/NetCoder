package edu.ycp.cs.netcoder.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.ycp.cs.netcoder.shared.problems.Course;
import edu.ycp.cs.netcoder.shared.problems.Problem;
import edu.ycp.cs.netcoder.shared.problems.User;

public interface GetCoursesAndProblemsServiceAsync {

	void getCourses(User user, AsyncCallback<Course[]> callback);

	void getProblems(Course course, AsyncCallback<Problem[]> callback);

}
