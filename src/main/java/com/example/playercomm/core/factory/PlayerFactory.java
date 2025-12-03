package com.example.playercomm.core.factory;

import com.example.playercomm.core.Player;
import com.example.playercomm.transport.PlayerMessageRouter;

/**
 * PlayerFactory is responsible for creating Player instances
 * and registering them with a provided PlayerMessageRouter.
 *
 * Responsibilities:
 * - Create new Player objects
 * - Ensure they are registered with the router immediately
 */
public class PlayerFactory {

    private final PlayerMessageRouter router;

    /**
     * Constructs a PlayerFactory with the given PlayerMessageRouter.
     *
     * @param router The PlayerMessageRouter to register players with
     */
    public PlayerFactory(PlayerMessageRouter router) {
        this.router = router;
    }

    /**
     * Creates a new Player with the specified name, registers it with the router,
     * and returns the instance.
     *
     * @param playerName Unique name for the player
     * @return Newly created Player instance
     */
    public Player createPlayer(String playerName) {
        Player player = new Player(playerName, router);
        router.registerPlayer(player);
        return player;
    }
}
