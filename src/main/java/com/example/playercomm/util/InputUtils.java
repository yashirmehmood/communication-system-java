package com.example.playercomm.util;

import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * Utility class to read validated input from the user.
 *
 * Responsibilities:
 * - Read integers within a specified range
 * - Read yes/no decisions
 * - Read role selection (initiator/responder)
 * - Read port numbers with validation
 *
 * Notes:
 * - All input methods loop until valid input is provided
 * - Designed to be reusable across different communication modes
 */
public class InputUtils {

    private static final int MIN_PORT = 1024;
    private static final int MAX_PORT = 65535;

    /**
     * Reads an integer within a specified range.
     *
     * @param scanner Scanner object for input
     * @param prompt  Message to show to the user
     * @param min     Minimum valid value
     * @param max     Maximum valid value
     * @return Valid integer input
     */
    public static int readInt(Scanner scanner, String prompt, int min, int max) {
        int value;
        while (true) {
            System.out.print(prompt);
            try {
                value = Integer.parseInt(scanner.nextLine().trim());
                if (value < min || value > max) {
                    System.out.println("Please enter a number between " + min + " and " + max + ".");
                } else {
                    return value;
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid integer.");
            }
        }
    }

    /**
     * Reads a yes/no input from the user.
     *
     * @param scanner Scanner object for input
     * @param prompt  Message to show to the user
     * @return true if user entered 'y' or 'Y', false otherwise
     */
    public static boolean readYesNo(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt + " (y/n): ");
            String input = scanner.nextLine().trim().toLowerCase();
            if ("y".equals(input)) return true;
            if ("n".equals(input)) return false;
            System.out.println("Invalid input. Please enter 'y' or 'n'.");
        }
    }

    /**
     * Reads the role from user: initiator or responder.
     * Short input allowed: 'i' for initiator, 'r' for responder (case-insensitive)
     *
     * @param scanner Scanner object for input
     * @param prompt  Message to show to the user
     * @return "initiator" or "responder"
     */
    public static String readRoleShort(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim().toLowerCase();
            if ("i".equals(input)) return "initiator";
            if ("r".equals(input)) return "responder";
            System.out.println("Invalid input. Enter 'i' for initiator or 'r' for responder.");
        }
    }

    /**
     * Reads a valid port number from the user.
     *
     * @param scanner Scanner object for input
     * @param prompt  Message to show to the user
     * @return Valid port number within 1024-65535
     */
    public static int readPort(Scanner scanner, String prompt) {
        return readInt(scanner, prompt, MIN_PORT, MAX_PORT);
    }
}