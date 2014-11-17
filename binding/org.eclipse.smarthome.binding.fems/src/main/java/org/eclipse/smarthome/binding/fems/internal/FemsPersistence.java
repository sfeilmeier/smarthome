/**
 * Copyright (c) 2014 FENECON GmbH & Co. KG.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.smarthome.binding.fems.internal;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import org.eclipse.smarthome.config.core.Configuration;
import org.eclipse.smarthome.core.library.types.DecimalType;
import org.eclipse.smarthome.core.library.types.OnOffType;
import org.eclipse.smarthome.core.library.types.StringType;
import org.eclipse.smarthome.core.types.State;
import org.eclipse.smarthome.core.types.UnDefType;
import org.json.JSONObject;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

/**
 * The {@link FemsPersistence} is responsible for sending data to the FEMS Online-Service.
 * 
 * @author Stefan Feilmeier - Initial contribution
 */
public class FemsPersistence {
	public static volatile String apikey = null;
	private static volatile JsonCache jsonCache = new JsonCache();
	
	public static volatile String femsmonitorUrl = "https://fenecon.de/femsmonitor";
	public static final int protocolVersion = 1;
	private static Logger staticLogger = LoggerFactory.getLogger(FemsPersistence.class);
	public static synchronized void initialize(Configuration config) {
		String apikey = (String)config.get("apikey");
		if(apikey != null) {
			if(apikey.compareTo("")!=0) {
				FemsPersistence.apikey = apikey;
				staticLogger.info("Set Apikey");
			}
		}
		String femsmonitorUrl = (String)config.get("femsmonitorUrl");
		if (femsmonitorUrl != null) {
			FemsPersistence.femsmonitorUrl = femsmonitorUrl;
			staticLogger.info("Set FEMSmonitor-Url to " + femsmonitorUrl);
		}
	}
	
	private static synchronized JSONObject send(JSONObject json) throws Exception {
		if(femsmonitorUrl == null) { throw new Exception("No FEMSmonitor-URL provided. Not sending data!"); }
		if(apikey == null) { throw new Exception("No FEMSmonitor-ApiKey provided. Not sending data!"); }
		HttpsURLConnection con;
		URL url = new URL(femsmonitorUrl);	
		con = (HttpsURLConnection)url.openConnection();
		
		con.setRequestMethod("POST");
		con.setRequestProperty("Content-Type","application/json"); 
		con.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0;Windows98;DigExt)"); 
		con.setDoOutput(true); 
		con.setDoInput(true);
		
		DataOutputStream output = new DataOutputStream(con.getOutputStream());  
		try {
			output.writeBytes(json.toString());
		} finally {
			output.close();
		}

		String content = "unknown";
		if(json.has("content")) {
			content = json.getString("content");
		}
		if(con.getResponseCode() == 200) {
			staticLogger.info("Successfully sent " + content + "-data; server answered: " + con.getResponseMessage());
		} else {
			staticLogger.info("Error while sending " + content + "-data; server response: " + con.getResponseCode() + "; will try again later");
			jsonCache.push(json);
		}
		
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		JSONObject retJson = null;
        try {
            String inputLine = in.readLine();
            if(inputLine != null) {
	        	try {
	        		retJson = new JSONObject(inputLine);
	        	} catch (Exception e) {}
	        }
        } finally {
        	in.close();
        }
        return retJson;
	}
	private String content; // describes the type of content, e.g. "dess", "cess", "weather"
	
	private Logger logger = LoggerFactory.getLogger(FemsPersistence.class);
	
	private JSONObject moreJson;
	
	HashMap<String, State> states = new HashMap<String, State>();
	
	private Date timestamp;
	public FemsPersistence(Date timestamp, String content, JSONObject moreJson) {
		this.timestamp = timestamp;
		this.content = content;
		this.moreJson = moreJson;
	} 
	
	public FemsPersistence(String content) {
		this(new Date(), content, null);
	}
	
	/*
	 * @param content: type of sent data, e.g. 'cess', 'dess', 'io' or 'weather'
	 * @param moreJson: additional values to transfer to the server (like refresh time for DESS/CESS)
	 */
	public FemsPersistence(String content, JSONObject moreJson) {
		this(new Date(), content, moreJson);
	}
	
	public JSONObject getJSON() {
		JSONObject mainJson = new JSONObject();
		mainJson.put("version", protocolVersion);
		mainJson.put("apikey", apikey);
		mainJson.put("timestamp", timestamp.getTime());
		mainJson.put("content", content);
		if(moreJson != null) {
			for(Object k : moreJson.keySet()) {
				String key = (String)k;
				mainJson.put(key, moreJson.get(key));
			}
		}
		
		JSONObject statesJson = new JSONObject(); 
		for (String key : states.keySet()) {
			State state = states.get(key);
			if(state instanceof OnOffType) {
				if((OnOffType)state == OnOffType.ON) {
					statesJson.put(key, 1);
				} else {
					statesJson.put(key, 0);
				}
			} else if (state instanceof UnDefType) {
				statesJson.put(key, JSONObject.NULL);
			} else if (state instanceof StringType) {
				statesJson.put(key, state.toString());
			} else if (state instanceof DecimalType) {
				DecimalType stateDecimal = (DecimalType)state;
				statesJson.put(key, stateDecimal.toBigDecimal());
			} else {
				statesJson.put(key, state.toString());
			}
		}
		mainJson.put("states", statesJson);
		return mainJson;
	}
	
	public final Map<String, State> getStates() {
		return states;
	}
	
	public void putAllStates(Map<String, State> map) {
		states.putAll(map);
	}
		
	public void putState(String name, State state) {
		states.put(name, state);
	}
	
	public synchronized JSONObject send() throws Exception {
		if(apikey == null) { throw new Exception("No apikey provided. Not sending data!"); }
		
		JSONObject sendJson = getJSON();	
		JSONObject returnJson = send(sendJson);

		// no error happend: try sending the cached requests
		try {
			synchronized (jsonCache) {
				while(!jsonCache.isEmpty()) {
					JSONObject cachedJson = jsonCache.pop();
					Date cachedTime = null;
					if(cachedJson.has("timestamp")) {
						cachedTime = new Date(cachedJson.getLong("timestamp"));
					}
					logger.info("Trying to send cached data " + (cachedTime != null ? "from " + cachedTime.toString() : ""));
					send(cachedJson);
				}
			}
		} catch (Exception e) {
			logger.warn("Error sending cached data");
		}
		return returnJson;
	}
}
