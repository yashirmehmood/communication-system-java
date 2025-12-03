package com.example.playercomm.handler;

import com.example.playercomm.core.Player;
import com.example.playercomm.core.factory.PlayerFactory;
import com.example.playercomm.handler.base.AbstractCommunicationHandler;
import com.example.playercomm.transport.PlayerMessageRouter;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Handles communication between Player instances running in separate JVM processes.
 *
 * Responsibilities:
 * - Supports two roles: initiator and responder
 * - Initiator can send messages automatically or manually
 * - Responder waits for initiator and replies with appended counters
 * - Uses TCP sockets for inter-process communication
 * - Manages proper registration and cleanup of Player instances
 * - Ensures flexible and extendable design for future communication modes
 */
public class SeparateProcessCommunicationHandler extends AbstractCommunicationHandler {

    private final String role;
    private final int myPort;
    private final int otherPort;

    private Player player;
    private final AtomicInteger messagesReceived = new AtomicInteger(0);

    private final PlayerMessageRouter broker;
    private final PlayerFactory factory;

    private BufferedWriter writer;
    private BufferedReader reader;

    /**
     * Constructs a SeparateProcessCommunicationHandler with the specified role and ports.
     *
     * @param scanner     Scanner instance for user input
     * @param role        Player role ("initiator" or "responder")
     * @param myPort      Local TCP port for this player
     * @param otherPort   TCP port of the other player (used by initiator)
     * @param maxMessages Maximum number of messages to send/receive
     */
    public SeparateProcessCommunicationHandler(Scanner scanner, String role, int myPort, int otherPort, int maxMessages) {
        super(scanner, maxMessages);
        this.role = role.toLowerCase();
        this.myPort = myPort;
        this.otherPort = otherPort;

        this.broker = new PlayerMessageRouter();
        this.factory = new PlayerFactory(broker);
    }

    /**
     * Starts communication based on the assigned role.
     * Delegates to role-specific methods for initiator or responder behavior.
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
     * Handles initiator role: connects to responder and sends messages (automatic/manual).
     */
    private void runInitiator() {
        Socket socket = connectToResponder();
        if (socket == null) return;

        try {
            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            sendMessagesWithUserChoice();
            System.out.println("[Initiator] Communication complete.");
        } catch (IOException e) {
            System.err.println("[Initiator] I/O error: " + e.getMessage());
        } finally {
            cleanup();
        }
    }

    /**
     * Attempts to establish a TCP connection to the responder with retries.
     *
     * @return Connected Socket instance or null if connection failed
     */
    private Socket connectToResponder() {
        int maxRetries = 8;
        int attempt = 0;
        Socket socket = null;

        while (attempt < maxRetries) {
            try {
                System.out.println("[Initiator] Connecting to responder at port " + otherPort +
                        " (Attempt " + (attempt + 1) + "/" + maxRetries + ")...");
                socket = new Socket("localhost", otherPort);
                break;
            } catch (IOException e) {
                attempt++;
                System.out.println("[Initiator] Responder not ready yet. Retrying in 1 second...");
                try { Thread.sleep(1000); } catch (InterruptedException ex) { Thread.currentThread().interrupt(); return null; }
            }
        }

        if (socket == null) {
            System.err.println("[Initiator] Could not connect. Please start the responder first.");
        }
        return socket;
    }

    /**
     * Handles responder role: waits for initiator connection and replies to messages.
     * Always waits passively and appends counters to each reply.
     */
    private void runResponder() {
        try (ServerSocket serverSocket = new ServerSocket(myPort)) {

            // Inform the user that responder is waiting
            System.out.println("[Responder] Waiting for initiator to connect on port " + myPort + "...");
            System.out.flush(); // Ensures the message is printed immediately

            // Blocking call to wait for initiator
            Socket socket = serverSocket.accept();
            System.out.println("[Responder] Initiator connected. Ready to receive messages.");

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                 BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))) {

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
            }

        } catch (IOException e) {
            System.err.println("[Responder] I/O error: " + e.getMessage());
        } finally {
            cleanup();
        }
    }

    /**
     * Sends messages automatically from initiator to responder.
     * Messages are generated sequentially and responses are printed.
     *
     * @throws IOException if an I/O error occurs during message exchange
     */
    @Override
    protected void sendMessagesAutomatically() throws IOException {
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
     * Sends messages manually from initiator to responder.
     * Prompts the user to enter each message and prints the corresponding response.
     *
     * @throws IOException if an I/O error occurs during message exchange
     */
    @Override
    protected void sendMessagesManually() throws IOException {
        for (int i = 1; i <= maxMessages; i++) {
            String msg = com.example.playercomm.util.InputUtils.readLine(scanner, "Enter message " + i + ": ");
            writer.write(msg);
            writer.newLine();
            writer.flush();

            String response = reader.readLine();
            System.out.println("[Initiator] Received: " + response);
        }
    }

    /**
     * Cleans up resources and unregisters the player from the broker.
     * Called after communication is complete or if an error occurs.
     */
    private void cleanup() {
        System.out.println("[" + role + "] Cleaning up resources...");
        broker.unregisterPlayer(player);
        System.out.println("[" + role + "] has been unregistered from the router.");
    }
}