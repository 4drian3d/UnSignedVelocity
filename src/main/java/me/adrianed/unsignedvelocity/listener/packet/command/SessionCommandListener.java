package me.adrianed.unsignedvelocity.listener.packet.command;

import com.velocitypowered.proxy.protocol.packet.chat.LastSeenMessages;
import com.velocitypowered.proxy.protocol.packet.chat.session.SessionPlayerCommand;
import com.velocitypowered.proxy.protocol.packet.chat.session.SessionPlayerCommand.ArgumentSignatures;
import dev.simplix.protocolize.api.listener.PacketReceiveEvent;
import dev.simplix.protocolize.api.listener.PacketSendEvent;
import me.adrianed.unsignedvelocity.listener.packet.PacketListener;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;

public class SessionCommandListener extends PacketListener<SessionPlayerCommand> {
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
    protected SessionCommandListener() {
        super(SessionPlayerCommand.class);
    }

    @Override
    public void packetReceive(PacketReceiveEvent<SessionPlayerCommand> event) {
        final SessionPlayerCommand packet = event.packet();
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

    @Override
    public void packetSend(PacketSendEvent<SessionPlayerCommand> packetSendEvent) {
    }
}
