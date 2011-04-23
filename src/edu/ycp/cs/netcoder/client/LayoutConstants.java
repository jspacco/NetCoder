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

/**
 * Constants used in the layouts of various views.
 */
public interface LayoutConstants {
	/** Height of the top bar (which is part of all views.) */
	public static final int TOP_BAR_HEIGHT_PX = 34;
	
	//
	// DevelopmentView layout constants
	//
	
	/** Height of the problem description widget in the DevelopmentView. */
	public static final int DEV_PROBLEM_DESC_HEIGHT_PX = 60;
	
	/** Height of the status and button bar widget in the DevelopmentView. */
	public static final int DEV_STATUS_AND_BUTTON_BAR_HEIGHT_PX = 28;
	
	/** Height of the results panel in the DevelopmentView. */
	public static final int DEV_RESULTS_PANEL_HEIGHT_PX = 175;

	/** Height of tab bar for results tab panel in DevelopmentView. */
	public static final int DEV_RESULTS_TAB_BAR_HEIGHT_PX = 18;
	
	//
	// CourseAndProblemView layout constants
	//
	
	/** Width of course tree in CourseAndProblemView.  */
	public static final int CP_COURSE_TREE_WIDTH_PX = 350;

	/** Height of status and button bar in CourseAndProblemView. */
	public static final int CP_STATUS_AND_BUTTON_BAR_HEIGHT_PX = 28;

	/** Height of problem description in CourseAndProblemView. */
	public static final int CP_PROBLEM_DESC_HEIGHT_PX = DEV_PROBLEM_DESC_HEIGHT_PX;
}
