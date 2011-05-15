package edu.ycp.cs.netcoder.server;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import edu.ycp.cs.netcoder.client.GetCoursesAndProblemsService;
import edu.ycp.cs.netcoder.server.util.HibernateUtil;
import edu.ycp.cs.netcoder.shared.problems.Course;
import edu.ycp.cs.netcoder.shared.problems.NetCoderAuthenticationException;
import edu.ycp.cs.netcoder.shared.problems.Problem;
import edu.ycp.cs.netcoder.shared.problems.Term;
import edu.ycp.cs.netcoder.shared.problems.User;

public class GetCoursesAndProblemsServiceImpl extends NetCoderServiceImpl
		implements GetCoursesAndProblemsService {
	private static final long serialVersionUID = 1L;

	@SuppressWarnings("unchecked")
	@Override
	public Course[] getCourses() throws NetCoderAuthenticationException {
		// make sure the client has authenticated
		User user = checkClientIsAuthenticated();
		
		EntityManager eman = HibernateUtil.getManager();
		
		Query q = eman.createQuery(
				"select c, t from Course c, Term t, CourseRegistration r " +
				"  where c.id = r.courseId " +
				"    and c.termId = t.id " +
				"    and r.userId = :userId " +
				"  order by c.year desc, t.seq desc"
				);
		q.setParameter("userId", user.getId());
		
		List<? extends Object[]> resultList = (List<? extends Object[]>) q.getResultList();
		
		Course[] result = new Course[resultList.size()];
		int count = 0;
		for (Object[] pair : resultList) {
			Course course = (Course) pair[0];
			Term term = (Term) pair[1];
			course.setTerm(term);
			result[count++] = course;
		}
		
		return result;
	}

	@Override
	public Problem[] getProblems(Course course) throws NetCoderAuthenticationException {
		// Make sure user is authenticated
		User user = checkClientIsAuthenticated();
		
		EntityManager eman = HibernateUtil.getManager();
		
		//
		// Note that we have to join on course registrations to ensure
		// that we return courses that the user is actually registered for.
		//
		TypedQuery<Problem> q = eman.createQuery(
				"select p from Problem p, Course c, CourseRegistration r " +
				" where p.courseId = c.id " +
				"   and r.courseId = c.id " +
				"   and r.userId = :userId " +
				"   and c.id = :courseId", // TODO: descending order by due date
				Problem.class);
		q.setParameter("userId", user.getId());
		q.setParameter("courseId", course.getId());
		
		List<Problem> resultList = q.getResultList();
		
		return resultList.toArray(new Problem[resultList.size()]);
	}

}
