package edu.ycp.cs.netcoder.client.affect;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.ycp.cs.netcoder.shared.affect.AffectData;
import edu.ycp.cs.netcoder.shared.affect.Emotion;

public class AffectWidget extends TabLayoutPanel {
	private static final String TAB_WIDTH = "96%";
	
	private AffectData data; // the model object
	
	private TextBox otherEmotionTextBox;
	private RadioButton[] boredomLevelRadioButtonList;
	
	private class EmotionButton extends Button implements ClickHandler {
		private Emotion emotion;
		
		public EmotionButton(Emotion emotion) {
			super(emotion.toNiceString());
			this.emotion = emotion;
			addClickHandler(this);
		}
		
		@Override
		public void onClick(ClickEvent event) {
			data.setEmotion(emotion);
			onEmotionSet();
		}
	}
	
	private class SubmitOtherEmotionButton extends Button implements ClickHandler {
		public SubmitOtherEmotionButton() {
			super("Submit");
			addClickHandler(this);
		}
		
		@Override
		public void onClick(ClickEvent event) {
			data.setOtherEmotion(otherEmotionTextBox.getText());
			onFinished();
		}
	}
	
	private class SubmitBoredomLevelButton extends Button implements ClickHandler {
		public SubmitBoredomLevelButton() {
			super("Submit");
			addClickHandler(this);
		}
		
		@Override
		public void onClick(ClickEvent event) {
			int level = 1;
			for (RadioButton b : boredomLevelRadioButtonList) {
				if (b.getValue()) {
					data.setBoredomLevel(level);
					break;
				}
				level++;
			}
			onFinished();
		}
	}
	
	private Emotion[] randomizeEmotions() {
		// GWT (as of version 2.2) does not have Collections.shuffle, sigh
	    Emotion[] orig=Emotion.values();
	    Emotion[] result=new Emotion[orig.length];
	    for (int i=0; i<orig.length; i++) {
	        result[i]=orig[i];
	    }
	    for (int i=0; i<result.length; i++){
	        int swap=Random.nextInt(result.length);
	        Emotion tmp=result[i];
	        result[i]=result[swap];
	        result[swap]=tmp;
	    }
	    return result;
	}

	public AffectWidget(AffectData affectData) {
		super(0.0, Unit.PX); // don't show tab bar!
		
		this.data = affectData;
		
		// First panel: start data collection by describing emotion
		FlowPanel emotionPanel = new FlowPanel();
		emotionPanel.setWidth(TAB_WIDTH);
		emotionPanel.add(new Label("Which of these best describes your emotion?"));
		
		Emotion[] currentEmotionOrder=randomizeEmotions();
		for (Emotion e : currentEmotionOrder) {
			emotionPanel.add(new EmotionButton(e));
		}
		add(emotionPanel, "");
		
		// Second panel (if emotion == OTHER): enter specific emotion
		FlowPanel otherEmotionPanel = new FlowPanel();
		otherEmotionPanel.setWidth(TAB_WIDTH);
		otherEmotionPanel.add(new Label("What one word would best describe your emotion?"));
		otherEmotionTextBox = new TextBox();
		otherEmotionTextBox.setWidth("95%");
		otherEmotionPanel.add(otherEmotionTextBox);
		otherEmotionPanel.add(new SubmitOtherEmotionButton());
		add(otherEmotionPanel, "");
		
		// Third panel (if emotion != OTHER): rate boredom
		VerticalPanel boredomLevelPanel = new VerticalPanel();
		boredomLevelPanel.setWidth(TAB_WIDTH);
		boredomLevelPanel.add(new Label("How bored are you?"));
		boredomLevelRadioButtonList = new RadioButton[] {
			new RadioButton("boredomLevel", "1 - A little bored"),
			new RadioButton("boredomLevel", "2"),
			new RadioButton("boredomLevel", "3 - Somewhat bored"),
			new RadioButton("boredomLevel", "4"),
			new RadioButton("boredomLevel", "5 - Extremely bored")
		};
		for (RadioButton b : boredomLevelRadioButtonList) {
			boredomLevelPanel.add(b);
		}
		boredomLevelPanel.add(new SubmitBoredomLevelButton());
		add(boredomLevelPanel, "");
		
		// Fourth panel: done
		HTML endPanel = new HTML("Thank you!");
		endPanel.setWidth(TAB_WIDTH);
		add(endPanel, "");
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
