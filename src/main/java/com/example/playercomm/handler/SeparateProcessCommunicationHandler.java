package com.example.playercomm.handler;

import com.example.playercomm.model.Message;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Handles communication between players running in separate JVM processes.
 *
 * Responsibilities:
 * - Connects to other players over TCP sockets
 * - Sends and receives Message objects
 * - Supports initiator and responder roles
 *
 * Notes:
 * - Generic and DRY: easily extensible to multiple players or roles
 */
public class SeparateProcessCommunicationHandler {

    private final String role;
    private final int myPort;
    private final int otherPort;
    private Socket socketToOther;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private final int maxMessages;

    public SeparateProcessCommunicationHandler(String role, int myPort, int otherPort, int maxMessages) {
        this.role = role.toLowerCase();
        this.myPort = myPort;
        this.otherPort = otherPort;
        this.maxMessages = maxMessages;
    }

    /**
     * Starts the communication process based on the role.
     */
    public void startCommunication() {
        try (ServerSocket serverSocket = new ServerSocket(myPort)) {

            startClientConnectionThread();

            // Accept connection from the other player
            Socket incomingSocket = serverSocket.accept();
            in = new ObjectInputStream(incomingSocket.getInputStream());
            out = new ObjectOutputStream(incomingSocket.getOutputStream());

            if (role.equals("initiator")) runInitiator();
            else if (role.equals("responder")) runResponder();
            else System.out.println("Invalid role. Must be 'initiator' or 'responder'.");

        } catch (IOException e) {
            System.err.println("[" + role + "] IO error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /** Starts a background thread to connect to the other player's socket */
    private void startClientConnectionThread() {
        Thread clientThread = new Thread(() -> {
            while (true) {
                try {
                    socketToOther = new Socket("localhost", otherPort);
                    break;
                } catch (IOException ignored) {
                    try { Thread.sleep(50); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
                }
            }
        });
        clientThread.setDaemon(true);
        clientThread.start();
    }

    /** Initiator sends messages and waits for replies */
    private void runInitiator() throws IOException {
        for (int i = 1; i <= maxMessages; i++) {
            Message msg = new Message("Initiator", "Responder", "Message " + i);
            sendMessage(msg);
            Message reply = receiveMessage();
            System.out.println("[Initiator] Received: " + reply);
        }
        System.out.println("[Initiator] Communication complete.");
    }

    /** Responder receives messages and sends replies */
    private void runResponder() throws IOException {
        int replyCounter = 0;
        while (replyCounter < maxMessages) {
            Message received = receiveMessage();
            System.out.println("[Responder] Received: " + received);
            replyCounter++;
            Message reply = new Message("Responder", "Initiator", received.getContent() + " [" + replyCounter + "]");
            sendMessage(reply);
        }
        System.out.println("[Responder] Communication complete.");
    }

    /** Sends a Message object to the other player */
    private void sendMessage(Message msg) throws IOException {
        waitForConnection();
        if (out == null) out = new ObjectOutputStream(socketToOther.getOutputStream());
        out.writeObject(msg);
        out.flush();
    }

    /** Receives a Message object from the other player */
    private Message receiveMessage() {
        try { return (Message) in.readObject(); }
        catch (IOException | ClassNotFoundException e) { throw new RuntimeException(e); }
    }

    /** Waits until socket connection is established */
    private void waitForConnection() {
        while (socketToOther == null || socketToOther.isClosed()) {
            try { Thread.sleep(50); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        }
    }
}
