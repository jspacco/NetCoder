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

import edu.ycp.cs.netcoder.shared.util.Publisher;

/**
 * A publisher of window ResizeEvents.
 * We use this mechanism rather than directly registering to handle
 * ResizeEvents because there is no way to unregister an object
 * from being a window resize handler.
 */
public class WindowResizeNotifier extends Publisher {
	/**
	 * Event type indicating that the window was resized.
	 * Hint will be the ResizeEvent.
	 */
	public static final Object WINDOW_RESIZED = new Object(); 
}
