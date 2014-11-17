/**
 * Copyright (c) 2014 FENECON GmbH & Co. KG.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.smarthome.binding.fems.handler;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import org.eclipse.smarthome.binding.fems.internal.io.AnalogOutput;
import org.eclipse.smarthome.binding.fems.internal.io.DigitalOutput;
import org.eclipse.smarthome.binding.fems.internal.io.GpioPin;
import org.eclipse.smarthome.binding.fems.internal.io.Io;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandler;
import org.eclipse.smarthome.core.types.Command;
import org.eclipse.smarthome.core.types.State;
import org.eclipse.smarthome.config.core.Configuration;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

/**
 * The {@link IoHandler} is responsible for handling commands, which are
 * sent to one of the channels.
 * 
 * @author Stefan Feilmeier - Initial contribution
 */
public class IoHandler extends BaseThingHandler {
	   private Logger logger = LoggerFactory.getLogger(IoHandler.class);
	   private HashMap<String, Io> gpios = null;
	   
		public IoHandler(Thing thing) {
			super(thing);
		}
		
		@Override
		public void initialize() {
			super.initialize();
			Configuration config = getThing().getConfiguration();
//X			FemsPersistence.initialize(config);
			
			System.out.println("X 1");
//X			Board board = Platform.createBoard();
			System.out.println("X 2");
//X			org.bulldog.core.gpio.DigitalOutput output = board.getPin(BBBNames.P8_12).as(org.bulldog.core.gpio.DigitalOutput.class);
			System.out.println("X 3");
//X	        output.high();
//X	        BulldogUtil.sleepMs(500);
//X	        output.low();
//X	        BulldogUtil.sleepMs(500); 
			
			
			
			gpios = new HashMap<String, Io>();
			gpios.put("RelayOutput_1", new DigitalOutput(44));
			gpios.put("RelayOutput_2", new DigitalOutput(45));
			gpios.put("RelayOutput_3", new DigitalOutput(46));
			gpios.put("RelayOutput_4", new DigitalOutput(47));
			gpios.put("AnalogOutput_1", new AnalogOutput("pwm_test_P9_14.*", new GpioPin(113), AnalogOutput.DIVIDE.VOLTAGE));
			gpios.put("AnalogOutput_3", new AnalogOutput("pwm_test_P8_19.*", new GpioPin(112), AnalogOutput.DIVIDE.VOLTAGE));

			Runnable runnable = new Runnable() {
				public void run() {
					for (String name : gpios.keySet()) {
						try {
							Io io = gpios.get(name);
							State state = io.getState();
							updateState(new ChannelUID(getThing().getUID(), name), state);
						} catch (Exception e) {
							logger.error("Error while updating " + name + ": " + e.getMessage());
							e.printStackTrace();
						}					
					}
				}
			};
			scheduler.schedule(runnable, 5, TimeUnit.SECONDS);
		}
		
		@Override
		public void dispose() {
			for (String name : gpios.keySet()) {
				try {
					gpios.get(name).dispose();
				} catch (Exception e) {
					logger.warn(e.getMessage());
				}
			}
			gpios.clear();
			super.dispose();
		}
		
		@Override
		public void handleCommand(ChannelUID channelUID, Command command) {
			if(gpios != null) {
				Io io = gpios.get(channelUID.getId());
				try {
					io.handleCommand(command);
					
					try {
//X						FemsPersistence persist = new FemsPersistence("io");
//X						persist.putState(channelUID.getId(), io.getState());
//X						persist.send();
//X					} catch (IOException e) {
//X						logger.error("Cannot send HTTPS request: " + e.getMessage());
					} catch (Exception e) {
						logger.error("Error while persisting to FEMSmonitor: " + e.getMessage());
					}
				} catch (Exception e) {
					logger.error("Error while handling command: " + channelUID.toString() + ": " + command.toString());
					e.printStackTrace();
				}
			}	
		}
		
		@Override
		public void handleUpdate(ChannelUID channelUID, State newState) {
			// TODO Auto-generated method stub
			logger.info("handleUpdate " + channelUID.toString() + newState.toString());

			super.handleUpdate(channelUID, newState);
		}

}
