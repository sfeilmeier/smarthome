/**
 * Copyright (c) 2014 openHAB UG (haftungsbeschraenkt) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.smarthome.io.rest.core.item;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import org.eclipse.smarthome.core.events.EventPublisher;
import org.eclipse.smarthome.core.items.GenericItem;
import org.eclipse.smarthome.core.items.GroupItem;
import org.eclipse.smarthome.core.items.Item;
import org.eclipse.smarthome.core.items.ItemFactory;
import org.eclipse.smarthome.core.items.ItemNotFoundException;
import org.eclipse.smarthome.core.items.ItemRegistry;
import org.eclipse.smarthome.core.items.ManagedItemProvider;
import org.eclipse.smarthome.core.library.items.RollershutterItem;
import org.eclipse.smarthome.core.library.items.SwitchItem;
import org.eclipse.smarthome.core.library.types.OnOffType;
import org.eclipse.smarthome.core.library.types.UpDownType;
import org.eclipse.smarthome.core.types.Command;
import org.eclipse.smarthome.core.types.State;
import org.eclipse.smarthome.core.types.TypeParser;
import org.eclipse.smarthome.io.rest.RESTResource;
import org.eclipse.smarthome.io.rest.core.item.beans.GroupItemBean;
import org.eclipse.smarthome.io.rest.core.item.beans.ItemBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>This class acts as a REST resource for items and provides different methods to interact with them,
 * like retrieving lists of items, sending commands to them or checking a single status.</p>
 * 
 * <p>The typical content types are plain text for status values and XML or JSON(P) for more complex data
 * structures</p>
 * 
 * <p>This resource is registered with the Jersey servlet.</p>
 *
 * @author Kai Kreuzer - Initial contribution and API
 * @author Dennis Nobel - Added methods for item management
 * @author Andre Fuechsel - Added tag support
 */
@Path(ItemResource.PATH_ITEMS)
public class ItemResource implements RESTResource {

	private static final Logger logger = LoggerFactory.getLogger(ItemResource.class); 
	
	/** The URI path to this resource */
    public static final String PATH_ITEMS = "items";
    
	private ItemRegistry itemRegistry;
	private EventPublisher eventPublisher;
	private ManagedItemProvider managedItemProvider;
	private Set<ItemFactory> itemFactories = new HashSet<>();
	
	protected void setItemRegistry(ItemRegistry itemRegistry) {
		this.itemRegistry = itemRegistry;
	}
	
	protected void unsetItemRegistry(ItemRegistry itemRegistry) {
		this.itemRegistry = null;
	}

	protected void setEventPublisher(EventPublisher eventPublisher) {
		this.eventPublisher = eventPublisher;
	}
	
	protected void unsetEventPublisher(EventPublisher eventPublisher) {
		this.eventPublisher = null;
	}

	protected void setManagedItemProvider(ManagedItemProvider managedItemProvider) {
		this.managedItemProvider = managedItemProvider;
	}
	
	protected void unsetManagedItemProvider(ManagedItemProvider managedItemProvider) {
		this.managedItemProvider = null;
	}

	protected void addItemFactory(ItemFactory itemFactory) {
		this.itemFactories.add(itemFactory);
	}
	
	protected void removeItemFactory(ItemFactory itemFactory) {
		this.itemFactories.remove(itemFactory);
	}

	@Context UriInfo uriInfo;
	
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getItems(@QueryParam("type") String type, @QueryParam("tags") String tags,
            @DefaultValue("false") @QueryParam("recursive") boolean recursive) {
        logger.debug("Received HTTP GET request at '{}'", uriInfo.getPath());

        Object responseObject = getItemBeans(type, tags, recursive);
        return Response.ok(responseObject).build();
    }

    @GET @Path("/{itemname: [a-zA-Z_0-9]*}/state") 
	@Produces( { MediaType.TEXT_PLAIN })
    public Response getPlainItemState(
		@PathParam("itemname") String itemname) {
    	Item item = getItem(itemname);
    	if(item!=null) {
			logger.debug("Received HTTP GET request at '{}'.", uriInfo.getPath());
			throw new WebApplicationException(Response.ok(item.getState().toString()).build());
    	} else {
    		logger.info("Received HTTP GET request at '{}' for the unknown item '{}'.", uriInfo.getPath(), itemname);
    		throw new WebApplicationException(404);
    	}
    }

    @GET @Path("/{itemname: [a-zA-Z_0-9]*}")
    @Produces( { MediaType.WILDCARD })
    public Response  getItemData(
    		@PathParam("itemname") String itemname) {
		logger.debug("Received HTTP GET request at '{}'", uriInfo.getPath());

    	final Object responseObject = getItemDataBean(itemname);
    	throw new WebApplicationException(Response.ok(responseObject).build());  
    }
    
