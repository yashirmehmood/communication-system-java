package com.example.playercomm.util;

import java.util.Scanner;

/**
 * Utility class for reading validated inputs from the console.
 *
 * Responsibilities:
 * - Read integers within a specified range
 * - Read TCP ports (1024–65535)
 * - Read valid roles for the player
 *
 * Notes:
 * - reusable across multiple classes
 */
public class InputUtils {
    private static final int MIN_PORT = 1024;
    private static final int MAX_PORT = 65535;

    /**
     * Reads an integer from the user within the specified range.
     */
    public static int readInt(Scanner scanner, String prompt, int min, int max) {
        int value;
        while (true) {
            System.out.print(prompt);
            if (scanner.hasNextInt()) {
                value = scanner.nextInt();
                if (value >= min && value <= max) {
                    break;
                }
            } else {
                scanner.next(); // discard invalid input
            }
            System.out.printf("Invalid input. Enter a number between %d and %d.%n", min, max);
        }
        return value;
    }

    /**
     * Reads a valid TCP port (1024–65535) from the user.
     */
    public static int readPort(Scanner scanner, String prompt) {
        return readInt(scanner, prompt, MIN_PORT, MAX_PORT);
    }

    /**
     * Reads a valid role ("initiator" or "responder") from the user.
     */
    public static String readRole(Scanner scanner, String prompt) {
        String role;
        while (true) {
            System.out.print(prompt);
            role = scanner.next().trim().toLowerCase();
            if (role.equals("initiator") || role.equals("responder")) {
                break;
            }
            System.out.println("Invalid role. Enter 'initiator' or 'responder'.");
        }
        return role;
    }
}