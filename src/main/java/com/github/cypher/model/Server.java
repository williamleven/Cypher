package com.github.cypher.model;

import com.github.cypher.model.Room;
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

	String getAddress() {
		return null;
	}

	@Override
	public void addRoom(Room room) {

	}
	public Server(){

	}
	@Override
	public ObjectProperty<Image> getImageProperty(){
		return imageProperty;
	}


}
