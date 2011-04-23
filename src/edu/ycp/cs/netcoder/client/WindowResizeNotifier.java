package edu.ycp.cs.netcoder.client;

import edu.ycp.cs.netcoder.shared.util.Publisher;

/**
 * A publisher of window ResizeEvents.
 */
public class WindowResizeNotifier extends Publisher {
	/**
	 * Event type indicating that the window was resized.
	 * Hint will be the ResizeEvent.
	 */
	public static final Object WINDOW_RESIZED = new Object(); 
}
