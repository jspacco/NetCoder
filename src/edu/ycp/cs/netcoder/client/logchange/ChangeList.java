package edu.ycp.cs.netcoder.client.logchange;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.ycp.cs.netcoder.client.util.Observable;

public class ChangeList extends Observable {
	public enum State {
        /** No unsent changes. */
		CLEAN, 
        /** There are unsent changes that have not been transmitted. */
		UNSENT,
        /** Some changes are currently in transmission. */
		TRANSMISSION,
	}
	
	private State state;                 // current state
	private boolean transmitSuccess;     // last transmit succeeded
	private List<String> unsent;         // changes waiting to be sent
	private List<String> inTransmission; // changes currently in-transit
	
	public ChangeList() {
		this.state = State.CLEAN;
		this.transmitSuccess = true;
		this.unsent = new ArrayList<String>();
		this.inTransmission = new ArrayList<String>();
	}
	
	public int getNumUnsentChanges() {
		return unsent.size();
	}
	
	public State getState() {
		return state;
	}
	
	public boolean isTransmitSuccess() {
		return transmitSuccess;
	}
	
	public void addChange(String change) {
		unsent.add(change);
		if (state == State.CLEAN) {
			state = State.UNSENT;
			setChanged();
			notifyObservers();
		}
	}
	
	public List<String> beginTransmit() {
		assert state == State.UNSENT;
		assert !unsent.isEmpty();
		assert inTransmission.isEmpty() || !transmitSuccess;
		
		inTransmission.addAll(unsent);
		unsent.clear();
		
		state = State.TRANSMISSION;
		setChanged();
		notifyObservers();
		
		return Collections.unmodifiableList(inTransmission);
	}
	
	public void endTrasnmit(boolean success) {
		assert state == State.TRANSMISSION;
		inTransmission.clear();
		state = (success && unsent.isEmpty()) ? State.CLEAN : State.UNSENT;
		transmitSuccess = success;
		setChanged();
		notifyObservers();
	}
}
