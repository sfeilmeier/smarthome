/**
 * Copyright (c) 2014 openHAB UG (haftungsbeschraenkt) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.smarthome.binding.wemo.handler;

import static org.eclipse.smarthome.binding.wemo.WemoBindingConstants.CHANNEL_STATE;
import static org.eclipse.smarthome.binding.wemo.WemoBindingConstants.UDN;
import static org.eclipse.smarthome.binding.wemo.WemoBindingConstants.WEMO_INSIGHT_TYPE_UID;
import static org.eclipse.smarthome.binding.wemo.WemoBindingConstants.WEMO_LIGHTSWITCH_TYPE_UID;
import static org.eclipse.smarthome.binding.wemo.WemoBindingConstants.WEMO_SOCKET_TYPE_UID;

import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URL;
import java.util.Set;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.smarthome.config.core.Configuration;
import org.eclipse.smarthome.core.library.types.OnOffType;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.ThingTypeUID;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandler;
import org.eclipse.smarthome.core.types.Command;
import org.eclipse.smarthome.core.types.State;
import org.eclipse.smarthome.io.transport.upnp.UpnpIOParticipant;
import org.eclipse.smarthome.io.transport.upnp.UpnpIOService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Sets;


/**
 * The {@link WemoHandler} is responsible for handling commands, which are
 * sent to one of the channels and to update their states.
 * 
 * @author Hans-Jörg Merk - Initial contribution
 * @author Kai Kreuzer - some refactoring for performance and simplification
 */
public class WemoHandler extends BaseThingHandler implements UpnpIOParticipant {

    private static final Logger logger = LoggerFactory.getLogger(WemoHandler.class);
    
    public final static Set<ThingTypeUID> SUPPORTED_THING_TYPES = Sets.newHashSet(WEMO_SOCKET_TYPE_UID, WEMO_INSIGHT_TYPE_UID, WEMO_LIGHTSWITCH_TYPE_UID);

    private static String getRequestXML;
    private static String setRequestXML;

    static {
    	try {
    		getRequestXML = IOUtils.toString(WemoHandler.class.getResourceAsStream("/org/eclipse/smarthome/binding/wemo/internal/GetRequest.xml"));
    		setRequestXML = IOUtils.toString(WemoHandler.class.getResourceAsStream("/org/eclipse/smarthome/binding/wemo/internal/SetRequest.xml"));
    	} catch(Exception e) {
    		logger.error("Cannot read XML files!", e);
    	}
    }

	private UpnpIOService service;

	/**
	 * The default refresh interval in Seconds.
	 */
	private int refresh = 10;
    

	private ScheduledFuture<?> refreshJob;

    public WemoHandler(Thing thing, UpnpIOService upnpIOService) {
		super(thing);
		
		logger.debug("Create a WemoHandler for thing '{}'", getThing().getUID());
		
		if (upnpIOService != null) {
			this.service = upnpIOService;
		} else {
			logger.debug("upnpIOService not set.");
		}
    }

 
    @Override
	public void initialize() {
		Configuration configuration = getConfig();
    	
    	if (configuration.get(UDN) != null) {
        	logger.debug("Initializing WemoHandler for UDN '{}'", configuration.get(UDN));
			onSubscription();
			onUpdate();
		} else {
			logger.debug("Cannot initalize WemoHandler. UDN not set.");
		}
    			
    	if (getThing().getStatus() == ThingStatus.OFFLINE) {
    		logger.debug("Setting status for thing '{}' to ONLINE", getThing().getUID());
    		getThing().setStatus(ThingStatus.ONLINE);
    	}
    	startAutomaticRefresh();
	}

    @Override
    public void dispose() {
    	refreshJob.cancel(true);
		
		if (getThing().getStatus() == ThingStatus.ONLINE) {
			logger.debug("Setting status for thing '{}' to OFFLINE", getThing()
					.getUID());
			getThing().setStatus(ThingStatus.OFFLINE);
		}
    }
	
    private void startAutomaticRefresh() {
    	
    	Runnable runnable = new Runnable() {
			public void run() {
				try {
			    	logger.debug("Refreshing thing '{}'", getThing().getUID());
			    	State state = getWemoState(new ChannelUID(getThing().getUID(), CHANNEL_STATE));
			    	if(state != null) {
			    		updateState(new ChannelUID(getThing().getUID(), CHANNEL_STATE), state);
			    	}
				} catch(Exception e) {
					logger.debug("Exception occurred during Refresh: {}", e);
				}
			}
		};
		
		refreshJob = scheduler.scheduleAtFixedRate(runnable, 0, refresh, TimeUnit.SECONDS);
	}

