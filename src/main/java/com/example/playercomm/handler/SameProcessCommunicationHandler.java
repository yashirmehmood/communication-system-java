package com.example.playercomm.handler;

import com.example.playercomm.core.Player;
import com.example.playercomm.core.factory.PlayerFactory;
import com.example.playercomm.handler.base.AbstractCommunicationHandler;
import com.example.playercomm.model.Message;
import com.example.playercomm.transport.PlayerMessageRouter;
import com.example.playercomm.util.InputUtils;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Handles communication between two Player instances running in the same JVM process.
 *
 * Responsibilities:
 * - Creates and registers initiator and responder players using PlayerFactory
 * - Manages message exchange between the two players
 * - Supports both automatic and manual message sending modes for the initiator
 * - Implements stop condition: terminates after initiator has sent and received the defined number of messages
 * - Provides thread-safe counters for received messages
 */
public class SameProcessCommunicationHandler extends AbstractCommunicationHandler {

    private final PlayerMessageRouter broker;
    private final PlayerFactory factory;

    private Player initiator;
    private Player responder;

    private final AtomicInteger initiatorReceivedCount = new AtomicInteger(0);

    /**
     * Constructs the SameProcessCommunicationHandler with a scanner and default max messages.
     *
     * @param scanner Scanner instance for reading user input
     */
    public SameProcessCommunicationHandler(Scanner scanner) {
        super(scanner, 10); // maxMessages = 10
        this.broker = new PlayerMessageRouter();
        this.factory = new PlayerFactory(broker);
    }

    /**
     * Sets up the initiator and responder players and registers them with the message broker.
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
     * Starts the communication flow between initiator and responder.
     * Delegates the sending mode (automatic or manual) to the common method defined in the abstract base.
     */
    public void startCommunication() {
        try {
            sendMessagesWithUserChoice();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sends messages automatically from the initiator to the responder.
     * Messages are generated sequentially and a small delay is added for readability.
     */
    @Override
    protected void sendMessagesAutomatically() {
        for (int i = 1; i <= maxMessages; i++) {
            String msg = "Message " + i;
            initiator.sendMessage(responder.getName(), msg);
            try { Thread.sleep(100); } catch (InterruptedException ignored) {}
        }
    }

    /**
     * Sends messages manually, prompting the user to enter each message.
     * This method is used when the user selects manual mode.
     */
    @Override
    protected void sendMessagesManually() {
        for (int i = 1; i <= maxMessages; i++) {
            String msg = InputUtils.readLine(scanner, "Enter message " + i + ": ");
            initiator.sendMessage(responder.getName(), msg);
        }
    }
}