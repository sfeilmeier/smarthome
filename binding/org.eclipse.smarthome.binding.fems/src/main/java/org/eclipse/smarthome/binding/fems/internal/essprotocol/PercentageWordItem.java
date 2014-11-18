/**
 * Copyright (c) 2014 FENECON GmbH & Co. KG.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.smarthome.binding.fems.internal.essprotocol;

import org.eclipse.smarthome.core.library.types.PercentType;

import net.wimpi.modbus.procimg.Register;

public class PercentageWordItem extends ModbusItem implements ModbusWordElement {
	public PercentageWordItem(String name) {
		super(name);
	}

	@Override
	public void updateData(Register register) {
		setState(new PercentType(register.getValue()));
	}
}
