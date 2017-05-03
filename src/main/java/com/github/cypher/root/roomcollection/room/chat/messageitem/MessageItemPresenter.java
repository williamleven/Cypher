package com.github.cypher.root.roomcollection.room.chat.messageitem;

import com.github.cypher.DebugLogger;
import com.github.cypher.model.Client;
import com.github.cypher.model.Message;
import com.github.cypher.root.CustomListCell;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
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

	@FXML
	private void initialize() {
		//body.prefWidthProperty().bind(root.widthProperty().subtract(author.prefWidthProperty()).subtract(13));
	}

	@Override
	protected Node getRoot() {
		return root;
	}

	@Override
	protected void updateBindings() {
		Message message = getModelComponent();
		author.textProperty().bind(message.getAuthor());

		if(message.getFormattedBody().get() != null) {
			formatted = true;
			message.getFormattedBody().addListener(bodyChangeListener);
		} else {
			message.getBody().addListener(bodyChangeListener);
		}

		if(formatted) {
			generateFormattedTextObjects(message.getFormattedBody().get());
		} else {
			generateTextObjects(message.getBody().get());
		}
		avatar.imageProperty().bind(message.getAvatar());
	}

	@Override
	protected void clearBindings() {
		Message message = getModelComponent();
		author.textProperty().unbind();
		message.getBody().removeListener(bodyChangeListener);
		message.getFormattedBody().removeListener(bodyChangeListener);
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
			if(!node.outerHtml().replace(" ", "").equals("")) {

				String text = ((TextNode) node).getWholeText();
				Text textObject = new Text(text);
				boolean pre = false;

				for(Element element : parents) {
					String tagName = element.tagName();

					if       (tagName.equals("ul")) {
					} else if(tagName.equals("ol")) {
					} else if(tagName.equals("li")) {
						textFlowList.add(new Text(" • "));
					} else if(tagName.equals("blockquote")) {
						textObject.getStyleClass().add("block-quote");
					} else if(tagName.equals("pre")) {
						pre = true;
					} else if(tagName.equals("code")) {
						if(!pre) {
							textObject.getStyleClass().add("inline-monospace");
						} else {
							textObject.getStyleClass().add("block-monospace");
						}
						break; // We don't care about anything appearing within a <code> tag
					} else {
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

		for(org.jsoup.nodes.Node child: node.childNodes()) {
			parseFormattedMessageNode(child, parents);
			//DebugLogger.log(String.format("%s: %s", node.nodeName(), node.toString()));
		}
	}
}
