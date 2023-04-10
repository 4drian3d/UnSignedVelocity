package io.github._4drian3d.unsignedvelocity.listener.packet.command;

import com.google.inject.Inject;
import com.velocitypowered.api.event.EventManager;
import com.velocitypowered.api.event.ResultedEvent;
import com.velocitypowered.api.event.command.CommandExecuteEvent;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.proxy.VelocityServer;
import com.velocitypowered.proxy.connection.client.ConnectedPlayer;
import com.velocitypowered.proxy.protocol.packet.chat.CommandHandler;
import com.velocitypowered.proxy.protocol.packet.chat.builder.ChatBuilderV2;
import com.velocitypowered.proxy.protocol.packet.chat.keyed.KeyedPlayerCommand;
import io.github._4drian3d.unsignedvelocity.UnSignedVelocity;
import io.github._4drian3d.unsignedvelocity.configuration.Configuration;
import io.github._4drian3d.unsignedvelocity.listener.EventListener;
import io.github._4drian3d.vpacketevents.api.event.PacketReceiveEvent;

import java.util.concurrent.CompletableFuture;

public final class KeyedCommandListener implements EventListener, CommandHandler<KeyedPlayerCommand> {
    @Inject
    private Configuration configuration;
    @Inject
    private EventManager eventManager;
    @Inject
    private UnSignedVelocity plugin;
    private final VelocityServer proxyServer;

    @Inject
    public KeyedCommandListener(final ProxyServer proxyServer) {
        this.proxyServer = (VelocityServer) proxyServer;
    }

    @Override
    public void register() {
        eventManager.register(plugin, PacketReceiveEvent.class, this::onCommand);
    }

    @Override
    public boolean canBeLoaded() {
        return configuration.removeSignedCommandInformation();
    }

    public void onCommand(final PacketReceiveEvent event) {
        if (!(event.getPacket() instanceof final KeyedPlayerCommand packet)) {
            return;
        }

        final ConnectedPlayer player = (ConnectedPlayer) event.getPlayer();
        if (checkConnection(player)) return;

        event.setResult(ResultedEvent.GenericResult.denied());
        final String commandExecuted = packet.getCommand();

        queueCommandResult(proxyServer, player, commandEvent -> {
            final CommandExecuteEvent.CommandResult result = commandEvent.getResult();
            if (result == CommandExecuteEvent.CommandResult.denied()) {
                return CompletableFuture.completedFuture(null);
            }

            final String commandToRun = result.getCommand().orElse(commandExecuted);
            if (result.isForwardToServer()) {
                ChatBuilderV2 write = player.getChatBuilderFactory()
                        .builder()
                        .setTimestamp(packet.getTimestamp())
                        .asPlayer(player);

                if (commandToRun.equals(commandExecuted)) {
                    return CompletableFuture.completedFuture(packet);
                } else {
                    write.message("/" + commandToRun);
                }
                return CompletableFuture.completedFuture(write.toServer());
            }

            return runCommand(proxyServer, player, commandToRun, hasRun -> {
                if (hasRun) return null;

                if (commandToRun.equals(packet.getCommand())) {
                    return packet;
                }

                return player.getChatBuilderFactory()
                        .builder()
                        .setTimestamp(packet.getTimestamp())
                        .asPlayer(player)
                        .message("/" + commandToRun)
                        .toServer();
            });
        }, packet.getCommand(), packet.getTimestamp());
    }

    @Override
    public Class<KeyedPlayerCommand> packetClass() {
        return KeyedPlayerCommand.class;
    }

    @Override
    public void handlePlayerCommandInternal(KeyedPlayerCommand keyedPlayerCommand) {
        //noop
    }
}
