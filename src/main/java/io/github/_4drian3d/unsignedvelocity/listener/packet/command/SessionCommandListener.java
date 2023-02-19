package io.github._4drian3d.unsignedvelocity.listener.packet.command;

import com.google.inject.Inject;
import com.velocitypowered.api.event.EventManager;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.proxy.protocol.packet.chat.LastSeenMessages;
import com.velocitypowered.proxy.protocol.packet.chat.session.SessionPlayerCommand;
import com.velocitypowered.proxy.protocol.packet.chat.session.SessionPlayerCommand.ArgumentSignatures;
import io.github._4drian3d.unsignedvelocity.UnSignedVelocity;
import io.github._4drian3d.unsignedvelocity.event.PacketReceiveEvent;
import io.github._4drian3d.unsignedvelocity.listener.EventListener;
import io.github._4drian3d.unsignedvelocity.configuration.Configuration;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;

public class SessionCommandListener implements EventListener {
    private static final MethodHandle SALT_SETTER;
    private static final MethodHandle LAST_SEEN_MESSAGES_SETTER;
    private static final MethodHandle SIGNATURE_SETTER;
    private static final ArgumentSignatures EMPTY_SIGNATURES = new ArgumentSignatures();
    private static final LastSeenMessages EMPTY_SEEN_MESSAGES = new LastSeenMessages();

    static {
        try {
            final var lookup = MethodHandles.privateLookupIn(SessionPlayerCommand.class, MethodHandles.lookup());
            SALT_SETTER = lookup.findSetter(SessionPlayerCommand.class, "salt", long.class);
            LAST_SEEN_MESSAGES_SETTER = lookup.findSetter(SessionPlayerCommand.class, "lastSeenMessages", LastSeenMessages.class);
            SIGNATURE_SETTER = lookup.findSetter(SessionPlayerCommand.class, "argumentSignatures", ArgumentSignatures.class);
        } catch (Exception e) {
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
        if (event.getPacket() instanceof SessionPlayerCommand) {
            final SessionPlayerCommand packet = (SessionPlayerCommand) event.getPacket();

            if (!packet.isSigned()) {
                return;
            }
            try {
                LAST_SEEN_MESSAGES_SETTER.invoke(packet, EMPTY_SEEN_MESSAGES);
                SIGNATURE_SETTER.invoke(packet, EMPTY_SIGNATURES);
                SALT_SETTER.invoke(packet, 0);
            } catch (Throwable t) {
                throw new RuntimeException(t);
            }
        }

    }
}