    @PUT @Path("/{itemname: [a-zA-Z_0-9]*}/state")
	@Consumes(MediaType.TEXT_PLAIN)	
	public Response putItemState(@PathParam("itemname") String itemname, String value) {
    	Item item = getItem(itemname);
    	if(item!=null) {
    		State state = TypeParser.parseState(item.getAcceptedDataTypes(), value);
    		if(state!=null) {
    			logger.debug("Received HTTP PUT request at '{}' with value '{}'.", uriInfo.getPath(), value);
    			eventPublisher.postUpdate(itemname, state);
    			return Response.ok().build();
    		} else {
    			logger.warn("Received HTTP PUT request at '{}' with an invalid status value '{}'.", uriInfo.getPath(), value);
    			return Response.status(Status.BAD_REQUEST).build();
    		}
    	} else {
    		logger.info("Received HTTP PUT request at '{}' for the unknown item '{}'.", uriInfo.getPath(), itemname);
    		throw new WebApplicationException(404);
    	}
	}

	@Context UriInfo localUriInfo;
    @POST @Path("/{itemname: [a-zA-Z_0-9]*}")
	@Consumes(MediaType.TEXT_PLAIN)	
	public Response postItemCommand(@PathParam("itemname") String itemname, String value) {
    	Item item = getItem(itemname);
    	Command command = null;
    	if(item!=null) {
    		if("toggle".equalsIgnoreCase(value) && 
    				(item instanceof SwitchItem || 
    				 item instanceof RollershutterItem)) {
    			if(OnOffType.ON.equals(item.getStateAs(OnOffType.class))) command = OnOffType.OFF;
    			if(OnOffType.OFF.equals(item.getStateAs(OnOffType.class))) command = OnOffType.ON;
    			if(UpDownType.UP.equals(item.getStateAs(UpDownType.class))) command = UpDownType.DOWN;
    			if(UpDownType.DOWN.equals(item.getStateAs(UpDownType.class))) command = UpDownType.UP;
    		} else {
    			command = TypeParser.parseCommand(item.getAcceptedCommandTypes(), value);
    		}
    		if(command!=null) {
    			logger.debug("Received HTTP POST request at '{}' with value '{}'.", uriInfo.getPath(), value);
    			eventPublisher.postCommand(itemname, command);
    			return Response.created(localUriInfo.getAbsolutePathBuilder().path("state").build()).build();
    		} else {
    			logger.warn("Received HTTP POST request at '{}' with an invalid status value '{}'.", uriInfo.getPath(), value);
    			return Response.status(Status.BAD_REQUEST).build();
    		}
    	} else {
    		logger.info("Received HTTP POST request at '{}' for the unknown item '{}'.", uriInfo.getPath(), itemname);
    		throw new WebApplicationException(404);
    	}
	}
    
    @PUT @Path("/{itemname: [a-zA-Z_0-9]*}")
	@Consumes(MediaType.TEXT_PLAIN)	
	public Response createOrUpdate(@PathParam("itemname") String itemname, String itemType) {

        GenericItem newItem = null;
        
        if (itemType != null && itemType.equals("Group")) {
            newItem = new GroupItem(itemname);
        } else {
            for (ItemFactory itemFactory : itemFactories) {
                newItem = itemFactory.createItem(itemType, itemname);
                if (newItem != null)
                    break;
            }
        }
        
        if (newItem == null) {
            logger.warn("Received HTTP PUT request at '{}' with an invalid item type '{}'.", uriInfo.getPath(),
                    itemType);
            return Response.status(Status.BAD_REQUEST).build();
        }

        Item existingItem = getItem(itemname);

        if (existingItem == null) {
            managedItemProvider.add(newItem);
        } else if(managedItemProvider.get(itemname) != null) {
            managedItemProvider.update(newItem);
        } else {
            logger.warn("Cannot update existing item '{}', because is not managed.", itemname);
            return Response.status(Status.METHOD_NOT_ALLOWED).build();
        }

        return Response.ok().build();
	}
    
    @PUT
    @Path("/{itemName: [a-zA-Z_0-9]*}/members/{memberItemName: [a-zA-Z_0-9]*}")
    public Response addMember(@PathParam("itemName") String itemName, @PathParam("memberItemName") String memberItemName) {
        try {
            Item item = itemRegistry.getItem(itemName);

            if (!(item instanceof GroupItem)) {
                return Response.status(Status.NOT_FOUND).build();
            }
 
            GroupItem groupItem = (GroupItem) item;

            Item memberItem = itemRegistry.getItem(memberItemName);
            
            if (!(memberItem instanceof GenericItem)) {
                return Response.status(Status.NOT_FOUND).build();
            }
            
            if(managedItemProvider.get(memberItemName) == null) {
                return Response.status(Status.METHOD_NOT_ALLOWED).build();
            }
            
            GenericItem genericMemberItem = (GenericItem) memberItem;
            genericMemberItem.addGroupName(groupItem.getName());
            managedItemProvider.update(genericMemberItem);
            
            return Response.ok().build();
        } catch (ItemNotFoundException e) {
            return Response.status(Status.NOT_FOUND).build();
        }
    }
    
