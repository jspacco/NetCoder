// NetCoder - a web-based pedagogical programming environment
// Copyright (C) 2011, Jaime Spacco
// Copyright (C) 2011, David H. Hovemeyer
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

import edu.ycp.cs.netcoder.shared.util.Observable;

/**
 * Client-side session object.
 * Can hold any number of objects, but only one object of any given
 * class is allowed.
 */
public class Session extends Observable {
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
		setChanged();
		notifyObservers(obj);
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
