package me.adrianed.unsignedvelocity.listener;

import com.google.inject.Inject;
import com.velocitypowered.api.event.EventManager;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PostLoginEvent;
import com.velocitypowered.api.proxy.crypto.IdentifiedKey;
import com.velocitypowered.proxy.connection.client.ConnectedPlayer;
import me.adrianed.unsignedvelocity.UnSignedVelocity;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;

public class JoinListener implements Listener {
    private static final MethodHandle KEY_SETTER;

    static {
        try {
            final var lookup = MethodHandles.privateLookupIn(ConnectedPlayer.class, MethodHandles.lookup());
            KEY_SETTER = lookup.findSetter(ConnectedPlayer.class, "playerKey", IdentifiedKey.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    @Inject
    private EventManager eventManager;
    @Inject
    private UnSignedVelocity plugin;

    @Subscribe
    void onJoin(PostLoginEvent event) throws Throwable {
        KEY_SETTER.invoke(event.getPlayer(), null);
    }

    @Override
    public void register() {
        eventManager.register(plugin, this);
    }
}
