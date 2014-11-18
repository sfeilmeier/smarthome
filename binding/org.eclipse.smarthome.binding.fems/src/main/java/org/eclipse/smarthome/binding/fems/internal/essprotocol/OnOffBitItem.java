/**
 * Copyright (c) 2014 FENECON GmbH & Co. KG.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.smarthome.binding.fems.internal.essprotocol;

import java.math.BigInteger;
import net.wimpi.modbus.procimg.Register;
import org.eclipse.smarthome.core.library.types.OnOffType;

public class OnOffBitItem extends ModbusItem {
//	private Logger logger = LoggerFactory.getLogger(BitItemOnOff.class);
	private int bitAddress;
	
	public OnOffBitItem(int bitAddress, String name) {
		super(name);
		this.bitAddress = bitAddress;
	}
	
	public void updateData(Register register) {
		if(BigInteger.valueOf(register.getValue()).testBit(bitAddress)) {
			setState(OnOffType.ON);	
		} else {
			setState(OnOffType.OFF);
		}		
	}
}