    @DELETE @Path("/{itemName: [a-zA-Z_0-9]*}/members/{memberItemName: [a-zA-Z_0-9]*}")
    public Response removeMember(@PathParam("itemName") String itemName, @PathParam("memberItemName") String memberItemName) {
        try {
            Item item = itemRegistry.getItem(itemName);

            if (!(item instanceof GroupItem)) {
                return Response.status(Status.NOT_FOUND).build();
            }
            
            GroupItem groupItem = (GroupItem) item;

            Item memberItem = itemRegistry.getItem(memberItemName);
            
            if (!(memberItem instanceof GenericItem)) {
                return Response.status(Status.NOT_FOUND).build();
            }
            
            if(managedItemProvider.get(memberItemName) == null) {
                return Response.status(Status.METHOD_NOT_ALLOWED).build();
            }
            
            GenericItem genericMemberItem = (GenericItem) memberItem;
            genericMemberItem.removeGroupName(groupItem.getName());
            managedItemProvider.update(genericMemberItem);
            
            return Response.ok().build();
        } catch (ItemNotFoundException e) {
            return Response.status(Status.NOT_FOUND).build();
        }
    }
    
    @DELETE
    @Path("/{itemname: [a-zA-Z_0-9]*}")
    public Response removeItem(@PathParam("itemname") String itemname) {

        if (managedItemProvider.remove(itemname) == null) {
            logger.info("Received HTTP DELETE request at '{}' for the unknown item '{}'.", uriInfo.getPath(), itemname);
            return Response.status(Status.NOT_FOUND).build();
        }

        return Response.ok().build();
    }

    @PUT
    @Path("/{itemname: [a-zA-Z_0-9]*}/tags/{tag: [a-zA-Z_0-9]*}")
    public Response addTag(@PathParam("itemname") String itemname, @PathParam("tag") String tag) {

        Item item = getItem(itemname);

        if (item == null) {
            logger.info("Received HTTP PUT request at '{}' for the unknown item '{}'.", uriInfo.getPath(), itemname);
            return Response.status(Status.NOT_FOUND).build();
        }
        
        if(managedItemProvider.get(itemname) == null) {
            return Response.status(Status.METHOD_NOT_ALLOWED).build();
        }

        item.addTag(tag);
        managedItemProvider.update(item);

        return Response.ok().build();
    }
    
    @DELETE
    @Path("/{itemname: [a-zA-Z_0-9]*}/tags/{tag: [a-zA-Z_0-9]*}")
    public Response removeTag(@PathParam("itemname") String itemname, @PathParam("tag") String tag) {

        Item item = getItem(itemname);

        if (item == null) {
            logger.info("Received HTTP DELETE request at '{}' for the unknown item '{}'.", uriInfo.getPath(), itemname);
            return Response.status(Status.NOT_FOUND).build();
        }
        
        if(managedItemProvider.get(itemname) == null) {
            return Response.status(Status.METHOD_NOT_ALLOWED).build();
        }

        item.removeTag(tag);
        managedItemProvider.update(item);

        return Response.ok().build();
    }

    public static ItemBean createItemBean(Item item, boolean drillDown, String uriPath) {
    	ItemBean bean;
    	if(item instanceof GroupItem && drillDown) {
    		GroupItem groupItem = (GroupItem) item;
    		GroupItemBean groupBean = new GroupItemBean();
    		Collection<ItemBean> members = new LinkedHashSet<ItemBean>();
    		for(Item member : groupItem.getMembers()) {
    			members.add(createItemBean(member, false, uriPath));
    		}
    		groupBean.members = members.toArray(new ItemBean[members.size()]);
    		bean = groupBean;
    	} else {
    		 bean = new ItemBean();
    	}
    	bean.name = item.getName();
    	bean.state = item.getState().toString();
    	bean.type = item.getClass().getSimpleName();
    	bean.link = UriBuilder.fromUri(uriPath).path(ItemResource.PATH_ITEMS).path(bean.name).build().toASCIIString();
        bean.tags = item.getTags(); 
    	
    	return bean;
    }
    
    private Item getItem(String itemname) {
    	try {
			Item item = itemRegistry.getItem(itemname);
			return item;
		} catch (ItemNotFoundException ignored) {
		}
        return null;
    }


    private List<ItemBean> getItemBeans(String type, String tags, boolean recursive) {
        List<ItemBean> beans = new LinkedList<ItemBean>();
        Collection<Item> items; 
        if (tags == null) {
            if (type == null) {
                items = itemRegistry.getItems(); 
            } else {
                items = itemRegistry.getItemsOfType(type); 
            }
        } else {
            String[] tagList = tags.split(","); 
            if (type == null) {
                items = itemRegistry.getItemsByTag(tagList); 
            } else {
                items = itemRegistry.getItemsByTagAndType(type, tagList); 
            }
        }
        if (items != null) {
            for (Item item : items) {
                beans.add(createItemBean(item, recursive, uriInfo.getBaseUri().toASCIIString()));
            }
        }
        return beans;
    }

	private ItemBean getItemDataBean(String itemname) {
		Item item = getItem(itemname);
		if(item!=null) {
			return createItemBean(item, true, uriInfo.getBaseUri().toASCIIString());
		} else {
			logger.info("Received HTTP GET request at '{}' for the unknown item '{}'.", uriInfo.getPath(), itemname);
			throw new WebApplicationException(404);
		}
	}
}
