package com.github.cypher.model;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.image.Image;

public class PMCollection implements RoomCollection {
	private static Image PM_COLLECTION_IMAGE = new Image(GeneralCollection.class.getResourceAsStream("/images/fa-users-white-40.png"));
	private ObjectProperty<Image> imageProperty = new SimpleObjectProperty<>(PM_COLLECTION_IMAGE); // Should this maybe be generated on first request instead?

	@Override
	public void addRoom(Room room) {

	}

	@Override
	public Image getImage() {
		return PM_COLLECTION_IMAGE;
	}

	@Override
	public ObjectProperty<Image> getImageProperty() {
		return imageProperty;
	}
}
