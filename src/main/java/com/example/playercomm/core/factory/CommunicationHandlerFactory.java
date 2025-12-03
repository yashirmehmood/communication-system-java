package com.example.playercomm.core.factory;

import com.example.playercomm.handler.SameProcessCommunicationHandler;
import com.example.playercomm.handler.SeparateProcessCommunicationHandler;

/**
 * Factory class to create communication handler instances based on mode.
 *
 * Responsibilities:
 * - Hide concrete handler instantiation from Main
 * - Provide an easy way to extend new communication modes in the future
 */
public class CommunicationHandlerFactory {

    /**
     * Creates a handler object based on the selected mode.
     *
     * @param mode       "same" for same-process, "separate" for separate-process
     * @param role       player role (only for separate-process mode)
     * @param myPort     local port (only for separate-process mode)
     * @param otherPort  other player port (only for separate-process mode)
     * @param maxMessages number of messages to exchange (only for separate-process mode)
     * @return Object of the appropriate handler class
     */
    public static Object createHandler(String mode, String role, int myPort, int otherPort, int maxMessages) {
        return switch (mode.toLowerCase()) {
            case "same" -> new SameProcessCommunicationHandler();
            case "separate" -> new SeparateProcessCommunicationHandler(role, myPort, otherPort, maxMessages);
            default -> throw new IllegalArgumentException("Invalid mode: " + mode);
        };
    }
}
