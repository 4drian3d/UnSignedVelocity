package io.github._4drian3d.unsignedvelocity.listener.event;

import com.google.inject.Inject;
import com.velocitypowered.api.event.EventManager;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PostLoginEvent;
import com.velocitypowered.api.proxy.crypto.IdentifiedKey;
import com.velocitypowered.proxy.connection.client.ConnectedPlayer;
import io.github._4drian3d.unsignedvelocity.listener.EventListener;
import io.github._4drian3d.unsignedvelocity.UnSignedVelocity;
import io.github._4drian3d.unsignedvelocity.configuration.Configuration;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;

public class ConnectListener implements EventListener {
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
    @Inject
    private Configuration configuration;

    @Subscribe
    void onJoin(PostLoginEvent event) throws Throwable {
        if (configuration.removeSignedKey()) {
            KEY_SETTER.invoke(event.getPlayer(), null);
        }
    }

    @Override
    public void register() {
        eventManager.register(plugin, this);
    }

    @Override
    public boolean canBeLoaded() {
        return true;
    }
}
