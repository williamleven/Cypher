package com.github.cypher.model;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;

public class Server implements RoomCollection {
	private ObjectProperty<Image> imageProperty = new SimpleObjectProperty<>();
	ObservableList<Room> rooms;


	public Server(String server) {
		//TODO
	}

	public String getAddress() {
		return null;
	}

	@Override
	public void addRoom(Room room) {

	}

	@Override
	public Image getImage() {
		return imageProperty.get();
	}

	@Override
	public ObjectProperty<Image> getImageProperty(){
		return imageProperty;
	}
}
