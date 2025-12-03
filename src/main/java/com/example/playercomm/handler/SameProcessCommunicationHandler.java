package com.example.playercomm.handler;

import com.example.playercomm.core.Player;
import com.example.playercomm.core.factory.PlayerFactory;
import com.example.playercomm.model.Message;
import com.example.playercomm.transport.MessageBroker;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Orchestrates the communication between Player instances in the same JVM.
 *
 * Responsibilities:
 * - Creates players using PlayerFactory
 * - Sets up message sending and receiving
 * - Implements the assignment stop condition: initiator sends 10 messages and stops after 10 replies
 *
 * Notes:
 * - Both players run in the same process
 */
public class SameProcessCommunicationHandler {

    private final MessageBroker broker;
    private final PlayerFactory factory;

    private Player initiator;
    private Player responder;

    private final AtomicInteger initiatorReceivedCount = new AtomicInteger(0);
    private final int maxMessages = 10;

    public SameProcessCommunicationHandler() {
        this.broker = new MessageBroker();
        this.factory = new PlayerFactory(broker);
    }

    /**
     * Creates and registers the initiator and responder players.
     *
     * @param initiatorName Name of the initiating player
     * @param responderName Name of the responding player
     */
    public void setupPlayers(String initiatorName, String responderName) {

        initiator = factory.createPlayer(initiatorName);
        responder = factory.createPlayer(responderName);

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
    }

    /**
     * Starts the messaging process where the initiator sends messages to the responder.
     */
    public void startCommunication() {
        try {
            for (int i = 1; i <= maxMessages; i++) {
                String msg = "Message " + i;
                initiator.sendMessage(responder.getName(), msg);
                Thread.sleep(100);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Communication interrupted: " + e.getMessage());
        }
    }
}