package com.github.cypher.model;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;

public class Server implements RoomCollection {
	private final ObjectProperty<Image> imageProperty = new SimpleObjectProperty<>();
	private final StringProperty nameProperty = new SimpleStringProperty();
	private final ObservableList<Room> rooms = FXCollections.synchronizedObservableList(FXCollections.observableArrayList());


	public Server(String server) {
		//TODO
	}

	public String getAddress() {
		return null;
	}

	public String getName(){
		return nameProperty.getValue();
	}

	public StringProperty nameProperty(){
		return nameProperty;
	}

	@Override
	public ObservableList<Room> getRoomsProperty() {
		return rooms;
	}

	@Override
	public void addRoom(Room room) {
		rooms.add(room);
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
