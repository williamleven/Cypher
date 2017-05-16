package com.github.cypher.gui;

import javafx.scene.Node;
import javafx.scene.control.ListCell;

/*
List view cellFactory example:
	listView.setCellFactory((o) -> {
		ExampleListItemView view = new ExampleListItemView();
		view.getView();
		return (ExampleListItemPresenter) view.getPresenter();
	});
*/

/**
 * Abstract class to be extended by Custom ListCells.
 *
 * Simply extend this class, implement the methods and add a cellFactory to your listView
 * @param <T>
 */
abstract public class CustomListCell<T> extends ListCell<T> {

	// The data to be represented by this list cell
	private T modelComponent = null;

	protected T getModelComponent(){
		return modelComponent;
	}

	@Override
	public void updateItem(T newModelComponent, boolean empty){
		super.updateItem(newModelComponent, empty);

		if (empty) {
			// Clear and hide cell
			setGraphic(null);
			setModelComponent(null);
		} else {
			// Populate and show cell
			setModelComponent(newModelComponent);
			setGraphic(getRoot());
		}
	}

	// Populate, update or clear the cell
	private void setModelComponent(T s) {
		if (s != modelComponent) {
			this.modelComponent = s;
			if (s == null){
				clearBindings();
			}else{
				updateBindings();
			}
		}
	}

	/**
	 * Returns the root node for the presenter
	 * @return
	 */
	protected abstract Node getRoot();

	/**
	 * Binds all properties to the modelComponent
	 */
	protected abstract void updateBindings();

	/**
	 * Removes all bindings to the previous modelComponent
	 */
	protected abstract void clearBindings();
}