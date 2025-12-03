package com.example.playercomm;

import com.example.playercomm.handler.PlayerCommunicationHandler;

/**
 * Main class to demonstrate the Player communication system.
 *
 * Responsibilities:
 * - Initialize the PlayerCommunicationHandler
 * - Set up players
 * - Start the communication flow
 *
 * Notes:
 * - Demonstrates initiator sending 10 messages and receiving 10 replies
 */
public class Main {

    public static void main(String[] args) {
        try {
            // Initialize the communication handler
            PlayerCommunicationHandler handler = new PlayerCommunicationHandler();

            // Setup players: initiator and responder
            handler.setupPlayers("Initiator", "Responder");

            // Start sending messages
            handler.startCommunication();

            System.out.println("Communication finished successfully.");

        } catch (Exception e) {
            System.err.println("An unexpected error occurred during communication:");
            e.printStackTrace();
        }
    }
}
