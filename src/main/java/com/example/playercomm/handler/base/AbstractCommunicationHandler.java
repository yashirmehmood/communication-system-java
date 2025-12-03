package com.example.playercomm.handler.base;

import java.io.IOException;
import java.util.Scanner;

/**
 * Abstract base class for communication handlers.
 *
 * Responsibilities:
 * - Provides common logic for sending messages automatically or manually
 * - Ensures DRY design for future extensions
 */
public abstract class AbstractCommunicationHandler {

    protected final Scanner scanner;
    protected final int maxMessages;

    protected AbstractCommunicationHandler(Scanner scanner, int maxMessages) {
        this.scanner = scanner;
        this.maxMessages = maxMessages;
    }

    /**
     * Starts the communication flow. Must be implemented by subclasses.
     */
    public abstract void startCommunication();

    /**
     * Provides option for user to send messages automatically or manually.
     *
     * @throws IOException if sending fails (used for socket-based handlers)
     */
    protected void sendMessagesWithUserChoice() throws IOException {
        boolean automatic = com.example.playercomm.util.InputUtils.readYesNo(scanner, "Do you want to send messages automatically?");
        if (automatic) {
            sendMessagesAutomatically();
        } else {
            sendMessagesManually();
        }
    }

    /**
     * Sends messages automatically. Implementation provided by subclass.
     *
     * @throws IOException if sending fails
     */
    protected abstract void sendMessagesAutomatically() throws IOException;

    /**
     * Sends messages manually. Implementation provided by subclass.
     *
     * @throws IOException if sending fails
     */
    protected abstract void sendMessagesManually() throws IOException;
}