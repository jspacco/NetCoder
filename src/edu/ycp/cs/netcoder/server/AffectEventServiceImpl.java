package edu.ycp.cs.netcoder.server;

import javax.persistence.EntityManager;

import edu.ycp.cs.netcoder.client.AffectEventService;
import edu.ycp.cs.netcoder.server.util.HibernateUtil;
import edu.ycp.cs.netcoder.shared.affect.AffectEvent;
import edu.ycp.cs.netcoder.shared.event.Event;
import edu.ycp.cs.netcoder.shared.problems.NetCoderAuthenticationException;

/**
 * Servlet to log AffectEvents to the database.
 */
public class AffectEventServiceImpl extends NetCoderServiceImpl implements AffectEventService {
	private static final long serialVersionUID = 1L;

	@Override
	public void recordAffectEvent(AffectEvent affectEvent) throws NetCoderAuthenticationException {
		// Make sure we're storing the data for the correct user.
		checkClientIsAuthenticatedAs(affectEvent.getEvent().getUserId());
		
		EntityManager eman = HibernateUtil.getManager();
		eman.getTransaction().begin();
		
		Event event = affectEvent.getEvent();
		eman.persist(event);
		
		affectEvent.setEventId(event.getId());
		eman.persist(affectEvent);
		
		eman.getTransaction().commit();
	}
}
