package com.github.cypher.sdk.api;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

/*
  This class allows for use of the mxc:// custom url schema used by the Matrix protocol
 */
public class MatrixMediaURLStreamHandler extends URLStreamHandler {
	@Override
	protected URLConnection openConnection(URL url) throws IOException {
		return new MatrixMediaURLConnection(url);
	}
}
