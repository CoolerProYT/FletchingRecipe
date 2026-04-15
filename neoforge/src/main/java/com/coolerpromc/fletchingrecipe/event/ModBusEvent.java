package com.coolerpromc.fletchingrecipe.event;

import com.coolerpromc.fletchingrecipe.Constants;
import com.coolerpromc.fletchingrecipe.network.packet.ClientBoundConfigSyncPacket;
import com.coolerpromc.fletchingrecipe.platform.util.NeoForgePayloadContext;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = Constants.MODID)
public class ModBusEvent {
    @SubscribeEvent
    public static void onRegisterPayloadHandlers(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");
        registrar.playToClient(ClientBoundConfigSyncPacket.TYPE, ClientBoundConfigSyncPacket.STREAM_CODEC, (payload, context) -> ClientBoundConfigSyncPacket.handle(payload, new NeoForgePayloadContext(context)));
    }
}
