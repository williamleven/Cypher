package com.github.cypher;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

public class Util {
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

	static String capitalize(String name) {
		return name.substring(0, 1).toUpperCase() + name.substring(1);
	}

	static String decapitalize(String name) {
		return name.substring(0, 1).toLowerCase() + name.substring(1);
	}

	static Image getIconImage() {
		Image image = null;
		try {
			File pathToFile = new File(Util.class.getResource("/icon/small.gif").toURI());
			image = ImageIO.read(pathToFile);
		} catch (URISyntaxException | IOException ex) {
			DebugLogger.log(ex);
		}
		return image;
	}

	static MenuItem createMenuItem(String label, ActionListener listener) {
		MenuItem item = new MenuItem(label);
		item.addActionListener(listener);
		return item;
	}
}
