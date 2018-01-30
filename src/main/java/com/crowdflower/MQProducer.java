package com.crowdflower;

import com.github.javafaker.Faker;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.transport.TMemoryBuffer;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeoutException;

public class MQProducer implements Runnable {
    private final Connection connection;
    private final Channel channel;
    private final AMQP.Queue.DeclareOk mq;
    private Faker faker;

    MQProducer(String url) throws NoSuchAlgorithmException,
            KeyManagementException,
            URISyntaxException,
            IOException,
            TimeoutException
    {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setUri(url);
        connection = connectionFactory.newConnection();
        channel = connection.createChannel();
        mq = channel.queueDeclare("abcdefghi", false, false, true, null);
    }

    public String getQueue() {
        return mq.getQueue();
    }

    @Override
    public void run() {
        try {
            System.out.println("Publishing on " + mq.getQueue());

            for(int i = 0; i < 1000; i++) {
                faker = Faker.instance();
                IntAndString ias = new IntAndString(faker.name().fullName(), i);
                TMemoryBuffer tMemoryBuffer = new TMemoryBuffer(1024);
                TBinaryProtocol tbp = new TBinaryProtocol(tMemoryBuffer);
                ias.write(tbp);
                channel.basicPublish("", mq.getQueue(), null, tMemoryBuffer.getArray());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TException e) {
            e.printStackTrace();
        }
    }

    public void shutdown() throws IOException, TimeoutException {
        channel.close();
        connection.close();
    }
}
