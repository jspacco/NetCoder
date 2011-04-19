package edu.ycp.cs.netcoder.shared.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Superclass for objects that are event publishers.
 */
public abstract class Publisher {
	private static class Registration {
		Object key;
		Subscriber subscriber;
		
		Registration(Object key, Subscriber subscriber) {
			this.key = key;
			this.subscriber = subscriber;
		}
	}
	
	private List<Registration> registrationList;
	
	/**
	 * Constructor.
	 */
	protected Publisher() {
		registrationList = new ArrayList<Registration>();
	}
	
	/**
	 * Called by a Subscriber to subscribe to a particular type of event.
	 * 
	 * @param key        key indicating type of event Subscriber wants to be notified of
	 * @param subscriber the Subscriber
	 */
	public void subscribe(Object key, Subscriber subscriber, SubscriptionRegistrar registrar) {
		registrationList.add(new Registration(key, subscriber));
		registrar.addToSubscriptionRegistry(subscriber);
	}
	
	/**
	 * Called by a Subscriber to unsubscribe from a particular type of event.
	 * 
	 * @param key        key indicating type of event Subscriber no longer wants to be notified of
	 * @param subscriber the Subscriber
	 */
	public void unsubscribe(Object key, Subscriber subscriber) {
		for (Iterator<Registration> i = registrationList.iterator(); i.hasNext(); ) {
			Registration reg = i.next();
			if (reg.key.equals(key) && reg.subscriber == subscriber) {
				i.remove();
				return;
			}
		}
	}
	
	/**
	 * Called by a Subscriber to unsubscribe from all events published by this Publisher.
	 * 
	 * @param subscriber the Subscriber
	 */
	public void unsubscribeFromAll(Subscriber subscriber) {
		for (Iterator<Registration> i = registrationList.iterator(); i.hasNext(); ) {
			Registration reg = i.next();
			if (reg.subscriber == subscriber) {
				i.remove();
			}
		}
	}
	
	/**
	 * Publish an event.
	 * 
	 * @param key   key indicating the type of the event
	 * @param hint  object with additional information about the event
	 */
	public void notifySubscribers(Object key, Object hint) {
		// protect against concurrent modification exceptions
		ArrayList<Registration> registrationListCopy = new ArrayList<Registration>(registrationList);
		
		// notify all subscribers subscribed for events with this key
		for (Registration reg : registrationListCopy) {
			if (reg.key.equals(key)) {
				reg.subscriber.eventOccurred(key, this, hint);
			}
		}
	}
}
