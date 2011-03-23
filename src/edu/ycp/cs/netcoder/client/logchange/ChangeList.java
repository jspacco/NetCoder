package edu.ycp.cs.netcoder.client.logchange;

import java.util.ArrayList;
import java.util.List;

import edu.ycp.cs.netcoder.client.util.Observable;
import edu.ycp.cs.netcoder.shared.logchange.Change;

/**
 * ChangeList stores a list of Change objects representing textual
 * changes in the editor.  It supports scheduling batches of changes
 * to be transmitted to the server.
 */
public class ChangeList extends Observable {
	/**
	 * State enumeration - represents whether editor is clean,
	 * contains unsent changes, or is currently transmitting changes.
	 */
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
	private List<Change> unsent;         // changes waiting to be sent
	private List<Change> inTransmission; // changes currently in-transit
	
	/**
	 * Constructor.
	 */
	public ChangeList() {
		this.state = State.CLEAN;
		this.transmitSuccess = true;
		this.unsent = new ArrayList<Change>();
		this.inTransmission = new ArrayList<Change>();
	}
	
	/**
	 * @return number of unsent changes
	 */
	public int getNumUnsentChanges() {
		return unsent.size();
	}

	/**
	 * @return the state
	 */
	public State getState() {
		return state;
	}
	
	/**
	 * @return true if most recent transmission was successful, false otherwise
	 */
	public boolean isTransmitSuccess() {
		return transmitSuccess;
	}
	
	/**
	 * Add a change (scheduling it to be sent to the server at some point in the future).
	 * 
	 * @param change a change
	 */
	public void addChange(Change change) {
		unsent.add(change);
		if (state == State.CLEAN) {
			state = State.UNSENT;
			setChanged();
			notifyObservers();
		}
	}
	
	/**
	 * Begin a transmission.
	 * 
	 * @return array of Change objects to be sent to server
	 */
	public Change[] beginTransmit() {
		assert state == State.UNSENT;
		assert !unsent.isEmpty();
		assert inTransmission.isEmpty() || !transmitSuccess;
		
		inTransmission.addAll(unsent);
		unsent.clear();
		
		state = State.TRANSMISSION;
		setChanged();
		notifyObservers();
		
		// return a single string containing the entire batch of changes
		return inTransmission.toArray(new Change[inTransmission.size()]);
	}
	
	/**
	 * Mark end of transmission.
	 * 
	 * @param success true if transmission was successful, false otherwise
	 */
	public void endTransmit(boolean success) {
		assert state == State.TRANSMISSION;
		inTransmission.clear();
		state = (success && unsent.isEmpty()) ? State.CLEAN : State.UNSENT;
		transmitSuccess = success;
		setChanged();
		notifyObservers();
	}
}
