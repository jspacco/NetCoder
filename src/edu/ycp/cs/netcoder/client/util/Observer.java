package edu.ycp.cs.netcoder.client.util;

// GWT 2.2 doesn't have this in java.util!
public interface Observer {
	public void update(Observable obj, Object hint);
}
