// NetCoder - a web-based pedagogical programming environment
// Copyright (C) 2011, Jaime Spacco <jspacco@knox.edu>
// Copyright (C) 2011, David H. Hovemeyer <dhovemey@ycp.edu>
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU Affero General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU Affero General Public License for more details.
//
// You should have received a copy of the GNU Affero General Public License
// along with this program.  If not, see <http://www.gnu.org/licenses/>.

package edu.ycp.cs.netcoder.client;

import java.util.HashMap;
import java.util.Map;

import edu.ycp.cs.netcoder.shared.util.Publisher;

/**
 * Client-side session object.
 * Can hold any number of objects, but only one object of any given
 * class is allowed.  The session is also used to distribute events
 * (such as logging in, logging out, switching views, etc.)
 * that various view objects will want to know about.
 */
public class Session extends Publisher {
	private Map<Class<?>, Object> data;
	
	/**
	 * Event types.
	 */
	public enum Event {
		/** An object was added to the session. The hint is the object added. */
		ADDED_OBJECT,
		
		/** An object was removed from the session. The hint is the object removed. */
		REMOVED_OBJECT,
		
		/** The user logged in. Hint is the User object. */
		LOGIN,
		
		/** The user logged out.  Hint is null. */
		LOGOUT,
	}
	
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
		notifySubscribers(Event.ADDED_OBJECT, obj);
		data.put(obj.getClass(), obj);
	}

	/**
	 * Remove the object belonging to the given class from the session.
	 * 
	 * @param cls the class of the object to be removed
	 */
	public void remove(Class<?> cls) {
		Object obj = get(cls);
		if (obj != null) {
			notifySubscribers(Event.REMOVED_OBJECT, obj);
			data.remove(cls);
		}
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
