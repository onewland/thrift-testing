package com.crowdflower;

import com.rabbitmq.client.*;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.transport.TMemoryInputTransport;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class MQConsumer implements Runnable {
    private final Connection connection;
    private final String listenerQueue;
    private final Channel channel;

    public MQConsumer(String rabbitMqUrl, String listenerQueue) throws Exception {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setUri(rabbitMqUrl);
        connection = connectionFactory.newConnection();
        channel = connection.createChannel();
        this.listenerQueue = listenerQueue;
    }

    public void run() {
        try {
            System.out.println("Listening on " + listenerQueue);
            channel.basicConsume(listenerQueue, true, new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope,
                                           AMQP.BasicProperties properties, byte[] body)
                        throws IOException {
                    TMemoryInputTransport tMemoryInputTransport = new TMemoryInputTransport(body);
                    TBinaryProtocol tbp = new TBinaryProtocol(tMemoryInputTransport);

                    IntAndString intAndString = new IntAndString();
                    try {
                        intAndString.read(tbp);
                    } catch (TException e) {
                        e.printStackTrace();
                    }

                    System.out.println(intAndString);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void shutdown() throws IOException, TimeoutException {
        channel.close();
        connection.close();
    }
}
