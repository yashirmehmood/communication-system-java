package com.example.playercomm.handler;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class SameProcessCommunicationHandlerTest {

    @Test
    void testCommunicationFlowAutomatic() {
        // Simulate user input: "Y" for automatic messages
        String simulatedInput = "Y\n";
        ByteArrayInputStream inputStream =
                new ByteArrayInputStream(simulatedInput.getBytes(StandardCharsets.UTF_8));
        Scanner scanner = new Scanner(inputStream);

        SameProcessCommunicationHandler handler =
                new SameProcessCommunicationHandler(scanner);

        assertDoesNotThrow(() -> {
            handler.setupPlayers("Initiator", "Responder");
            handler.startCommunication();
        });
    }
}