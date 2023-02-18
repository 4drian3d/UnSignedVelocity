package me.adrianed.unsignedvelocity.event;

import com.velocitypowered.api.event.ResultedEvent;
import com.velocitypowered.proxy.protocol.MinecraftPacket;

import static java.util.Objects.requireNonNull;

public class PacketSendEvent implements ResultedEvent<ResultedEvent.GenericResult> {
    private GenericResult result = GenericResult.allowed();
    private final MinecraftPacket packet;

    public PacketSendEvent(MinecraftPacket packet) {
        this.packet = packet;
    }

    public PacketSendEvent(Object packet) {
        this((MinecraftPacket) packet);
    }
    @Override
    public GenericResult getResult() {
        return result;
    }

    @Override
    public void setResult(GenericResult result) {
        this.result = requireNonNull(result);
    }

    public MinecraftPacket getPacket() {
        return packet;
    }
}
