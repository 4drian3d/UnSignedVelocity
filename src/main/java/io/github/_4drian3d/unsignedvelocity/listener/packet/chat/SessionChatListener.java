package io.github._4drian3d.unsignedvelocity.listener.packet.chat;

import com.google.inject.Inject;
import com.velocitypowered.api.event.EventManager;
import com.velocitypowered.api.event.ResultedEvent;
import com.velocitypowered.api.event.player.PlayerChatEvent;
import com.velocitypowered.proxy.connection.client.ConnectedPlayer;
import com.velocitypowered.proxy.protocol.packet.chat.session.SessionPlayerChat;
import io.github._4drian3d.unsignedvelocity.UnSignedVelocity;
import io.github._4drian3d.unsignedvelocity.configuration.Configuration;
import io.github._4drian3d.unsignedvelocity.listener.EventListener;
import io.github._4drian3d.vpacketevents.api.event.PacketReceiveEvent;

public final class SessionChatListener implements EventListener {
    @Inject
    private UnSignedVelocity plugin;
    @Inject
    private EventManager eventManager;
    @Inject
    private Configuration configuration;


    @Override
    public void register() {
        eventManager.register(plugin, PacketReceiveEvent.class, this::onChat);
    }

    private void onChat(PacketReceiveEvent event) {
        // Packet sent by players with version 1.19.3 and up
        if (!(event.getPacket() instanceof final SessionPlayerChat chatPacket)) {
            return;
        }

        final ConnectedPlayer player = (ConnectedPlayer) event.getPlayer();
        if (checkConnection(player)) return;

        event.setResult(ResultedEvent.GenericResult.denied());

        final String chatMessage = chatPacket.getMessage();

        player.getChatQueue().queuePacket(
                eventManager.fire(new PlayerChatEvent(player, chatMessage))
                        .thenApply(PlayerChatEvent::getResult)
                        .thenApply(result -> {
                            if (!result.isAllowed()) {
                                return null;
                            }

                            final boolean isModified = result
                                    .getMessage()
                                    .map(str -> !str.equals(chatMessage))
                                    .orElse(false);

                            if (isModified) {
                                return player.getChatBuilderFactory()
                                        .builder()
                                        .message(result.getMessage().orElseThrow())
                                        .setTimestamp(chatPacket.getTimestamp())
                                        .toServer();
                            }
                            return chatPacket;
                        }),
                chatPacket.getTimestamp()
        );
    }

    @Override
    public boolean canBeLoaded() {
        return configuration.applyChatMessages();
    }
}
