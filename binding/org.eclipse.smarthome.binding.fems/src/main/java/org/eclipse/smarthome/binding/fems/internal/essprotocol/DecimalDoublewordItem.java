/**
 * Copyright (c) 2014 FENECON GmbH & Co. KG.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.smarthome.binding.fems.internal.essprotocol;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.eclipse.smarthome.core.library.types.DecimalType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.wimpi.modbus.procimg.Register;

public class DecimalDoublewordItem extends ModbusItem implements ModbusDoublewordElement {
	private Logger logger = LoggerFactory.getLogger(DecimalDoublewordItem.class);
	
	public DecimalDoublewordItem(String name) {
		super(name);
	}

	@Override
	public void updateData(Register highRegister, Register lowRegister) {
		// TODO Auto-generated method stub
		byte[] highBytes = highRegister.toBytes();
		byte[] lowBytes = lowRegister.toBytes();
		byte[] bytes = new byte[4];
		System.arraycopy(highBytes, 0, bytes, 0, highBytes.length);
		System.arraycopy(lowBytes, 0, bytes, highBytes.length, lowBytes.length);
		setState(new DecimalType( new BigDecimal( new BigInteger(bytes) ) ) );
	}
}
