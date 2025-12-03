package com.example.playercomm.util;

import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * Utility class for reading and validating user inputs from the console.
 *
 * Responsibilities:
 * - Read integers with min/max constraints
 * - Read role selection (initiator/responder) with shortcuts
 * - Read port numbers within valid TCP range
 * - Read a line of text for manual messages
 *
 * Notes:
 * - Designed for reusability across different handlers
 * - All methods validate input and prompt until valid input is entered
 */
public class InputUtils {

    private static final int MIN_PORT = 1024;
    private static final int MAX_PORT = 65535;

    /**
     * Reads an integer from the user within the specified range.
     *
     * @param scanner Scanner instance
     * @param prompt  Message to display
     * @param min     Minimum allowed value
     * @param max     Maximum allowed value
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
                    continue;
                }
                return value;
            } catch (NumberFormatException e) {
                System.out.println("Invalid number. Please try again.");
            }
        }
    }

    /**
     * Reads the player role from user input.
     * Accepts 'i' or 'r' (case-insensitive) and converts to full role name.
     *
     * @param scanner Scanner instance
     * @param prompt  Message to display
     * @return "initiator" or "responder"
     */
    public static String readRole(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim().toLowerCase();
            switch (input) {
                case "i":
                    return "initiator";
                case "r":
                    return "responder";
                default:
                    System.out.println("Invalid input. Please enter 'i' for initiator or 'r' for responder.");
            }
        }
    }

    /**
     * Reads a TCP port number from the user within valid range (1024–65535).
     *
     * @param scanner Scanner instance
     * @param prompt  Message to display
     * @return Valid port number
     */
    public static int readPort(Scanner scanner, String prompt) {
        return readInt(scanner, prompt, MIN_PORT, MAX_PORT);
    }

    /**
     * Reads a single line of text from the user.
     *
     * @param scanner Scanner instance
     * @param prompt  Message to display
     * @return User-entered text
     */
    public static String readLine(Scanner scanner, String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }

    /**
     * Reads yes/no choice from user input.
     * Accepts 'y' or 'n' (case-insensitive).
     *
     * @param scanner Scanner instance
     * @param prompt  Message to display
     * @return true for yes, false for no
     */
    public static boolean readYesNo(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt + " (y/n): ");
            String input = scanner.nextLine().trim().toLowerCase();
            switch (input) {
                case "y":
                    return true;
                case "n":
                    return false;
                default:
                    System.out.println("Invalid input. Please enter 'y' or 'n'.");
            }
        }
    }
}