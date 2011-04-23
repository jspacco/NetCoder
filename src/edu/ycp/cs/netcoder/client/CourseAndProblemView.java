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

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.gen2.table.client.FixedWidthFlexTable;
import com.google.gwt.gen2.table.client.FixedWidthGrid;
import com.google.gwt.gen2.table.client.ScrollTable;
import com.google.gwt.gen2.table.client.SelectionGrid.SelectionPolicy;
import com.google.gwt.gen2.table.override.client.FlexTable.FlexCellFormatter;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;

import edu.ycp.cs.netcoder.shared.problems.Course;
import edu.ycp.cs.netcoder.shared.problems.Problem;
import edu.ycp.cs.netcoder.shared.problems.TermAndYear;
import edu.ycp.cs.netcoder.shared.problems.User;
import edu.ycp.cs.netcoder.shared.util.Publisher;
import edu.ycp.cs.netcoder.shared.util.Subscriber;

/**
 * View for browsing courses and problems.
 * TODO: refactor out some widgets out of this class (course tree, problem list, etc.)
 */
public class CourseAndProblemView extends NetCoderView implements Subscriber {
	// Tree for displaying user's courses (organized by term/year)
	private Tree courseTree;
	
	// Table for displaying available problems for selected course
	private FixedWidthFlexTable headerTable;
	private FixedWidthGrid grid;
	private ScrollTable table;
	
	private GetCoursesAndProblemsServiceAsync getCoursesAndProblemsService =
		GWT.create(GetCoursesAndProblemsService.class);
	
	/**
	 * Object to manage current Course selection.
	 */
	private static class CourseSelection extends Publisher {
		/**
		 * Event types broadcast to subscribers.
		 */
		public enum Event {
			/** A course was selected. */
			COURSE_SELECTED,
			
			/** A course was loaded, and its problems are available. */
			COURSE_LOADED,
		}
		
		private Course current;    // course currently loading (most recent selection)
		private Problem[] problemList;
		
		/**
		 * Called to select a course.
		 * Initiates loading of course.
		 * 
		 * @param course the selected Course
		 */
		public void courseSelected(Course course) {
			current = course;
			notifySubscribers(Event.COURSE_SELECTED, course);
		}
		
		/**
		 * Called to indicate that a course has been loaded
		 * (along with its problems).
		 * 
		 * @param course       the loaded Course
		 * @param problemList  the Course's Problems
		 */
		public void courseLoaded(Course course, Problem[] problemList) {
			// Only update the current course if it is the one most
			// recently selected.
			if (course == this.current) {
				this.current = null;
				this.problemList = problemList;
				notifySubscribers(Event.COURSE_LOADED, course);
			}
		}

		/**
		 * Get list of Problems for the currently-loaded course.
		 * 
		 * @return list of Problems
		 */
		public Problem[] getProblemList() {
			return problemList;
		}
	}
	
	/**
	 * Constructor.
	 * 
	 * @param session the Session
	 */
	public CourseAndProblemView(Session session) {
		super(session);
		
		// Subscribe to session ADDED_OBJECT events (to find out when course list is loaded)
		getSession().subscribe(Session.Event.ADDED_OBJECT, this, getSubscriptionRegistrar());
		
		LayoutPanel layoutPanel = getLayoutPanel();
		
		// Add grid to display problems
		headerTable = new FixedWidthFlexTable();
		FlexCellFormatter formatter = headerTable.getFlexCellFormatter();
		headerTable.setHTML(0, 0, "Problem name");
		headerTable.setHTML(0, 1, "Description");
		formatter.setColSpan(0, 0, 1);
		formatter.setColSpan(0, 1, 1);
		
		grid = new FixedWidthGrid();
		grid.setSelectionPolicy(SelectionPolicy.ONE_ROW);
		
		setColumnWidth(0, 100);
		setColumnWidth(1, 200);
		
		table = new ScrollTable(grid, headerTable);
		layoutPanel.add(table);
		
		initWidget(layoutPanel);
		
		// Subscribe to window resize events
		getSession().get(WindowResizeNotifier.class).subscribe(WindowResizeNotifier.WINDOW_RESIZED, this, getSubscriptionRegistrar());
		
		// Subscribe to changes in selected course
		CourseSelection courseSelection = new CourseSelection(); 
		addSessionObject(courseSelection);
		courseSelection.subscribe(CourseSelection.Event.COURSE_SELECTED, this, getSubscriptionRegistrar());
		courseSelection.subscribe(CourseSelection.Event.COURSE_LOADED, this, getSubscriptionRegistrar());
	}

	private void setColumnWidth(int col, int width) {
		headerTable.setColumnWidth(col, width);
		grid.setColumnWidth(col, width);
	}
	
	private static class TermAndYearNode extends InlineLabel {
		private TermAndYear termAndYear;
		
		public TermAndYearNode(TermAndYear termAndYear) {
			super(termAndYear.toString());
			this.termAndYear = termAndYear;
		}
		
