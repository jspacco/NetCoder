package edu.ycp.cs.netcoder.client;

import java.util.HashSet;
import java.util.Set;

import edu.ycp.cs.netcoder.shared.util.Subscriber;
import edu.ycp.cs.netcoder.shared.util.SubscriptionRegistrar;

public class DefaultSubscriptionRegistrar implements SubscriptionRegistrar {
	private Set<Subscriber> eventSubscriberSet;
	
	public DefaultSubscriptionRegistrar() {
		this.eventSubscriberSet = new HashSet<Subscriber>();
	}

	@Override
	public void addToSubscriptionRegistry(Subscriber subscriber) {
		eventSubscriberSet.add(subscriber);
	}

	@Override
	public void unsubscribeAllEventSubscribers() {
		for (Subscriber subscriber : eventSubscriberSet) {
			subscriber.unsubscribeFromAll();
		}
	}
}
