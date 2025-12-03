package com.example.playercomm.core.factory;

import com.example.playercomm.core.Player;
import com.example.playercomm.transport.PlayerMessageRouter;

/**
 * PlayerFactory is responsible for creating Player instances
 * and registering them with a provided MessageBroker.
 *
 * Responsibilities:
 * - Create new Player objects
 * - Ensure they are registered with the broker immediately
 */
public class PlayerFactory {

    private final PlayerMessageRouter broker;

    /**
     * Constructs a PlayerFactory with the given MessageBroker.
     *
     * @param broker The MessageBroker to register players with
     */
    public PlayerFactory(PlayerMessageRouter broker) {
        this.broker = broker;
    }

    /**
     * Creates a new Player with the specified name, registers it with the broker,
     * and returns the instance.
     *
     * @param playerName Unique name for the player
     * @return Newly created Player instance
     */
    public Player createPlayer(String playerName) {
        Player player = new Player(playerName, broker);
        broker.registerPlayer(player);
        return player;
    }
}
