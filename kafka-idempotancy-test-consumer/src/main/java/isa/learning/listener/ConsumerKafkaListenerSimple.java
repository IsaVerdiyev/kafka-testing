package isa.learning.listener;

import isa.learning.logger.MainLogger;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.lang.invoke.MethodHandles;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ConsumerKafkaListenerSimple {

    private static final MainLogger LOGGER = MainLogger.getLogger(MethodHandles.lookup().lookupClass());
    private static int counter = 0;
    private final ObjectMapper mapper;

//    @org.springframework.kafka.annotation.KafkaListener(topics = "${kafka.topics.test.message-producer}")
//    @Transactional
    public void consumeCbarNewIndividualOperationMessage(String message) {
        try {
            LOGGER.info("Kafka event model for triggering job: {}", message);
            counter++;
            LOGGER.info("counter: " + counter);
            LOGGER.info("end listening");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
