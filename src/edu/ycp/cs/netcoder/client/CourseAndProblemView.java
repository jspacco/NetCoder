package edu.ycp.cs.netcoder.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.ListBox;

import edu.ycp.cs.netcoder.shared.problems.Course;
import edu.ycp.cs.netcoder.shared.problems.User;
import edu.ycp.cs.netcoder.shared.util.Publisher;
import edu.ycp.cs.netcoder.shared.util.Subscriber;

public class CourseAndProblemView extends NetCoderView implements Subscriber {
	
	private ListBox coursesListBox;
	
	private GetCoursesAndProblemsServiceAsync getCoursesAndProblemsService =
		GWT.create(GetCoursesAndProblemsService.class);
	
	public CourseAndProblemView(Session session) {
		super(session);
		
		getSession().subscribe(Session.Event.ADDED_OBJECT, this, getSubscriptionRegistrar());
		
		LayoutPanel layoutPanel = getLayoutPanel();
		
		coursesListBox = new ListBox();
		coursesListBox.setVisibleItemCount(10);
		coursesListBox.setWidth("30em");
		layoutPanel.add(coursesListBox);
		layoutPanel.setWidgetTopHeight(
				coursesListBox,
				LayoutConstants.TOP_BAR_HEIGHT_PX + 50, Unit.PX,
				100, Unit.PX);
		
		initWidget(layoutPanel);
	}
	
	@Override
	public void eventOccurred(Object key, Publisher publisher, Object hint) {
		if (hint.getClass() == new Course[0].getClass()) {
			Course[] courseList = (Course[]) hint;
			coursesListBox.clear();
			for (Course course : courseList) {
				coursesListBox.addItem(course.toString());
			}
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
