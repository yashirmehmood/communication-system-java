package com.example.playercomm.transport;

import com.example.playercomm.core.Player;
import com.example.playercomm.model.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlayerMessageRouterTest {

    private PlayerMessageRouter router;
    private Player sender;
    private Player receiver;

    @BeforeEach
    void setUp() {
        router = new PlayerMessageRouter();
        sender = new Player("Sender", router);
        receiver = new Player("Receiver", router);
        router.registerPlayer(sender);
        router.registerPlayer(receiver);
    }

    @Test
    void testRegisterAndUnregisterPlayer() {
        Player newPlayer = new Player("Test", router);
        router.registerPlayer(newPlayer);
        router.unregisterPlayer(newPlayer);

        // Unregistering again should not throw error
        assertDoesNotThrow(() -> router.unregisterPlayer(newPlayer));
    }

    @Test
    void testPublishMessage() {
        Message message = new Message("Sender", "Receiver", "Test Message");
        assertDoesNotThrow(() -> router.publishMessage(message));

        // Publishing to unknown receiver
        Message badMessage = new Message("Sender", "Unknown", "Test");
        assertDoesNotThrow(() -> router.publishMessage(badMessage));
    }
}