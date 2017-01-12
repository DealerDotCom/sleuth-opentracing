package io.opentracing.sleuth;

import io.opentracing.Span;
import io.opentracing.SpanContext;
import org.springframework.cloud.sleuth.Tracer;

import java.util.ArrayList;
import java.util.Map;

public class SleuthSpan implements Span, SpanContext {

    private Tracer tracer;

    private org.springframework.cloud.sleuth.Span span;

    static SleuthSpan create(Tracer tracer, String operationName) {
        return new SleuthSpan(tracer, operationName);
    }

    private SleuthSpan(Tracer tracer, String operationName) {
        this.tracer = tracer;

        if (tracer.getCurrentSpan() != null) {
            this.span = this.tracer.createSpan(operationName, this.tracer.getCurrentSpan());
        } else {
            this.span = this.tracer.createSpan(operationName);
        }
    }

    public SpanContext context() {
        return this;
    }

    public void finish() {
        tracer.close(span);
    }

    public void finish(long finishMicros) {
        // TODO sleuth doesn't support setting the end time
        finish();
    }

    public void close() {
        finish();
    }

    public SleuthSpan setTag(String key, String value) {
        span.tag(key, value);
        return this;
    }

    public SleuthSpan setTag(String key, boolean value) {
        return setTag(key, String.valueOf(value));
    }

    public SleuthSpan setTag(String key, Number value) {
        return setTag(key, String.valueOf(value));
    }

    public SleuthSpan log(Map<String, ?> fields) {
        for (Map.Entry<String, ?> field : fields.entrySet()) {
            log(String.format("%s: %s", field.getKey(), field.getValue().toString()));
        }
        return this;
    }

    public SleuthSpan log(long timestampMicroseconds, Map<String, ?> fields) {
        for (Map.Entry<String, ?> field : fields.entrySet()) {
            log(String.format("[%d] %s: %s", timestampMicroseconds, field.getKey(), field.getValue().toString()));
        }
        return this;
    }

    public SleuthSpan log(String event) {
        span.logEvent(event);
        return this;
    }

    public SleuthSpan log(long timestampMicroseconds, String event) {
        return log(String.format("[%d] %s", timestampMicroseconds, event));
    }

    public SleuthSpan setBaggageItem(String key, String value) {
        return this;
    }

    public String getBaggageItem(String key) {
        return null;
    }

    public Iterable<Map.Entry<String, String>> baggageItems() {
        return new ArrayList<Map.Entry<String, String>>(0);
    }

    public SleuthSpan setOperationName(String operationName) {
        // TODO sleuth does not support this at this time
        return this;
    }

    public SleuthSpan log(String eventName, Object payload) {
        return log(String.format("%s: %s", eventName, payload.toString()));
    }

    public SleuthSpan log(long timestampMicroseconds, String eventName, Object payload) {
        return log(String.format("[%d] %s: %s", timestampMicroseconds, eventName, payload.toString()));
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
