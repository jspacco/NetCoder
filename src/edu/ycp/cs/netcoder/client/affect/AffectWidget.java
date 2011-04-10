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

package edu.ycp.cs.netcoder.client.affect;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.ycp.cs.netcoder.shared.affect.AffectEvent;
import edu.ycp.cs.netcoder.shared.affect.Emotion;

/**
 * Widget for affect data collection.
 */
public class AffectWidget extends Composite {
	private static final String TAB_WIDTH = "96%";
	
	private AffectEvent data; // the model object
	
	private TextBox otherEmotionTextBox;
	private RadioButton[] emotionLevelRadioButtonList;

	private TabLayoutPanel tabPanel;
	private VerticalPanel emotionLevelPanel;
	
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
	
	private class SubmitEmotionLevelButton extends Button implements ClickHandler {
		public SubmitEmotionLevelButton() {
			super("Submit");
			addClickHandler(this);
		}
		
		@Override
		public void onClick(ClickEvent event) {
			int level = 1;
			for (RadioButton b : emotionLevelRadioButtonList) {
				if (b.getValue()) {
					data.setEmotionLevel(level);
					break;
				}
				level++;
			}
			onFinished();
		}
	}

	/**
	 * Constructor.
	 * 
	 * @param affectData the AffectData object to be edited by this widget
	 */
	public AffectWidget(AffectEvent affectData) {
		this.data = affectData;
		initUI();
	}

	private void initUI() {
		this.tabPanel = new TabLayoutPanel(0.0, Unit.PX); // don't show tab bar!
		
		// First panel: start data collection by describing emotion
		FlowPanel emotionPanel = new FlowPanel();
		emotionPanel.setWidth(TAB_WIDTH);
		emotionPanel.add(new Label("Which of these best describes your emotion?"));
		
		Emotion[] currentEmotionOrder=randomizeEmotions();
		for (Emotion e : currentEmotionOrder) {
			emotionPanel.add(new EmotionButton(e));
		}
		tabPanel.add(emotionPanel, "");
		
		// Second panel (if emotion == OTHER): enter specific emotion
		FlowPanel otherEmotionPanel = new FlowPanel();
		otherEmotionPanel.setWidth(TAB_WIDTH);
		otherEmotionPanel.add(new Label("What one word would best describe your emotion?"));
		otherEmotionTextBox = new TextBox();
		otherEmotionTextBox.setWidth("95%");
		otherEmotionPanel.add(otherEmotionTextBox);
		otherEmotionPanel.add(new SubmitOtherEmotionButton());
		tabPanel.add(otherEmotionPanel, "");
		
		// Third panel (if emotion != OTHER): rate level of emotion
		this.emotionLevelPanel = new VerticalPanel();
		emotionLevelPanel.setWidth(TAB_WIDTH);
		tabPanel.add(emotionLevelPanel, "");
		
		// Fourth panel: done
		HTML endPanel = new HTML("Thank you!");
		endPanel.setWidth(TAB_WIDTH);
		tabPanel.add(endPanel, "");
		
		initWidget(tabPanel);
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

	protected void onEmotionSet() {
		if (data.getEmotion() == Emotion.OTHER) {
			tabPanel.selectTab(1);
		} else {
			populateEmotionLevelPanel();
			tabPanel.selectTab(2);
		}
	}

	private void populateEmotionLevelPanel() {
		String emotionName = data.getEmotion().toLowerCaseString();
		
		emotionLevelPanel.add(new Label("How " + emotionName + " are you?"));
		emotionLevelRadioButtonList = new RadioButton[] {
			new RadioButton("boredomLevel", "1 - A little " + emotionName),
			new RadioButton("boredomLevel", "2"),
			new RadioButton("boredomLevel", "3 - Somewhat " + emotionName),
			new RadioButton("boredomLevel", "4"),
			new RadioButton("boredomLevel", "5 - Extremely " + emotionName)
		};
		for (RadioButton b : emotionLevelRadioButtonList) {
			emotionLevelPanel.add(b);
		}
		emotionLevelPanel.add(new SubmitEmotionLevelButton());
	}

	protected void onFinished() {
		tabPanel.selectTab(3);
	}
}
