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
 * - Provides mode selection: same-process or separate-process
 * - Validates all user inputs
 * - Uses CommunicationHandlerFactory to create handler instances
 * - Starts communication flow
 */
public class Main {

    private static final int MIN_PORT = 1024;
    private static final int MAX_PORT = 65535;
    private static final int MAX_MESSAGES = 10;

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
     *
     * @param scanner Scanner instance for user input
     */
    private static void runSameProcessMode(Scanner scanner) {
        System.out.println("[Mode] Same-process mode selected.");

        SameProcessCommunicationHandler handler =
                (SameProcessCommunicationHandler) CommunicationHandlerFactory.createHandler("same", scanner, null, 0, 0, 0);

        handler.setupPlayers("Initiator", "Responder");
        handler.startCommunication();
    }

    /**
     * Handles separate-process mode.
     *
     * @param scanner Scanner instance for user input
     */
    private static void runSeparateProcessMode(Scanner scanner) {
        System.out.println("[Mode] Separate-process mode selected.");

        String role = InputUtils.readRole(scanner, "Enter role (i = initiator, r = responder): ");

        System.out.println("Please select ports in the range " + MIN_PORT + " - " + MAX_PORT + ".");
        int myPort = InputUtils.readPort(scanner, "Enter your port (" + MIN_PORT + "-" + MAX_PORT + "): ");
        int otherPort = InputUtils.readPort(scanner, "Enter other player's port (" + MIN_PORT + "-" + MAX_PORT + "): ");

        SeparateProcessCommunicationHandler handler =
                (SeparateProcessCommunicationHandler) CommunicationHandlerFactory.createHandler(
                        "separate", scanner, role, myPort, otherPort, MAX_MESSAGES);

        handler.startCommunication();
    }
}