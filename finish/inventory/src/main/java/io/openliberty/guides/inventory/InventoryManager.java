// tag::copyright[]
/*******************************************************************************
 * Copyright (c) 2017, 2022 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial implementation
 *******************************************************************************/
// end::copyright[]
package io.openliberty.guides.inventory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import io.openliberty.guides.inventory.client.SystemClient;
import io.openliberty.guides.inventory.model.InventoryList;
import io.openliberty.guides.inventory.model.SystemData;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
// tag::InventoryManager[]
public class InventoryManager {

    @Inject
    @ConfigProperty(name = "system.http.port")
    int SYSTEM_PORT;

    private List<SystemData> systems = Collections.synchronizedList(new ArrayList<>());
    private SystemClient systemClient = new SystemClient();
    // tag::customTracer[]
    @Inject Tracer tracer;
    // end::customTracer[]

    public Properties get(String hostname) {
        systemClient.init(hostname, SYSTEM_PORT);
        Properties properties = systemClient.getProperties();
        return properties;
    }

    public void add(String hostname, Properties systemProps) {
        Properties props = new Properties();
        props.setProperty("os.name", systemProps.getProperty("os.name"));
        props.setProperty("user.name", systemProps.getProperty("user.name"));

        SystemData system = new SystemData(hostname, props);
        // tag::Add[]
        if (!systems.contains(system)) {
            // tag::addSpan[]
            Span span = tracer.spanBuilder("add() Span").startSpan();
            // end::addSpan[]
            // tag::Try[]
            try (Scope childScope = span.makeCurrent()) {
                // tag::addToInvList[]
                systems.add(system);
                // end::addToInvList[]
            } finally {
                span.end();
            }
            // end::Try[]
        }
        // end::Add[]
    }

    // tag::list[]
    public InventoryList list() {
        return new InventoryList(systems);
    }
    // end::list[]

    int clear() {
        int propertiesClearedCount = systems.size();
        systems.clear();
        return propertiesClearedCount;
    }
}
// end::InventoryManager[]
