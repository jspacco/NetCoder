package edu.ycp.cs.netcoder.server;

import javax.persistence.EntityManager;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import edu.ycp.cs.netcoder.client.AffectEventService;
import edu.ycp.cs.netcoder.server.util.HibernateUtil;
import edu.ycp.cs.netcoder.shared.affect.AffectEvent;
import edu.ycp.cs.netcoder.shared.event.Event;

/**
 * Servlet to log AffectEvents to the database.
 */
public class AffectEventServiceImpl extends RemoteServiceServlet implements AffectEventService {
	private static final long serialVersionUID = 1L;

	@Override
	public void recordAffectEvent(AffectEvent affectEvent) {
		EntityManager eman=HibernateUtil.getManager();
		eman.getTransaction().begin();
		
		Event event = affectEvent.getEvent();
		eman.persist(event);
		
		affectEvent.setEventId(event.getId());
		eman.persist(affectEvent);
		
		eman.getTransaction().commit();
	}
}
