/**
 * Copyright (c) 2014 FENECON GmbH & Co. KG.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.smarthome.binding.fems.internal.io;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PwmPin {
	protected static final Charset DEFAULT_ENCODING = StandardCharsets.UTF_8;
	private Logger logger = LoggerFactory.getLogger(PwmPin.class);
	
	private String pwmPath = null;
	
	/*
	 * @param pwmGlob: something like "pwm_test_P9_14.*" as a folder below "/sys/devices/ocp.*"
	 */
	public PwmPin(String pwmGlob) throws Exception {
		DirectoryStream<Path> ocpDirs = Files.newDirectoryStream(Paths.get("/sys/devices"), "ocp.*");
		for(Path ocpDir : ocpDirs) {
			DirectoryStream<Path> pwmDirs = Files.newDirectoryStream(ocpDir, pwmGlob);
			for(Path pwmDir : pwmDirs) {
				this.pwmPath = pwmDir.toAbsolutePath().toString();
			}
		}
		if(pwmPath == null) {
			throw new Exception("Unable to find analog output in /sys/devices/ocp.*/" + pwmGlob);
		}
	}
	
	public synchronized int readValue() throws Exception {
		// TODO: set writelock, because readValue and setValue could access simultaneously
		for(String line : Files.readAllLines(Paths.get(pwmPath + "/run"), DEFAULT_ENCODING)) {
			if(Integer.parseInt(line) == 0) {
				// pwm deactivated: return 0 %
				return 0;
			}
		}
		
		double period = 0.;
		for(String line : Files.readAllLines(Paths.get(pwmPath + "/period"), DEFAULT_ENCODING)) {
			period = Long.parseLong(line);
		};
		
		double duty = 0.;
		for(String line : Files.readAllLines(Paths.get(pwmPath + "/duty"), DEFAULT_ENCODING)) {
			duty = Long.parseLong(line);
		};

		return 100-(int)Math.round((duty*100) / period); //invert
	}
	
	public synchronized void setValue(double percent) throws IOException {
		/* setting values according to http://elinux.org/EBC_Exercise_13_Pulse_Width_Modulation
		 * The units are in ns. Try a 1Hz frequency with a 25% duty cycle:
		 * echo 1000000000 > period
		 * echo  250000000 > duty
		 * echo 1 > run
		 */
		// first: deactivate pwm	
		long multiplicator = 10000000;
		long period = 100 * multiplicator;
		long duty = period - Math.round(percent * multiplicator); //invert
		
		Files.write(Paths.get(pwmPath + "/period"), Long.toString(period).getBytes());
		Files.write(Paths.get(pwmPath + "/duty"), Long.toString(duty).getBytes());
		Files.write(Paths.get(pwmPath + "/run"), "1".getBytes());
	}
}
