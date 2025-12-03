package com.example.playercomm.model;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Represents a message exchanged between players.
 * Responsibilities:
 * - Hold sender and receiver information
 * - Hold message content
 * - Optionally maintain messageId and timestamp
 */
public class Message {

    private static final AtomicInteger COUNTER = new AtomicInteger(0);

    private final int messageId;
    private final String sender;
    private final String receiver;
    private final String content;
    private final LocalDateTime timestamp;

    public Message(String sender, String receiver, String content) {
        this.messageId = COUNTER.incrementAndGet();
        this.sender = sender;
        this.receiver = receiver;
        this.content = content;
        this.timestamp = LocalDateTime.now();
    }

    public int getMessageId() {
        return messageId;
    }

    public String getSender() {
        return sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public String getContent() {
        return content;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return "[" + timestamp + "] " + sender + " -> " + receiver + ": " + content;
    }
}