package com.github.cypher.model;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public final class Util {

	// Check if sting could be a roomcollection
	static boolean isHomeserver(String s) {
		return s.matches("^(https:\\/\\/|[a-zA-Z0-9-])([a-zA-Z0-9-]+\\.[a-zA-Z0-9]+)+(:[0-9]+)?$");
	}

	// Check if sting could be a room label
	static boolean isRoomLabel(String s) {
		return s.matches("^(!|#)[a-zA-Z0-9_\\.-]+:([a-zA-Z0-9]+\\.[a-zA-Z0-9]+)+(:[0-9]+)?$");
	}

	// Check if sting could be a user
	static boolean isUser(String s) {
		return s.matches("^(@|[a-zA-Z0-9_\\.-])[a-zA-Z0-9_\\.-]+:([a-zA-Z0-9]+\\.[a-zA-Z0-9]+)+(:[0-9]+)?$");
	}

	static String extractServer(String input) {
		String[] splitString = input.split(":", 2);
		return splitString[splitString.length - 1];
	}

	/**
	 * Code taken from: <a href="https://community.oracle.com/thread/2238566">oracle community.</a>
	 * Comments and slight modifications added.
	 */
	public static javafx.scene.image.Image createImage(java.awt.Image image) throws IOException {

		java.awt.Image imageReady;

		// Make sure the image is rendered
		if (image instanceof RenderedImage) {
			imageReady = image;

		}else {
			BufferedImage bufferedImage = new BufferedImage(image.getWidth(null),
			                                                image.getHeight(null),
			                                                BufferedImage.TYPE_INT_ARGB);

			// Render the image
			Graphics g = bufferedImage.createGraphics();
			g.drawImage(image, 0, 0, null);
			g.dispose();

			imageReady = bufferedImage;
		}

		// Convert image to byte array
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ImageIO.write((RenderedImage) imageReady, "png", out);
		out.flush();

		// Construct FX-image from byte array
		ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
		return new javafx.scene.image.Image(in);
	}

	/**
	 * Code taken from: <a href="https://stackoverflow.com/a/40699460">Stackoverflow answer from Kevin G. based on code from davidhampgonsalves.com/Identicons</a>
	 * Comments and slight modifications added.
	 */
	public static javafx.scene.image.Image generateIdenticon(String text, int image_width, int image_height) throws IOException {
		// If the input name/text is null or empty no image can be created.
		if (text == null || text.length() < 3) {
			return null;
		}

		int width = 5, height = 5;

		byte[] hash = text.getBytes();

		BufferedImage identicon = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		WritableRaster raster = identicon.getRaster();

		int [] background = new int [] {255,255,255, 0};
		int [] foreground = new int [] {hash[0] & 255, hash[1] & 255, hash[2] & 255, 255};

		for(int x=0 ; x < width ; x++) {
			//Enforce horizontal symmetry
			int i = x < 3 ? x : 4 - x;
			for(int y=0 ; y < height; y++) {
				int [] pixelColor;
				//toggle pixels based on bit being on/off
				if((hash[i] >> y & 1) == 1)
					pixelColor = foreground;
				else
					pixelColor = background;
				raster.setPixel(x, y, pixelColor);
			}
		}

		BufferedImage finalImage = new BufferedImage(image_width, image_height, BufferedImage.TYPE_INT_ARGB);

		//Scale image to the size you want
		AffineTransform at = new AffineTransform();
		at.scale(image_width / width, image_height / height);
		AffineTransformOp op = new AffineTransformOp(at, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
		finalImage = op.filter(identicon, finalImage);

		// Convert BufferedImage to javafx image
		return createImage(finalImage);
	}

	private Util(){}
}
