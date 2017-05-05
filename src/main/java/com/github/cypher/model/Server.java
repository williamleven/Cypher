package com.github.cypher.model;

import com.github.cypher.sdk.Room;
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
	public Server(){

	}
	@Override
	public ObjectProperty<Image> getImageProperty(){
		return imageProperty;
	}


}
