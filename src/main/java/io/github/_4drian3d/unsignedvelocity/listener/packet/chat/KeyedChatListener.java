package io.github._4drian3d.unsignedvelocity.listener.packet.chat;

import com.google.inject.Inject;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.proxy.protocol.packet.chat.keyed.KeyedPlayerChat;
import io.github._4drian3d.unsignedvelocity.listener.EventListener;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.util.Map;
import java.util.WeakHashMap;

//TODO: Make it work
public final class KeyedChatListener implements EventListener {
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

    private final Map<KeyedPlayerChat, Player> map = new WeakHashMap<>();

    @Override
    public void register() {

    }

    @Override
    public boolean canBeLoaded() {
        return false;
    }
}
