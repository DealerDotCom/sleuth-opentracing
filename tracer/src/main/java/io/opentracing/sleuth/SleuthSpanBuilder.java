package io.opentracing.sleuth;

import io.opentracing.References;
import io.opentracing.SpanContext;
import io.opentracing.Tracer;
import org.springframework.cloud.sleuth.Span;

import java.util.*;

public class SleuthSpanBuilder implements Tracer.SpanBuilder {

    private String operationName;
    private final Map<String, String> tags = new LinkedHashMap<>();

    private long timestamp;
    private List<org.springframework.cloud.sleuth.SpanContext> parents = new ArrayList<>();

    public SleuthSpanBuilder(String operationName) {
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

        if (parents instanceof Span) {
            for (org.springframework.cloud.sleuth.SpanContext parent : parents) {
                builder.parent(((Span) parent).getSpanId());
            }
        }
        if (timestamp != 0) {
            builder.begin(timestamp / 1000);
        }

        return SleuthSpan.wrap(builder.build());
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
