package io.github._4drian3d.unsignedvelocity.event;

import com.velocitypowered.api.event.ResultedEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.proxy.protocol.MinecraftPacket;

import static java.util.Objects.requireNonNull;

public class PacketSendEvent
        extends PacketEvent
        implements ResultedEvent<ResultedEvent.GenericResult>
{
    private GenericResult result = GenericResult.allowed();

    public PacketSendEvent(Object packet, Player player) {
        super((MinecraftPacket) packet, player);
    }

    @Override
    public GenericResult getResult() {
        return result;
    }

    @Override
    public void setResult(GenericResult result) {
        this.result = requireNonNull(result);
    }
}
