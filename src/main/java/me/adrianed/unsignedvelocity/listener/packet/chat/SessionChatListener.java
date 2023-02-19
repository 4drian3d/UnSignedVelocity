package me.adrianed.unsignedvelocity.listener.packet.chat;

import com.google.inject.Inject;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.proxy.protocol.packet.chat.session.SessionPlayerChat;
import me.adrianed.unsignedvelocity.listener.EventListener;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.util.Map;
import java.util.WeakHashMap;

//TODO: Make it work
public final class SessionChatListener implements EventListener {
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

    @Override
    public void register() {

    }

    @Override
    public boolean canBeLoaded() {
        return false;
    }
}
