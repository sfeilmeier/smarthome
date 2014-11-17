/**
 * Copyright (c) 2014 FENECON GmbH & Co. KG.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.smarthome.binding.fems.internal;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Stack;

import org.json.JSONObject;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

/**
 * The {@link JsonCache} is able to cache JSONObjects in non-volatile files.
 * 
 * @author Stefan Feilmeier - Initial contribution
 */
public class JsonCache {
	private class JSONCacheObject {
		public Path path;
		public JSONObject jsonObject;
		public JSONCacheObject(Path path, JSONObject jsonObject) {
			this.path = path;
			this.jsonObject = jsonObject;
		}
	}
	
	private static final Stack<JSONCacheObject> stack = new Stack<JSONCacheObject>();
	private static final String cacheFilePrefix = "cache.";
	private static final Charset defaultCharset = StandardCharsets.ISO_8859_1;
	private Logger logger = LoggerFactory.getLogger(JsonCache.class);
			
	public JsonCache() {
		// Load all cached entries on start up
		try (DirectoryStream<Path> cacheFiles = Files.newDirectoryStream(
				Paths.get(System.getProperty("user.dir")), cacheFilePrefix + '*')) {
		    for(Path cacheFile : cacheFiles) {
		    	String jsonString = "";
		    	for(String line : Files.readAllLines(cacheFile, defaultCharset)) {
		    		jsonString += line;
		    	}
		    	if(jsonString.compareTo("")!=0) {
		    		stack.push(new JSONCacheObject(cacheFile, new JSONObject(jsonString)));
		    	}
		    }
		} catch(Exception e) {
			logger.error("Unable to load cached files: " + e.getMessage());
		}
	}

	public synchronized JSONObject pop() {
		JSONCacheObject obj = stack.pop();
		try {
			Files.delete(obj.path);
		} catch (IOException e) {
			logger.error("Could not delete temporary file " + obj.path + ": " + e.getMessage());
		}
		return obj.jsonObject;
	}

	public synchronized void push(JSONObject obj) {
		try {
			File cacheFile = File.createTempFile(cacheFilePrefix, "", Paths.get(System.getProperty("user.dir")).toFile());
			stack.push(new JSONCacheObject(cacheFile.toPath(), obj));
			Files.write(cacheFile.toPath(), obj.toString().getBytes(),
					StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.SYNC);
		} catch (IOException e) {
			logger.error("Unable to cache " + obj.toString() + ": " + e.getMessage());
		}
	}
	
	public synchronized boolean isEmpty() {
		return stack.isEmpty();
	}
}
