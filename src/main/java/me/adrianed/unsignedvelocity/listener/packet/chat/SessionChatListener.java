package me.adrianed.unsignedvelocity.listener.packet.chat;

import com.google.inject.Inject;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.proxy.protocol.packet.chat.session.SessionPlayerChat;
import dev.simplix.protocolize.api.listener.PacketReceiveEvent;
import dev.simplix.protocolize.api.listener.PacketSendEvent;
import me.adrianed.unsignedvelocity.listener.packet.PacketListener;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.util.Map;
import java.util.Objects;
import java.util.WeakHashMap;

public final class SessionChatListener extends PacketListener<SessionPlayerChat> {
    private static final MethodHandle SIGNED_SETTER;

    static {
        try {
            final var lookup = MethodHandles.privateLookupIn(SessionPlayerChat.class, MethodHandles.lookup());
            SIGNED_SETTER = lookup.findSetter(SessionPlayerChat.class, "signed", Boolean.TYPE);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Inject
    private ProxyServer proxyServer;

    private final Map<SessionPlayerChat, Player> map = new WeakHashMap<>();
    public SessionChatListener() {
        super(SessionPlayerChat.class);
    }

    @Override
    public void packetReceive(PacketReceiveEvent<SessionPlayerChat> event) {
        System.out.println("Received SessionPlayerChat on receive");
        final SessionPlayerChat packet = event.packet();
        if (!packet.isSigned()) {
            return;
        }

        proxyServer.getPlayer(event.player().uniqueId())
                .ifPresent(player -> {
                    try {
                        map.put(packet, player);
                        SIGNED_SETTER.invoke(packet, false);
                    } catch (Throwable e) {
                        throw new RuntimeException(e);
                    }
                });

    }

    @Override
    public void packetSend(PacketSendEvent<SessionPlayerChat> event) {
        System.out.println("Received SessionPlayerChat on send");
        final SessionPlayerChat packet = event.packet();
        var mapPlayer = map.get(packet);

        if (mapPlayer == null) {
            return;
        }

        Player player = proxyServer.getPlayer(event.player().uniqueId())
                .orElse(null);

        if (Objects.equals(mapPlayer, player)) {
            try {
                map.remove(packet);
                SIGNED_SETTER.invoke(packet, true);
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }
    }
}
