package com.example.playercomm.handler;

import com.example.playercomm.core.Player;
import com.example.playercomm.core.factory.PlayerFactory;
import com.example.playercomm.transport.PlayerMessageRouter;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Handles communication between Player instances running in separate JVM processes.
 *
 * Responsibilities:
 * - Uses sockets to send and receive messages between processes
 * - Creates a Player object to manage sending/receiving
 * - Implements stop condition: initiator sends/receives 10 messages
 * - Ensures proper cleanup of sockets, streams, and players
 *
 * Notes:
 * - Can act as either initiator or responder
 * - Uses pure Java socket programming
 */
public class SeparateProcessCommunicationHandler {

    private final String role;
    private final int myPort;
    private final int otherPort;
    private final int maxMessages;

    private ServerSocket serverSocket;
    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;

    private Player player;
    private final AtomicInteger messagesReceived = new AtomicInteger(0);

    private final PlayerMessageRouter router;
    private final PlayerFactory factory;

    public SeparateProcessCommunicationHandler(String role, int myPort, int otherPort, int maxMessages) {
        this.role = role.toLowerCase();
        this.myPort = myPort;
        this.otherPort = otherPort;
        this.maxMessages = maxMessages;

        this.router = new PlayerMessageRouter();
        this.factory = new PlayerFactory(router);
    }

    /**
     * Starts the communication based on the role (initiator/responder)
     */
    public void startCommunication() {
        try {
            setupPlayer();

            if ("initiator".equals(role)) {
                runInitiator();
            } else if ("responder".equals(role)) {
                runResponder();
            } else {
                throw new IllegalArgumentException("Invalid role: " + role);
            }

        } catch (IOException e) {
            System.err.println("I/O error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Creates the Player instance for this process
     */
    private void setupPlayer() {
        player = factory.createPlayer(role);
    }

    /**
     * Initiator logic: connects to responder, sends 10 messages, and reads replies.
     * Retries connection if responder is not ready.
     */
    private void runInitiator() {
        int maxRetries = 8;
        int attempt = 0;

        while (attempt < maxRetries) {
            try {
                System.out.println("[Initiator] Connecting to responder at port " + otherPort
                        + " (Attempt " + (attempt + 1) + "/" + maxRetries + ")...");
                socket = new Socket("localhost", otherPort);
                break;
            } catch (IOException e) {
                attempt++;
                if (attempt >= maxRetries) {
                    System.err.println("[Initiator] Could not connect after " + maxRetries + " attempts.");
                    System.err.println("Please make sure the responder is running.");
                    cleanup();
                    return;
                }
                System.out.println("[Initiator] Responder not ready. Retrying in 2 second...");
                try { Thread.sleep(2000); } catch (InterruptedException ex) { Thread.currentThread().interrupt(); return; }
            }
        }

        System.out.println("[Initiator] Connected. Starting communication...");

        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            this.writer = new PrintWriter(writer, true);
            this.reader = reader;

            for (int i = 1; i <= maxMessages; i++) {
                String msg = "Message " + i;
                writer.write(msg);
                writer.newLine();
                writer.flush();

                String response = reader.readLine();
                System.out.println("[Initiator] Received: " + response);

                Thread.sleep(100);
            }

            System.out.println("[Initiator] Communication complete.");

        } catch (IOException | InterruptedException e) {
            System.err.println("I/O error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            cleanup();
        }
    }

    /**
     * Responder logic: listens on myPort, receives messages, replies with incremented counter.
     */
    private void runResponder() throws IOException {
        System.out.println("[Responder] Waiting for initiator on port " + myPort + "...");
        serverSocket = new ServerSocket(myPort);
        socket = serverSocket.accept();

        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        writer = new PrintWriter(socket.getOutputStream(), true);

        int replyCounter = 0;
        String received;
        while ((received = reader.readLine()) != null && messagesReceived.get() < maxMessages) {
            System.out.println("[Responder] Received: " + received);
            replyCounter++;
            String reply = received + " [" + replyCounter + "]";
            writer.println(reply);
            messagesReceived.incrementAndGet();
        }

        System.out.println("[Responder] Communication complete.");
        cleanup();
    }

    /**
     * Cleans up resources: sockets, streams, and unregisters the player.
     */
    private void cleanup() {
        System.out.println("[" + role + "] Cleaning up resources...");

        try {
            if (reader != null) reader.close();
        } catch (IOException e) { System.err.println("Error closing reader: " + e.getMessage()); }

        if (writer != null) writer.close();

        try {
            if (socket != null && !socket.isClosed()) socket.close();
            if (serverSocket != null && !serverSocket.isClosed()) serverSocket.close();
        } catch (IOException e) { System.err.println("Error closing socket: " + e.getMessage()); }

        if (router != null && player != null) {
            router.unregisterPlayer(player);
            System.out.println("[" + role + "] has been unregistered from the router.");
        }

        System.out.println("SeparateProcessCommunicationHandler: cleanup completed.");
    }
}