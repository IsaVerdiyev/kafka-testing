package isa.learning;

import static org.springframework.boot.SpringApplication.run;

import java.lang.invoke.MethodHandles;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class KafkaIdempontancyTestConsumer {
    public static void main(String[] args) {
        run(MethodHandles.lookup().lookupClass());
    }
}
