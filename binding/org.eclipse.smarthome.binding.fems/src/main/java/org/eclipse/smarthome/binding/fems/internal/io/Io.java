/**
 * Copyright (c) 2014 FENECON GmbH & Co. KG.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.smarthome.binding.fems.internal.io;

import org.eclipse.smarthome.core.types.Command;
import org.eclipse.smarthome.core.types.State;

public abstract class Io {
	public abstract State getState() throws Exception;

	public abstract void handleCommand(Command command) throws Exception;
	
	public abstract void dispose() throws Exception;
}
