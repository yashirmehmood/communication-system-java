package com.example.playercomm;

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
 * - Initializes the appropriate communication handler
 * - Starts the communication flow
 *
 * Notes:
 * - Designed for extensibility and minimal changes for future modes
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
     * Initializes SameProcessCommunicationHandler and starts communication.
     */
    private static void runSameProcessMode() {
        System.out.println("[Mode] Same-process mode selected.");

        SameProcessCommunicationHandler handler = new SameProcessCommunicationHandler();
        handler.setupPlayers("Initiator", "Responder");
        handler.startCommunication();
    }

    /**
     * Handles separate-process mode.
     * Prompts the user for role, ports, and message count, then starts the socket-based handler.
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
                new SeparateProcessCommunicationHandler(role, myPort, otherPort, maxMessages);

        handler.startCommunication();
    }
}
