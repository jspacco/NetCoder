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

package edu.ycp.cs.netcoder.shared.util;

import java.util.ArrayList;
import java.util.Iterator;
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
	
	public void removeObserver(Observer obs) {
		for (Iterator<Observer> i = observerList.iterator(); i.hasNext(); ) {
			Observer o = i.next();
			if (o == obs) {
				i.remove();
				break;
			}
		}
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
