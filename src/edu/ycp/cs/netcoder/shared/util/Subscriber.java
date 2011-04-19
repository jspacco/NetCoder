package edu.ycp.cs.netcoder.shared.util;

/**
 * Interface implemented by classes that are event subscribers.
 */
public interface Subscriber {
	/**
	 * Called by a Publisher when an event occurs.
	 * 
	 * @param key        key indicating the type of the event
	 * @param publisher  the Publisher
	 * @param hint       additional information about the event
	 */
	public void eventOccurred(Object key, Publisher publisher, Object hint);
	
	/**
	 * When this method is called, the Subscriber should unsubscribe from
	 * all events it has registered for.
	 */
	public void unsubscribeFromAll();
}
