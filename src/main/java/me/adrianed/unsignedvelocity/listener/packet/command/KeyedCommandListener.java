package me.adrianed.unsignedvelocity.listener.packet.command;

import com.velocitypowered.proxy.protocol.packet.chat.keyed.KeyedPlayerCommand;
import dev.simplix.protocolize.api.listener.PacketReceiveEvent;
import dev.simplix.protocolize.api.listener.PacketSendEvent;
import me.adrianed.unsignedvelocity.listener.packet.PacketListener;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;

public class KeyedCommandListener extends PacketListener<KeyedPlayerCommand> {
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
    public KeyedCommandListener() {
        super(KeyedPlayerCommand.class);
    }

    @Override
    public void packetReceive(PacketReceiveEvent<KeyedPlayerCommand> event) {
        KeyedPlayerCommand packet = event.packet();
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

    @Override
    public void packetSend(PacketSendEvent<KeyedPlayerCommand> packetSendEvent) {

    }
}
