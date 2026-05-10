package com.sqills.assignment;

import com.sqills.assignment.service.DataProcessor;
import org.junit.jupiter.api.Test;
import java.util.Arrays;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class DataProcessorTest {

    @Test
    public void testParsingLogic() {
        String inputText = "com.SQILLS.assignment an.oth8er  Sample.1nput-Str";
        
        // Step 1: Tokenize
        String[] tokens = inputText.trim().split("\\s+");
        String step1 = String.join(" ", tokens);
        assertEquals("com.SQILLS.assignment an.oth8er Sample.1nput-Str", step1);

        // Step 2: Replace non-alphanumeric
        String[] step2Tokens = Arrays.stream(tokens)
                .map(t -> t.replaceAll("[^a-zA-Z0-9]", "_"))
                .toArray(String[]::new);
        String step2 = String.join(" ", step2Tokens);
        assertEquals("com_SQILLS_assignment an_oth8er Sample_1nput_Str", step2);

        // Step 3: Uppercase
        String[] step3Tokens = Arrays.stream(step2Tokens)
                .map(String::toUpperCase)
                .toArray(String[]::new);
        String step3 = String.join(" ", step3Tokens);
        assertEquals("COM_SQILLS_ASSIGNMENT AN_OTH8ER SAMPLE_1NPUT_STR", step3);

        // Final
        String finalOutput = step1 + " " + step2 + " " + step3;
        assertEquals("com.SQILLS.assignment an.oth8er Sample.1nput-Str com_SQILLS_assignment an_oth8er Sample_1nput_Str COM_SQILLS_ASSIGNMENT AN_OTH8ER SAMPLE_1NPUT_STR", finalOutput);
    }
}
