package com.example.playercomm.core.factory;

import com.example.playercomm.handler.SameProcessCommunicationHandler;
import com.example.playercomm.handler.SeparateProcessCommunicationHandler;

import java.util.Scanner;

/**
 * Factory for creating communication handler instances.
 *
 * Responsibilities:
 * - Creates handler instances based on the selected mode
 * - Passes Scanner to handlers for user input
 * - Ensures future extensibility for new communication modes
 */
public class CommunicationHandlerFactory {

    /**
     * Creates a communication handler based on the selected mode.
     *
     * @param mode        "same" or "separate"
     * @param scanner     Scanner instance to read user input
     * @param role        Role (initiator/responder) - only used for separate mode
     * @param myPort      Local port - only used for separate mode
     * @param otherPort   Other player's port - only used for separate mode
     * @param maxMessages Max messages - only used for separate mode
     * @return Communication handler instance
     */
    public static Object createHandler(String mode, Scanner scanner,
                                       String role, int myPort, int otherPort, int maxMessages) {
        return switch (mode.toLowerCase()) {
            case "same" -> new SameProcessCommunicationHandler(scanner);
            case "separate" -> new SeparateProcessCommunicationHandler(scanner, role, myPort, otherPort, maxMessages);
            default -> throw new IllegalArgumentException("Invalid mode: " + mode);
        };
    }
}