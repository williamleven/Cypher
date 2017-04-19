package com.github.cypher.sdk.api;

import com.github.cypher.DebugLogger;

import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;

/*
  This class allows for use of the mxc:// custom url schema used by the Matrix protocol
 */
public class MatrixMediaURLStreamHandlerFactory implements URLStreamHandlerFactory {
	@Override
	public URLStreamHandler createURLStreamHandler(String protocol) {
		if ("mxc".equals(protocol)) {
			return new MatrixMediaURLStreamHandler();
		}
		return null;
	}
}
