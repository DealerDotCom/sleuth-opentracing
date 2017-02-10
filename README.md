# sleuth-opentracing

This project aims to provide a bridge to allow OpenTracing native instrumentation to
work nicely in apps using the `spring-cloud-sleuth` tracer. It provides implementations
of OpenTracings interfaces that create and modify sleuth Spans.

### Caveats

1. While `sleuth-opentracing`'s implementation works on sleuth spans, it does not do the
work to associate new spans with sleuths Tracer, nor does it provide a way to retrieve
the current sleuth span from sleuth. To associate the span with sleuth, a developer must
call `continue` on the sleuth tracer with their new sleuth span. 

2. While `sleuth-opentracing`'s implementation works on sleuth spans, it does not do the
work to have sleuth report those spans. This must be done after you finish the
OpenTracing span by closing the sleuth span with the sleuth tracer.

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