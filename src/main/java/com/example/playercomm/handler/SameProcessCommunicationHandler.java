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
 * - Supports automatic or manual message sending
 * - Implements stop condition: initiator sends 10 messages and stops after 10 replies
 *
 * Notes:
 * - Both players run in the same process
 * - Scanner is used for manual message input
 */
public class SameProcessCommunicationHandler {

    private final PlayerMessageRouter broker;
    private final PlayerFactory factory;
    private final Scanner scanner;

    private Player initiator;
    private Player responder;

    private final AtomicInteger initiatorReceivedCount = new AtomicInteger(0);
    private final int maxMessages = 10;

    private boolean manualMode = false;

    public SameProcessCommunicationHandler(Scanner scanner) {
        this.broker = new PlayerMessageRouter();
        this.factory = new PlayerFactory(broker);
        this.scanner = scanner;
    }

    public void setupPlayers(String initiatorName, String responderName) {
        // Create and register players
        initiator = factory.createPlayer(initiatorName);
        responder = factory.createPlayer(responderName);

        // Wrap responder to automatically reply with counter
        responder = new Player(responderName, broker) {
            private int replyCounter = 0;

            @Override
            public void receiveMessage(Message message) {
                super.receiveMessage(message);
                replyCounter++;
                String reply = message.getContent() + " [" + replyCounter + "]";
                sendMessage(message.getSender(), reply);
            }
        };

        // Wrap initiator to track received messages
        initiator = new Player(initiatorName, broker) {
            @Override
            public void receiveMessage(Message message) {
                super.receiveMessage(message);
                int received = initiatorReceivedCount.incrementAndGet();
                if (received >= maxMessages) {
                    System.out.println("Initiator received all replies. Communication complete.");
                }
            }
        };

        broker.registerPlayer(initiator);
        broker.registerPlayer(responder);

        // Ask user if manual or automatic mode
        manualMode = com.example.playercomm.util.InputUtils.readYesNo(scanner,
                "Do you want to send messages manually?");
    }

    public void startCommunication() {
        try {
            for (int i = 1; i <= maxMessages; i++) {
                String msg;
                if (manualMode) {
                    System.out.print("Enter message " + i + ": ");
                    msg = scanner.nextLine();
                } else {
                    msg = "Message " + i;
                    Thread.sleep(100); // small delay for readability
                }
                initiator.sendMessage(responder.getName(), msg);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Communication interrupted: " + e.getMessage());
        }
    }
}