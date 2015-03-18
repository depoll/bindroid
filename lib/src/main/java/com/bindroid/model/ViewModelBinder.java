package com.bindroid.model;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.bindroid.trackable.Trackable;
import com.bindroid.trackable.Tracker;
import com.bindroid.utils.Action;
import com.bindroid.utils.Property;
import com.bindroid.utils.ReflectedProperty;

/**
 * Provides watcher methods for view models and their properties.
 */
public class ViewModelBinder {

	private static final String GETTER_PREFIX = "get";

	/**
	 * Watch a single property of the passed view model. This will only work for properties that are
	 * backed by a {@link com.bindroid.trackable.Trackable}.
	 *
	 * @param model a model instance
	 * @param propertyPath the property path of the property to watch
	 * @param action the action to execute on change
	 * @param <T> the model property type
	 * @return a watcher
	 */
	public static <T> ViewModelWatcher watch(Object model, String propertyPath, Action<T> action) {
		final Property property = new ReflectedProperty(model, propertyPath);

		PropertyWatcher<T> tracker = new PropertyWatcher<T>(property, action);
		Trackable.track(tracker, property.getGetter());
		return tracker;
	}

	/**
	 * Watch for changes across all public properties (i.e. public getters) notifying the passed Action
	 * when changes are detected. Changes will only be reported for properties that are backed by a
	 * {@link com.bindroid.trackable.Trackable}.
	 *
	 * @param model a model instance
	 * @param action the action to execute on change
	 * @return a watcher
	 */
	public static ViewModelWatcher watchAll(Object model, Action<Object> action) {
		List<ViewModelWatcher> propertyWatchers = new ArrayList<ViewModelWatcher>();

		Method[] declaredMethods = model.getClass().getMethods();
		for (Method method : declaredMethods) {
			if (method.getName().startsWith(GETTER_PREFIX)) {
				String propertyPath = method.getName().replaceFirst(GETTER_PREFIX, "");
				propertyWatchers.add(watch(model, propertyPath, action));
			}
		}
		return new WatcherGroup(propertyWatchers);
	}

	private static class PropertyWatcher<T> implements ViewModelWatcher, Tracker {
		private final Property property;
		private final Action<T> action;

		private boolean stopped = false;

		public PropertyWatcher(Property property, Action<T> action) {
			this.property = property;
			this.action = action;
		}

		@Override
		public void update() {
			if (!stopped) {
				action.invoke((T) property.getValue());
				Trackable.track(this, property.getGetter());
			}
		}

		@Override
		public boolean isWatching() {
			return !stopped;
		}

		@Override
		public void start() {
			stopped = false;
			Trackable.track(this, property.getGetter());
		}

		@Override
		public void stop() {
			stopped = true;
		}
	}

	private static class WatcherGroup implements ViewModelWatcher {
		private final List<ViewModelWatcher> watchers;

		private boolean stopped = false;

		public WatcherGroup(List<ViewModelWatcher> watchers) {
			this.watchers = watchers;
		}

		@Override
		public boolean isWatching() {
			return !stopped;
		}

		@Override
		public void start() {
			for (ViewModelWatcher watcher : watchers) {
				watcher.start();
			}
			stopped = false;
		}

		@Override
		public void stop() {
			for (ViewModelWatcher watcher : watchers) {
				watcher.stop();
			}
			stopped = true;
		}
	}
}
