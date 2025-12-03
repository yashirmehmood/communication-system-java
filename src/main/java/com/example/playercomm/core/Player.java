package com.example.playercomm.core;

import com.example.playercomm.model.Message;
import com.example.playercomm.transport.PlayerMessageRouter;

/**
 * Represents a Player in the communication system.
 *
 * Responsibilities:
 * - Holds the identity of the player
 * - Sends messages via the PlayerMessageRouter
 * - Receives messages from other players through the Router
 * - Can unregister itself from the Router when shutting down
 */
public class Player {

    private final String name;
    private final PlayerMessageRouter router;

    public Player(String name, PlayerMessageRouter router) {
        this.name = name;
        this.router = router;
    }

    /**
     * Sends a Message object to another player through the router.
     *
     * @param receiverName name of the receiver
     * @param content      message content
     */
    public void sendMessage(String receiverName, String content) {
        Message message = new Message(name, receiverName, content);
        router.publishMessage(message);
    }

    /**
     * Callback invoked by the router when a message is received.
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
     * Unregisters this player from the router.
     * Should be called when the player is no longer needed.
     */
    public void shutdown() {
        router.unregisterPlayer(this);
        System.out.println("[" + name + "] has been unregistered from the router.");
    }
}