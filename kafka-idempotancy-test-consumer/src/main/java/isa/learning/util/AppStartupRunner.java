package isa.learning.util;

import isa.learning.listener.ConsumerKafkaListenerTransactional;
import isa.learning.logger.MainLogger;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AppStartupRunner implements ApplicationRunner {
    private static final MainLogger LOG =
            MainLogger.getLogger(AppStartupRunner.class);

    private final ConsumerKafkaListenerTransactional consumerKafkaListenerTransactional;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        LOG.info("Application started with option names : {}",
                args.getOptionNames());
        consumerKafkaListenerTransactional.consumeCbarNewIndividualOperationMessage();

    }
}