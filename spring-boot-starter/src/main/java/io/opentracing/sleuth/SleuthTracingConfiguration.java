package io.opentracing.sleuth;

import io.opentracing.Tracer;
import io.opentracing.impl.SleuthTracer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SleuthTracingConfiguration {

    @Bean
    public Tracer tracer() {
        return new SleuthTracer();
    }

}
