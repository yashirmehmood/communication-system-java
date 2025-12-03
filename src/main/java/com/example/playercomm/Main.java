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
 * - Uses CommunicationHandlerFactory to create appropriate handler instances
 * - Supports automatic or manual message sending
 * - Starts the communication flow
 *
 * Notes:
 * - Scanner is passed to handlers to centralize user input
 * - Designed to be easily extensible for future modes or features
 */
public class Main {

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
     * - Sets up players
     * - Starts communication (automatic or manual)
     *
     * @param scanner Scanner instance for user input
     */
    private static void runSameProcessMode(Scanner scanner) {
        System.out.println("[Mode] Same-process mode selected.");

        SameProcessCommunicationHandler handler =
                (SameProcessCommunicationHandler) CommunicationHandlerFactory.createHandler(
                        "same", scanner, null, 0, 0, 0
                );

        handler.setupPlayers("Initiator", "Responder");
        handler.startCommunication();
    }

    /**
     * Handles separate-process mode.
     * Responsibilities:
     * - Reads role, ports from user
     * - Creates SeparateProcessCommunicationHandler using factory
     * - Starts communication (automatic or manual)
     *
     * @param scanner Scanner instance for user input
     */
    private static void runSeparateProcessMode(Scanner scanner) {
        System.out.println("[Mode] Separate-process mode selected.");

        String role = InputUtils.readRoleShort(scanner, "Enter role (i = initiator, r = responder): ");
        int myPort = InputUtils.readPort(scanner, "Enter your port (1024-65535): ");
        int otherPort = InputUtils.readPort(scanner, "Enter other player's port (1024-65535): ");

        SeparateProcessCommunicationHandler handler =
                (SeparateProcessCommunicationHandler) CommunicationHandlerFactory.createHandler(
                        "separate", scanner, role, myPort, otherPort, 10
                );

        handler.startCommunication();
    }
}