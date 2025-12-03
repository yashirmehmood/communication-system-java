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
 * - For same-process mode, allows user to choose automatic or manual messaging
 * - Validates all user inputs (role, ports, message count)
 * - Uses CommunicationHandlerFactory to create the appropriate handler
 * - Starts the communication flow
 *
 * Notes:
 * - Factory pattern is used to handle handler object creation
 * - Fully interactive for manual messaging or automated for testing
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
            case 1 -> runSameProcessMode(scanner);
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
     * - Prompts user to select automatic or manual messaging mode
     * - Starts the communication flow
     *
     * @param scanner Scanner for reading user input
     */
    private static void runSameProcessMode(Scanner scanner) {
        System.out.println("[Mode] Same-process mode selected.");

        // Ask user for messaging mode: automatic vs manual
        System.out.println("Select messaging mode:");
        System.out.println("1. Automatic messages (10 messages sent automatically)");
        System.out.println("2. Manual messages (type each message)");

        int messageMode = InputUtils.readInt(scanner, "Enter choice (1 or 2): ", 1, 2);
        boolean automaticMode = messageMode == 1;

        // Create handler using factory
        SameProcessCommunicationHandler handler =
                (SameProcessCommunicationHandler) CommunicationHandlerFactory.createHandler(
                        "same", null, 0, 0, 0, automaticMode);

        handler.setupPlayers("Initiator", "Responder");

        // Start communication based on user's choice
        handler.startCommunication(automaticMode);
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

        String role = InputUtils.readRole(scanner, "Enter role");

        System.out.println("Please select ports in the range 1024 - 65535.");
        int myPort = InputUtils.readPort(scanner, "Enter your port: ");
        int otherPort = InputUtils.readPort(scanner, "Enter other player's port: ");

        // Number of messages is fixed as per requirement
        int maxMessages = 10;

        SeparateProcessCommunicationHandler handler =
                (SeparateProcessCommunicationHandler) CommunicationHandlerFactory.createHandler(
                        "separate", role, myPort, otherPort, maxMessages);

        handler.startCommunication();
    }
}