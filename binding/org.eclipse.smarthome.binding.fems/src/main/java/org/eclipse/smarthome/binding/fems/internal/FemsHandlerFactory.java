/**
 * Copyright (c) 2014 FENECON GmbH & Co. KG.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.smarthome.binding.fems.internal;

import static org.eclipse.smarthome.binding.fems.FemsBindingConstants.*;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

//Ximport org.eclipse.smarthome.binding.fems.handler.CessHandler;
//Ximport org.eclipse.smarthome.binding.fems.handler.DessHandler;
//Ximport org.eclipse.smarthome.binding.fems.handler.EssHandler;
import org.eclipse.smarthome.binding.fems.handler.IoHandler;
//Ximport org.eclipse.smarthome.binding.fems.handler.WeatherHandler;
import org.eclipse.smarthome.config.core.Configuration;
import org.eclipse.smarthome.core.thing.Bridge;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingTypeUID;
import org.eclipse.smarthome.core.thing.ThingUID;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandlerFactory;
import org.eclipse.smarthome.core.thing.binding.ThingHandler;

/**
 * The {@link FemsHandlerFactory} is responsible for creating things and thing 
 * handlers.
 * 
 * @author Stefan Feilmeier - Initial contribution
 */
public class FemsHandlerFactory extends BaseThingHandlerFactory {
    
    private final static Set<ThingTypeUID> SUPPORTED_THING_TYPES_UIDS = new HashSet<ThingTypeUID>() {{
    	add(THING_TYPE_CESS);
    	add(THING_TYPE_DESS);
    	add(THING_TYPE_WEATHER);
    	add(THING_TYPE_IO);
    }};
    
    @Override
    public boolean supportsThingType(ThingTypeUID thingTypeUID) {
        return SUPPORTED_THING_TYPES_UIDS.contains(thingTypeUID);
    }

    @Override
    protected ThingHandler createHandler(Thing thing) {

        ThingTypeUID thingTypeUID = thing.getThingTypeUID();

        if (thingTypeUID.equals(THING_TYPE_CESS)) {
//X            return new CessHandler(thing);
        } else if (thingTypeUID.equals(THING_TYPE_DESS)) {
//X        	return new DessHandler(thing);
        } else if (thingTypeUID.equals(THING_TYPE_WEATHER)) {
//X        	return new WeatherHandler(thing);
        } else if (thingTypeUID.equals(THING_TYPE_IO)) {
        	return new IoHandler(thing);
        }

        return null;
    }
}

