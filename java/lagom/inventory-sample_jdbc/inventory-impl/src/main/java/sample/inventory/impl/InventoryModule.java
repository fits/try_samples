package sample.inventory.impl;

import com.google.inject.AbstractModule;
import com.lightbend.lagom.javadsl.server.ServiceGuiceSupport;

import sample.inventory.api.InventoryService;

public class InventoryModule extends AbstractModule implements ServiceGuiceSupport {
    @Override
    protected void configure() {
        bindServices(serviceBinding(InventoryService.class, InventoryServiceImpl.class));
    }
}
