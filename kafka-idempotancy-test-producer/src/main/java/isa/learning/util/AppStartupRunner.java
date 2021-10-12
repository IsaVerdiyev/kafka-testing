package isa.learning.util;

import isa.learning.logger.MainLogger;
import isa.learning.service.KafkaProducerTransactional;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AppStartupRunner implements ApplicationRunner {
    private static final MainLogger LOG =
            MainLogger.getLogger(AppStartupRunner.class);

    private final KafkaProducerTransactional kafkaProducerTransactional;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        LOG.info("Application started with option names : {}",
                args.getOptionNames());
        kafkaProducerTransactional.produceMessage();

    }
}