package com.example.playercomm.core;

import com.example.playercomm.model.Message;
import com.example.playercomm.transport.MessageBroker;

/**
 * Represents a Player in the communication system.
 *
 * Responsibilities:
 * - Holds the identity of the player
 * - Sends messages via the MessageBroker
 * - Receives messages from other players through the broker
 *
 * Notes:
 * - The Player is decoupled from other Player instances
 * - The class supports extension for custom message handling
 */
public class Player {

    private final String name;
    private final MessageBroker broker;

    public Player(String name, MessageBroker broker) {
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
}