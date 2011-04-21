package edu.ycp.cs.netcoder.server;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.EntityManager;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import edu.ycp.cs.netcoder.client.GetCoursesAndProblemsService;
import edu.ycp.cs.netcoder.server.util.HibernateUtil;
import edu.ycp.cs.netcoder.shared.problems.Course;
import edu.ycp.cs.netcoder.shared.problems.Problem;
import edu.ycp.cs.netcoder.shared.problems.User;

public class GetCoursesAndProblemsServiceImpl extends RemoteServiceServlet
		implements GetCoursesAndProblemsService {
	private static final long serialVersionUID = 1L;

	private static final Map<String, Integer> termMap = new HashMap<String, Integer>();
	static {
		termMap.put("winter", 0);
		termMap.put("spring", 1);
		termMap.put("summer", 2);
		termMap.put("fall", 3);
	}
	
	static class CourseComparator implements Comparator<Course> {
		@Override
		public int compare(Course lhs, Course rhs) {
			int semCmp = compareSemesters(lhs, rhs);
			if (semCmp != 0) {
				return semCmp;
			}
			
			int nameCmp = lhs.getName().compareTo(rhs.getName());
			if (nameCmp != 0) {
				return nameCmp;
			}
			
			return lhs.getTitle().compareTo(rhs.getTitle());
		}

		private int compareSemesters(Course lhs, Course rhs) {
			int strCmp = lhs.getSemester().compareTo(rhs.getSemester());
			
			String[] ltok = lhs.getSemester().trim().split("\\s+");
			String[] rtok = rhs.getSemester().trim().split("\\s+");
			
			if (ltok.length != 2 || rtok.length != 2) {
				return strCmp;
			}
			
			try {
				int lyear = Integer.parseInt(ltok[1]);
				int ryear = Integer.parseInt(rtok[1]);
				
				if (lyear != ryear) {
					return lyear - ryear;
				}
			} catch (NumberFormatException e) {
				return strCmp;
			}
			
			Integer lterm = termMap.get(ltok[0]);
			Integer rterm = termMap.get(rtok[0]);
			if (lterm == null || rterm == null) {
				return strCmp;
			}
			
			return lterm.intValue() - rterm.intValue();
		}
	}

	@Override
	public Course[] getCourses(User user) {
		EntityManager eman = HibernateUtil.getManager();
		
		List<Course> courseList = eman.createQuery(
				"select c from Course c, CourseRegistration r " +
				"  where c.id = r.courseId " +
				"  and r.userId = :userId",
				Course.class).setParameter("userId", user.getId()).getResultList();
		
		Collections.sort(courseList, new CourseComparator());
		
		return courseList.toArray(new Course[courseList.size()]);
	}

	@Override
	public Problem[] getProblems(Course course) {
		// TODO Auto-generated method stub
		return null;
	}

}
