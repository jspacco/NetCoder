package edu.ycp.cs.netcoder.server.util;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class HibernateUtil
{
    private static EntityManagerFactory factory;
    static {
        factory=Persistence.createEntityManagerFactory("entman");
    }
    public static EntityManager getManager() {
        return factory.createEntityManager();
    }
}
