package edu.ycp.cs.netcoder.client.affect;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TabLayoutPanel;

import edu.ycp.cs.netcoder.shared.affect.AffectData;
import edu.ycp.cs.netcoder.shared.affect.Emotion;

public class AffectWidget extends TabLayoutPanel {
	private AffectData data;
	
	private class EmotionButton extends Button implements ClickHandler {
		private Emotion emotion;
		
		public EmotionButton(Emotion emotion) {
			super(emotion.toString().charAt(0) + emotion.toString().substring(1).toLowerCase());
			this.emotion = emotion;
			addClickHandler(this);
		}
		
		@Override
		public void onClick(ClickEvent event) {
			data.setEmotion(emotion);
			onEmotionSet();
		}
	}

	public AffectWidget(AffectData affectData) {
		super(0.0, Unit.PX); // don't show tab bar!
		
		this.data = affectData;
		
		FlowPanel emotionPanel = new FlowPanel();
		emotionPanel.add(new Label("Which of these best describes your emotion?"));
		
		for (Emotion e : Emotion.values()) {
			emotionPanel.add(new EmotionButton(e));
		}
		
		add(emotionPanel);
		
		add(new HTML("tab 2"), "enter specific emotion for 'other'");
		add(new HTML("tab 3"), "rate level of boredom");
	}

	protected void onEmotionSet() {
		if (data.getEmotion() == Emotion.OTHER) {
			selectTab(1);
		} else {
			selectTab(2);
		}
	}

}
