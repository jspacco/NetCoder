package edu.ycp.cs.netcoder.shared.util;

/**
 * A SubscriptionRegistrar object keeps track of a set of objects
 * that have subscribed to events.
 */
public interface SubscriptionRegistrar {
	/**
	 * Subscribe a subscriber to an event type published by given publisher.
	 * 
	 * @param publisher  a Publisher
	 * @param subscriber a Subscriber
	 * @param key        object indicating the type of event the Subscriber is interested in
	 */
	public void addToSubscriptionRegistry(Subscriber subscriber);

	/**
	 * Unsubscribe all event Subscribers from all events.
	 */
	public void unsubscribeAllEventSubscribers();
}
