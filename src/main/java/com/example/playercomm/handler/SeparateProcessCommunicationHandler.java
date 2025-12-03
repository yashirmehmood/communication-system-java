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
 * - Uses sockets to send/receive messages between processes
 * - Supports automatic or manual message sending for initiator
 * - Implements stop condition: initiator sends/receives maxMessages
 * - Ensures proper cleanup of sockets, streams, and player registration
 *
 * Notes:
 * - Can act as initiator or responder
 * - Pure Java implementation (no frameworks)
 */
public class SeparateProcessCommunicationHandler {

    private final String role;
    private final int myPort;
    private final int otherPort;
    private final int maxMessages;
    private final Scanner scanner;

    private Player player;
    private final PlayerMessageRouter router;
    private final PlayerFactory factory;

    private Socket socket;
    private ServerSocket serverSocket;
    private BufferedReader in;
    private PrintWriter out;

    private final AtomicInteger messagesReceived = new AtomicInteger(0);
    private boolean manualMode = false;

    public SeparateProcessCommunicationHandler(String role, int myPort, int otherPort, int maxMessages, Scanner scanner) {
        this.role = role.toLowerCase();
        this.myPort = myPort;
        this.otherPort = otherPort;
        this.maxMessages = maxMessages;
        this.scanner = scanner;

        this.router = new PlayerMessageRouter();
        this.factory = new PlayerFactory(router);
    }

    public void startCommunication() {
        try {
            setupPlayer();

            if ("initiator".equals(role)) {
                manualMode = InputUtils.readYesNo(scanner, "Do you want to send messages manually?");
                runInitiator();
            } else if ("responder".equals(role)) {
                runResponder();
            } else {
                throw new IllegalArgumentException("Invalid role: " + role);
            }
        } catch (IOException e) {
            System.err.println("I/O error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            cleanup();
        }
    }

    private void setupPlayer() {
        player = factory.createPlayer(role);
    }

    private void runInitiator() {
        int maxRetries = 3;
        int attempt = 0;

        while (attempt < maxRetries) {
            try {
                System.out.println("[Initiator] Attempting to connect to responder at port " + otherPort + " (Attempt " + (attempt + 1) + "/" + maxRetries + ")...");
                socket = new Socket("localhost", otherPort);
                break;
            } catch (IOException e) {
                attempt++;
                if (attempt >= maxRetries) {
                    System.err.println("[Initiator] Could not connect after " + maxRetries + " attempts. Please ensure responder is running.");
                    return;
                }
                System.out.println("[Initiator] Responder not ready yet. Retrying in 1 second...");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        }

        System.out.println("[Initiator] Connected to responder. Starting communication...");

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter writer = new PrintWriter(socket.getOutputStream(), true)) {

            for (int i = 1; i <= maxMessages; i++) {
                String msg;
                if (manualMode) {
                    System.out.print("Enter message " + i + ": ");
                    msg = scanner.nextLine();
                } else {
                    msg = "Message " + i;
                    Thread.sleep(100); // small delay for readability
                }

                writer.println(msg);

                String response = reader.readLine();
                if (response != null) {
                    System.out.println("[Initiator] Received: " + response);
                    messagesReceived.incrementAndGet();
                }
            }

            System.out.println("[Initiator] Communication complete.");

        } catch (IOException | InterruptedException e) {
            System.err.println("[Initiator] I/O error: " + e.getMessage());
        }
    }

    private void runResponder() throws IOException {
        System.out.println("[Responder] Waiting for initiator on port " + myPort + "...");
        serverSocket = new ServerSocket(myPort);
        socket = serverSocket.accept();
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        int replyCounter = 0;
        String received;
        while ((received = in.readLine()) != null && messagesReceived.get() < maxMessages) {
            System.out.println("[Responder] Received: " + received);
            replyCounter++;
            String reply = received + " [" + replyCounter + "]";
            player.sendMessage("initiator", reply);
            out.println(reply);
            messagesReceived.incrementAndGet();
        }

        System.out.println("[Responder] Communication complete.");
    }

    private void cleanup() {
        try {
            if (in != null) in.close();
        } catch (IOException ignored) {}
        if (out != null) out.close();
        try {
            if (socket != null && !socket.isClosed()) socket.close();
        } catch (IOException ignored) {}
        try {
            if (serverSocket != null && !serverSocket.isClosed()) serverSocket.close();
        } catch (IOException ignored) {}

        if (player != null && router != null) {
            router.unregisterPlayer(player);
            System.out.println("[" + role + "] has been unregistered from the router.");
        }

        System.out.println("SeparateProcessCommunicationHandler: cleanup completed.");
    }
}