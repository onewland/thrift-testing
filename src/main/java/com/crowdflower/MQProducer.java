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
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
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
            Random r = new Random();
            System.out.println("Publishing on " + mq.getQueue());

            for(int i = 0; i < 100000; i++) {
                IntAndString ias = new IntAndString();
                SubStruct ss = new SubStruct();
                ss.addToNumbers(i);
                ss.addToNumbers(i*2);
                ss.addToNumbers(i/2);
                ias.setName(faker.name().fullName());
                ias.setCount(i);
                Map<String,String> m = new HashMap<>();
                for(int j = 0; j < 3; j++) {
                    m.put(faker.harryPotter().character(), faker.harryPotter().quote());
                }
                ias.setFlexMetaData(m);
                ias.setNumberOfAtoms(Math.abs(r.nextLong()));
                ias.setHello(ss);

                tbp = new TBinaryProtocol(tMemoryBuffer);
                ias.write(tbp);
                channel.basicPublish("", mq.getQueue(), null, tMemoryBuffer.getBuf().array());
                tMemoryBuffer.reset();
                Thread.sleep(250);
            }

            shutdown();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void shutdown() throws IOException, TimeoutException {
        channel.close();
        connection.close();
    }
}
