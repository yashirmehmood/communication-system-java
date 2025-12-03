package com.example.playercomm.handler;

import com.example.playercomm.core.Player;
import com.example.playercomm.factory.PlayerFactory;
import com.example.playercomm.transport.MessageBroker;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * PlayerCommunicationHandler is responsible for orchestrating
 * the communication between Player instances in the same JVM.
 *
 * Responsibilities:
 * - Create players using PlayerFactory
 * - Manage sending and receiving of messages
 * - Implement the stop condition:
 *   initiator sends 10 messages and stops after receiving 10 replies
 */
public class PlayerCommunicationHandler {

    private final MessageBroker broker;
    private final PlayerFactory factory;

    private Player initiator;
    private Player responder;

    private final AtomicInteger initiatorReceivedCount = new AtomicInteger(0);
    private final int maxMessages = 10;

    public PlayerCommunicationHandler() {
        this.broker = new MessageBroker();
        this.factory = new PlayerFactory(broker);
    }

    /**
     * Initializes the players and sets up the message flow.
     */
    public void setupPlayers(String initiatorName, String responderName) {
        initiator = factory.createPlayer(initiatorName);
        responder = factory.createPlayer(responderName);

        // Override receiveMessage to implement auto-reply
        responder = new Player(responderName, broker) {
            @Override
            public void receiveMessage(String senderName, String message) {
                super.receiveMessage(senderName, message);

                // Reply with received message + send counter
                String reply = message + " [" + (super.getName() + "-reply") + "]";
                sendMessage(senderName, reply);
            }
        };

        // Override initiator's receiveMessage to track replies
        initiator = new Player(initiatorName, broker) {
            @Override
            public void receiveMessage(String senderName, String message) {
                super.receiveMessage(senderName, message);
                int received = initiatorReceivedCount.incrementAndGet();
                if (received >= maxMessages) {
                    System.out.println("Initiator received all messages. Ending communication.");
                }
            }
        };

        // Register overridden players in the broker
        broker.registerPlayer(initiator);
        broker.registerPlayer(responder);
    }

    /**
     * Starts the messaging process where the initiator sends messages to the responder.
     */
    public void startCommunication() {
        for (int i = 1; i <= maxMessages; i++) {
            String message = "Message " + i;
            initiator.sendMessage(responder.getName(), message);

            // Sleep briefly to allow messages to be processed sequentially
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}
