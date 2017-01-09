package io.opentracing.impl;

import io.opentracing.SpanContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Created by ddcbdevins on 1/9/17.
 */
@Component
public class SleuthTracer extends AbstractTracer {

    @Autowired
    private Tracer tracer;

    AbstractSpanBuilder createSpanBuilder(String s) {
        return null;
    }

    Map<String, Object> getTraceState(SpanContext spanContext) {
        return null;
    }
}
