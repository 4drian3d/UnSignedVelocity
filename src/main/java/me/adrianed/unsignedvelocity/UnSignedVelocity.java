package me.adrianed.unsignedvelocity;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Dependency;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import me.adrianed.unsignedvelocity.configuration.Configuration;
import me.adrianed.unsignedvelocity.listener.JoinListener;
import me.adrianed.unsignedvelocity.listener.Listener;
import me.adrianed.unsignedvelocity.listener.packet.chat.KeyedChatListener;
import me.adrianed.unsignedvelocity.listener.packet.chat.SessionChatListener;
import me.adrianed.unsignedvelocity.listener.packet.command.KeyedCommandListener;
import me.adrianed.unsignedvelocity.listener.packet.command.SessionCommandListener;
import me.adrianed.unsignedvelocity.utils.Constants;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.file.Path;
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
    @Inject
    @DataDirectory
    private Path path;
    @Inject
    private Logger logger;
    private Configuration configuration;

    @Subscribe
    void onProxyInitialize(ProxyInitializeEvent event) {
        try {
            configuration = Configuration.loadConfig(path);
        } catch (IOException e) {
            logger.error("Cannot load configuration", e);
            return;
        }
        injector = injector.createChildInjector(
                binder -> binder.bind(Configuration.class).toInstance(configuration)
        );
        Stream.of(
            KeyedChatListener.class,
            SessionChatListener.class,
            KeyedCommandListener.class,
            SessionCommandListener.class,
            JoinListener.class
        )
            .map(injector::getInstance)
            .filter(Listener::canBeLoaded)
            .forEach(Listener::register);

        logger.info("UnSignedVelocity has been successfully loaded");
    }
}
