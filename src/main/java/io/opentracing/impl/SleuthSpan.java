package io.opentracing.impl;

import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.Tracer;

/**
 * Created by ddcbdevins on 1/9/17.
 */
public class SleuthSpan extends AbstractSpan implements SleuthSpanContext {

    private Tracer tracer;

    private Span span;

    static SleuthSpan create(Tracer tracer, String operationName) {
        return new SleuthSpan(tracer, operationName);
    }

    private SleuthSpan(Tracer tracer, String operationName) {
        super(operationName);
        this.tracer = tracer;

        if (tracer.getCurrentSpan() != null) {
            this.span = this.tracer.createSpan(operationName, this.tracer.getCurrentSpan());
        } else {
            this.span = this.tracer.createSpan(operationName);
        }
    }

    @Override
    public void finish() {
        super.finish();
        tracer.close(span);
    }

    public long getContextTraceId() {
        return span.getTraceId();
    }

    public long getContextSpanId() {
        return span.getSpanId();
    }

    public Long getContextParentSpanId() {
        return span.getParents().size() > 0 ? span.getParents().get(0) : null;
    }
}
