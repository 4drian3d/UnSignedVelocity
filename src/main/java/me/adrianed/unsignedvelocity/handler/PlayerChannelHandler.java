package me.adrianed.unsignedvelocity.handler;

import com.velocitypowered.api.event.EventManager;
import com.velocitypowered.api.event.ResultedEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.proxy.protocol.MinecraftPacket;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import me.adrianed.unsignedvelocity.event.PacketReceiveEvent;
import me.adrianed.unsignedvelocity.event.PacketSendEvent;
import org.jetbrains.annotations.NotNull;

public final class PlayerChannelHandler extends ChannelDuplexHandler {
    private final Player player;
    private final EventManager eventManager;

    public PlayerChannelHandler(Player player, EventManager eventManager) {
        this.player = player;
        this.eventManager = eventManager;
    }
    @Override
    public void channelRead(@NotNull ChannelHandlerContext ctx, @NotNull Object packet) throws Exception {
        if (!(packet instanceof MinecraftPacket)) {
            super.channelRead(ctx, packet);
            return;
        }

        var result = eventManager.fire(new PacketReceiveEvent(packet, player))
                .thenApply(ResultedEvent::getResult)
                .join();

        if (result.isAllowed()) {
            super.channelRead(ctx, packet);
        }
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object packet, ChannelPromise promise) throws Exception {
        if(!(packet instanceof MinecraftPacket)) {
            super.write(ctx, packet, promise);
            return;
        }

        var result = eventManager.fire(new PacketSendEvent(packet, player))
                .thenApply(ResultedEvent::getResult)
                .join();

        if (result.isAllowed()) {
            super.write(ctx, packet, promise);
        }
    }
}
