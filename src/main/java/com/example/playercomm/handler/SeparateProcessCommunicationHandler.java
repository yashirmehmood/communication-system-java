package com.example.playercomm.handler;

import com.example.playercomm.core.Player;
import com.example.playercomm.core.factory.PlayerFactory;
import com.example.playercomm.model.Message;
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
 * - Implements stop condition: initiator sends/receives maxMessages
 * - Ensures proper cleanup of sockets, streams, and players
 *
 * Notes:
 * - SeparateProcessCommunicationHandler can be either initiator or responder
 * - Uses simple socket programming (pure Java, no frameworks)
 */
public class SeparateProcessCommunicationHandler {

    private final String role;
    private final int myPort;
    private final int otherPort;
    private final int maxMessages;

    private ServerSocket serverSocket;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

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
     * Starts the separate-process communication.
     * Handles both initiator and responder roles.
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
        } finally {
            cleanup();
        }
    }

    /**
     * Sets up the Player instance for this process.
     */
    private void setupPlayer() {
        player = factory.createPlayer(role);
    }

    /**
     * Initiator role:
     * - Connects to responder
     * - Sends maxMessages
     * - Reads responses from responder
     */
    private void runInitiator() throws IOException {
        System.out.println("[Initiator] Connecting to responder at port " + otherPort + "...");
        socket = new Socket("localhost", otherPort);
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        for (int i = 1; i <= maxMessages; i++) {
            String message = "Message " + i;
            player.sendMessage("responder", message);
            out.println(message);

            String response = in.readLine();
            if (response != null) {
                messagesReceived.incrementAndGet();
                System.out.println("[Initiator] received: " + response);
            }
        }
        System.out.println("[Initiator] Communication complete.");
    }

    /**
     * Responder role:
     * - Listens on myPort for incoming connections
     * - Replies to each message with appended counter
     */
    private void runResponder() throws IOException {
        System.out.println("[Responder] Waiting for initiator on port " + myPort + "...");
        serverSocket = new ServerSocket(myPort);
        socket = serverSocket.accept();
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        int replyCounter = 0;
        String received;
        while ((received = in.readLine()) != null && messagesReceived.get() < maxMessages) {
            System.out.println("[Responder] received: " + received);
            replyCounter++;
            String reply = received + " [" + replyCounter + "]";
            player.sendMessage("initiator", reply);
            out.println(reply);
            messagesReceived.incrementAndGet();
        }

        System.out.println("[Responder] Communication complete.");
    }

    /**
     * Cleans up all resources including sockets, streams, and unregisters the player.
     */
    private void cleanup() {
        try {
            if (out != null) out.close();
            if (in != null) in.close();
            if (socket != null && !socket.isClosed()) socket.close();
            if (serverSocket != null && !serverSocket.isClosed()) serverSocket.close();
        } catch (IOException e) {
            System.err.println("Error during socket cleanup: " + e.getMessage());
        }

        if (player != null) {
            player.shutdown();
            player = null;
        }

        System.out.println("SeparateProcessCommunicationHandler: cleanup completed.");
    }
}