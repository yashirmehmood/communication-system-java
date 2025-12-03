package com.example.playercomm.core.factory;

import com.example.playercomm.handler.SameProcessCommunicationHandler;
import com.example.playercomm.handler.SeparateProcessCommunicationHandler;
import org.junit.jupiter.api.Test;

import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertTrue;

class CommunicationHandlerFactoryTest {

    @Test
    void testCreateHandler() {
        Scanner scanner = new Scanner(System.in);

        Object sameHandler = CommunicationHandlerFactory.createHandler("same", scanner, null, 0, 0, 0);
        assertTrue(sameHandler instanceof SameProcessCommunicationHandler);

        Object separateHandler = CommunicationHandlerFactory.createHandler("separate", scanner, "initiator", 5000, 5001, 10);
        assertTrue(separateHandler instanceof SeparateProcessCommunicationHandler);
    }
}