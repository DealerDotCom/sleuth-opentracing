package io.opentracing.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.Tracer;

/**
 * Created by ddcbdevins on 1/9/17.
 */
public class SleuthSpanBuilder extends AbstractSpanBuilder implements SleuthSpanContext {

    private Tracer tracer;

    private Long traceId;
    private long parentId;
    private long spanId;

    static SleuthSpanBuilder create(Tracer tracer, String operationName) {
        return new SleuthSpanBuilder(tracer, operationName);
    }

    private SleuthSpanBuilder(Tracer tracer, String operationName) {
        super(operationName);
        this.tracer = tracer;
    }

    protected SleuthSpan createSpan() {
        return SleuthSpan.create(tracer, operationName);
    }

    SleuthSpanBuilder withStateItem(String key, Object value) {
        if (key.equals(Span.TRACE_ID_NAME)) {
            traceId = value instanceof Number ? ((Number) value).longValue() : Span.hexToId(value.toString());
        } else if (key.equals(Span.PARENT_ID_NAME)) {
            parentId = value instanceof Number ? ((Number) value).longValue() : Span.hexToId(value.toString());
        } else if (key.equals(Span.SPAN_ID_NAME)) {
            spanId = value instanceof Number ? ((Number) value).longValue() : Span.hexToId(value.toString());
        } else {
            throw new IllegalArgumentException(key + " is not a valid B3 header");
        }
        return this;
    }

    boolean isTraceState(String s, Object o) {
        return Span.SPAN_HEADERS.contains(s);
    }

    public long getContextTraceId() {
        return traceId;
    }

    public long getContextSpanId() {
        return spanId;
    }

    public Long getContextParentSpanId() {
        return parentId;
    }
}
