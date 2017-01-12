package io.opentracing.impl;

import io.opentracing.References;
import io.opentracing.SpanContext;
import io.opentracing.Tracer;
import org.springframework.cloud.sleuth.Span;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ddcbdevins on 1/9/17.
 */
public class SleuthSpanBuilder implements Tracer.SpanBuilder {

    private org.springframework.cloud.sleuth.Tracer tracer;

    private String operationName;
    private long startTime;
    private SpanContext parent;

    private Map<String, String> withTags;

    static SleuthSpanBuilder create(org.springframework.cloud.sleuth.Tracer tracer, String operationName) {
        return new SleuthSpanBuilder(tracer, operationName);
    }

    private SleuthSpanBuilder(org.springframework.cloud.sleuth.Tracer tracer, String operationName) {
        this.tracer = tracer;
        this.operationName = operationName;

        withTags = new HashMap<String, String>();
    }

    protected SleuthSpan createSpan() {
        return SleuthSpan.create(tracer, operationName);
    }

    /**
     * A shorthand for addReference(References.CHILD_OF, parent).
     *
     * @param parent
     */
    public SleuthSpanBuilder asChildOf(SpanContext parent) {
        this.parent = parent;
        return this;
    }

    /**
     * A shorthand for addReference(References.CHILD_OF, parent.context()).
     *
     * @param parent
     */
    public SleuthSpanBuilder asChildOf(io.opentracing.Span parent) {
        return asChildOf((SpanContext) parent);
    }

    /**
     * Add a reference from the Span being built to a distinct (usually parent) Span. May be called multiple times to
     * represent multiple such References.
     *
     * @param referenceType     the reference type, typically one of the constants defined in References
     * @param referencedContext the SpanContext being referenced; e.g., for a References.CHILD_OF referenceType, the
     *                          referencedContext is the parent
     * @see References
     */
    public SleuthSpanBuilder addReference(String referenceType, SpanContext referencedContext) {
        if (referenceType.equals(References.CHILD_OF)) {
            asChildOf(referencedContext);
        }
        return this;
    }

    /**
     * Same as {@link Span#setTag(String, String)}, but for the span being built.
     *
     * @param key
     * @param value
     */
    public SleuthSpanBuilder withTag(String key, String value) {
        withTags.put(key, value);
        return this;
    }

    /**
     * Same as {@link Span#setTag(String, String)}, but for the span being built.
     *
     * @param key
     * @param value
     */
    public SleuthSpanBuilder withTag(String key, boolean value) {
        return withTag(key, String.valueOf(value));
    }

    /**
     * Same as {@link Span#setTag(String, String)}, but for the span being built.
     *
     * @param key
     * @param value
     */
    public SleuthSpanBuilder withTag(String key, Number value) {
        return withTag(key, String.valueOf(value));
    }

    /**
     * Specify a timestamp of when the Span was started, represented in microseconds since epoch.
     *
     * @param microseconds
     */
    public SleuthSpanBuilder withStartTimestamp(long microseconds) {
        this.startTime = microseconds;
        return this;
    }

    /**
     * Returns the started Span.
     */
    public SleuthSpan start() {
        return createSpan();
    }

    /**
     * @return all zero or more baggage items propagating along with the associated Span
     * @see Span#setBaggageItem(String, String)
     * @see Span#getBaggageItem(String)
     */
    public Iterable<Map.Entry<String, String>> baggageItems() {
        return new ArrayList<Map.Entry<String, String>>(0);
    }
}
