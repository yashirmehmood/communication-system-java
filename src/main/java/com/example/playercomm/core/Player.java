package com.example.playercomm.core;

import com.example.playercomm.model.Message;
import com.example.playercomm.transport.PlayerMessageRouter;

/**
 * Represents a Player in the communication system.
 *
 * Responsibilities:
 * - Holds the identity of the player
 * - Sends messages via the MessageBroker
 * - Receives messages from other players through the broker
 * - Can unregister itself from the broker when shutting down
 */
public class Player {

    private final String name;
    private final PlayerMessageRouter broker;

    public Player(String name, PlayerMessageRouter broker) {
        this.name = name;
        this.broker = broker;
    }

    /**
     * Sends a Message object to another player through the broker.
     *
     * @param receiverName name of the receiver
     * @param content      message content
     */
    public void sendMessage(String receiverName, String content) {
        Message message = new Message(name, receiverName, content);
        broker.publishMessage(message);
    }

    /**
     * Callback invoked by the broker when a message is received.
     *
     * @param message Message object
     */
    public void receiveMessage(Message message) {
        System.out.println("[" + name + "] received: " + message);
    }

    public String getName() {
        return name;
    }

    /**
     * Unregisters this player from the broker.
     * Should be called when the player is no longer needed.
     */
    public void shutdown() {
        broker.unregisterPlayer(this);
        System.out.println("[" + name + "] has been unregistered from the broker.");
    }
}