package com.sqills.assignment.service;

import com.sqills.assignment.entity.ProcessedData;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.jms.ConnectionFactory;
import jakarta.jms.JMSContext;
import jakarta.jms.Session;
import jakarta.transaction.Transactional;
import org.jboss.logging.Logger;

import java.util.Arrays;
import java.util.stream.Collectors;

@ApplicationScoped
public class DataProcessor {

    private static final Logger LOG = Logger.getLogger(DataProcessor.class);

    @Inject
    ConnectionFactory connectionFactory;

    public void publishToTopic(String text) {
        try (JMSContext context = connectionFactory.createContext(Session.AUTO_ACKNOWLEDGE)) {
            context.createProducer().send(context.createTopic("data-topic"), text);
        }
    }

    @Transactional
    public void processAndStore(String message) {
        int firstColon = message.indexOf(":");
        if (firstColon == -1) return;
        
        Long id = Long.parseLong(message.substring(0, firstColon));
        String inputText = message.substring(firstColon + 1);

        // Step 1: Tokenize using space as delimiter (one or more spaces)
        String[] tokens = inputText.trim().split("\\s+");
        String step1 = String.join(" ", tokens);

        // Step 2: Replace non-alphanumeric with '_'
        String[] step2Tokens = Arrays.stream(tokens)
                .map(t -> t.replaceAll("[^a-zA-Z0-9]", "_"))
                .toArray(String[]::new);
        String step2 = String.join(" ", step2Tokens);

        // Step 3: Convert to uppercase
        String[] step3Tokens = Arrays.stream(step2Tokens)
                .map(String::toUpperCase)
                .toArray(String[]::new);
        String step3 = String.join(" ", step3Tokens);

        // Final combination
        String finalOutput = step1 + " " + step2 + " " + step3;

        LOG.info(finalOutput);

        ProcessedData entity = ProcessedData.findById(id);
        if (entity != null) {
            entity.outputText = finalOutput;
        }
    }
}
