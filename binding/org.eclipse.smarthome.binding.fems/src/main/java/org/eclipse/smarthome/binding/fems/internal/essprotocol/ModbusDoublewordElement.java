/**
 * Copyright (c) 2014 FENECON GmbH & Co. KG.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.smarthome.binding.fems.internal.essprotocol;

import net.wimpi.modbus.procimg.Register;

public interface ModbusDoublewordElement extends ModbusElement {
	/*
	 * Handle data from two registers
	 */
	public void updateData(Register highRegister, Register lowRegister);
}
