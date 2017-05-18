package com.github.cypher.gui.root.roomcollection.room.chat.messageitem;

import com.github.cypher.gui.CustomListCell;
import com.github.cypher.gui.FXThreadedObservableValueWrapper;
import com.github.cypher.model.Client;
import com.github.cypher.model.Message;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;

import javax.inject.Inject;
import java.util.LinkedList;
import java.util.List;

public class MessageItemPresenter extends CustomListCell<Message> {

	@Inject
	private Client client;

	@FXML
	private AnchorPane root;

	@FXML
	private Label author;

	@FXML
	private TextFlow bodyContainer;

	@FXML
	private ImageView avatar;

	private boolean formatted = false;
	private ChangeListener<String> bodyChangeListener = (ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
		if(formatted) {
			generateFormattedTextObjects(newValue);
		} else {
			generateTextObjects(newValue);
		}
	};

	@Override
	protected Node getRoot() {
		return root;
	}

	@Override
	protected void updateBindings() {
		Message message = getModelComponent();
		author.textProperty().bind(new FXThreadedObservableValueWrapper<>(message.getAuthor().nameProperty()));

		if(message.getFormattedBody() != null) {
			formatted = true;
			new FXThreadedObservableValueWrapper<>(message.formattedBodyProperty()).addListener(bodyChangeListener);
		} else {
			new FXThreadedObservableValueWrapper<>(message.bodyProperty()).addListener(bodyChangeListener);
		}

		if(formatted) {
			generateFormattedTextObjects(message.getFormattedBody());
		} else {
			generateTextObjects(message.getBody());
		}
		avatar.imageProperty().bind(new FXThreadedObservableValueWrapper<>(message.getAuthor().avatarProperty()));
	}

	@Override
	protected void clearBindings() {
		Message message = getModelComponent();
		author.textProperty().unbind();
		message.bodyProperty().removeListener(bodyChangeListener);
		message.formattedBodyProperty().removeListener(bodyChangeListener);
		avatar.imageProperty().unbind();
	}

	private void generateTextObjects(String text) {
		bodyContainer.getChildren().clear();
		Text textObject = new Text(text);
		bodyContainer.getChildren().add(textObject);
	}

	private void generateFormattedTextObjects(String text) {

		Document document = Jsoup.parseBodyFragment(text);
		document.outputSettings(new Document.OutputSettings().prettyPrint(false));
		parseFormattedMessageNode(document.body(), new LinkedList<>());
	}

	private void parseFormattedMessageNode(org.jsoup.nodes.Node node, List<Element> parents) {
		List textFlowList = bodyContainer.getChildren();

		if(node instanceof TextNode) {
			// Ignore TextNodes containing only whitespace
			if(!node.outerHtml().replace(" ", "").equals("")) {

				String text = ((TextNode) node).getWholeText();
				Text textObject = new Text(text);
				boolean pre = false;

				// Go through all parent tags and apply styling
				for(Element element : parents) {
					String tagName = element.tagName();

					if       (tagName.equals("ul")) { // Begin bullet list
					} else if(tagName.equals("ol")) { // TODO: Begin numbered list
					} else if(tagName.equals("li")) {
						// List item
						textFlowList.add(new Text(" • "));
					} else if(tagName.equals("blockquote")) {
						textObject.getStyleClass().add("block-quote");
					} else if(tagName.equals("pre")) {
						// Preceeds a <code> tag to specify a multiline block
						pre = true;
					} else if(tagName.equals("code")) {
						// Monospace and TODO: code highlighting
						if(!pre) {
							textObject.getStyleClass().add("inline-monospace");
						} else {
							textObject.getStyleClass().add("block-monospace");
						}
						break; // We don't care about anything appearing within a <code> tag
					} else {
						// Other tags are applied ass CSS classes
						textObject.getStyleClass().add(tagName);
					}
				}
				textFlowList.add(textObject);
				textObject.applyCss();
			}
		} else if(node instanceof Element) {
			parents = new LinkedList<>(parents);
			parents.add((Element)node);
		}

		// Recursively parse child tags
		for(org.jsoup.nodes.Node child: node.childNodes()) {
			parseFormattedMessageNode(child, parents);
		}
	}
}
