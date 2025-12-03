package com.example.playercomm;

import com.example.playercomm.core.factory.CommunicationHandlerFactory;
import com.example.playercomm.handler.base.AbstractCommunicationHandler;
import com.example.playercomm.handler.SameProcessCommunicationHandler;
import com.example.playercomm.util.InputUtils;

import java.util.Scanner;

/**
 * Main entry point of the Player Communication System.
 *
 * Responsibilities:
 * - Provides an interactive console to select communication mode
 * - Validates all user inputs using InputUtils
 * - Instantiates appropriate communication handler using CommunicationHandlerFactory
 * - Starts the messaging workflow for same-process or separate-process mode
 * - Manages proper shutdown of resources
 */
public class Main {

    private static final int MIN_PORT = 1024;
    private static final int MAX_PORT = 65535;
    private static final int MAX_MESSAGES = 10;

    /**
     * Application entry point.
     *
     * @param args command-line arguments (unused)
     */
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
     * Handles the same-process communication mode.
     *
     * Responsibilities:
     * - Creates initiator and responder players within the same JVM
     * - Sets up player instances using SameProcessCommunicationHandler
     * - Starts the messaging flow (automatic or manual) for the initiator
     *
     * @param scanner Scanner instance for user input
     */
    private static void runSameProcessMode(Scanner scanner) {
        System.out.println("[Mode] Same-process mode selected.");

        AbstractCommunicationHandler handler =
                CommunicationHandlerFactory.createHandler("same", scanner, null, 0, 0, 0);

        // Cast is safe because same-process handler requires setup
        SameProcessCommunicationHandler sameHandler =
                (SameProcessCommunicationHandler) handler;

        sameHandler.setupPlayers("Initiator", "Responder");
        sameHandler.startCommunication();
    }

    /**
     * Handles the separate-process communication mode.
     *
     * Responsibilities:
     * - Prompts the user to choose the role (initiator or responder)
     * - Reads TCP port configuration for local and remote communication
     * - Instantiates SeparateProcessCommunicationHandler via factory
     * - Starts the communication flow (automatic/manual for initiator, always listening for responder)
     *
     * @param scanner Scanner instance for user input
     */
    private static void runSeparateProcessMode(Scanner scanner) {
        System.out.println("[Mode] Separate-process mode selected.");

        System.out.println("Select your role:");
        String role = InputUtils.readRole(scanner, "Enter role (i = initiator, r = responder): ");

        System.out.println("Please select TCP ports in the range " + MIN_PORT + " - " + MAX_PORT + ".");
        int myPort = InputUtils.readPort(scanner, "Enter your port (" + MIN_PORT + "-" + MAX_PORT + "): ");

        int otherPort = 0;
        if ("initiator".equals(role)) {
            otherPort = InputUtils.readPort(scanner, "Enter responder's port (" + MIN_PORT + "-" + MAX_PORT + "): ");
        }

        System.out.println("Starting " + role + " on port " + myPort + "...");
        if ("responder".equals(role)) {
            System.out.println("[Responder] Waiting for initiator to connect...");
        } else {
            System.out.println("[Initiator] Will attempt to connect to responder at port " + otherPort + "...");
        }

        AbstractCommunicationHandler handler =
                CommunicationHandlerFactory.createHandler("separate", scanner, role, myPort, otherPort, MAX_MESSAGES);

        handler.startCommunication();
    }
}