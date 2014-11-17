/**
 * Copyright (c) 2014 openHAB UG (haftungsbeschraenkt) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.smarthome.binding.fems;

import org.eclipse.smarthome.core.thing.ThingTypeUID;

/**
 * The {@link FemsBinding} class defines common constants, which are 
 * used across the whole binding.
 * 
 * @author Stefan Feilmeier - Initial contribution
 */
public class FemsBindingConstants {

    public static final String BINDING_ID = "fems";
    
    // List of all Thing Type UIDs
    public final static ThingTypeUID THING_TYPE_CESS = new ThingTypeUID(BINDING_ID, "cess");
    public final static ThingTypeUID THING_TYPE_DESS = new ThingTypeUID(BINDING_ID, "dess");
    public final static ThingTypeUID THING_TYPE_WEATHER = new ThingTypeUID(BINDING_ID, "weather");
    public final static ThingTypeUID THING_TYPE_IO = new ThingTypeUID(BINDING_ID, "io");

    // List of all Channel ids
    public final static String CHANNEL_1 = "channel1";

}
