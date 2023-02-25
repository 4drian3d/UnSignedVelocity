package io.github._4drian3d.unsignedvelocity.listener.packet.command;

import com.google.inject.Inject;
import com.velocitypowered.api.event.EventManager;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.proxy.protocol.packet.chat.keyed.KeyedPlayerCommand;
import io.github._4drian3d.unsignedvelocity.listener.EventListener;
import io.github._4drian3d.unsignedvelocity.UnSignedVelocity;
import io.github._4drian3d.unsignedvelocity.configuration.Configuration;
import io.github._4drian3d.vpacketevents.api.event.PacketReceiveEvent;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;

public class KeyedCommandListener implements EventListener {
    private static final MethodHandle UNSIGNED_SETTER;
    private static final MethodHandle SIGNED_PREVIEW;

    static {
        try {
            final var lookup = MethodHandles.privateLookupIn(KeyedPlayerCommand.class, MethodHandles.lookup());
            UNSIGNED_SETTER = lookup.findSetter(KeyedPlayerCommand.class, "unsigned", Boolean.TYPE);
            SIGNED_PREVIEW = lookup.findSetter(KeyedPlayerCommand.class, "signedPreview", Boolean.TYPE);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    @Inject
    private Configuration configuration;
    @Inject
    private EventManager eventManager;
    @Inject
    private UnSignedVelocity plugin;

    @Override
    public void register() {
        eventManager.register(plugin, this);
    }

    @Override
    public boolean canBeLoaded() {
        return configuration.removeSignedCommandInformation();
    }


    @Subscribe
    public void onCommand(PacketReceiveEvent event) {
        if (event.getPacket() instanceof KeyedPlayerCommand) {
            final KeyedPlayerCommand packet = (KeyedPlayerCommand) event.getPacket();

            if (packet.isUnsigned()) {
                return;
            }
            try {
                UNSIGNED_SETTER.invoke(packet, true);
                SIGNED_PREVIEW.invoke(packet, false);
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }
    }
}
