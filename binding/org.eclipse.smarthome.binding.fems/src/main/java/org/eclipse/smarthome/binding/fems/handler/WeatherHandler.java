/**
 * Copyright (c) 2014 FENECON GmbH & Co. KG.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.smarthome.binding.fems.handler;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.bitpipeline.lib.owm.ForecastWeatherData;
import org.bitpipeline.lib.owm.LocalizedWeatherData;
import org.bitpipeline.lib.owm.OwmClient;
import org.bitpipeline.lib.owm.StatusWeatherData;
import org.bitpipeline.lib.owm.WeatherForecastResponse;
import org.eclipse.smarthome.binding.fems.internal.FemsPersistence;
import org.eclipse.smarthome.config.core.Configuration;
import org.eclipse.smarthome.core.library.types.DecimalType;
import org.eclipse.smarthome.core.library.types.PercentType;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandler;
import org.eclipse.smarthome.core.types.Command;
import org.eclipse.smarthome.core.types.State;
import org.eclipse.smarthome.core.types.UnDefType;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WeatherHandler extends BaseThingHandler {
	private Logger logger = LoggerFactory.getLogger(WeatherHandler.class);

	protected int refresh = 60 * 30; // refresh every 30 minutes as default
	protected String appid = null;
	protected long cityid = 0;
	
	ScheduledFuture<?> refreshJob;
	
	public WeatherHandler(Thing thing) {
		super(thing);
	}

	@Override
	public void handleCommand(ChannelUID channelUID, Command command) {
		logger.info("handleCommand()");
	}

	@Override
	public void dispose() {
		super.dispose();
		refreshJob.cancel(true);
	}
	
	@Override
	public void initialize() {
		super.initialize();
		
		// Read configuration
		Configuration config = getThing().getConfiguration();
		try {
			refresh = Integer.parseInt((String)config.get("refresh"));
			logger.info("Set refresh to " + refresh);
		} catch(Exception e) {
			// let's ignore it and go for the default
		}
		try {
			cityid = Long.parseLong((String)config.get("cityid"));
			if(cityid == 0) {
				throw new Exception("OpenWeatherMap CityID not provided - Weather deactivated");
			}
			logger.info("Set cityId to " + cityid);
		} catch(Exception e) {
			logger.warn(e.getMessage());
		}
		try {
			appid = (String)config.get("appid");
			if(appid == null) {
				throw new Exception("OpenWeatherMap AppID not provided - Weather deactivated");
			}
			logger.info("Set AppID");
		} catch(Exception e) {
			logger.warn(e.getMessage());
		}
		
		FemsPersistence.initialize(config);
		
		// Start refresh service
		if (appid != null && cityid != 0) {
			startAutomaticRefresh();
		}
	}
	
	private void startAutomaticRefresh() {
		final Runnable runnable = new Runnable() {
			private Map<String, State> getItems(String prefix, LocalizedWeatherData w) {
				prefix = "OWM_" + prefix;
				HashMap<String, State> map = new HashMap<String, State>();
				String name = prefix + "clouds";
				State state;
				if (w.hasClouds() && w.getClouds().hasAll()) {
					state = new PercentType( new BigDecimal(w.getClouds().getAll()) );
				} else {
					state = UnDefType.UNDEF;
				}
				map.put(name, state);
				
				/* not taken: 
				 * 	if (c.hasConditions()) { logger.info("Conditions: " + c.getConditions()); } 
				 *	if (w.hasCoord()) {
				 *		GeoCoord c = w.getCoord();
				 *		if (c.hasLatitude()) { logger.info("Latitude: " + c.getLatitude()); }
				 *		if (c.hasLongitude()) { logger.info("Longitude: " + c.getLongitude()); }
				 *	}
				 *	logger.info("Date: " + w.getDateTime());
				 *	if (w.hasDistance()) { logger.info(Long.toString(w.getDateTime())); }
				 *	if (w.hasId()) { logger.info("ID: " + w.getId()); }
				 */
				name = prefix + "humidity";
				if (w.hasMain() && w.getMain().hasHumidity()) {
					state = new PercentType( new BigDecimal(w.getMain().getHumidity()).setScale(2, BigDecimal.ROUND_HALF_UP) );
				} else {
					state = UnDefType.UNDEF;
				}
				map.put(name, state);
				
				name = prefix + "pressure";
				if (w.hasMain() && w.getMain().hasPressure()) {
					state = new DecimalType( new BigDecimal(w.getMain().getPressure()).setScale(2, BigDecimal.ROUND_HALF_UP) );
				} else {
					state = UnDefType.UNDEF;
				}
				map.put(name, state);
				
				name = prefix + "temp";
				if (w.hasMain() && w.getMain().hasTemp()) {
					state = new DecimalType( new BigDecimal(w.getMain().getTemp()).setScale(2, BigDecimal.ROUND_HALF_UP) );
				} else {
					state = UnDefType.UNDEF;
				}
				map.put(name, state);
				
				/* not taken
				 *	if (m.hasTempMax())		{ logger.info("TempMax : " + m.getTempMax()); }
				 *	if (m.hasTempMin())		{ logger.info("TempMin : " + m.getTempMin()); }
				 *	if (w.hasName()) { logger.info("Name: " + w.getName()); }
				 */
				// TODO: Fix error with wrong values			
