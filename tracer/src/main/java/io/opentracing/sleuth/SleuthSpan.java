package io.opentracing.sleuth;

import io.opentracing.Span;
import io.opentracing.SpanContext;
import org.springframework.cloud.sleuth.Tracer;

import java.util.Map;

public final class SleuthSpan implements Span {

    static SleuthSpan wrap(Tracer tracer, org.springframework.cloud.sleuth.Span span) {
        if (span == null) throw new NullPointerException("span == null");
        return new SleuthSpan(tracer, span);
    }

    public org.springframework.cloud.sleuth.Span unwrap() {
        return delegate;
    }

    private final Tracer tracer;

    private final org.springframework.cloud.sleuth.Span delegate;
    private final SpanContext spanContext;

    private SleuthSpan(Tracer tracer, org.springframework.cloud.sleuth.Span span) {
        this.tracer = tracer;
        this.delegate = span;
        this.spanContext = SleuthSpanContext.wrap(span);
    }

    public SpanContext context() {
        return spanContext;
    }

    public void finish() {
        delegate.stop();
        tracer.close(delegate);
    }

    public void finish(long finishMicros) {
        // TODO sleuth doesn't support setting the end time
        log(finishMicros, "io.opentracing.sleuth.SleuthSpan.finish(long)");
        finish();
    }

    public void close() {
        finish();
    }

    public SleuthSpan setTag(String key, String value) {
        delegate.tag(key, value);
        return this;
    }

    public SleuthSpan setTag(String key, boolean value) {
        return setTag(key, Boolean.toString(value));
    }

    public SleuthSpan setTag(String key, Number value) {
        return setTag(key, value.toString());
    }

    // Helper method
    private String fieldsToString(Map<String, ?> fields) {
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, ?> field : fields.entrySet()) {
            builder.append(String.format("%s=%s", field.getKey(), field.getValue().toString()));
        }
        return builder.toString();
    }

    public SleuthSpan log(Map<String, ?> fields) {
        if (fields.isEmpty()) return this;
        return log(fieldsToString(fields));
    }

    public SleuthSpan log(long timestampMicroseconds, Map<String, ?> fields) {
        if (fields.isEmpty()) return this;
        return log(timestampMicroseconds, fieldsToString(fields));
    }

    public SleuthSpan log(String event) {
        delegate.logEvent(event);
        return this;
    }

    public SleuthSpan log(long timestampMicroseconds, String event) {
        delegate.logEvent(timestampMicroseconds, event);
        return this;
    }

    public SleuthSpan setBaggageItem(String key, String value) {
        delegate.setBaggageItem(key, value);
        return this;
    }

    public String getBaggageItem(String key) {
        return delegate.getBaggageItem(key);
    }

    public Iterable<Map.Entry<String, String>> baggageItems() {
        return delegate.baggageItems();
    }

    public SleuthSpan setOperationName(String operationName) {
        // TODO sleuth does not support this at this time
        return this;
    }

    public SleuthSpan log(String eventName, Object ignored) {
        return log(eventName);
    }

    public SleuthSpan log(long timestampMicroseconds, String eventName, Object ignored) {
        return log(timestampMicroseconds, eventName);
    }
}
