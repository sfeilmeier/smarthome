/**
 * Copyright (c) 2014 FENECON GmbH & Co. KG.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.smarthome.binding.fems.internal.io;

import org.eclipse.smarthome.core.library.types.OnOffType;
import org.eclipse.smarthome.core.types.Command;
import org.eclipse.smarthome.core.types.State;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DigitalOutput extends Io {
	private Logger logger = LoggerFactory.getLogger(DigitalOutput.class);
	
	private GpioPin gpioPin;
	
	public DigitalOutput(int pinNo) {
		super();
		gpioPin = new GpioPin(pinNo);
		try {
			//TODO: this sets the relay to OFF whenever the binding is reloaded (e.g. software update)
			gpioPin.setHigh();
		} catch (Exception e) {
			logger.error("Unable to set initial state of pin " + pinNo + ": " + e.getMessage());
		}
	}
	
	public void dispose() throws Exception {
		gpioPin.dispose();
	}

	@Override
	public State getState() throws Exception {
		switch(gpioPin.readValue()) {
		case 0:
			return OnOffType.ON;
		default:
			return OnOffType.OFF;
		}
	}

	@Override
	public void handleCommand(Command command) throws Exception {	
		if(command instanceof OnOffType) {
			OnOffType cmd = (OnOffType) command;
			switch(cmd) {
			case ON:
				gpioPin.setLow();
				break;
			case OFF:
				gpioPin.setHigh();
				break;
			}
		}
	}
}
