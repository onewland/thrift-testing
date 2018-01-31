package com.crowdflower;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class TestbedForThrift {
    public static final String RABBIT_MQ_URL = "amqp://guest:guest@localhost:5672";

    public static void main(String[] args) throws Exception {
        ExecutorService executorService = Executors.newFixedThreadPool(4);
        MQProducer mqProducer = new MQProducer(RABBIT_MQ_URL);
        MQConsumer mqConsumer = new MQConsumer(RABBIT_MQ_URL, mqProducer.getQueue());
        MQConsumer mqConsumer2 = new MQConsumer(RABBIT_MQ_URL, mqProducer.getQueue());

        executorService.submit(mqProducer);
//        executorService.submit(mqConsumer);
//        executorService.submit(mqConsumer2);

        executorService.awaitTermination(15, TimeUnit.MINUTES);

        mqConsumer.shutdown();
        mqConsumer2.shutdown();
    }
}
