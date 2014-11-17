/**
 * Copyright (c) 2014 FENECON GmbH & Co. KG.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.smarthome.binding.fems.internal.io;

import java.io.IOException;

import org.eclipse.smarthome.core.library.types.IncreaseDecreaseType;
import org.eclipse.smarthome.core.library.types.OnOffType;
import org.eclipse.smarthome.core.library.types.PercentType;
import org.eclipse.smarthome.core.types.Command;
import org.eclipse.smarthome.core.types.State;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AnalogOutput extends Io {
	public enum DIVIDE {
		AMPERE, VOLTAGE
	}

	private Logger logger = LoggerFactory.getLogger(AnalogOutput.class);
	private PwmPin pwmPin;
	private GpioPin divideCtrlPin;
	
	public AnalogOutput(String pwmGlob, GpioPin divideCtrlPin, DIVIDE divide) {
		super();
		this.divideCtrlPin = divideCtrlPin;
		switch(divide) {
		case VOLTAGE:
			try {
				divideCtrlPin.setHigh();
			} catch (Exception e) {
				logger.error("Unable to set initial high-state of divideCtrlPin: " + e.getMessage());
			}
			break;
		case AMPERE:
			try {
				divideCtrlPin.setLow();
			} catch (Exception e) {
				logger.error("Unable to set initial low-state of divideCtrlPin: " + e.getMessage());
			}
		}
		try {
			pwmPin = new PwmPin(pwmGlob);
			pwmPin.setValue(0);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}

	@Override
	public void dispose() throws Exception {
		divideCtrlPin.dispose();
	}

	@Override
	public State getState() {
		long value;
		try {
			value = pwmPin.readValue();
		} catch (Exception e) {
			logger.error("Unable to read Pin: " + e.getMessage());
			try {
				pwmPin.setValue(0);
			} catch (IOException e1) {
				logger.error("Unable to set Pin: " + e1.getMessage());
			}
			return PercentType.ZERO;
		}
		if(value >= 100) {
			return PercentType.HUNDRED;
		} else if(value <= 0){
			return PercentType.ZERO;
		} else {
			return new PercentType((int)value);
		}
	}

	@Override
	public void handleCommand(Command command) throws Exception {
		if(command instanceof OnOffType) {
			OnOffType cmd = (OnOffType) command;
			switch(cmd) {
			case ON:
				pwmPin.setValue(100);
				break;
			case OFF:
				pwmPin.setValue(0);
				break;
			}
		} else if (command instanceof IncreaseDecreaseType) {
			logger.warn("Not implemented! BUG!");
//	TODO
//			IncreaseDecreaseType cmd = (IncreaseDecreaseType) command;
//			int currentValue = pwmPin.readValue();
//			switch(cmd) {
//			case INCREASE:
//				pwmPin.setValue(currentValue + 1);
//				break;
//			case DECREASE:
//				pwmPin.setValue(currentValue - 1);
//			}
		} else if (command instanceof PercentType) {
			logger.warn("Not implemented! BUG!");
			/* TODO: fix real analog output:
			PercentType cmd = (PercentType) command;
			pwmPin.setValue(cmd.doubleValue());
			*/
		}
	}
}
