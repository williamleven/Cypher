package com.github.cypher.gui;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

// Wraps an ObservableList to support binding directly to JavaFX GUI elements
// while editing the underlying list from another thread.
public class FXThreadedObservableListWrapper<T> {
	private final ObservableList<T> sourceList;
	private final ObservableList<T> delegatedList;
	private final ListChangeListener<? super T> sourceListListener; // CurrentRoomCollectionListener?

	public FXThreadedObservableListWrapper(ObservableList<T> sourceList) {
		this.sourceList = sourceList;
		this.delegatedList = FXCollections.synchronizedObservableList(FXCollections.observableArrayList());
		delegatedList.addAll(sourceList);
		sourceListListener = ((ListChangeListener.Change<? extends T> change) -> {
			Platform.runLater(() -> {
				while (change.next()) {
					if (change.wasAdded()) {
						delegatedList.addAll(change.getAddedSubList());
					}
					if (change.wasRemoved()) {
						delegatedList.removeAll(change.getRemoved());
					}
				}
			});
		});
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
