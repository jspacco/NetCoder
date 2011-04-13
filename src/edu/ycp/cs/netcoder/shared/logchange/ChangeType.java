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

package edu.ycp.cs.netcoder.shared.logchange;

import java.util.HashMap;
import java.util.Map;

/**
 * Type of textual change.
 */
public enum ChangeType {
	/** Insertion of text within a particular line */
	INSERT_TEXT,
	
	/** Removal of text within a particular line. */
	REMOVE_TEXT,
	
	/** Insertion of one or more lines. */
	INSERT_LINES,
	
	/** Removal of one or more lines. */
	REMOVE_LINES;
}

