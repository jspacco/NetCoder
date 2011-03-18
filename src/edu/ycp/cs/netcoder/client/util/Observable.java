package edu.ycp.cs.netcoder.client.util;

import java.util.ArrayList;
import java.util.List;

// GWT 2.2 doesn't have this in java.util!
public class Observable {
	private boolean changed;
	private List<Observer> observerList;
	
	public Observable() {
		changed = false;
		observerList = new ArrayList<Observer>();
	}
	
	public void setChanged() {
		this.changed = true;
	}
	
	public void addObserver(Observer obs) {
		observerList.add(obs);
	}
	
	public void notifyObservers() {
		notifyObservers(null);
	}

	public void notifyObservers(Object hint) {
		if (changed) {
			for (Observer obs : observerList) {
				obs.update(this, hint);
			}
			changed = false;
		}
	}
}
