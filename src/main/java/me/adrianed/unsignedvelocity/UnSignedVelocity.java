package me.adrianed.unsignedvelocity;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.velocitypowered.api.event.EventManager;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import me.adrianed.unsignedvelocity.configuration.Configuration;
import me.adrianed.unsignedvelocity.listener.EventListener;
import me.adrianed.unsignedvelocity.listener.event.ConnectListener;
import me.adrianed.unsignedvelocity.listener.packet.command.KeyedCommandListener;
import me.adrianed.unsignedvelocity.listener.packet.command.SessionCommandListener;
import me.adrianed.unsignedvelocity.manager.PacketManager;
import me.adrianed.unsignedvelocity.utils.Constants;
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
