package com.github.cypher.model;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.image.*;


public class Member {


	private ObjectProperty<Image> imageProperty = new SimpleObjectProperty();

	private final StringProperty name = new SimpleStringProperty();

	public Member(String name) {
		this.name.set(name);
	}


	public Image getImageProperty() {
		return imageProperty.get();
	}

	public ObjectProperty<Image> imagePropertyProperty() {
		return imageProperty;
	}

	public StringProperty getName() {
		return name;
	}
}
