package com.coolerpromc.fletchingrecipe.network.packet;

import com.coolerpromc.fletchingrecipe.FletchingRecipe;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.function.Supplier;

public record ClientBoundConfigSyncPacket(boolean allowExplosiveCrafting, int tippedArrowCraftingAmount, int explosiveArrowCraftingAmount) {
    private static final String PROTOCOL_VERSION = "1";
    public static final ResourceLocation TYPE = new ResourceLocation(FletchingRecipe.MODID, "config_sync");
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(TYPE,
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    public static ClientBoundConfigSyncPacket INSTANCE = null;

    private static int packetId = 0;
    private static int nextId() {
        return packetId++;
    }

    public static void encode(ClientBoundConfigSyncPacket packet, FriendlyByteBuf buf){
        buf.writeBoolean(packet.allowExplosiveCrafting);
        buf.writeInt(packet.tippedArrowCraftingAmount);
        buf.writeInt(packet.explosiveArrowCraftingAmount);
    }

    public static ClientBoundConfigSyncPacket decode(FriendlyByteBuf buf){
        return new ClientBoundConfigSyncPacket(buf.readBoolean(), buf.readInt(), buf.readInt());
    }

    public static void handle(ClientBoundConfigSyncPacket packet, Supplier<NetworkEvent.Context> context){
        context.get().enqueueWork(() -> {
            INSTANCE = packet;
        });
    }

    public static void register(){
        CHANNEL.registerMessage(
                nextId(),
                ClientBoundConfigSyncPacket.class,
                ClientBoundConfigSyncPacket::encode,
                ClientBoundConfigSyncPacket::decode,
                ClientBoundConfigSyncPacket::handle
        );
    }
}
