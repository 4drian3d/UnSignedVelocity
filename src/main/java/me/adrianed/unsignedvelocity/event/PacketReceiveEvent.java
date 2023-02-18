package me.adrianed.unsignedvelocity.event;

import com.velocitypowered.api.event.ResultedEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.proxy.protocol.MinecraftPacket;

import static java.util.Objects.requireNonNull;

public class PacketReceiveEvent implements ResultedEvent<ResultedEvent.GenericResult> {
    private GenericResult result = GenericResult.allowed();
    private final MinecraftPacket packet;
    private final Player player;

    public PacketReceiveEvent(MinecraftPacket packet, Player player) {
        this.packet = packet;
        this.player = player;
    }

    public PacketReceiveEvent(Object packet, Player player) {
        this((MinecraftPacket) packet, player);
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

    public Player getPlayer() {
        return this.player;
    }
}
