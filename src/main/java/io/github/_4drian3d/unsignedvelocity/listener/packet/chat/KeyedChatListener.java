package io.github._4drian3d.unsignedvelocity.listener.packet.chat;

import com.google.inject.Inject;
import com.velocitypowered.api.event.EventManager;
import com.velocitypowered.api.event.ResultedEvent;
import com.velocitypowered.api.event.player.PlayerChatEvent;
import com.velocitypowered.api.network.ProtocolVersion;
import com.velocitypowered.proxy.connection.client.ConnectedPlayer;
import com.velocitypowered.proxy.protocol.packet.chat.keyed.KeyedPlayerChat;
import io.github._4drian3d.unsignedvelocity.UnSignedVelocity;
import io.github._4drian3d.unsignedvelocity.configuration.Configuration;
import io.github._4drian3d.unsignedvelocity.listener.EventListener;
import io.github._4drian3d.vpacketevents.api.event.PacketReceiveEvent;

public final class KeyedChatListener implements EventListener {
    @Inject
    private EventManager eventManager;
    @Inject
    private UnSignedVelocity plugin;
    @Inject
    private Configuration configuration;

    @Override
    public void register() {
        eventManager.register(plugin, PacketReceiveEvent.class, this::onChat);
    }

    private void onChat(PacketReceiveEvent event) {
        if (!(event.getPacket() instanceof KeyedPlayerChat)) {
            return;
        }

        if (event.getPlayer().getProtocolVersion().compareTo(ProtocolVersion.MINECRAFT_1_19_1) < 0) {
            return;
        }

        event.setResult(ResultedEvent.GenericResult.denied());

        final ConnectedPlayer player = (ConnectedPlayer) event.getPlayer();
        final KeyedPlayerChat chatPacket = (KeyedPlayerChat) event.getPacket();

        player.getChatQueue().queuePacket(
                eventManager.fire(new PlayerChatEvent(player, chatPacket.getMessage()))
                        .thenApply(PlayerChatEvent::getResult)
                        .thenApply(result -> {
                            if (!result.isAllowed()) {
                                return null;
                            }
                            final boolean isModified = result.getMessage()
                                    .map(str -> !str.equals(chatPacket.getMessage()))
                                    .orElse(false);

                            if (isModified) {
                                return player.getChatBuilderFactory()
                                        .builder()
                                        .message(result.getMessage().orElseThrow())
                                        .setTimestamp(chatPacket.getExpiry())
                                        .toServer();
                            }
                            return chatPacket;

                        }), chatPacket.getExpiry());
    }

    @Override
    public boolean canBeLoaded() {
        return configuration.applyChatMessages();
    }
}
