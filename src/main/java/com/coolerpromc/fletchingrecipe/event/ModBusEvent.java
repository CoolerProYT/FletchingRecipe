package com.coolerpromc.fletchingrecipe.event;

import com.coolerpromc.fletchingrecipe.FletchingRecipe;
import com.coolerpromc.fletchingrecipe.network.packet.ClientBoundConfigSyncPacket;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = FletchingRecipe.MODID)
public class ModBusEvent {
    @SubscribeEvent
    public static void onRegisterPayloadHandlers(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");
        registrar.playToClient(ClientBoundConfigSyncPacket.TYPE, ClientBoundConfigSyncPacket.STREAM_CODEC, ClientBoundConfigSyncPacket::handle);
    }
}
