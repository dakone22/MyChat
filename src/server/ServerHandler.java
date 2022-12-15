package server;

import core.network.listeners.ServerPacketListener;
import core.network.packets.Packet;
import core.network.packets.c2s.chat.ChatMessageC2SPacket;
import core.network.packets.s2c.chat.*;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * This class provides a server for a chat application.
 * It listens for incoming client connections, maintains a list of connected clients,
 * and allows clients to send public and private messages.
 *
 * <p>
 *     The server listens for incoming client connections using a {@link ServerSocket}.
 *     Each time a new client connects, a new {@link ClientConnectionHandler} is created
 *     to handle the client's messages and events.
 *     The list of clients is maintained using an {@link ArrayList}.
 * </p>
 *
 * <p>
 *     The server listens for incoming messages from clients using a {@link ServerPacketListener}.
 *     When a client sends a public or private message, the server passes the message to the
 *     {@link ServerHandlerListener} so that it can be broadcasted to all clients or just the intended recipient.
 * </p>
 *
 * <p>
 *     The server can also send messages to clients.
 *     This is done by creating a {@link MessageS2CPacket} and sending it to the desired client(s)
 *     using the {@link ClientConnectionHandler#sendPacket(Packet)} method.
 * </p>
 *
 * @see ServerHandlerListener
 * @see ClientConnectionHandler
 * @see ServerPacketListener
 * @see ServerSocket
 * @see ArrayList
 * @see MessageS2CPacket
 * @see ClientConnectionHandler#sendPacket(Packet)
 */
public class ServerHandler {
    // ServerSocket to listen for incoming client connections
    private ServerSocket serverSocket;

    // Listener to handle events and messages from the server
    private final ServerHandlerListener listener;

    // List of connected clients
    private final List<ClientConnectionHandler> clients = new ArrayList<>();

    // Incrementing message ID
    private int messageId = 0;

    /**
     * Constructs a new ServerHandler with the given listener.
     *
     * @param listener the listener to handle events and messages from the server
     */
    public ServerHandler(ServerHandlerListener listener) {
        this.listener = listener;
    }

    /**
     * Accepts a new client connection and creates a new ClientConnectionHandler
     * to handle the client's messages and events.
     *
     * @param clientSocket the socket representing the client's connection
     * @throws IOException if an I/O error occurs when creating the ClientConnectionHandler
     */
    private void acceptNewClient(Socket clientSocket) throws IOException {
        // Create a new ClientConnectionHandler for the client
        var client = new ClientConnectionHandler(clientSocket);

        // Register an ExceptionOccurredListener to handle any exceptions that occur
        client.addExceptionOccurredListener((sender, exception) -> {
            // Remove the client from the list of clients
            clients.remove(client);
            // Notify the listener that the client has disconnected
            listener.onClientDisconnect(client);

            // Notify the listener of the exception that occurred
            listener.onExceptionOccurred(sender, exception);
        });

        // Start the client connection handler and register a ServerPacketListener to handle incoming messages
        client.start(new ServerPacketListener() {
            @Override
            public void onPublicMessage(ChatMessageC2SPacket.Public packet) {
                // Notify the listener of the incoming public message
                listener.onPublicMessage(client, packet.getMessage());
            }

            @Override
            public void onPrivateMessage(ChatMessageC2SPacket.Private packet) {
                // Notify the listener of the incoming private message
                listener.onPrivateMessage(client, packet.getReceiver(), packet.getMessage());
            }
        });

        // Add the client to the list of clients
        clients.add(client);

        // Notify the listener that the client has joined
        listener.onClientJoin(client);
    }

    /**
     * Sends the given message packet to the specified client.
     *
     * @param client the client to send the message to
     * @param messagePacket the message packet to send
     */
    private void send(ClientConnectionHandler client, MessageS2CPacket messagePacket) {
        try {
            // Send the message packet to the client
            client.sendPacket(messagePacket);
        } catch (InterruptedException e) {
            // Notify the listener of the exception that occurred
            listener.onExceptionOccurred(client, e);
        }
    }

    /**
     * Sends the given message packet to the specified list of clients.
     *
     * @param clients the list of clients to send the message to
     * @param messagePacket the message packet to send
     */
    private void send(List<ClientConnectionHandler> clients, MessageS2CPacket messagePacket) {
        // Iterate over the list of clients and send the message packet to each one
        for (var client : clients) {
            send(client, messagePacket);
        }
    }

    /**
     * Sends the given message packet to all connected clients.
     *
     * @param messagePacket the message packet to send
     */
    private void sendAll(MessageS2CPacket messagePacket) {
        // Send the message packet to all clients in the list
        send(this.clients, messagePacket);
    }

    /**
     * Starts the server and begins listening for incoming client connections on the specified port.
     *
     * @param port the port to listen on
     * @throws IOException if an I/O error occurs when creating the ServerSocket
     */
    public void start(int port) throws IOException {
        // Create a new ServerSocket to listen for incoming connections on the specified port
        serverSocket = new ServerSocket(port);

        // Create a new thread to handle incoming connections
        var acceptionThread = new Thread(() -> {
            try {
                // Keep listening for incoming connections until the ServerSocket is closed
                while (!serverSocket.isClosed()) {
                    var clientSocket = serverSocket.accept();
                    acceptNewClient(clientSocket);
                }
            } catch (IOException e) {
                // Throw a runtime exception to terminate the thread
                throw new RuntimeException(e);
            }
        });
        // Register an uncaught exception handler to handle any exceptions thrown by the acception thread
        acceptionThread.setUncaughtExceptionHandler(listener::onExceptionOccurred);

        // Start the acception thread
        acceptionThread.start();

        // Notify the listener that the server has started
        listener.onServerStart();
    }

    /**
     * Stops the server and closes the ServerSocket.
     * Any connected clients will be disconnected and their connections will be closed.
     *
     * @throws IOException if an I/O error occurs when closing the ServerSocket
     */
    public void stop() throws IOException {
        // If the server has not been started or has already been stopped, do nothing
        if (serverSocket == null || serverSocket.isClosed()) return;

        // Close the ServerSocket
        serverSocket.close();

        // Stop all connected clients and clear the list of clients
        for (var c : clients) {
            c.stop();
        }
        clients.clear();
    }

    /**
     * Sends a public message from the specified user to all connected clients.
     *
     * @param fromUser the user sending the message
     * @param message the message to send
     */
    public void sendPublicMessage(ClientConnectionHandler fromUser, String message) {
        // Create a new public message packet
        var messagePacket = new ChatMessageS2CPacket.Public(messageId++, LocalDateTime.now(), fromUser.toString(), message);

        // Iterate over the list of clients and send the message packet to each one
        for (var client : clients) {
            send(client, messagePacket);
        }
    }

    /**
     * Sends a private message from the specified sender to the specified recipient.
     *
     * @param fromUser the sender of the message
     * @param toUser the recipient of the message
     * @param message the message to send
     */
    public void sendPrivateMessage(ClientConnectionHandler fromUser, ClientConnectionHandler toUser, String message) {
        // Create a new private message packet
        var messagePacket = new ChatMessageS2CPacket.Private(messageId++, LocalDateTime.now(), fromUser.toString(), toUser.toString(), message);

        // Send the message packet to the recipient
        send(toUser, messagePacket);
    }

    /**
     * Sends a message to all connected clients indicating that the specified user has joined the chat.
     *
     * @param user the user who has joined the chat
     */
    public void sendUserJoin(ClientConnectionHandler user) {
        // Create a new user join packet
        var messagePacket = new ClientJoinS2CPacket(messageId++, LocalDateTime.now(), user.toString());

        // Send the message packet to all connected clients
        sendAll(messagePacket);
    }

    /**
     * Sends a message to all connected clients indicating that the specified user has left the chat.
     *
     * @param user the user who has left the chat
     * @param reason the reason for the user's disconnection
     */
    public void sendUserLeave(ClientConnectionHandler user, ClientLeaveS2CPacket.DisconnectReason reason) {
        // Create a new user leave packet
        var messagePacket = new ClientLeaveS2CPacket(messageId++, LocalDateTime.now(), user.toString(), reason);
        // Send the message packet to all connected clients
        sendAll(messagePacket);
    }

    /**
     * @deprecated This method is deprecated and should no longer be used.
     * Use {@link #sendPublicMessage(ClientConnectionHandler, String)} instead.
     * Sends a custom system message to all connected clients.
     *
     * @param message the custom system message to send
     */
    @Deprecated
    public void sendCustomMessage(String message) {
        var messagePacket = new CustomSystemMessageS2CPacket(messageId++, LocalDateTime.now(), message);
        sendAll(messagePacket);
    }
}
