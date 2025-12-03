package com.example.playercomm.core.factory;

import com.example.playercomm.core.Player;
import com.example.playercomm.transport.PlayerMessageRouter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlayerFactoryTest {

    private PlayerMessageRouter router;
    private PlayerFactory factory;

    @BeforeEach
    void setUp() {
        router = new PlayerMessageRouter();
        factory = new PlayerFactory(router);
    }

    @Test
    void testCreatePlayer() {
        Player player = factory.createPlayer("Alice");
        assertNotNull(player);
        assertEquals("Alice", player.getName());
    }
}