package server;

import core.PasswordHasher;
import core.User;
import core.network.packets.s2c.chat.*;
import core.network.packets.s2c.service.ConnectedFailureS2CPacket;
import core.network.packets.s2c.service.ConnectedSuccessS2CPacket;
import core.network.packets.s2c.service.LoginRequestS2CPacket;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.*;

public class ServerController {
    public static final int AUTHORIZATION_TIMEOUT_DELAY_SECONDS = 10;
    private final ServerUIUpdater output;
    private final ServerNetwork serverNetwork;

    private final Map<ClientConnectionHandler, User> users;
    private final List<ClientConnectionHandler> unauthorizedClients;
    private String hashedServerPassword;
    private final Timer timer;

    public ServerController(ServerUIUpdater serverUIUpdater) {
        users = new HashMap<>();
        unauthorizedClients = new ArrayList<>();
        output = serverUIUpdater;
        serverNetwork = new ServerNetwork(getHandlerListener());
        timer = new Timer();
        setPassword("");
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
                    throw new RuntimeException("Got public message %s from unknown client %s".formatted(message, senderClient.toString()));
                }

                serverNetwork.sendAll(new PublicChatMessageS2CPacket(senderUser, message));
                output.onPublicMessage(senderUser, message);
            }

            @Override
            public void onPrivateMessage(ClientConnectionHandler senderClient, User receiverUser, String message) {
                User senderUser;
                synchronized (users) { senderUser = getUser(senderClient); }
                if (senderUser == null) {
                    throw new RuntimeException("Got private message %s from unknown client %s to %s".formatted(message, senderClient.toString(), receiverUser.toString()));
                }

                ClientConnectionHandler receiverClient;
                synchronized (users) { receiverClient = getClient(receiverUser); }

                if (receiverClient == null) {
                    serverNetwork.send(senderClient, new SystemMessageS2CPacket(SystemMessageS2CPacket.MessageType.ErrorNoSuchUser));
                    return;
                }

                serverNetwork.send(receiverClient, new PrivateChatMessageS2CPacket(senderUser, receiverUser, message));
                output.onPrivateMessage(senderUser, receiverUser, message);
            }

            @Override
            public void onClientExceptionDisconnected(ClientConnectionHandler client, Throwable exception) {
                User user;
                synchronized (users) {
                    user = getUser(client);
                    if (user == null)
                        throw new RuntimeException("Got exception disconnect from unknown client %s: %s".formatted(client.toString(), exception.toString()));
                    users.remove(user);
                }

                serverNetwork.sendAll(new ClientLeaveS2CPacket(user, ClientLeaveS2CPacket.DisconnectReason.Error));
                output.onClientExceptionDisconnected(user, exception);
                output.updateUserList(users.values());
            }

            @Override
            public void onDisconnect(ClientConnectionHandler client) {
                User user;
                synchronized (users) { user = getUser(client); }
                if (user == null) {
                    throw new RuntimeException("Got disconnect request from unknown client %s: %s".formatted(client.toString()));
                }

                synchronized (users) { users.remove(user); }

                serverNetwork.send(client, new ClientLeaveS2CPacket(user, ClientLeaveS2CPacket.DisconnectReason.SelfDisconnect));
                serverNetwork.forceDisconnect(client);
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
                        if (unauthorizedClients.contains(client)) {
                            synchronized (unauthorizedClients) {
                                unauthorizedClients.remove(client);
                            }
                            serverNetwork.send(client, new ConnectedFailureS2CPacket(ConnectedFailureS2CPacket.FailReason.InvalidPassword));
                            serverNetwork.forceDisconnect(client);
                            output.onAuthorizationTimeout(client);
                        }
                    }
                }, AUTHORIZATION_TIMEOUT_DELAY_SECONDS * 1000);
            }

            @Override
            public void onLogin(ClientConnectionHandler client, User requestedUserData, String hashedPassword) {
                System.out.println(hashedPassword);
                System.out.println(hashedServerPassword);
                synchronized (unauthorizedClients) {
                    unauthorizedClients.remove(client);
                }

                if (!Objects.equals(hashedServerPassword, hashedPassword)) {
                    serverNetwork.send(client, new ConnectedFailureS2CPacket(ConnectedFailureS2CPacket.FailReason.InvalidPassword));
                    serverNetwork.forceDisconnect(client);
                    output.onAuthorizationFailed(client);
                    return;
                }

                User newUser;
                synchronized (users) {
                    for (var user : users.values())
                        if (Objects.equals(user.username(), requestedUserData.username())) {
                            serverNetwork.send(client, new ConnectedFailureS2CPacket(ConnectedFailureS2CPacket.FailReason.UsernameAlreadyTaken));
                            serverNetwork.forceDisconnect(client);
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
            public void exceptionOccurred(Object sender, Throwable exception) {
                output.exceptionOccurred(sender, exception);

                if (sender == serverNetwork)
                    stop();
            }
        };
    }

    private User getUser(ClientConnectionHandler client) {
        return users.getOrDefault(client, null);
    }

    private ClientConnectionHandler getClient(User user) {
        for (var entry : users.entrySet())
            if (entry.getValue() == user)
                return entry.getKey();
        return null;
    }

    public void start(int port) {
        try {
            serverNetwork.start(port);
        } catch (Exception e) {
            output.exceptionOccurred(this, e);
        }
    }

    public void stop() {
        try {
            serverNetwork.stop();
        } catch (Exception e) {
            output.exceptionOccurred(this, e);
        }
    }

    public void setPassword(String password) {
        try {
            this.hashedServerPassword = PasswordHasher.getHashedPassword(password);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            output.exceptionOccurred(this, e);
        }
    }

    public void send(String text) {
        serverNetwork.sendAll(new CustomSystemMessageS2CPacket(text));
    }

    public Iterable<User> getUsers() {
        return users.values();
    }
}
