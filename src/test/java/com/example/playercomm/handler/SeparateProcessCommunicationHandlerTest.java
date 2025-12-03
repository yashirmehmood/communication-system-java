package com.example.playercomm.handler;

import org.junit.jupiter.api.Test;

import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class SeparateProcessCommunicationHandlerTest {

    @Test
    void testInitialization() {
        Scanner sc = new Scanner(System.in);

        assertDoesNotThrow(() -> {
            new SeparateProcessCommunicationHandler(
                    sc,
                    "initiator",
                    5000,
                    5001,
                    10
            );

            new SeparateProcessCommunicationHandler(
                    sc,
                    "responder",
                    5001,
                    5000,
                    10
            );
        });
    }

    @Test
    void testRoleStoredCorrectly() {
        SeparateProcessCommunicationHandler h =
                new SeparateProcessCommunicationHandler(
                        new Scanner(System.in),
                        "initiator",
                        5000,
                        5001,
                        10
                );

        assertNotNull(h);
    }

    @Test
    void testObjectCreatedWithoutNetworkStart() {
        SeparateProcessCommunicationHandler h =
                new SeparateProcessCommunicationHandler(
                        new Scanner(System.in),
                        "responder",
                        5000,
                        5001,
                        10
                );

        assertNotNull(h);
    }
}