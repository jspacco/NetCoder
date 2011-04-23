package edu.ycp.cs.netcoder.client;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.CellTree;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SingleSelectionModel;
import com.google.gwt.view.client.TreeViewModel;

import edu.ycp.cs.netcoder.shared.problems.Course;
import edu.ycp.cs.netcoder.shared.problems.TermAndYear;
import edu.ycp.cs.netcoder.shared.problems.User;
import edu.ycp.cs.netcoder.shared.util.Publisher;
import edu.ycp.cs.netcoder.shared.util.Subscriber;

public class CourseAndProblemView extends NetCoderView implements Subscriber {
	
//	private ListBox coursesListBox;
	private CellTree courseTree;
	
	private GetCoursesAndProblemsServiceAsync getCoursesAndProblemsService =
		GWT.create(GetCoursesAndProblemsService.class);
	
	private static class TermNode extends AbstractCell<TermAndYear> {
		@Override
		public void render(Context context, TermAndYear value, SafeHtmlBuilder sb) {
			String text = value.getTerm().getName() + " " + value.getYear();
			sb.appendHtmlConstant("<span class='NetCoderCourseTreeTerm'>");
			sb.appendEscaped(text);
			sb.appendHtmlConstant("</span>");
		}
		
		
	}
	
	private static class CourseNode extends AbstractCell<Course> {
		@Override
		public void render(Context context, Course value, SafeHtmlBuilder sb) {
			String text = value.toString();
			sb.appendHtmlConstant("<span class='NetCoderCourseTreeCourse'>");
			sb.appendEscaped(text);
			sb.appendHtmlConstant("</span>");
		}
	}
	
	private static final SingleSelectionModel<Course> selectionModel = new SingleSelectionModel<Course>();
	
	private static class CourseTreeModel implements TreeViewModel {
		private Course[] courseList;
		
		public CourseTreeModel(Course[] courseList) {
			this.courseList = courseList;
		}
		
		@Override
		public <T> NodeInfo<?> getNodeInfo(T value) {
			if (value == null) {
				// root node
				
				// Get a representative Course for each term/year combination
				List<TermAndYear> termList = new ArrayList<TermAndYear>();
				TermAndYear last = null;
				for (Course course : courseList) {
					TermAndYear courseTermAndYear = new TermAndYear(course.getTerm(), course.getYear());
					if (last == null || !courseTermAndYear.equals(last)) {
						termList.add(courseTermAndYear);
					}
					last = courseTermAndYear;
				}
				
				ListDataProvider<TermAndYear> dataProvider = new ListDataProvider<TermAndYear>(termList);
				
				TermNode cell = new TermNode();
				
				return new DefaultNodeInfo<TermAndYear>(dataProvider, cell);
			} else if (value instanceof TermAndYear) {
				TermAndYear termAndYear = (TermAndYear) value;
				
				List<Course> coursesForTermAndYear = new ArrayList<Course>();
				for (Course course : courseList) {
					if (course.getTerm().equals(termAndYear.getTerm()) && course.getYear() == termAndYear.getYear()) {
						coursesForTermAndYear.add(course);
					}
				}
				
				ListDataProvider<Course> dataProvider = new ListDataProvider<Course>(coursesForTermAndYear);
				
				CourseNode cell = new CourseNode();
				return new DefaultNodeInfo<Course>(dataProvider, cell, selectionModel, null);
			}
			
			// should not happen
			throw new IllegalStateException();
		}

		@Override
		public boolean isLeaf(Object value) {
			return value instanceof Course;
		}
		
	}
	
	public CourseAndProblemView(Session session) {
		super(session);
		
		getSession().subscribe(Session.Event.ADDED_OBJECT, this, getSubscriptionRegistrar());
		
		LayoutPanel layoutPanel = getLayoutPanel();
		
//		coursesListBox = new ListBox();
//		coursesListBox.setVisibleItemCount(10);
//		coursesListBox.setWidth("30em");
//		layoutPanel.add(coursesListBox);
//		layoutPanel.setWidgetTopHeight(
//				coursesListBox,
//				LayoutConstants.TOP_BAR_HEIGHT_PX + 50, Unit.PX,
//				100, Unit.PX);
		
		AsyncCallback<Course[]> callback = new AsyncCallback<Course[]>() {
			@Override
			public void onFailure(Throwable caught) {
				GWT.log("Could not load courses");
			}
			
			@Override
			public void onSuccess(Course[] result) {
				onCoursesLoaded(result);
			}
		};
		getCoursesAndProblemsService.getCourses(getSession().get(User.class), callback);
		
		initWidget(layoutPanel);
	}
	
	protected void onCoursesLoaded(Course[] result) {
		courseTree = new CellTree(new CourseTreeModel(result), null);
		getLayoutPanel().add(courseTree);
		
		getLayoutPanel().setWidgetLeftWidth(
				courseTree,
				0, Unit.PX,
				250, Unit.PX);
		getLayoutPanel().setWidgetTopHeight(
				courseTree,
				LayoutConstants.TOP_BAR_HEIGHT_PX, Unit.PX,
				400, Unit.PX);
	}

	@Override
	public void eventOccurred(Object key, Publisher publisher, Object hint) {
		if (hint.getClass() == new Course[0].getClass()) {
			Course[] courseList = (Course[]) hint;
//			coursesListBox.clear();
//			for (Course course : courseList) {
//				coursesListBox.addItem(course.toString());
//			}
		}
	}
	
	@Override
	public void unsubscribeFromAll() {
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
	}

	@Override
	public void deactivate() {
	}

}
