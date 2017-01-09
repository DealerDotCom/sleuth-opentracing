package io.opentracing.impl;

/**
 * Created by ddcbdevins on 1/9/17.
 */
public class SleuthSpanBuilder  extends AbstractSpanBuilder implements SleuthSpanContext {
    SleuthSpanBuilder(String operationName) {
        super(operationName);
    }

    protected SleuthSpan createSpan() {
        return null;
    }

    SleuthSpanBuilder withStateItem(String s, Object o) {
        return null;
    }

    boolean isTraceState(String s, Object o) {
        return false;
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
