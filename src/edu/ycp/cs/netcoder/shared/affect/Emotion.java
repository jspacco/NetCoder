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

package edu.ycp.cs.netcoder.shared.affect;

/**
 * Emotion values for affect data collection.
 */
public enum Emotion {
	BORED,
	CONFUSED,
	DELIGHTED,
	NEUTRAL,
	FOCUSED,
	OTHER,
	FRUSTRATED;
	
	/**
	 * @return a "nice" string suitable for presentation in the UI
	 */
	public String toNiceString() {
		String s = toString();
		return s.charAt(0) + s.substring(1).toLowerCase();
	}

	/**
	 * @return a lower case string suitable for presentation in the UI
	 */
	public String toLowerCaseString() {
		return toString().toLowerCase();
	}
}
