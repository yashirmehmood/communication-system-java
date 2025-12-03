package com.example.playercomm.handler;

import com.example.playercomm.core.Player;
import com.example.playercomm.core.factory.PlayerFactory;
import com.example.playercomm.model.Message;
import com.example.playercomm.transport.PlayerMessageRouter;

import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Orchestrates the communication between Player instances in the same JVM.
 *
 * Responsibilities:
 * - Creates players using PlayerFactory
 * - Sets up message sending and receiving
 * - Implements automatic and manual messaging modes
 * - Stops communication after initiator sends and receives maxMessages
 * - Performs cleanup by unregistering players and freeing resources
 *
 * Notes:
 * - Both players run in the same process
 */
public class SameProcessCommunicationHandler {

    private final PlayerMessageRouter router;
    private final PlayerFactory factory;

    private Player initiator;
    private Player responder;

    private final AtomicInteger initiatorReceivedCount = new AtomicInteger(0);
    private final int maxMessages = 10;

    public SameProcessCommunicationHandler() {
        this.router = new PlayerMessageRouter();
        this.factory = new PlayerFactory(router);
    }

    /**
     * Creates and registers the initiator and responder players.
     *
     * @param initiatorName Name of the initiating player
     * @param responderName Name of the responding player
     */
    public void setupPlayers(String initiatorName, String responderName) {

        // Basic registration (factory + router)
        initiator = factory.createPlayer(initiatorName);
        responder = factory.createPlayer(responderName);

        // Responder automatically replies with incremented counter
        responder = new Player(responderName, router) {
            private int replyCounter = 0;

            @Override
            public void receiveMessage(Message message) {
                super.receiveMessage(message);
                replyCounter++;
                String reply = message.getContent() + " [" + replyCounter + "]";
                sendMessage(message.getSender(), reply);
            }
        };

        // Initiator tracks received messages and stops at maxMessages
        initiator = new Player(initiatorName, router) {
            @Override
            public void receiveMessage(Message message) {
                super.receiveMessage(message);
                int received = initiatorReceivedCount.incrementAndGet();
                if (received >= maxMessages) {
                    System.out.println("Initiator received all replies. Communication complete.");
                }
            }
        };

        router.registerPlayer(initiator);
        router.registerPlayer(responder);
    }

    /**
     * Starts the messaging process in either automatic or manual mode.
     *
     * @param automaticMode true for automatic messages, false for manual input
     */
    public void startCommunication(boolean automaticMode) {
        try {
            if (automaticMode) {
                sendAutomaticMessages();
            } else {
                sendManualMessages();
            }
        } finally {
            cleanup(); // Ensure cleanup always happens
        }
    }

    /**
     * Sends maxMessages automatically from initiator to responder.
     */
    private void sendAutomaticMessages() {
        try {
            for (int i = 1; i <= maxMessages; i++) {
                String msg = "Message " + i;
                initiator.sendMessage(responder.getName(), msg);
                Thread.sleep(100);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Automatic messaging interrupted: " + e.getMessage());
        }
    }

    /**
     * Reads messages from the user and sends them from initiator to responder.
     */
    private void sendManualMessages() {
        Scanner scanner = new Scanner(System.in);
        int counter = 1;

        while (initiatorReceivedCount.get() < maxMessages) {
            System.out.print("Enter message #" + counter + ": ");
            String msg = scanner.nextLine();
            if (msg == null || msg.isEmpty()) {
                System.out.println("Message cannot be empty. Try again.");
                continue;
            }
            initiator.sendMessage(responder.getName(), msg);
            counter++;
        }
    }

    /**
     * Performs cleanup by unregistering players from the router
     * and clearing local references to help garbage collection.
     */
    private void cleanup() {
        if (initiator != null) {
            initiator.shutdown(); // unregister from router
            initiator = null;
        }
        if (responder != null) {
            responder.shutdown(); // unregister from router
            responder = null;
        }
        System.out.println("SameProcessCommunicationHandler: cleanup completed.");
    }
}