package com.coolerpromc.fletchingrecipe.network.packet;

import com.coolerpromc.fletchingrecipe.FletchingRecipe;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.event.network.CustomPayloadEvent;

public record ClientBoundConfigSyncPacket(boolean allowExplosiveCrafting, int tippedArrowCraftingAmount, int explosiveArrowCraftingAmount) implements CustomPacketPayload {
    public static final Type<ClientBoundConfigSyncPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(FletchingRecipe.MODID, "config_sync"));
    public static ClientBoundConfigSyncPacket INSTANCE = null;

    public static final StreamCodec<RegistryFriendlyByteBuf, ClientBoundConfigSyncPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL,
            ClientBoundConfigSyncPacket::allowExplosiveCrafting,
            ByteBufCodecs.INT,
            ClientBoundConfigSyncPacket::tippedArrowCraftingAmount,
            ByteBufCodecs.INT,
            ClientBoundConfigSyncPacket::explosiveArrowCraftingAmount,
            ClientBoundConfigSyncPacket::new
    );

    public static void handle(ClientBoundConfigSyncPacket packet, CustomPayloadEvent.Context context){
        context.enqueueWork(() -> {
            INSTANCE = packet;
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
