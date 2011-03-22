package edu.ycp.cs.netcoder.server;

import java.util.List;

import javax.persistence.EntityManager;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import edu.ycp.cs.netcoder.client.LoadExerciseService;
import edu.ycp.cs.netcoder.server.problems.Problem;
import edu.ycp.cs.netcoder.server.util.HibernateUtil;

public class LoadExerciseServiceImpl extends RemoteServiceServlet implements LoadExerciseService 
{
    private static final long serialVersionUID = 1L;
    
    public String load(int problemId) {
        if (problemId<=0) {
            return "Cannot find problem with id "+problemId;
        }
        EntityManager eman=HibernateUtil.getManager();
        List<Problem> problems=eman.createQuery("select p from Problem p where p.id = :id", 
                Problem.class).setParameter("id", problemId).
                getResultList();
        if (problems.size()==0) {
            return "Cannot find problem with id "+problemId;
        }
        return problems.get(0).getDescription();
    }
}
