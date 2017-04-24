package com.github.cypher.model;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.image.Image;

public class Server implements RoomCollection {
	private ObjectProperty<Image> imageProperty = new SimpleObjectProperty<>();



	Server(String server) {

	}
	public Server(){

	}

	public ObjectProperty<Image> getImageProperty(){
		return imageProperty;
	}


}
