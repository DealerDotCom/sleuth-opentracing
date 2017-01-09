package io.opentracing.impl;

/**
 * Created by ddcbdevins on 1/9/17.
 */
public class SleuthSpan extends AbstractSpan implements SleuthSpanContext {
    SleuthSpan(String operationName) {
        super(operationName);
    }

    public long getContextTraceId() {
        return 0;
    }

    public long getContextSpanId() {
        return 0;
    }

    public Long getContextParentSpanId() {
        return null;
    }
}
