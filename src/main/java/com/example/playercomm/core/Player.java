package com.example.playercomm.core;

import com.example.playercomm.transport.MessageBroker;

/**
 * Represents a player participating in the communication system.
 * A Player can send messages to other players through the MessageBroker
 * and can also receive messages passed by the broker.
 *
 * This class is intentionally lightweight:
 * - It contains only identity information and basic send/receive behavior.
 * - It does not know about other players directly (decoupled design).
 * - All routing is handled by the MessageBroker.
 */
public class Player {

    private final String name;
    private final MessageBroker messageBroker;

    /**
     * Creates a new Player instance.
     *
     * @param name  Unique name of the player.
     * @param messageBroker  The broker responsible for routing messages.
     */
    public Player(String name, MessageBroker messageBroker) {
        this.name = name;
        this.messageBroker = messageBroker;
    }

    /**
     * Sends a message to another player by delegating the request
     * to the MessageBroker.
     *
     * @param receiverName  The name of the player who should receive the message.
     * @param message       The content of the message.
     */
    public void sendMessage(String receiverName, String message) {
        messageBroker.publishMessage(this.name, receiverName, message);
    }

    /**
     * Callback method invoked by the MessageBroker when this player receives a message.
     *
     * @param senderName  Name of the sender.
     * @param message     Content of the incoming message.
     */
    public void receiveMessage(String senderName, String message) {
        System.out.println("[" + name + "] received from " + senderName + ": " + message);
    }

    /**
     * Returns the player's name.
     *
     * @return the name assigned to this player.
     */
    public String getName() {
        return name;
    }
}