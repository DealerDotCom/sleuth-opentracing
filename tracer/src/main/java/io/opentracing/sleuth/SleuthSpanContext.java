package io.opentracing.sleuth;

import io.opentracing.SpanContext;

import java.util.Map;

public class SleuthSpanContext implements SpanContext {

    private org.springframework.cloud.sleuth.SpanContext spanContext;

    static SleuthSpanContext wrap(org.springframework.cloud.sleuth.SpanContext spanContext) {
        return new SleuthSpanContext(spanContext);
    }

    private SleuthSpanContext(org.springframework.cloud.sleuth.SpanContext spanContext) {
        this.spanContext = spanContext;
    }

    final org.springframework.cloud.sleuth.SpanContext unwrap() {
        return spanContext;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterable<Map.Entry<String, String>> baggageItems() {
        return spanContext.baggageItems();
    }
}
