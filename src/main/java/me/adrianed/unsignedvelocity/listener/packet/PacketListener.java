package me.adrianed.unsignedvelocity.listener.packet;

import com.velocitypowered.proxy.protocol.MinecraftPacket;
import dev.simplix.protocolize.api.Direction;
import dev.simplix.protocolize.api.Protocolize;
import dev.simplix.protocolize.api.listener.AbstractPacketListener;
import me.adrianed.unsignedvelocity.listener.Listener;

public abstract class PacketListener<P extends MinecraftPacket>
        extends AbstractPacketListener<P> implements Listener {
    protected PacketListener(Class<P> type) {
        super(type, Direction.UPSTREAM, 0);
    }

    @Override
    public void register() {
        Protocolize.listenerProvider().registerListener(this);
    }
}
