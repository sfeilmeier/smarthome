/**
 * Copyright (c) 2014 FENECON GmbH & Co. KG.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.smarthome.binding.fems.internal.io;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

enum GPIO_PIN_DIRECTION {
	IN, OUT
}

public class GpioPin {
	private Logger logger = LoggerFactory.getLogger(GpioPin.class);

	protected static final Charset DEFAULT_ENCODING = StandardCharsets.UTF_8;
	protected final static String GPIO_PATH = "/sys/class/gpio";
	
	private static synchronized void export(int pinNo) throws Exception {
		Path pinPath = Paths.get(GPIO_PATH + "/gpio" + pinNo);
		if(Files.isDirectory(pinPath)) {
			// Gpio was already exported
			return;
		} else {
			Files.write(Paths.get(GPIO_PATH + "/export"), Integer.toString(pinNo).getBytes());
			if(Files.isDirectory(pinPath)) {
				// Gpio is now exported
				return;
			}
		}
		throw new Exception("Unable to export Gpio pin " + pinNo);		
	}
	
	private static synchronized void unexport(int pinNo) throws Exception {
		Path pinPath = Paths.get(GPIO_PATH + "/gpio" + pinNo);
		if(Files.isDirectory(pinPath)) {
			// Gpio was exported
			Files.write(Paths.get(GPIO_PATH + "/unexport"), Integer.toString(pinNo).getBytes());
			if(!Files.isDirectory(pinPath)) {
				// Gpio is now unexported
				return;
			}
		} else {
			// Gpio was not exported
			return;
		}
		throw new Exception("Unable to unexport Gpio pin " + pinNo);
	}
	
	private int pinNo;
	
	public GpioPin(int pinNo) {
		this.pinNo = pinNo;
	}
	
	public synchronized void dispose() throws Exception {
		unexport(pinNo);
	}
	
	private Path getPinPath() throws Exception {
		export(pinNo);
		return Paths.get(GPIO_PATH + "/gpio" + pinNo);
	}
	
	public boolean isAvailable() throws Exception {
		// wait up to 1 second
		for(int i=0; i<10 && !hasDirection(); i++) {
			Thread.sleep(100);
		}
		if(!hasDirection()) {
			throw new Exception("'direction' file does not exist");
		}
		return true;
	}
	
	private synchronized boolean hasDirection() {
		try {
			Path path = Paths.get(getPinPath() + "/direction");
			if(Files.exists(path) && Files.isWritable(path)) {
				return true;
			}
		} catch (Exception e) { }
		return false;
	}
	
	public synchronized int readValue() throws Exception {
		for(String line : Files.readAllLines(Paths.get(getPinPath() + "/value"), DEFAULT_ENCODING)) {
			// returns first line
			return Integer.parseInt(line);
		};
		throw new Exception("File is empty");
	}
	
	public synchronized void setHigh() throws Exception {
		isAvailable();
		Files.write(Paths.get(getPinPath() + "/direction"), new String("high").getBytes());
	}
	
	public synchronized void setLow() throws Exception {
		isAvailable();
		Files.write(Paths.get(getPinPath() + "/direction"), new String("low").getBytes());
	}
}
