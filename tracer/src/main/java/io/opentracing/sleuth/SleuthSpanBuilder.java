package io.opentracing.sleuth;

import io.opentracing.References;
import io.opentracing.SpanContext;
import io.opentracing.Tracer;
import org.springframework.cloud.sleuth.Span;

import java.util.*;

public class SleuthSpanBuilder implements Tracer.SpanBuilder {
    private static final Random random = new Random();

    private final org.springframework.cloud.sleuth.Tracer tracer;

    private final Map<String, String> tags = new LinkedHashMap<>();
    private final List<org.springframework.cloud.sleuth.SpanContext> parents = new ArrayList<>();

    private String operationName;
    private long timestamp;

    public SleuthSpanBuilder(org.springframework.cloud.sleuth.Tracer tracer, String operationName) {
        this.tracer = tracer;
        this.operationName = operationName;
    }

    /**
     * {@inheritDoc}
     */
    public SleuthSpanBuilder asChildOf(SpanContext parent) {
        return addReference(References.CHILD_OF, parent);
    }

    /**
     * {@inheritDoc}
     */
    public SleuthSpanBuilder asChildOf(io.opentracing.Span parent) {
        return asChildOf(parent.context());
    }

    /**
     * {@inheritDoc}
     */
    public SleuthSpanBuilder addReference(String referenceType, SpanContext referencedContext) {
        if (References.CHILD_OF.equals(referenceType) || References.FOLLOWS_FROM.equals(referenceType)) {
            parents.add(((SleuthSpanContext) referencedContext).unwrap());
        }
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public SleuthSpanBuilder withTag(String key, String value) {
        tags.put(key, value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public SleuthSpanBuilder withTag(String key, boolean value) {
        return withTag(key, Boolean.toString(value));
    }

    /**
     * {@inheritDoc}
     */
    public SleuthSpanBuilder withTag(String key, Number value) {
        return withTag(key, value.toString());
    }

    /**
     * {@inheritDoc}
     */
    public SleuthSpanBuilder withStartTimestamp(long microseconds) {
        this.timestamp = microseconds;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public SleuthSpan start() {
        Span.SpanBuilder builder = Span.builder();
        builder.name(operationName).tags(tags);

        Span parentSpan = null;
        for (org.springframework.cloud.sleuth.SpanContext parent : parents) {
            if (parent instanceof Span) {
                parentSpan = (Span) parent;
            }
        }
        if (parentSpan != null) {
            builder.parent(parentSpan.getSpanId())
                    .traceIdHigh(parentSpan.getTraceIdHigh())
                    .traceId(parentSpan.getTraceId())
                    .spanId(random.nextLong());
        }
        if (timestamp != 0) {
            builder.begin(Math.round(timestamp / 1000));
        }

        return SleuthSpan.wrap(tracer, builder.build());
    }

    /**
     * {@inheritDoc}
     */
    public Iterable<Map.Entry<String, String>> baggageItems() {
        if (parents.size() == 0) {
            return Collections.emptyList();
        }
        List<Map.Entry<String, String>> baggage = new ArrayList<>();
        for (org.springframework.cloud.sleuth.SpanContext parent : parents) {
            for (Map.Entry<String, String> bag : parent.baggageItems()) {
                baggage.add(bag);
            }
        }
        return baggage;
    }
}
