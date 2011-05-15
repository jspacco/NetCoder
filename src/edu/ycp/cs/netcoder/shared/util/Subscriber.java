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