	@Override
	public void handleCommand(ChannelUID channelUID, Command command) {
    	logger.debug("Command '{}' received for channel '{}'", command, channelUID);
        if(channelUID.getId().equals(CHANNEL_STATE)) {
        	if (command instanceof OnOffType) {
        		try {
    				boolean onOff = OnOffType.ON.equals(command);
    				logger.debug("command '{}' transformed to '{}'", command, onOff); 
    				String wemoCallResponse = wemoCall(channelUID,
    						"urn:Belkin:service:basicevent:1#SetBinaryState",
    						setRequestXML.replace("{{state}}", onOff ? "1" : "0"));

    				logger.trace("WeMo setOn = {}", wemoCallResponse);
    				
    			} catch (Exception e) {
    				logger.error("Failed to send command '{}' for device '{}' ", command, getThing().getUID(), e);
    			}
        	}
        }
	}

	
	public void onValueReceived(String variable, String value, String service) {
		// Due to the bad upnp implementation of the WeMo devices, this will be done at a later time.
	}
	
	private synchronized void onSubscription() {
		// Set up GENA Subscriptions
		// Due to the bad upnp implementation of the WeMo devices, this will be done at a later time.
	}

	private synchronized void onUpdate() {
		if (service.isRegistered(this)) {
			// Due to the bad upnp implementation of the WeMo devices, this will be done at a later time.
		}
	}

	public String getUDN() {
		return (String) this.getThing().getConfiguration().get(UDN);
	}

	/**
	 * The {@link wemoCall} is responsible for communicating with the WeMo devices by sending SOAP messages
	 * sent to one of the channels.
	 * 
	 */
	private String wemoCall(ChannelUID channelUID, String soapMethod, String content) {
		try {
			
			String endpoint = "/upnp/control/basicevent1";

			URL wemoDescriptorURL = service.getDescriptorURL(this);

			if (wemoDescriptorURL != null) {
				logger.debug("WeMo descriptorURL found as '{}' ", wemoDescriptorURL);
				String wemoLocation = StringUtils.substringBefore(wemoDescriptorURL.toString(), "/setup.xml");
				
				if (wemoLocation != null && endpoint != null) {
					logger.debug("item '{}' is located at '{}'", getThing().getUID(), wemoLocation);
					URL url = new URL(wemoLocation + endpoint);
					try (Socket wemoSocket = new Socket()) {
						wemoSocket.connect(new InetSocketAddress(url.getHost(), url.getPort()), 2000);
						OutputStream wemoOutputStream = wemoSocket.getOutputStream();
						StringBuffer wemoStringBuffer = new StringBuffer();
						wemoStringBuffer.append("POST " + url + " HTTP/1.1\r\n");
						wemoStringBuffer.append("Content-Type: text/xml; charset=utf-8\r\n");
						wemoStringBuffer.append("Content-Length: " + content.getBytes().length + "\r\n");
						wemoStringBuffer.append("SOAPACTION: \"" + soapMethod + "\"\r\n");
						wemoStringBuffer.append("\r\n");
						wemoOutputStream.write(wemoStringBuffer.toString().getBytes());
						wemoOutputStream.write(content.getBytes());
						wemoOutputStream.flush();
						String wemoCallResponse = IOUtils.toString(wemoSocket.getInputStream());
						getThing().setStatus(ThingStatus.ONLINE);
						return wemoCallResponse;
					} catch(Exception e) {
						logger.debug("Could not send request to WeMo device '{}': {}", getThing().getUID(), e.getMessage());
						getThing().setStatus(ThingStatus.OFFLINE);
					}
					return null;
				} else {
					return null;
				}		
			} else {
				// device was not found in the upnp registry
				getThing().setStatus(ThingStatus.OFFLINE);
				return null;
			}
			
		} catch (Exception e) {
			logger.error("Could not call WeMo device '{}'", getThing().getUID(), e);
			return null;

		}
	}
	
	private State getWemoState(ChannelUID channelUID) {
		String stateRequest = null;
		String returnState = null;

			try {
				stateRequest = wemoCall(channelUID,
						"urn:Belkin:service:basicevent:1#GetBinaryState", getRequestXML);
				if(stateRequest != null) {
					returnState = StringUtils.substringBetween(stateRequest, "<BinaryState>", "</BinaryState>");
					
					logger.debug("New binary state '{}' for device '{}' received", returnState, getThing().getUID() );
				}
			} catch (Exception e) {
				logger.error("Failed to get binary state for device '{}'", getThing().getUID(), e);
			}

			if (returnState != null) {
				OnOffType newState = null;
				newState = returnState.equals("0") ? OnOffType.OFF : OnOffType.ON;
				return newState;
		} else {
			return null;
		}
	}

}