//				name = "rain";
//				if (w.hasRain()) { 
//					state = new DecimalType( w.getRain() );
//				} else {
//					state = UnDefType.UNDEF;
//				}
//				updateState(new ChannelUID(getThing().getUID(), name), state );
//				persist.putState(name, state);
				
				name = prefix + "snow";
				if (w.hasSnow()) {
					state = new DecimalType( new BigDecimal(w.getSnow()).setScale(2, BigDecimal.ROUND_HALF_UP) );
				} else {
					state = UnDefType.UNDEF;
				}
				map.put(name, state);
				
				/* not taken
				 *	if (w.hasStation()) {
				 *		Station s = w.getStation();
				 *		if (s.hasZoom()) { logger.info("Zoom: " + s.getZoom()); }
				 *	}
				 *	if (w.hasUrl()) { logger.info("URL: " + w.getUrl()); }
				 *	if (w.hasWeatherConditions()) {
				 *		for (WeatherCondition c : w.getWeatherConditions()) {
				 *			if (c.hasDescription()) { logger.info("Description: " + c.getDescription()); }
				 *			if (c.hasIconName()) { logger.info("Iconname: " + c.getIconName()); }
				 *			if (c.hasMain()) { logger.info("Main: " + c.getMain()); }
				 *		}
				 *	} 
				 */
				name = prefix + "wind_degrees"; 
				if (w.hasWind() && w.getWind().hasDeg()) {
					state = new DecimalType( new BigDecimal(w.getWind().getDeg()).setScale(2, BigDecimal.ROUND_HALF_UP) );
				} else {
					state = UnDefType.UNDEF;
				}
				map.put(name, state);
				
				name = prefix + "wind_speed";
				if (w.hasWind() && w.getWind().hasDeg()) {
					state = new DecimalType( new BigDecimal(w.getWind().getDeg()).setScale(2, BigDecimal.ROUND_HALF_UP) );
				} else {
					state = UnDefType.UNDEF;
				}
				map.put(name, state);
				
				/* not taken
				 * 	if (d.hasGust()) { logger.info("Wind gust: " + d.getGust()); }
				 *	if (d.hasVarBeg()) { logger.info("VarBeg: " + d.getVarBeg()); }
				 *	if (d.hasVarEnd()) { logger.info("VarEnd: " + d.getVarEnd()); }
				 */
				
				return map;
			}
			
			public void run() {
				try {
					OwmClient owm = new OwmClient();
					owm.setAPPID(appid);
					JSONObject moreJson = new JSONObject();
					moreJson.put("cityid", cityid);
					FemsPersistence persist = new FemsPersistence("weather", moreJson); 
					
					/*
					 * Current weather
					 */
					
					StatusWeatherData currentWeatherData = owm.currentWeatherAtCity(cityid);
					Map<String, State> currentWeatherDataMap = getItems("cur_", currentWeatherData);
					persist.putAllStates(currentWeatherDataMap);
					
					/*
					 * Forecast
					 */
					
					WeatherForecastResponse forecastResponse = owm.forecastWeatherAtCity(cityid);
					List<ForecastWeatherData> forecastWeatherDataList = forecastResponse.getForecasts();
					for (int i = 0; i < (forecastWeatherDataList.size() > 2 ? 3 : forecastWeatherDataList.size()); i++) {
						Map<String, State> forecastWeatherDataMap = getItems("fc" + i + "_", forecastWeatherDataList.get(i));
						persist.putAllStates(forecastWeatherDataMap);
					}
					
					// Persist
					for (String name : persist.getStates().keySet()) {
						updateState(new ChannelUID(getThing().getUID(), name), persist.getStates().get(name) );
					}
					try {
						JSONObject json = persist.send();
						
						// Evaluate return data
			        	if(json != null && json.has("cityid")) {
			        		int newCityid = json.getInt("cityid");
							if(newCityid != cityid) {
								logger.info("Set cityid to " + newCityid + " on behalf of FEMS server");
								cityid = newCityid;
								run(); // send again
							}
			        	}
					} catch (Exception e) {
						logger.error("Persistence error: " + e.getMessage());
					}
					
				} catch(Exception e) {
					logger.error("Exception occurred during execution: {}", e.getMessage());
				}
			}
		};
		refreshJob = scheduler.scheduleAtFixedRate(runnable, 0, refresh, TimeUnit.SECONDS);
	}
}
