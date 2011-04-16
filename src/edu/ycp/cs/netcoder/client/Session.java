package edu.ycp.cs.netcoder.client;

import java.util.HashMap;
import java.util.Map;

/**
 * Client-side session object.
 * Can hold any number of objects, but only one object of any given
 * class is allowed.
 */
public class Session {
	private Map<Class<?>, Object> data;
	
	/**
	 * Constructor.
	 */
	public Session() {
		this.data = new HashMap<Class<?>, Object>();
	}
	
	/**
	 * Add an object to the session.
	 * Replaces any previously-added object belonging to the same class.
	 * 
	 * @param obj object to add to the session
	 */
	public void add(Object obj) {
		data.put(obj.getClass(), obj);
	}
	
	/**
	 * Get an object from the session.
	 * 
	 * @param <E> type of object to get
	 * @param cls Class object representing the type of the object to get
	 * @return the object whose type is the given class, or null if there
	 *         is no object belonging to the class
	 */
	@SuppressWarnings("unchecked")
	public<E> E get(Class<E> cls) {
		Object obj = data.get(cls);
		return (E) obj;
	}
}
