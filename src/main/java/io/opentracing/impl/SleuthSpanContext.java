package io.opentracing.impl;

/**
 * Created by ddcbdevins on 1/9/17.
 */
public interface SleuthSpanContext {
    long getContextTraceId();
    long getContextSpanId();
    Long getContextParentSpanId();
}
