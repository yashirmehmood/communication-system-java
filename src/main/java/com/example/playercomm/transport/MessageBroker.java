package com.example.playercomm.transport;

import com.example.playercomm.core.Player;
import com.example.playercomm.model.Message;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Minimal pub-sub message broker for Players.
 *
 * Responsibilities:
 * - Maintains a registry of all active players
 * - Routes Message objects from senders to intended receivers
 *
 * Notes:
 * - Thread-safe using ConcurrentHashMap
 * - Supports single-process communication
 * - Easily extendable for future features such as broadcasting or filtering
 */
public class MessageBroker {

    private final Map<String, Player> playerRegistry = new ConcurrentHashMap<>();

    /**
     * Registers a player to allow it to send and receive messages.
     *
     * @param player Player instance to register
     */
    public void registerPlayer(Player player) {
        if (player == null || player.getName() == null) {
            throw new IllegalArgumentException("Player and player name cannot be null");
        }
        playerRegistry.put(player.getName(), player);
    }

    /**
     * Removes a player from the broker registry.
     *
     * @param player Player instance to unregister
     */
    public void unregisterPlayer(Player player) {
        if (player != null && player.getName() != null) {
            playerRegistry.remove(player.getName());
        }
    }

    /**
     * Publishes a message from a sender to the intended receiver.
     *
     * @param message Message object containing sender, receiver, and content
     */
    public void publishMessage(Message message) {
        try {
            Player receiver = playerRegistry.get(message.getReceiver());
            if (receiver != null) {
                receiver.receiveMessage(message);
            } else {
                System.out.println("[" + message.getSender() + "] attempted to send message to unknown player: " + message.getReceiver());
            }
        } catch (Exception e) {
            System.err.println("Error delivering message from " + message.getSender() + " to " + message.getReceiver());
            e.printStackTrace();
        }
    }
}