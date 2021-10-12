package isa.learning.service;

import isa.learning.logger.MainLogger;
import java.lang.invoke.MethodHandles;
import java.util.Properties;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaProducerTransactional {

    private static final MainLogger LOGGER = MainLogger.getLogger(MethodHandles.lookup().lookupClass());


    public void produceMessage(){
        Properties producerProps = new Properties();
        producerProps.put("bootstrap.servers", "localhost:9092");
        producerProps.put("enable.idempotence", "true");
        producerProps.put("transactional.id", UUID.randomUUID().toString());
        producerProps.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        producerProps.put("value.serializer", "org.springframework.kafka.support.serializer.JsonSerializer");
        KafkaProducer<String, String> producer = new KafkaProducer(producerProps);
        producer.initTransactions();
        producer.beginTransaction();
        int counter = 0;
        for(int i = 1; i <= 100000; i++){
            LOGGER.info("trying to send");
            producer.send(new ProducerRecord<String, String>("message-producer", String.valueOf(i), String.valueOf(i)));
            if(i % 100 == 0){
                try{
                    LOGGER.info("committing");
                    producer.commitTransaction();
                    LOGGER.info("after commit. before begin");
                    counter+= 100;
                    LOGGER.info("counter: " + counter);
                    producer.beginTransaction();
                    LOGGER.info("after beginTransaction");
                }catch (Exception e){
                    LOGGER.error("exception occured");
                    e.printStackTrace();
                }
            }
        }
        producer.commitTransaction();
        producer.close();

    }
}
