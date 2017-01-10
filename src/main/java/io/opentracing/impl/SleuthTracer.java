package io.opentracing.impl;

import io.opentracing.SpanContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ddcbdevins on 1/9/17.
 */
@Component
public class SleuthTracer extends AbstractTracer {

    @Autowired
    private Tracer tracer;

    @Override
    SleuthSpanBuilder createSpanBuilder(String operationName) {
        return SleuthSpanBuilder.create(tracer, operationName);
    }

    Map<String, Object> getTraceState(SpanContext spanContext) {
        final SleuthSpanContext sc = (SleuthSpanContext) spanContext;

        return new HashMap<String, Object>() {{
            put(Span.TRACE_ID_NAME, Span.idToHex(sc.getContextTraceId()));
            if (sc.getContextParentSpanId() != null) {
                put(Span.PARENT_ID_NAME, Span.idToHex(sc.getContextParentSpanId()));
            }
            put(Span.SPAN_ID_NAME, Span.idToHex(sc.getContextSpanId()));
            put(Span.SAMPLED_NAME, Span.SPAN_SAMPLED);
        }};
    }
}
