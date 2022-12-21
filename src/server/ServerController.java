package server;

import core.PasswordHasher;
import core.User;
import core.network.packets.s2c.chat.*;
import core.network.packets.s2c.service.ConnectedFailureS2CPacket;
import core.network.packets.s2c.service.ConnectedSuccessS2CPacket;
import core.network.packets.s2c.service.DisconnectedS2CPacket;
import core.network.packets.s2c.service.LoginRequestS2CPacket;

import java.security.spec.InvalidKeySpecException;
import java.util.*;

public class ServerController {
    private static final int AUTHORIZATION_TIMEOUT_SECONDS = 10;
    private final ServerUIUpdater output;
    private final ServerNetwork serverNetwork;

    private final Map<ClientConnectionHandler, User> users;
    private final List<ClientConnectionHandler> unauthorizedClients;
    private String serverPassword = "";
    private final Timer timer;

    public ServerController(ServerUIUpdater serverUIUpdater) {
        users = new HashMap<>();
        unauthorizedClients = new ArrayList<>();
        output = serverUIUpdater;
        serverNetwork = new ServerNetwork(getHandlerListener());
        timer = new Timer();
    }

    private ServerNetworkListener getHandlerListener() {
        return new ServerNetworkListener() {
            @Override
            public void onServerStart() {
                output.onServerStart();
            }

            @Override
            public void onServerStop() {
                output.onServerStop();
            }

            @Override
            public void onPublicMessage(ClientConnectionHandler senderClient, String message) {
                User senderUser;
                synchronized (users) { senderUser = getUser(senderClient); }
                if (senderUser == null) {
                    (new RuntimeException("Got public message %s from unknown client %s".formatted(message, senderClient.toString()))).printStackTrace();
                }

                serverNetwork.sendAll(new PublicChatMessageS2CPacket(senderUser, message));
                output.onPublicMessage(senderUser, message);
            }

            @Override
            public void onPrivateMessage(ClientConnectionHandler senderClient, User receiverUser, String message) {
                User senderUser;
                ClientConnectionHandler receiverClient;
                synchronized (users) {
                    senderUser = getUser(senderClient);
                    if (senderUser == null) {
                        (new RuntimeException("Got private message %s from unknown client %s to %s".formatted(message, senderClient.toString(), receiverUser.toString()))).printStackTrace();
                    }

                    receiverClient = getClient(receiverUser);
                    if (receiverClient == null) {
                        (new RuntimeException("Got private message %s from %s to unknown client %s".formatted(message, senderClient.toString(), receiverUser.toString()))).printStackTrace();
                    }
                }

                if (receiverClient == null) {
                    serverNetwork.send(senderClient, new SystemMessageS2CPacket(SystemMessageS2CPacket.MessageType.ErrorNoSuchUser));
                    return;
                }

                serverNetwork.send(receiverClient, new PrivateChatMessageS2CPacket(senderUser, receiverUser, message));
                serverNetwork.send(senderClient, new PrivateChatMessageS2CPacket(senderUser, receiverUser, message));
                output.onPrivateMessage(senderUser, receiverUser, message);
            }

            @Override
            public void onClientExceptionDisconnected(ClientConnectionHandler client, Throwable exception) {
                User user;
                synchronized (users) {
                    user = getUser(client);
                    if (user == null)
                        (new RuntimeException("Got exception disconnect from unknown client %s: %s".formatted(client.toString(), exception.toString()))).printStackTrace();
                    removeUser(user);
                }

                serverNetwork.sendAndDisconnect(client, new DisconnectedS2CPacket(DisconnectedS2CPacket.DisconnectReason.ErrorOccurred));
                serverNetwork.sendAll(new ClientLeaveS2CPacket(user, ClientLeaveS2CPacket.LeaveReason.ErrorOccurred));
                output.onClientExceptionDisconnected(user, exception);
                output.updateUserList(users.values());
            }

            @Override
            public void onDisconnect(ClientConnectionHandler client) {
                User user;
                synchronized (users) {
                    user = getUser(client);
                    if (user == null) {
                        (new RuntimeException("Got disconnect request from unknown client %s".formatted(client.toString()))).printStackTrace();
                    }

                    removeUser(user);
                }
                serverNetwork.forceDisconnect(client);
                serverNetwork.sendAll(new ClientLeaveS2CPacket(user, ClientLeaveS2CPacket.LeaveReason.SelfDisconnect));
                output.onClientLeft(user);
                output.updateUserList(getUsers());
            }

            @Override
            public void onUserListRequest(ClientConnectionHandler client) {
                serverNetwork.send(client, new ChatUserListS2CPacket(List.copyOf(users.values())));
            }

            @Override
            public void onNewClient(ClientConnectionHandler client) {
                synchronized (unauthorizedClients) {
                    unauthorizedClients.add(client);
                }

                serverNetwork.send(client, new LoginRequestS2CPacket());

                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        synchronized (unauthorizedClients) {
                            if (unauthorizedClients.contains(client)) {
                                unauthorizedClients.remove(client);
                                serverNetwork.sendAndDisconnect(client, new ConnectedFailureS2CPacket(ConnectedFailureS2CPacket.FailReason.InvalidPassword));
                                output.onAuthorizationTimeout(client);
                            }
                        }

                    }
                }, AUTHORIZATION_TIMEOUT_SECONDS * 1000);
            }

            @Override
            public void onLogin(ClientConnectionHandler client, User requestedUserData, String hashedPassword) {
                synchronized (unauthorizedClients) {
                    unauthorizedClients.remove(client);
                }

                try {
                    if (!PasswordHasher.validatePassword(serverPassword, hashedPassword)) {
                        serverNetwork.sendAndDisconnect(client, new ConnectedFailureS2CPacket(ConnectedFailureS2CPacket.FailReason.InvalidPassword));
                        output.onAuthorizationFailed(client);
                        return;
                    }
                } catch (InvalidKeySpecException e) {
                    exceptionOccurred(client, e);
                }

                User newUser;
                synchronized (users) {
                    for (var user : users.values())
                        if (Objects.equals(user.username(), requestedUserData.username())) {
                            serverNetwork.sendAndDisconnect(client, new ConnectedFailureS2CPacket(ConnectedFailureS2CPacket.FailReason.UsernameAlreadyTaken));
                            output.onAuthorizationFailed(client);
                            return;
                        }

                    newUser = new User(UUID.randomUUID(), requestedUserData.username());

                    users.put(client, newUser);
                }

                serverNetwork.send(client, new ConnectedSuccessS2CPacket(newUser));

                serverNetwork.sendAll(new ClientJoinS2CPacket(newUser));
                output.onClientJoined(newUser);

                output.updateUserList(getUsers());
            }

            @Override
            public void exceptionOccurred(Object source, Throwable exception) {
                output.exceptionOccurred(source, exception);

                if (source == serverNetwork)
                    stop();
            }
        };
    }

    private void removeUser(User user) {
        var client = getClient(user);
        users.remove(client);
    }

    private User getUser(ClientConnectionHandler client) {
        return users.getOrDefault(client, null);
    }

    private ClientConnectionHandler getClient(User user) {
        for (var entry : users.entrySet())
            if (entry.getValue().equals(user))
                return entry.getKey();
        return null;
    }

    public void start(int port, String password) {
        this.serverPassword = password;
        try {
            serverNetwork.start(port);
        } catch (Exception e) {
            output.exceptionOccurred(this, e);
        }
    }

    public void stop() {
        synchronized (users) {
            for (var entry : users.entrySet()) {
                serverNetwork.sendAndDisconnect(entry.getKey(), new DisconnectedS2CPacket(DisconnectedS2CPacket.DisconnectReason.ServerClosed));
            }
            users.clear();
        }

        synchronized (unauthorizedClients) {
            for (var client : unauthorizedClients) {
                serverNetwork.sendAndDisconnect(client, new DisconnectedS2CPacket(DisconnectedS2CPacket.DisconnectReason.ServerClosed));
            }
            unauthorizedClients.clear();
        }

        try {
            serverNetwork.stop();
        } catch (Exception e) {
            output.exceptionOccurred(this, e);
        }
    }

    public void sendCustomMessage(String text) {
        serverNetwork.sendAll(new CustomSystemMessageS2CPacket(text));
    }

    public Iterable<User> getUsers() {
        return users.values();
    }

    public void kickUser(User user) {
        ClientConnectionHandler client;
        synchronized (users) {
            client = getClient(user);
            if (client == null)
                (new RuntimeException("Kicking unknown user %s.".formatted(user.toString()))).printStackTrace();
            removeUser(user);
        }

        serverNetwork.sendAndDisconnect(client, new DisconnectedS2CPacket(DisconnectedS2CPacket.DisconnectReason.Kicked));
        serverNetwork.sendAll(new ClientLeaveS2CPacket(user, ClientLeaveS2CPacket.LeaveReason.Kicked));
        output.onClientLeft(user);
        output.updateUserList(users.values());
    }

    public void sendCustomMessage(User user, String msg) {
        ClientConnectionHandler client;
        synchronized (users) {
            client = getClient(user);
            if (client == null) {
                (new RuntimeException("Sending pm to unknown user %s: %s.".formatted(user.toString(), msg))).printStackTrace();
                return;
            }
            removeUser(user);
        }
        serverNetwork.send(client, new CustomSystemMessageS2CPacket(msg));
        output.onCustomMessage(msg);
    }
}
