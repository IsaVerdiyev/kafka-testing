package isa.learning.service;

import isa.learning.logger.MainLogger;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.lang.invoke.MethodHandles;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFutureCallback;

@Service
@RequiredArgsConstructor
public class KafkaProducerSimple {

    private static final MainLogger LOGGER = MainLogger.getLogger(MethodHandles.lookup().lookupClass());

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private static int counter = 0;

    @Value("${kafka.topics.test.message-producer}")
    private String messageProducerTopic;

    public void produceMessage(int i) {
        try {
            String message = objectMapper.writeValueAsString(i);
            UUID uuid = UUID.randomUUID();
            LOGGER.info("Sending message to consumer with topic and key {}, {}, {}", message, messageProducerTopic, uuid);

            kafkaTemplate.send(messageProducerTopic, uuid.toString(), message).addCallback(
                    new ListenableFutureCallback<>() {
                        @Override
                        public void onFailure(Throwable ex) {
                            LOGGER.error("Sending message to consumer failed: {}, {}. Key: {}", message, ex.getMessage(),
                                    uuid.toString());
                        }

                        @Override
                        public void onSuccess(SendResult<String, Object> result) {
                            LOGGER.info("Message successfully sent: {}, key: {}", message, uuid.toString());
                            counter++;
                            LOGGER.info("SUCCESSFULLY sent: " + counter);
                        }
                    });
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }
}

