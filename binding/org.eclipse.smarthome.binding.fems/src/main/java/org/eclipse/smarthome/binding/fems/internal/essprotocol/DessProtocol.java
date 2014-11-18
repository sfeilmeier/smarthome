/**
 * Copyright (c) 2014 FENECON GmbH & Co. KG.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.smarthome.binding.fems.internal.essprotocol;

import java.util.ArrayList;

import org.json.JSONObject;

public class DessProtocol extends EssProtocol {

	public DessProtocol(String modbusinterface, int unitid,
			ArrayList<ModbusElementRange> wordRanges) {
		super(modbusinterface, unitid, wordRanges);
	}

	@Override
	protected int getBaudrate() {
		return 9600;
	}

	@Override
	public JSONObject persistData(JSONObject moreJson) {
		return persistData("dess", moreJson);
	}
}
