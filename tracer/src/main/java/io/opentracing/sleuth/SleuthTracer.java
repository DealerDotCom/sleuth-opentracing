package io.opentracing.sleuth;

import io.opentracing.SpanContext;
import io.opentracing.Tracer;
import io.opentracing.propagation.Format;
import io.opentracing.propagation.TextMap;
import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.SpanTextMap;
import org.springframework.cloud.sleuth.instrument.web.SleuthWebProperties;
import org.springframework.cloud.sleuth.instrument.web.ZipkinHttpSpanExtractor;
import org.springframework.cloud.sleuth.instrument.web.ZipkinHttpSpanInjector;

import java.util.Iterator;
import java.util.Map;
import java.util.regex.Pattern;

public final class SleuthTracer implements Tracer {

    private final org.springframework.cloud.sleuth.Tracer tracer;

    public SleuthTracer(org.springframework.cloud.sleuth.Tracer tracer) {
        this.tracer = tracer;
    }

    public SpanBuilder buildSpan(String operationName) {
        return new SleuthSpanBuilder(tracer, operationName);
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
        if (format.equals(Format.Builtin.HTTP_HEADERS)) {
            Span span = ((SleuthSpan)(io.opentracing.Span) spanContext).unwrap();
            TextMap textMap = (TextMap) carrier;
            SpanTextMapAdapter adapter = new SpanTextMapAdapter(textMap);
            new ZipkinHttpSpanInjector().inject(span, adapter);
            // TODO do I need to add CS annotation?
        } else { // TODO implement other types?
            throw new IllegalArgumentException("Only HTTP HEADERS Format supported");
        }
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
        if (format.equals(Format.Builtin.HTTP_HEADERS)) {
            TextMap textMap = (TextMap) carrier;
            ZipkinHttpSpanExtractor extractor = new ZipkinHttpSpanExtractor(Pattern.compile(SleuthWebProperties.DEFAULT_SKIP_PATTERN));
            Span span = extractor.joinTrace(new SpanTextMapAdapter(textMap));
            // TODO do I need to add SR annotation?
            return SleuthSpanContext.wrap(span);
        } // TODO implement other types?
        throw new IllegalArgumentException("Only HTTP HEADERS Format supported");
    }

    class SpanTextMapAdapter implements SpanTextMap {

        private final TextMap delegate;

        public SpanTextMapAdapter(TextMap textMap) {
            this.delegate = textMap;
        }

        @Override
        public Iterator<Map.Entry<String, String>> iterator() {
            return delegate.iterator();
        }

        @Override
        public void put(String key, String value) {
            delegate.put(key, value);
        }
    }
}
