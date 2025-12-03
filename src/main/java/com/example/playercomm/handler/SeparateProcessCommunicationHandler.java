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

        if ("initiator".equals(role)) {
            runInitiator();
        } else if ("responder".equals(role)) {
            runResponder();
        } else {
            System.err.println("Invalid role: " + role);
        }
    }

    /**
     * Initiator role: sends messages automatically or manually to the responder.
     */
    private void runInitiator() {
        boolean automatic = InputUtils.readYesNo(scanner, "Do you want to send messages automatically?");
        int maxRetries = 8;
        int attempt = 0;
        Socket socket = null;

        // Retry connection until responder is available
        while (attempt < maxRetries) {
            try {
                System.out.println("[Initiator] Connecting to responder at port " + otherPort + " (Attempt " + (attempt + 1) + "/" + maxRetries + ")...");
                socket = new Socket("localhost", otherPort);
                break;
            } catch (IOException e) {
                attempt++;
                System.out.println("[Initiator] Responder not ready yet. Retrying in 1 second...");
                try { Thread.sleep(1000); } catch (InterruptedException ex) { Thread.currentThread().interrupt(); return; }
            }
        }

        if (socket == null) {
            System.err.println("[Initiator] Could not connect. Please start the responder first.");
            return;
        }

        System.out.println("[Initiator] Connected. Starting communication...");
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            for (int i = 1; i <= maxMessages; i++) {
                String msg;
                if (automatic) {
                    msg = "Message " + i;
                    System.out.println("[Initiator] Sending: " + msg);
                } else {
                    msg = InputUtils.readLine(scanner, "Enter message " + i + ": ");
                }
                writer.write(msg);
                writer.newLine();
                writer.flush();

                String response = reader.readLine();
                System.out.println("[Initiator] Received: " + response);
            }
            System.out.println("[Initiator] Communication complete.");

        } catch (IOException e) {
            System.err.println("[Initiator] I/O error: " + e.getMessage());
        } finally {
            cleanup();
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