package io.opentracing.sleuth;

import io.opentracing.SpanContext;
import io.opentracing.Tracer;
import io.opentracing.propagation.Format;

public final class SleuthTracer implements Tracer {

    public SpanBuilder buildSpan(String operationName) {
        return new SleuthSpanBuilder(operationName);
    }

    /**
     * Inject a SpanContext into a `carrier` of a given type, presumably for propagation across process boundaries.
     * <p>
     * <p>Example:
     * <pre>{@code
     * Tracer tracer = ...
     * Span clientSpan = ...
     * TextMap httpHeadersCarrier = new AnHttpHeaderCarrier(httpRequest);
     * tracer.inject(span.context(), Format.Builtin.HTTP_HEADERS, httpHeadersCarrier);
     * }</pre>
     *
     * @param spanContext the SpanContext instance to inject into the carrier
     * @param format      the Format of the carrier
     * @param carrier     the carrier for the SpanContext state. All Tracer.inject() implementations must support io.opentracing.propagation.TextMap and java.nio.ByteBuffer.
     * @see Format
     * @see Format.Builtin
     */
    public <C> void inject(SpanContext spanContext, Format<C> format, C carrier) {
        // TODO implement
    }

    /**
     * Extract a SpanContext from a `carrier` of a given type, presumably after propagation across a process boundary.
     * <p>
     * <p>Example:
     * <pre>{@code
     * Tracer tracer = ...
     * TextMap httpHeadersCarrier = new AnHttpHeaderCarrier(httpRequest);
     * SpanContext spanCtx = tracer.extract(Format.Builtin.HTTP_HEADERS, httpHeadersCarrier);
     * tracer.buildSpan('...').asChildOf(spanCtx).start();
     * }</pre>
     * <p>
     * If the span serialized state is invalid (corrupt, wrong version, etc) inside the carrier this will result in an
     * IllegalArgumentException.
     *
     * @param format  the Format of the carrier
     * @param carrier the carrier for the SpanContext state. All Tracer.extract() implementations must support
     *                io.opentracing.propagation.TextMap and java.nio.ByteBuffer.
     * @return the SpanContext instance holding context to create a Span.
     * @see Format
     * @see Format.Builtin
     */
    public <C> SpanContext extract(Format<C> format, C carrier) {
        // TODO implement
        return null;
    }
}
