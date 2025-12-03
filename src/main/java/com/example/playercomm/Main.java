package com.example.playercomm;

import com.example.playercomm.core.factory.CommunicationHandlerFactory;
import com.example.playercomm.handler.SameProcessCommunicationHandler;
import com.example.playercomm.handler.SeparateProcessCommunicationHandler;
import com.example.playercomm.util.InputUtils;

import java.util.Scanner;

/**
 * Main entry point of the Player Communication System.
 *
 * Responsibilities:
 * - Provides user-friendly mode selection:
 *      1. Same-process mode (both players in same JVM)
 *      2. Separate-process mode (players in different JVMs)
 * - Validates all user inputs (role, ports, message count)
 * - Uses CommunicationHandlerFactory to create the appropriate handler
 * - Starts the communication flow
 *
 * Notes:
 * - Factory pattern is used to handle handler object creation
 */
public class Main {

    private static final int MIN_MESSAGES = 1;
    private static final int MAX_MESSAGES = 1000;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("=== Player Communication System ===");
        System.out.println("Select mode:");
        System.out.println("1. Same-process mode (both players in same JVM)");
        System.out.println("2. Separate-process mode (players in different JVMs)");

        int choice = InputUtils.readInt(scanner, "Enter choice (1 or 2): ", 1, 2);

        switch (choice) {
            case 1 -> runSameProcessMode();
            case 2 -> runSeparateProcessMode(scanner);
        }

        System.out.println("Program finished.");
        scanner.close();
    }

    /**
     * Handles same-process mode.
     * Responsibilities:
     * - Uses factory to create SameProcessCommunicationHandler
     * - Sets up players in the handler
     * - Starts the communication flow
     */
    private static void runSameProcessMode() {
        System.out.println("[Mode] Same-process mode selected.");

        SameProcessCommunicationHandler handler =
                (SameProcessCommunicationHandler) CommunicationHandlerFactory.createHandler("same", null, 0, 0, 0);

        handler.setupPlayers("Initiator", "Responder");
        handler.startCommunication();
    }

    /**
     * Handles separate-process mode.
     * Responsibilities:
     * - Reads role, ports, and max messages from user
     * - Uses factory to create SeparateProcessCommunicationHandler
     * - Starts the communication flow
     *
     * @param scanner Scanner object for reading console input
     */
    private static void runSeparateProcessMode(Scanner scanner) {
        System.out.println("[Mode] Separate-process mode selected.");

        String role = InputUtils.readRole(scanner, "Enter role (initiator/responder): ");
        int myPort = InputUtils.readPort(scanner, "Enter your port: ");
        int otherPort = InputUtils.readPort(scanner, "Enter other player's port: ");
        int maxMessages = InputUtils.readInt(scanner, "Enter number of messages to exchange: ", MIN_MESSAGES, MAX_MESSAGES);

        SeparateProcessCommunicationHandler handler =
                (SeparateProcessCommunicationHandler) CommunicationHandlerFactory.createHandler(
                        "separate", role, myPort, otherPort, maxMessages);

        handler.startCommunication();
    }
}