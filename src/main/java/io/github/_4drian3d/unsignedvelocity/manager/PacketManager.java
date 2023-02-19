package io.github._4drian3d.unsignedvelocity.manager;

import com.velocitypowered.api.event.EventManager;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.proxy.connection.client.ConnectedPlayer;
import com.velocitypowered.proxy.network.Connections;
import io.netty.channel.Channel;
import io.github._4drian3d.unsignedvelocity.handler.PlayerChannelHandler;

public class PacketManager {
    private static final String KEY = "unsigned-velocity";
    private final EventManager eventManager;

    public PacketManager(EventManager eventManager) {
        this.eventManager = eventManager;
    }

    public void injectPlayer(Player player) {
        final ConnectedPlayer p = (ConnectedPlayer) player;
        p.getConnection()
                .getChannel()
                .pipeline()
                .addBefore(Connections.HANDLER, KEY, new PlayerChannelHandler(player, eventManager));
    }

    public void removePlayer(Player player) {
        final ConnectedPlayer p = (ConnectedPlayer) player;
        final Channel channel = p.getConnection().getChannel();
        channel.eventLoop().submit(() -> {
            channel.pipeline().remove(KEY);
        });
    }
}
