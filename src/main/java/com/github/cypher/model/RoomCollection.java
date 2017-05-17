package com.github.cypher.model;

import javafx.beans.property.ObjectProperty;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;

public interface RoomCollection {
	ObservableList<Room> getRoomsProperty();
	void addRoom(Room room);
	Image getImage();
	ObjectProperty<Image> getImageProperty();
}
