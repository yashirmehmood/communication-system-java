package com.example.playercomm.transport;

import com.example.playercomm.core.Player;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * MessageBroker implements a minimal pub-sub system for players.
 * Responsibilities:
 * - Maintain a registry of all active players.
 * - Route messages from senders to receivers.
 * - Decouple players from direct references to each other.
 *
 * Notes:
 * - Thread-safe using ConcurrentHashMap.
 * - Supports single-process communication only (in-memory).
 * - Future extension: can add channels/topics, message filtering, or async delivery.
 */
public class MessageBroker {

    // Registry of players keyed by their names
    private final Map<String, Player> playerRegistry = new ConcurrentHashMap<>();

    /**
     * Registers a player to the broker so it can send/receive messages.
     *
     * @param player The player to register
     */
    public void registerPlayer(Player player) {
        if (player == null || player.getName() == null) {
            throw new IllegalArgumentException("Player and player name cannot be null");
        }
        playerRegistry.put(player.getName(), player);
    }

    /**
     * Unregisters a player from the broker.
     *
     * @param player The player to remove
     */
    public void unregisterPlayer(Player player) {
        if (player != null && player.getName() != null) {
            playerRegistry.remove(player.getName());
        }
    }

    /**
     * Publishes a message from a sender to a receiver.
     * If the receiver is registered, its receiveMessage() method is invoked.
     *
     * @param senderName   Name of the player sending the message
     * @param receiverName Name of the intended receiver
     * @param message      Message content
     */
    public void publishMessage(String senderName, String receiverName, String message) {
        Player receiver = playerRegistry.get(receiverName);
        if (receiver != null) {
            receiver.receiveMessage(senderName, message);
        } else {
            System.out.println("[" + senderName + "] attempted to send message to unknown player: " + receiverName);
        }
    }
}
