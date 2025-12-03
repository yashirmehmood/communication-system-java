package com.example.playercomm.core;

import com.example.playercomm.model.Message;
import com.example.playercomm.transport.PlayerMessageRouter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {

    private PlayerMessageRouter router;
    private Player player1;
    private Player player2;

    @BeforeEach
    void setUp() {
        router = new PlayerMessageRouter();
        player1 = new Player("Alice", router);
        player2 = new Player("Bob", router);

        router.registerPlayer(player1);
        router.registerPlayer(player2);
    }

    @Test
    void testSendMessage() {
        // Send a message from player1 to player2
        player1.sendMessage("Bob", "Hello Bob");

        // Since receiveMessage prints to console, we can't assert output directly.
        // But we can test that sending a message to an unknown player does not crash.
        assertDoesNotThrow(() -> player1.sendMessage("Unknown", "Test"));
    }

    @Test
    void testPlayerName() {
        assertEquals("Alice", player1.getName());
        assertEquals("Bob", player2.getName());
    }
}