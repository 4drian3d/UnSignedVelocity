package io.github._4drian3d.unsignedvelocity;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.velocitypowered.api.event.EventManager;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import io.github._4drian3d.unsignedvelocity.listener.EventListener;
import io.github._4drian3d.unsignedvelocity.listener.event.ConnectListener;
import io.github._4drian3d.unsignedvelocity.listener.packet.command.KeyedCommandListener;
import io.github._4drian3d.unsignedvelocity.manager.PacketManager;
import io.github._4drian3d.unsignedvelocity.utils.Constants;
import io.github._4drian3d.unsignedvelocity.configuration.Configuration;
import io.github._4drian3d.unsignedvelocity.listener.packet.command.SessionCommandListener;
import org.bstats.velocity.Metrics;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.file.Path;
import java.util.stream.Stream;

@Plugin(
        id = "unsignedvelocity",
        name = "UnSignedVelocity",
        authors = {"4drian3d"},
        version = Constants.VERSION
)
public final class UnSignedVelocity {
    @Inject
    private Injector injector;
    @Inject
    @DataDirectory
    private Path path;
    @Inject
    private Logger logger;
    @Inject
    private Metrics.Factory factory;
    @Inject
    private EventManager eventManager;
    private Configuration configuration;
    private PacketManager packetManager;


    @Subscribe
    void onProxyInitialize(ProxyInitializeEvent event) {
        try {
            configuration = Configuration.loadConfig(path);
        } catch (IOException e) {
            logger.error("Cannot load configuration", e);
            return;
        }
        packetManager = new PacketManager(eventManager);

        injector = injector.createChildInjector(
                binder -> {
                    binder.bind(Configuration.class).toInstance(configuration);
                    binder.bind(PacketManager.class).toInstance(packetManager);
                }
        );

        Stream.of(
            ConnectListener.class,
            KeyedCommandListener.class,
            SessionCommandListener.class
        ).map(injector::getInstance)
        .filter(EventListener::canBeLoaded)
        .forEach(EventListener::register);

        logger.info("UnSignedVelocity has been successfully loaded");
        logger.info("Option removeSignedKey: {}", configuration.removeSignedKey());
        logger.info("Option removeSignedCommandInformation: {}", configuration.removeSignedCommandInformation());

        factory.make(this, 17514);
    }
}
