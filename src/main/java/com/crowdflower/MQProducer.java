package com.crowdflower;

import com.github.javafaker.Faker;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.transport.AutoExpandingBufferWriteTransport;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeoutException;

public class MQProducer implements Runnable {
    private final Connection connection;
    private final Channel channel;
    private final AMQP.Queue.DeclareOk mq;
    private final Faker faker;
    private AutoExpandingBufferWriteTransport tMemoryBuffer;
    private TBinaryProtocol tbp;

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
        tMemoryBuffer = new AutoExpandingBufferWriteTransport(256, 2);
        faker = Faker.instance();
    }

    public String getQueue() {
        return mq.getQueue();
    }

    @Override
    public void run() {
        try {
            System.out.println("Publishing on " + mq.getQueue());

            for(int i = 0; i < 100; i++) {
                IntAndString ias = new IntAndString(faker.name().fullName(), i);
                tbp = new TBinaryProtocol(tMemoryBuffer);
                ias.write(tbp);
                channel.basicPublish("", mq.getQueue(), null, tMemoryBuffer.getBuf().array());
                tMemoryBuffer.reset();
            }

            shutdown();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }

    public void shutdown() throws IOException, TimeoutException {
        channel.close();
        connection.close();
    }
}
