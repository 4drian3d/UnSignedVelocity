package me.adrianed.unsignedvelocity.listener.packet.chat;

import com.google.inject.Inject;
import com.velocitypowered.api.network.ProtocolVersion;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.proxy.protocol.packet.chat.keyed.KeyedPlayerChat;
import dev.simplix.protocolize.api.listener.PacketReceiveEvent;
import dev.simplix.protocolize.api.listener.PacketSendEvent;
import me.adrianed.unsignedvelocity.listener.packet.PacketListener;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.util.Map;
import java.util.Objects;
import java.util.WeakHashMap;

public final class KeyedChatListener extends PacketListener<KeyedPlayerChat> {
    private static final MethodHandle UNSIGNED_SETTER;
    private static final MethodHandle SIGNED_PREVIEW;

    static {
        try {
            final var lookup = MethodHandles.privateLookupIn(KeyedPlayerChat.class, MethodHandles.lookup());
            UNSIGNED_SETTER = lookup.findSetter(KeyedPlayerChat.class, "unsigned", Boolean.TYPE);
            SIGNED_PREVIEW = lookup.findSetter(KeyedPlayerChat.class, "signedPreview", Boolean.TYPE);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    @Inject
    private ProxyServer proxyServer;

    public KeyedChatListener() {
        super(KeyedPlayerChat.class);
    }

    private final Map<KeyedPlayerChat, Player> map = new WeakHashMap<>();

    @Override
    public void packetReceive(PacketReceiveEvent<KeyedPlayerChat> event) {
        System.out.println("Received KeyedPlayerChat on receive");
        KeyedPlayerChat packet = event.packet();
        if (packet.isUnsigned()) {
            return;
        }
        proxyServer.getPlayer(event.player().uniqueId())
                .ifPresent(player -> {
                    try {
                        UNSIGNED_SETTER.invoke(packet, true);
                        if (!player.getProtocolVersion().equals(ProtocolVersion.MINECRAFT_1_19_1)) {
                            SIGNED_PREVIEW.invoke(packet, false);
                        }

                    } catch (Throwable e) {
                        throw new RuntimeException(e);
                    }

                    map.put(packet, player);
                });
    }

    @Override
    public void packetSend(PacketSendEvent<KeyedPlayerChat> event) {
        System.out.println("Received KeyedPlayerChat on send");
        KeyedPlayerChat packet = event.packet();
        Player mapPlayer = map.get(packet);

        if (mapPlayer == null) {
            return;
        }

        Player player = proxyServer.getPlayer(event.player().uniqueId())
                .orElse(null);

        if (Objects.equals(mapPlayer, player)) {
            try {
                map.remove(packet);
                UNSIGNED_SETTER.invoke(packet, false);
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }
    }
}
