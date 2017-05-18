package com.github.cypher.gui;

import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import java.util.LinkedList;
import java.util.List;

public class FXThreadedObservableValueWrapper<T> implements ObservableValue<T>{

	private final ObservableValue<? extends T> inner;

	private final List<ChangeListener<T>> listeners = new LinkedList<>();

	private final List<InvalidationListener> invalidationListeners = new LinkedList<>();

	// Storing the listeners applied to the inner object.
	private ChangeListener<T> changeListenerObject;
	private InvalidationListener invalidationListenerObject;

	public FXThreadedObservableValueWrapper(ObservableValue<? extends T> observableValue) {
		this.inner = observableValue;

	}

	@Override
	public synchronized void addListener(ChangeListener changeListener) {
		// If first listener start listening to inner value
		if (listeners.isEmpty()) {
			changeListenerObject = (observable, oldValue, newValue) -> {
					Platform.runLater(() -> {
						synchronized (this) {
							for (ChangeListener<T> l : listeners) {
								l.changed(observable, oldValue, newValue);
							}
						}
					});
			};
			inner.addListener(changeListenerObject);
		}
		listeners.add(changeListener);
	}

	@Override
	public synchronized void removeListener(ChangeListener changeListener) {
		listeners.remove(changeListener);

		// Remove listener on inner object if listeners list is empty
		if (listeners.isEmpty()){
			inner.removeListener(changeListenerObject);
			changeListenerObject = null;
		}
	}

	@Override
	public synchronized T getValue() {
		return inner.getValue();
	}

	@Override
	public synchronized void addListener(InvalidationListener invalidationListener) {
		// If first listener start listening to inner value
		if (invalidationListeners.isEmpty()){
			invalidationListenerObject = (observable) -> {
					Platform.runLater(() -> {
						synchronized (this) {
							for (InvalidationListener listener : invalidationListeners) {
								listener.invalidated(this);
							}
						}
					});
			};
			inner.addListener(invalidationListenerObject);
		}
		invalidationListeners.add(invalidationListener);
	}

	@Override
	public synchronized void removeListener(InvalidationListener invalidationListener) {
		invalidationListeners.remove(invalidationListener);

		// Remove listener on inner object if listeners list is empty
		if (invalidationListeners.isEmpty()){
			inner.removeListener(invalidationListenerObject);
			invalidationListenerObject = null;
		}
	}
}
