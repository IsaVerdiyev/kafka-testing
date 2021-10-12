package isa.learning.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.support.converter.ByteArrayJsonMessageConverter;

@EnableKafka
@Configuration
public class KafkaConfig {

    @Bean
    public ByteArrayJsonMessageConverter jsonConverter() {
        return new ByteArrayJsonMessageConverter();
    }
}
