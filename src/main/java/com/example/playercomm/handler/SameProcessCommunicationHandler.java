package com.example.playercomm.handler;

import com.example.playercomm.core.Player;
import com.example.playercomm.core.factory.PlayerFactory;
import com.example.playercomm.model.Message;
import com.example.playercomm.transport.PlayerMessageRouter;
import com.example.playercomm.util.InputUtils;

import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Handles communication between two Player instances running in the same JVM process.
 *
 * Responsibilities:
 * - Creates players using PlayerFactory
 * - Allows initiator to send messages automatically or manually
 * - Implements stop condition: initiator sends and receives 10 messages
 */
public class SameProcessCommunicationHandler {

    private final PlayerMessageRouter broker;
    private final PlayerFactory factory;
    private final Scanner scanner;

    private Player initiator;
    private Player responder;

    private final AtomicInteger initiatorReceivedCount = new AtomicInteger(0);
    private final int maxMessages = 10;

    public SameProcessCommunicationHandler(Scanner scanner) {
        this.scanner = scanner;
        this.broker = new PlayerMessageRouter();
        this.factory = new PlayerFactory(broker);
    }

    /**
     * Creates and registers the initiator and responder players.
     *
     * @param initiatorName Name of the initiating player
     * @param responderName Name of the responding player
     */
    public void setupPlayers(String initiatorName, String responderName) {

        responder = new Player(responderName, broker) {
            private int replyCounter = 0;

            @Override
            public void receiveMessage(Message message) {
                System.out.println("[" + getName() + "] received: " + message.getContent());
                replyCounter++;
                String reply = message.getContent() + " [" + replyCounter + "]";
                sendMessage(message.getSender(), reply);
            }
        };

        initiator = new Player(initiatorName, broker) {
            @Override
            public void receiveMessage(Message message) {
                System.out.println("[" + getName() + "] received: " + message.getContent());
                int received = initiatorReceivedCount.incrementAndGet();
                if (received >= maxMessages) {
                    System.out.println("Initiator received all replies. Communication complete.");
                }
            }
        };

        broker.registerPlayer(initiator);
        broker.registerPlayer(responder);
    }

    /**
     * Starts the messaging process where the initiator sends messages to the responder.
     * User can choose automatic or manual message sending.
     */
    public void startCommunication() {
        boolean automatic = InputUtils.readYesNo(scanner, "Do you want to send messages automatically?");
        try {
            if (automatic) {
                for (int i = 1; i <= maxMessages; i++) {
                    String msg = "Message " + i;
                    initiator.sendMessage(responder.getName(), msg);
                    Thread.sleep(100);
                }
            } else {
                int sent = 0;
                while (sent < maxMessages) {
                    String msg = InputUtils.readLine(scanner, "Enter message " + (sent + 1) + ": ");
                    initiator.sendMessage(responder.getName(), msg);
                    sent++;
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Communication interrupted: " + e.getMessage());
        }
    }
}