		public TermAndYear getTermAndYear() {
			return termAndYear;
		}
	}
	
	private static class CourseNode extends InlineLabel {
		private Course course;
		
		public CourseNode(Course course) {
			super(course.toString());
			this.course = course;
		}
		
		public Course getCourse() {
			return course;
		}
	}

	@Override
	public void eventOccurred(Object key, Publisher publisher, Object hint) {
		if (key == Session.Event.ADDED_OBJECT && hint instanceof Course[]) {
			Course[] courseList = (Course[]) hint;
			
			// Build the course tree
			courseTree = new Tree();
			TreeItem curTermAndYearTreeItem = null;
			TermAndYear curTermAndYear = null;
			for (Course course : courseList) {
				TermAndYear courseTermAndYear = new TermAndYear(course.getTerm(), course.getYear()); 
				if (curTermAndYear == null
						|| !curTermAndYear.equals(courseTermAndYear)) {
					curTermAndYearTreeItem = new TreeItem(new TermAndYearNode(courseTermAndYear));
					courseTree.addItem(curTermAndYearTreeItem);
					curTermAndYear = courseTermAndYear;
				}
				curTermAndYearTreeItem.addItem(new CourseNode(course));
			}
			
			// Handle selection events
			courseTree.addSelectionHandler(new SelectionHandler<TreeItem>() {
				@Override
				public void onSelection(SelectionEvent<TreeItem> event) {
					TreeItem treeItem = event.getSelectedItem();
					Widget w = treeItem.getWidget();
					if (w instanceof CourseNode) {
						getSession().get(CourseSelection.class).courseSelected(((CourseNode)w).getCourse());
					}
				}
			});
			
			getLayoutPanel().add(courseTree);
			
			doResize();
		} else if (key == WindowResizeNotifier.WINDOW_RESIZED) {
			doResize();
		} else if (key == CourseSelection.Event.COURSE_SELECTED) {
			final Course selectedCourse = (Course) hint;
			AsyncCallback<Problem[]> callback = new AsyncCallback<Problem[]>() {
				@Override
				public void onFailure(Throwable caught) {
					GWT.log("Could not load problems for course", caught);
				}
				
				@Override
				public void onSuccess(Problem[] result) {
					getSession().get(CourseSelection.class).courseLoaded(selectedCourse, result);
				}
			};
			
			getCoursesAndProblemsService.getProblems(selectedCourse, callback);
		} else if (key == CourseSelection.Event.COURSE_LOADED) {
			showProblems(getSession().get(CourseSelection.class).getProblemList());
		}
	}
	
	private void showProblems(Problem[] problemList) {
		grid.clear();
		grid.resize(problemList.length, 2);
		int row = 0;
		for (Problem problem : problemList) {
			grid.setWidget(row, 0, new InlineLabel(problem.getTestName()));
			grid.setWidget(row, 1, new InlineLabel(problem.getBriefDescription()));
			row++;
		}
	}

	@Override
	public void unsubscribeFromAll() {
		getSession().get(WindowResizeNotifier.class).unsubscribeFromAll(this);
		getSession().get(CourseSelection.class).unsubscribeFromAll(this);
		getSession().unsubscribeFromAll(this);
	}

	@Override
	public void activate() {
		AsyncCallback<Course[]> callback = new AsyncCallback<Course[]>() {
			@Override
			public void onFailure(Throwable caught) {
				GWT.log("Could not get courses", caught);
			}
			
			@Override
			public void onSuccess(Course[] result) {
				addSessionObject(result);
			}
		};
		
		getCoursesAndProblemsService.getCourses(getSession().get(User.class), callback);
		
		doResize(); // size the problems table
	}

	@Override
	public void deactivate() {
		getSubscriptionRegistrar().unsubscribeAllEventSubscribers();
		
		removeAllSessionObjects();
	}

	private void doResize() {
		int availHeight = Window.getClientHeight()
			- LayoutConstants.TOP_BAR_HEIGHT_PX
			- LayoutConstants.CP_STATUS_AND_BUTTON_BAR_HEIGHT_PX
			- LayoutConstants.CP_PROBLEM_DESC_HEIGHT_PX;
		
		if (courseTree != null) {
			getLayoutPanel().setWidgetLeftWidth(
					courseTree,
					0, Unit.PX,
					LayoutConstants.CP_COURSE_TREE_WIDTH_PX, Unit.PX);
			getLayoutPanel().setWidgetTopHeight(
					courseTree,
					LayoutConstants.TOP_BAR_HEIGHT_PX, Unit.PX,
					availHeight, Unit.PX);
		}
		
		getLayoutPanel().setWidgetRightWidth(
				table,
				0, Unit.PX,
				Window.getClientWidth() - LayoutConstants.CP_COURSE_TREE_WIDTH_PX - 8, Unit.PX);
		getLayoutPanel().setWidgetTopHeight(
				table,
				LayoutConstants.TOP_BAR_HEIGHT_PX, Unit.PX,
				availHeight, Unit.PX);
	}
}
