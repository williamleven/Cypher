package com.github.cypher.gui;

import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

// Wraps an ObservableList to support binding directly to JavaFX GUI elements
// while editing the underlying list from another thread.
public class FXThreadedObservableListWrapper<T> {
	private final ObservableList<T> sourceList;
	private final ObservableList<T> delegatedList;
	private InvalidationListener sourceListListener; // CurrentRoomCollectionListener?

	@SuppressWarnings("unchecked")
	public FXThreadedObservableListWrapper(ObservableList<T> sourceList) {
		this.sourceList = sourceList;
		this.delegatedList = FXCollections.synchronizedObservableList(FXCollections.observableArrayList());
		Platform.runLater(() -> delegatedList.addAll(sourceList.toArray((T[])new Object[sourceList.size()])));
		sourceListListener = i -> Platform.runLater(() ->
			delegatedList.setAll(sourceList.toArray((T[])new Object[sourceList.size()])));
		sourceList.addListener(sourceListListener);
	}

	// Returns the delegatedList which is supposed to be used in eg. ListView::setItems
	public ObservableList<T> getList(){
		return delegatedList;
	}

	public void dispose() {
		sourceList.removeListener(sourceListListener);
	}
}
