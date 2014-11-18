/**
 * Copyright (c) 2014 FENECON GmbH & Co. KG.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.smarthome.binding.fems.internal.essprotocol;

import java.util.Arrays;
import net.wimpi.modbus.io.ModbusSerialTransaction;
import net.wimpi.modbus.msg.ReadMultipleRegistersRequest;
import net.wimpi.modbus.msg.ReadMultipleRegistersResponse;
import net.wimpi.modbus.net.SerialConnection;

public class ModbusElementRange {
//    private Logger logger = LoggerFactory.getLogger(ModbusWordRange.class);
	
	private int startAddress;
	private ModbusElement[] elements;
	
	public ModbusElementRange(int startAddress, ModbusElement... words) {
		this.startAddress = startAddress;
		this.elements = words;
	}
	
	/*
	 * Returns the total number of bytes (lengths) of all elements
	 */
	private int getTotalLength() {
		int length = 0;
		for(ModbusElement element : elements) {
			if(element instanceof ModbusWordElement) {
				length += 1;
			} else if (element instanceof ModbusDoublewordElement) {
				length += 2;
			} else if (element instanceof ReservedElement) {
				ReservedElement reservedElement = (ReservedElement)element;
				length += reservedElement.getLength();
			}
		}
		return length;
	}
	
	public ModbusSerialTransaction getModbusSerialTransaction(SerialConnection serialConnection, int unitid) {
		ModbusSerialTransaction modbusSerialTransaction = null;
		ReadMultipleRegistersRequest req = new ReadMultipleRegistersRequest(startAddress, getTotalLength());
		req.setUnitID(unitid);
		req.setHeadless();	
		modbusSerialTransaction = new ModbusSerialTransaction(serialConnection);
		modbusSerialTransaction.setRequest(req);
		return modbusSerialTransaction;
	}
	
	public void updateData(ReadMultipleRegistersResponse res) {
		int position = 0;
		for(ModbusElement element : elements) {
			if(element instanceof ModbusWordElement) {
				ModbusWordElement word = (ModbusWordElement)element;
				word.updateData(res.getRegister(position));
				position++;
			} else if (element instanceof ModbusDoublewordElement) {
				ModbusDoublewordElement doubleword = (ModbusDoublewordElement)element;
				doubleword.updateData(res.getRegister(position), res.getRegister(position+1));
				position += 2;
			} else if (element instanceof ReservedElement) {
				ReservedElement reservedElement = (ReservedElement)element;
				position += reservedElement.getLength();
			}
		}
	};
	
	public void dispose() {
		// nothing to dispose for now...
	}

	@Override
	public String toString() {
		return "WordRange [startAddress=" + startAddress + ", words="
				+ Arrays.toString(elements) + "]";
	}
	
	public ModbusElement[] getWords() {
		return elements;
	}
	
	public int getStartAddress() {
		return startAddress;
	}
}
