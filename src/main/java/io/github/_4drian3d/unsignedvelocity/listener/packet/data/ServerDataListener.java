package io.github._4drian3d.unsignedvelocity.listener.packet.data;

import com.google.inject.Inject;
import com.velocitypowered.api.event.EventManager;
import com.velocitypowered.proxy.protocol.packet.ServerData;
import io.github._4drian3d.unsignedvelocity.UnSignedVelocity;
import io.github._4drian3d.unsignedvelocity.configuration.Configuration;
import io.github._4drian3d.unsignedvelocity.listener.EventListener;
import io.github._4drian3d.vpacketevents.api.event.PacketSendEvent;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;

public final class ServerDataListener implements EventListener {
    private static final MethodHandle ENFORCED_SETTER;

    static {
        try {
            final MethodHandles.Lookup lookup = MethodHandles.privateLookupIn(ServerData.class, MethodHandles.lookup());
            ENFORCED_SETTER = lookup.findSetter(ServerData.class, "secureChatEnforced", Boolean.TYPE);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Inject
    private EventManager eventManager;
    @Inject
    private UnSignedVelocity plugin;
    @Inject
    private Configuration configuration;

    @Override
    public void register() {
        eventManager.register(plugin, PacketSendEvent.class, this::onData);
    }

    private void onData(final PacketSendEvent event) {
        if (!(event.getPacket() instanceof final ServerData serverData)) {
            return;
        }

        if (serverData.isSecureChatEnforced()) {
            return;
        }

        try {
            ENFORCED_SETTER.invoke(serverData, true);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean canBeLoaded() {
        return configuration.sendSecureChatData();
    }
}
