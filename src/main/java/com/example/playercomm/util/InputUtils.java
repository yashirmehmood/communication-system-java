package com.example.playercomm.util;

import java.util.Scanner;

/**
 * Utility class to handle input validation from the console.
 */
public class InputUtils {

    /**
     * Reads an integer from the user between min and max (inclusive).
     */
    public static int readInt(Scanner scanner, String prompt, int min, int max) {
        int value;
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine();
            try {
                value = Integer.parseInt(line);
                if (value >= min && value <= max) {
                    return value;
                }
                System.out.println("Please enter a number between " + min + " and " + max + ".");
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
    }

    /**
     * Reads the role from the user.
     * Accepts 'i' or 'r' (case-insensitive) and converts to full role string.
     *
     * @param scanner Scanner for reading input
     * @param prompt  Prompt to display
     * @return "initiator" or "responder"
     */
    public static String readRole(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt + " (i = initiator, r = responder): ");
            String line = scanner.nextLine().trim().toLowerCase();
            if ("i".equals(line)) {
                return "initiator";
            } else if ("r".equals(line)) {
                return "responder";
            } else {
                System.out.println("Invalid input. Please enter 'i' for initiator or 'r' for responder.");
            }
        }
    }

    /**
     * Reads a port number from the user.
     */
    public static int readPort(Scanner scanner, String prompt) {
        return readInt(scanner, prompt + " (1024-65535): ", 1024, 65535);
    }
}