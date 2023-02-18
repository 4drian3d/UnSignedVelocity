package me.adrianed.unsignedvelocity.manager;

import com.velocitypowered.api.event.EventManager;
import com.velocitypowered.api.event.ResultedEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.proxy.connection.client.ConnectedPlayer;
import com.velocitypowered.proxy.network.Connections;
import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import me.adrianed.unsignedvelocity.event.PacketReceiveEvent;
import me.adrianed.unsignedvelocity.event.PacketSendEvent;
import org.jetbrains.annotations.NotNull;

public class PacketManager {
    private static final String KEY = "unsigned_velocity";
    private final EventManager eventManager;

    public PacketManager(EventManager eventManager) {
        this.eventManager = eventManager;
    }

    public void injectPlayer(Player player) {
        final var handler = new ChannelDuplexHandler() {
            @Override
            public void channelRead(@NotNull ChannelHandlerContext ctx, @NotNull Object packet) throws Exception {
                var result = eventManager.fire(new PacketReceiveEvent(packet))
                        .thenApply(ResultedEvent::getResult)
                        .join();

                if (result.isAllowed()) {
                    super.channelRead(ctx, packet);
                }
                System.out.println("on Read");
            }

            @Override
            public void write(ChannelHandlerContext ctx, Object packet, ChannelPromise promise) throws Exception {
                var result = eventManager.fire(new PacketSendEvent(packet))
                        .thenApply(ResultedEvent::getResult)
                        .join();
                System.out.println("on write");
                if (result.isAllowed()) {
                    super.write(ctx, packet, promise);
                }
            }
        };

        final ConnectedPlayer p = (ConnectedPlayer) player;
        System.out.println("Channels");
        for(var nose : p.getConnection().getChannel().pipeline().names()) {
            System.out.println(nose);
        }

        p.getConnection().getChannel().pipeline().addBefore(Connections.MINECRAFT_DECODER, KEY, handler);
    }

    public void removePlayer(Player player) {
        ConnectedPlayer p = (ConnectedPlayer) player;
        Channel channel = p.getConnection().getChannel();
        channel.eventLoop().submit(() -> {
            channel.pipeline().remove(KEY);
        });
    }
}
