/**
 * Copyright (c) 2014 FENECON GmbH & Co. KG.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.smarthome.binding.fems.handler;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Date;
import java.util.Enumeration;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import net.wimpi.modbus.ModbusException;

import org.eclipse.smarthome.binding.fems.internal.FemsPersistence;
import org.eclipse.smarthome.binding.fems.internal.essprotocol.BitWordElement;
import org.eclipse.smarthome.binding.fems.internal.essprotocol.EssProtocol;
import org.eclipse.smarthome.binding.fems.internal.essprotocol.ModbusElement;
import org.eclipse.smarthome.binding.fems.internal.essprotocol.ModbusElementRange;
import org.eclipse.smarthome.binding.fems.internal.essprotocol.ModbusItem;
import org.eclipse.smarthome.binding.fems.internal.essprotocol.OnOffBitItem;
import org.eclipse.smarthome.config.core.Configuration;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandler;
import org.eclipse.smarthome.core.types.Command;
import org.eclipse.smarthome.core.types.RefreshType;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class EssHandler extends BaseThingHandler {
	private Logger logger = LoggerFactory.getLogger(EssHandler.class);
	
	public EssHandler(Thing thing) {
		super(thing);
	}
	
	protected int refresh = 60; // refresh every minute as default 
	protected int unitid = 100;
	protected String modbusinterface = "/dev/ttyUSB0";
	protected EssProtocol protocol;
	
	ScheduledFuture<?> refreshJob;
	
	@Override
	public void dispose() {
		super.dispose();
		refreshJob.cancel(true);
		
		protocol.dispose();
	}
	
	protected abstract EssProtocol getProtocol();
	
	@Override
	public void initialize() {
		super.initialize();
		
		// Read configuration
		Configuration config = getThing().getConfiguration();
		try {
			String refreshString = (String)config.get("refresh");
			if(refreshString != null) {
				refresh = Integer.parseInt(refreshString);
			}
			logger.info("Set refresh to " + refresh);
		} catch(Exception e) { /* let's ignore it and go for the default */ }
		
		try {
			String unitidString = (String)config.get("unitid");
			if(unitidString != null) {
				unitid = Integer.parseInt(unitidString);
			}
			logger.info("Set Unit-ID to " + unitid);
		} catch(Exception e) { /* let's ignore it and go for the default */ }
		
		try {
			String modbusinterfaceString = (String)config.get("modbusinterface");
			if(modbusinterfaceString != null) {
				modbusinterface = modbusinterfaceString;
			}
			logger.info("Set Modbus-Interface to " + modbusinterface);
		} catch(Exception e) { /* let's ignore it and go for the default */ }

		FemsPersistence.initialize(config);
		
		protocol = getProtocol();
		
		// Start refresh service
		startAutomaticRefresh();
	}
	
	private void startAutomaticRefresh() {
		final Runnable runnable = new Runnable() {
			private int totalWaitTime = 0; // counts the total waited time in seconds for ModbusErrors
			public void run() {
				try {
					protocol.updateData();
					for (ModbusElementRange wordRange : protocol.getWordRanges()) {
						for (ModbusElement word : wordRange.getWords()) {
							if(word instanceof ModbusItem) {
								ModbusItem item = (ModbusItem)word;
//								logger.info("Number " + item.getName());
								updateState(new ChannelUID(getThing().getUID(), item.getName()), item.getState());
							} else if (word instanceof BitWordElement) {
								BitWordElement bitWord = (BitWordElement)word;
								for (OnOffBitItem bitItem : bitWord.getBitItems()) {
//									logger.info("Switch " + bitWord.getName() + "_" + bitItem.getName());
									updateState(new ChannelUID(getThing().getUID(), bitWord.getName() + "_" + bitItem.getName()), bitItem.getState());
								} 
							}
						}
					}
					
					// no error happened: kick the watchdog
					logger.info("No error: kick Watchdog @ " + new Date().toString());
				    Runtime.getRuntime().exec("/bin/systemd-notify WATCHDOG=1");
					
					// prepare data to be transfered
					JSONObject moreJson = new JSONObject();
					moreJson.put("refresh", refresh); // currently set refresh period
					try {
						NetworkInterface n = NetworkInterface.getByName("eth0");
						Enumeration<InetAddress> ee = n.getInetAddresses();
						while (ee.hasMoreElements()) {
							InetAddress i = (InetAddress) ee.nextElement();
							if(i instanceof Inet4Address) {
								moreJson.put("ipv4", i.getHostAddress()); // local ipv4 address
					        }
					    }
					} catch (SocketException e) { /* no IP-Address - ignore */ }
					JSONObject json = protocol.persistData(moreJson);
		        	if(json != null && json.has("refresh")) {
		        		int newRefresh = json.getInt("refresh");
						if(newRefresh != refresh) {
							logger.info("Set refresh to " + newRefresh + " on behalf of FEMS server");
							refresh = newRefresh;
							ScheduledFuture<?> oldRefreshJob = refreshJob;
							Thread.sleep(refresh*1000);
							startAutomaticRefresh();
							oldRefreshJob.cancel(false);
						}
		        	}
		        	totalWaitTime = 0;
				} catch(ModbusException e) {
					// modbus error: Try again within a timespan of 10 to 30 seconds:
					int min = 10; int max = 30;
					try {
						int waitTime = (int)(Math.random() * (max - min) + min);
						totalWaitTime += waitTime;
						if(totalWaitTime > refresh) {
							logger.info("Not waiting anymore... hoping for next regular run");
							totalWaitTime = 0;
						} else {
							logger.info("Try again in " + waitTime + " seconds");
							Thread.sleep((int)(Math.random() * (max - min) + min)*1000);
							run();
						}
					} catch (InterruptedException e1) { ; }
					
				} catch(Exception e) {
					logger.error("Exception occurred during execution: {}", e.getMessage());
					protocol.dispose();
				}
			}
		};
		refreshJob = scheduler.scheduleAtFixedRate(runnable, 0, refresh, TimeUnit.SECONDS);
	}
	
	@Override
	public void handleCommand(ChannelUID channelUID, Command command) {
		logger.info("handleCommand()");
		logger.info("ChannelUID: " + channelUID);
		logger.info("Command: " + command);
	    if (command instanceof RefreshType) {
	        try {
	        	protocol.updateData();
			} catch (Exception e) {
				e.printStackTrace();
				return;
			}
	        logger.info("TODO");
	        // TODO updateState(channelUID, getModbusData(channelUID.getId()));
	    }
	}
}
