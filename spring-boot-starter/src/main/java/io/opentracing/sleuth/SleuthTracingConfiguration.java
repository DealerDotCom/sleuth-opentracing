package io.opentracing.sleuth;

import io.opentracing.Tracer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SleuthTracingConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public Tracer tracer() {
        return new SleuthTracer();
    }

}
