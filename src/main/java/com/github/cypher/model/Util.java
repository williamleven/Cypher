package com.github.cypher.model;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class Util {

	// Check if sting could be a roomcollection
	static boolean isHomeserver(String s) {
		//Todo
		return true;
	}

	// Check if sting could be a room label
	static boolean isRoomLabel(String s) {
		//Todo
		return true;
	}

	// Check if sting could be a user
	static boolean isUser(String s) {
		//Todo
		return true;
	}

	/**
	 * Code taken from: <a href="https://community.oracle.com/thread/2238566">oracle community.</a>
	 * Comments added.
	 */
	public static javafx.scene.image.Image createImage(java.awt.Image image) throws IOException {

		// Make sure the image is rendered
		if (!(image instanceof RenderedImage)) {
			BufferedImage bufferedImage = new BufferedImage(image.getWidth(null),
				image.getHeight(null), BufferedImage.TYPE_INT_ARGB);

			// Render the image
			Graphics g = bufferedImage.createGraphics();
			g.drawImage(image, 0, 0, null);
			g.dispose();

			image = bufferedImage;
		}

		// Convert image to byte array
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ImageIO.write((RenderedImage) image, "png", out);
		out.flush();

		// Construct FX-image from byte array
		ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
		return new javafx.scene.image.Image(in);
	}
}
