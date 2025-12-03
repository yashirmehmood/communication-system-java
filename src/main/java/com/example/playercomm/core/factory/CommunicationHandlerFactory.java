package com.example.playercomm.core.factory;

import com.example.playercomm.handler.SameProcessCommunicationHandler;
import com.example.playercomm.handler.SeparateProcessCommunicationHandler;

/**
 * Factory responsible for creating communication handlers for the Player system.
 *
 * Responsibilities:
 * - Creates SameProcessCommunicationHandler or SeparateProcessCommunicationHandler
 * - Supports automatic/manual mode selection for same-process
 * - Encapsulates handler creation logic to keep Main clean
 */
public class CommunicationHandlerFactory {

    /**
     * Creates a handler based on the specified mode.
     *
     * @param mode         "same" for same-process, "separate" for separate-process
     * @param role         role for separate-process ("initiator" or "responder"), ignored for same-process
     * @param myPort       port for separate-process, ignored for same-process
     * @param otherPort    other player's port for separate-process, ignored for same-process
     * @param maxMessages  max messages to exchange (used for separate-process), ignored for same-process
     * @return Object instance of the handler (cast appropriately in Main)
     */
    public static Object createHandler(String mode, String role, int myPort, int otherPort, int maxMessages) {
        return createHandler(mode, role, myPort, otherPort, maxMessages, true);
    }

    /**
     * Overloaded version for same-process mode to specify automatic/manual message sending.
     *
     * @param mode          "same" or "separate"
     * @param role          role for separate-process
     * @param myPort        port for separate-process
     * @param otherPort     other player's port for separate-process
     * @param maxMessages   max messages for separate-process
     * @param automaticMode true = automatic messages, false = manual input (only for same-process)
     * @return Object instance of handler
     */
    public static Object createHandler(String mode, String role, int myPort, int otherPort, int maxMessages, boolean automaticMode) {
        if ("same".equalsIgnoreCase(mode)) {
            SameProcessCommunicationHandler handler = new SameProcessCommunicationHandler();
            // Store automaticMode inside the handler if needed, or pass when starting communication
            return handler;
        } else if ("separate".equalsIgnoreCase(mode)) {
            return new SeparateProcessCommunicationHandler(role, myPort, otherPort, maxMessages);
        } else {
            throw new IllegalArgumentException("Invalid mode: " + mode);
        }
    }
}