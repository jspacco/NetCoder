package edu.ycp.cs.netcoder.client.affect;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.TextBox;

import edu.ycp.cs.netcoder.shared.affect.AffectData;
import edu.ycp.cs.netcoder.shared.affect.Emotion;

public class AffectWidget extends TabLayoutPanel {
	private AffectData data; // the model object
	
	private TextBox otherEmotionTextBox;
	
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
	
	private class OtherEmotionButton extends Button implements ClickHandler {
		public OtherEmotionButton() {
			super("Submit");
			addClickHandler(this);
		}
		
		@Override
		public void onClick(ClickEvent event) {
			data.setOtherEmotion(otherEmotionTextBox.getText());
			onFinished();
		}
	}

	public AffectWidget(AffectData affectData) {
		super(0.0, Unit.PX); // don't show tab bar!
		
		this.data = affectData;
		
		// First panel: start data collection by describing emotion
		FlowPanel emotionPanel = new FlowPanel();
		emotionPanel.add(new Label("Which of these best describes your emotion?"));
		for (Emotion e : Emotion.values()) {
			emotionPanel.add(new EmotionButton(e));
		}
		add(emotionPanel, "");
		
		// Second panel (if emotion == OTHER): enter specific emotion
		FlowPanel otherEmotionPanel = new FlowPanel();
		otherEmotionPanel.add(new Label("What one word would best describe your emotion?"));
		otherEmotionTextBox = new TextBox();
		otherEmotionTextBox.setWidth("95%");
		otherEmotionPanel.add(otherEmotionTextBox);
		otherEmotionPanel.add(new OtherEmotionButton());
		add(otherEmotionPanel, "");
		
		// Third panel (if emotion != OTHER): rate boredom
		add(new HTML("Rate boredom"), "");
		
		// Fourth panel: done
		add(new HTML("Thank you!"), "");
	}

	protected void onEmotionSet() {
		if (data.getEmotion() == Emotion.OTHER) {
			selectTab(1);
		} else {
			selectTab(2);
		}
	}

	protected void onFinished() {
		selectTab(3);
	}

}
