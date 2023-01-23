package me.adrianed.unsignedvelocity;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Dependency;
import com.velocitypowered.api.plugin.Plugin;
import me.adrianed.unsignedvelocity.listener.JoinListener;
import me.adrianed.unsignedvelocity.listener.Listener;
import me.adrianed.unsignedvelocity.listener.packet.chat.KeyedChatListener;
import me.adrianed.unsignedvelocity.listener.packet.chat.SessionChatListener;
import me.adrianed.unsignedvelocity.listener.packet.command.KeyedCommandListener;
import me.adrianed.unsignedvelocity.listener.packet.command.SessionCommandListener;
import me.adrianed.unsignedvelocity.utils.Constants;

import java.util.stream.Stream;

@Plugin(
        id = "unsignedvelocity",
        name = "UnSignedVelocity",
        authors = {"4drian3d"},
        version = Constants.VERSION,
        dependencies = {
                @Dependency(id = "protocolize")
        }
)
public final class UnSignedVelocity {
    @Inject
    private Injector injector;

    @Subscribe
    void onProxyInitialize(ProxyInitializeEvent event) {
        Stream.of(
                KeyedChatListener.class,
                SessionChatListener.class,
                KeyedCommandListener.class,
                SessionCommandListener.class,
                JoinListener.class
        ).map(injector::getInstance).forEach(Listener::register);
    }
}
