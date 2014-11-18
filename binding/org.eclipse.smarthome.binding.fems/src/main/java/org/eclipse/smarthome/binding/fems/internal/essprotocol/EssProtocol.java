/**
 * Copyright (c) 2014 FENECON GmbH & Co. KG.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.smarthome.binding.fems.internal.essprotocol;

import java.io.IOException;
import java.util.ArrayList;

import net.wimpi.modbus.Modbus;
import net.wimpi.modbus.io.ModbusSerialTransaction;
import net.wimpi.modbus.msg.ExceptionResponse;
import net.wimpi.modbus.msg.ModbusResponse;
import net.wimpi.modbus.msg.ReadMultipleRegistersResponse;
import net.wimpi.modbus.net.SerialConnection;
import net.wimpi.modbus.util.SerialParameters;

import org.eclipse.smarthome.binding.fems.internal.FemsPersistence;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class EssProtocol {
	private Logger logger = LoggerFactory.getLogger(EssProtocol.class);
	
	public static volatile SerialConnection serialConnection = null;
	protected ArrayList<ModbusElementRange> wordRanges;
	
	protected String modbusinterface;
	protected int unitid;
	
    public EssProtocol(String modbusinterface, int unitid, ArrayList<ModbusElementRange> wordRanges) {
		this.wordRanges = wordRanges;
		this.modbusinterface = modbusinterface;
		this.unitid = unitid;
	}
    
	public void dispose() {
		for (ModbusElementRange wordRange : wordRanges) {
			wordRange.dispose();
		}
		if(EssProtocol.serialConnection!=null) {
			if(EssProtocol.serialConnection.isOpen()) {
				EssProtocol.serialConnection.close();
			}
			EssProtocol.serialConnection = null;
		}
	}
	
	public ArrayList<ModbusElementRange> getWordRanges() {
		return wordRanges;
	}
	
	public synchronized void updateData() throws Exception {
		SerialConnection serialConnection = getSerialConnection();
		for (ModbusElementRange wordRange : wordRanges) {
			ModbusSerialTransaction trans = wordRange.getModbusSerialTransaction(serialConnection, unitid);
			trans.setRetries(1);
			trans.execute();
			ModbusResponse res = trans.getResponse();
			
			if (res instanceof ReadMultipleRegistersResponse) {
				wordRange.updateData((ReadMultipleRegistersResponse)res);
	    	} else if (res instanceof ExceptionResponse) {
	    		throw new Exception("Modbus exception response:" + ((ExceptionResponse)res).getExceptionCode());
	    	} else {
	    		throw new Exception("Undefined Modbus response");
	    	}
			Thread.sleep(100);
		}
	}
	
	public abstract JSONObject persistData(JSONObject moreJson);
	
	// TODO this should be in a persistence binding
	protected synchronized JSONObject persistData(String content, JSONObject moreJson) {
		FemsPersistence persist = new FemsPersistence(content, moreJson); 
		
		for (ModbusElementRange wordRange : getWordRanges()) {
			for (ModbusElement word : wordRange.getWords()) {
				if(word instanceof ModbusItem) {
					ModbusItem item = (ModbusItem)word;
					persist.putState(item.getName(), item.getState());
				} else if (word instanceof BitWordElement) {
					BitWordElement bitWord = (BitWordElement)word;
					for (OnOffBitItem bitItem : bitWord.getBitItems()) {
						persist.putState(bitWord.getName() + "_" + bitItem.getName(), bitItem.getState());
					} 
				}
			}
		}
		
		try {
			return persist.send();
		} catch (IOException e) {
			logger.error("Cannot send HTTPS request: " + e.getMessage());
		} catch (Exception e) {
			logger.error("Error while persisting to FEMSmonitor: " + e.getMessage());
		}
		return null; 
	}
	
	public synchronized SerialConnection getSerialConnection() throws Exception {
		if(EssProtocol.serialConnection==null) {
			SerialParameters params = new SerialParameters();
			params.setPortName(modbusinterface);
			params.setBaudRate(getBaudrate());
			params.setDatabits(8);
			params.setParity("None");
			params.setStopbits(1);
			params.setEncoding(Modbus.SERIAL_ENCODING_RTU);
			params.setEcho(false);
			params.setReceiveTimeout(500);
			EssProtocol.serialConnection = new SerialConnection(params);
		}
		if(!EssProtocol.serialConnection.isOpen()) {
			EssProtocol.serialConnection.open();
		}
		return serialConnection;
	}
	
	protected abstract int getBaudrate();
}
