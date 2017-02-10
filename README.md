# sleuth-opentracing

This project aims to provide a bridge to allow OpenTracing native instrumentation to
work nicely in apps using the `spring-cloud-sleuth` tracer. It provides implementations
of OpenTracings interfaces that create and modify sleuth Spans.

### Caveats

While `sleuth-opentracing`'s implementation works on sleuth spans, the following caveats exist:

1. it does not do the work to associate new spans with sleuths tracer. To associate the span
with sleuth, a developer must call `continue` on the sleuth tracer with their new sleuth span.

2. it does not make the current sleuth span a parent of the new spans it creates. This must be
done by wrapping the current sleuth span in a `SleuthSpan` and pass it into the `asChildOf(Span)`
`SpanBuilder` method.

3. it does not do the work to have sleuth report those spans. This must be done after you
finish the OpenTracing span by closing the sleuth span with the sleuth tracer.

4. it does not reattach the old sleuth span upon completion of the span you created. This
must be done by passing the old span into the `continue` method on the sleuth tracer.

#### Caveat Example

```java
SleuthTracer otTracer = ....;
Tracer sleuthTracer = ...;

public Object around(ProceedingJoinPoint jp) {
    org.springframework.cloud.sleuth.Span parent = sleuthTracer.getCurrentSpan();
    Span span = otTracer.buildSpan("my-operation").asChildOf(Span.wrap(parent)).start();
    sleuthTracer.continue(span.unwrap());
    
    Object result = jp.proceed();
    
    span.finish();
    sleuthTracer.detach(span.unwrap());
    sleuthTracer.continue(parent);
    
    return result;
}
```