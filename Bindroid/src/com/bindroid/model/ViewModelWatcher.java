package com.bindroid.model;

/**
 * Defines a watcher that may be stopped and started. Watcher implementations are
 * expected to be initialized in a started state.
 */
public interface ViewModelWatcher {
	/**
	 * Returns true if the watcher is watching, false if stopped.
	 */
	boolean isWatching();

	/**
	 * Starts the watcher.
	 */
	void start();

	/**
	 * Stops the watcher.
	 */
	void stop();
}
