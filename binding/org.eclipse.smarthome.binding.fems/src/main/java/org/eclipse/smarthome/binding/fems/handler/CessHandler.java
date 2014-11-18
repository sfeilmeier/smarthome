/**
 * Copyright (c) 2014 FENECON GmbH & Co. KG.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.smarthome.binding.fems.handler;

import org.eclipse.smarthome.binding.fems.internal.essprotocol.CessProtocolFactory;
import org.eclipse.smarthome.binding.fems.internal.essprotocol.EssProtocol;
import org.eclipse.smarthome.core.thing.Thing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link CessHandler} is responsible for handling commands, which are
 * sent to one of the channels.
 * 
 * @author Stefan Feilmeier - Initial contribution
 */
public class CessHandler extends EssHandler {
    private Logger logger = LoggerFactory.getLogger(CessHandler.class);

	public CessHandler(Thing thing) {
		super(thing);
	}
	
	@Override
	public void initialize() {
		logger.info("Initializing FEMS CESS handler.");
		super.initialize();
	}
	
	protected EssProtocol getProtocol() {
		return CessProtocolFactory.getProtocol(modbusinterface, unitid);
	}
}
