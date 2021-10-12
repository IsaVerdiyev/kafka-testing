package isa.learning.listener;

import static java.time.Duration.ofSeconds;
import static java.util.Collections.singleton;

import isa.learning.logger.MainLogger;
import java.lang.invoke.MethodHandles;
import java.util.Properties;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ConsumerKafkaListenerTransactional {

    private static final MainLogger LOGGER = MainLogger.getLogger(MethodHandles.lookup().lookupClass());
    private static int counter = 0;

    public void consumeCbarNewIndividualOperationMessage() {
        try {
            Properties consumerProps = new Properties();
            consumerProps.put("bootstrap.servers", "localhost:9092");
            consumerProps.put("group.id", "kafka-idempontancy");
            consumerProps.put("enable.auto.commit", "false");
            consumerProps.put("isolation.level", "read_committed");
            consumerProps.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
            consumerProps.put("value.deserializer", "org.apache.kafka.common.serialization.ByteArrayDeserializer");
            consumerProps.put("enable.partition.eof", "false");
            KafkaConsumer<String, String> consumer = new KafkaConsumer<>(consumerProps);
            consumer.subscribe(singleton("message-producer"));

            while(true) {
                ConsumerRecords<String, String> records = consumer.poll(ofSeconds(3));
                for(ConsumerRecord<String, String> record: records){
                    counter++;
                }
                consumer.commitSync();
                LOGGER.info("counter: " + counter);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
