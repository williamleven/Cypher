package com.github.cypher.sdk.api;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

/*
  This class allows for use of the mxc:// custom url schema used by the Matrix protocol
 */
public class MatrixMediaURLConnection extends URLConnection {
	protected MatrixMediaURLConnection(URL url) {
		super(url);
	}

	@Override
	public void connect() throws IOException {}
}
