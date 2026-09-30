package com.stationerystore.stationery_store.shared.graphql;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.execution.RuntimeWiringConfigurer;

@Configuration
public class GraphQlConfig {

    @Bean
    RuntimeWiringConfigurer customScalars() {
        return wiring -> wiring.scalar(DateTimeScalar.INSTANCE);
    }
}
