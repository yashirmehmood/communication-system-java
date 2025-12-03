package com.example.playercomm.handler;

import com.example.playercomm.core.Player;
import com.example.playercomm.core.factory.PlayerFactory;
import com.example.playercomm.transport.PlayerMessageRouter;
import com.example.playercomm.util.InputUtils;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Handles communication between Player instances running in separate JVM processes.
 *
 * Responsibilities:
 * - Initiator can send messages automatically or manually
 * - Responder always replies with appended counter
 * - Uses sockets for inter-process communication
 * - Ensures proper cleanup of sockets and Player registration
 *
 * Notes:
 * - Designed to be flexible for future communication modes
 * - Follows DRY principle for input handling and cleanup
 */
public class SeparateProcessCommunicationHandler {

    private final Scanner scanner;
    private final String role;
    private final int myPort;
    private final int otherPort;
    private final int maxMessages;

    private Player player;
    private final AtomicInteger messagesReceived = new AtomicInteger(0);

    private final PlayerMessageRouter broker;
    private final PlayerFactory factory;

    public SeparateProcessCommunicationHandler(Scanner scanner, String role, int myPort, int otherPort, int maxMessages) {
        this.scanner = scanner;
        this.role = role.toLowerCase();
        this.myPort = myPort;
        this.otherPort = otherPort;
        this.maxMessages = maxMessages;

        this.broker = new PlayerMessageRouter();
        this.factory = new PlayerFactory(broker);
    }

    /**
     * Starts the communication depending on the role.
     */
    public void startCommunication() {
        player = factory.createPlayer(role);

        switch (role) {
            case "initiator" -> runInitiator();
            case "responder" -> runResponder();
            default -> System.err.println("Invalid role: " + role);
        }
    }

    /**
     * Initiator role: sends messages automatically or manually to the responder.
     */
    private void runInitiator() {
        boolean automatic = InputUtils.readYesNo(scanner, "Do you want to send messages automatically?");
        Socket socket = connectToResponder();

        if (socket == null) return;

        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            if (automatic) {
                sendMessagesAutomatically(writer, reader);
            } else {
                sendMessagesManually(writer, reader);
            }

            System.out.println("[Initiator] Communication complete.");

        } catch (IOException e) {
            System.err.println("[Initiator] I/O error: " + e.getMessage());
        } finally {
            cleanup();
        }
    }

    /**
     * Connects to the responder socket with retries.
     */
    private Socket connectToResponder() {
        int maxRetries = 8;
        int attempt = 0;
        Socket socket = null;

        while (attempt < maxRetries) {
            try {
                System.out.println("[Initiator] Connecting to responder at port " + otherPort + " (Attempt " + (attempt + 1) + "/" + maxRetries + ")...");
                socket = new Socket("localhost", otherPort);
                break;
            } catch (IOException e) {
                attempt++;
                System.out.println("[Initiator] Responder not ready yet. Retrying in 2 second...");
                try { Thread.sleep(2000); } catch (InterruptedException ex) { Thread.currentThread().interrupt(); return null; }
            }
        }

        if (socket == null) {
            System.err.println("[Initiator] Could not connect. Please start the responder first.");
        }
        return socket;
    }

    /**
     * Sends messages automatically in sequence (Message 1..10).
     */
    private void sendMessagesAutomatically(BufferedWriter writer, BufferedReader reader) throws IOException {
        for (int i = 1; i <= maxMessages; i++) {
            String msg = "Message " + i;
            System.out.println("[Initiator] Sending: " + msg);
            writer.write(msg);
            writer.newLine();
            writer.flush();

            String response = reader.readLine();
            System.out.println("[Initiator] Received: " + response);
        }
    }

    /**
     * Sends messages manually by reading user input.
     */
    private void sendMessagesManually(BufferedWriter writer, BufferedReader reader) throws IOException {
        for (int i = 1; i <= maxMessages; i++) {
            String msg = InputUtils.readLine(scanner, "Enter message " + i + ": ");
            writer.write(msg);
            writer.newLine();
            writer.flush();

            String response = reader.readLine();
            System.out.println("[Initiator] Received: " + response);
        }
    }

    /**
     * Responder role: waits for connection and replies to each message with appended counter.
     */
    private void runResponder() {
        try (ServerSocket serverSocket = new ServerSocket(myPort);
             Socket socket = serverSocket.accept();
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))) {

            System.out.println("[Responder] Connected. Waiting for messages...");
            int replyCounter = 0;
            String received;
            while ((received = reader.readLine()) != null && messagesReceived.get() < maxMessages) {
                replyCounter++;
                String reply = received + " [" + replyCounter + "]";
                System.out.println("[Responder] received: " + received);
                writer.write(reply);
                writer.newLine();
                writer.flush();
                messagesReceived.incrementAndGet();
            }
            System.out.println("[Responder] Communication complete.");
        } catch (IOException e) {
            System.err.println("[Responder] I/O error: " + e.getMessage());
        } finally {
            cleanup();
        }
    }

    /**
     * Cleans up resources and unregisters player.
     */
    private void cleanup() {
        System.out.println("[" + role + "] Cleaning up resources...");
        broker.unregisterPlayer(player);
        System.out.println("[" + role + "] has been unregistered from the router.");
    }
}