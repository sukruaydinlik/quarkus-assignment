package com.sqills.assignment.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.jms.ConnectionFactory;
import jakarta.jms.JMSConsumer;
import jakarta.jms.JMSContext;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.Session;
import jakarta.jms.TextMessage;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.event.Observes;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.jboss.logging.Logger;

@ApplicationScoped
public class MessageConsumer implements Runnable {

    private static final Logger LOG = Logger.getLogger(MessageConsumer.class);

    @Inject
    ConnectionFactory connectionFactory;

    @Inject
    DataProcessor dataProcessor;

    private final ExecutorService scheduler = Executors.newSingleThreadExecutor();

    void onStart(@Observes StartupEvent ev) {
        scheduler.submit(this);
    }

    @Override
    public void run() {
        try (JMSContext context = connectionFactory.createContext(Session.AUTO_ACKNOWLEDGE)) {
            // Subscribe to the topic using a queue (Artemis specific or just standard JMS queue consuming from topic)
            // The requirement says: "A consumer in the application should subscribe to this topic using a JMS queue"
            // This usually implies a shared subscription or just a queue that is bound to the topic.
            // In many cases, it means consuming from a queue that is already receiving messages from the topic.
            // For simplicity in a standard JMS way, I'll consume from a queue named "data-queue".
            // I will configure Artemis to bridge topic to queue or just use the queue.
            // Actually, the requirement says "subscribe to this topic using a JMS queue". 
            // This is a bit ambiguous, but I'll use a queue named "data-queue" and expect it to be bound to the topic.
            
            JMSConsumer consumer = context.createConsumer(context.createQueue("data-queue"));
            while (true) {
                Message message = consumer.receive();
                if (message instanceof TextMessage) {
                    String text = ((TextMessage) message).getText();
                    dataProcessor.processAndStore(text);
                }
            }
        } catch (JMSException e) {
            LOG.error("Error in consumer", e);
        }
    }
}
