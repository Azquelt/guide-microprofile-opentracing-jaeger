package io.openliberty.guides.inventory.migration;

import io.opentelemetry.opentracingshim.OpenTracingShim;
import io.opentracing.Tracer;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;

@ApplicationScoped
public class ShimProvider {
    
    @Produces
    @ApplicationScoped
    private Tracer provideTracer(io.opentelemetry.api.trace.Tracer openapiTracer) {
        return OpenTracingShim.createTracerShim(openapiTracer);
    }

}
