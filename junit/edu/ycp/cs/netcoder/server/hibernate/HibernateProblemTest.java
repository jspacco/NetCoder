package edu.ycp.cs.netcoder.server.hibernate;

import java.util.List;

import javax.persistence.EntityManager;

import org.junit.Test;

import edu.ycp.cs.netcoder.server.problems.Problem;
import edu.ycp.cs.netcoder.server.util.HibernateUtil;

public class HibernateProblemTest
{
    
    @Test
    public void testSelect()
    throws Exception
    {
        EntityManager eman=HibernateUtil.getManager();
        List<Problem> results=eman.createQuery("select p from Problem p").getResultList();
        for (Problem p : results) {
            System.out.println(p);
        }
    }
    
    @Test
    public void testSelectWithParam()
    throws Exception
    {
        String problemId="1";
        EntityManager eman=HibernateUtil.getManager();
        Problem p=eman.createQuery("select p from Problem p where p.id = :id", 
                Problem.class).setParameter("id", Integer.parseInt(problemId)).
                getSingleResult();
    }
}
