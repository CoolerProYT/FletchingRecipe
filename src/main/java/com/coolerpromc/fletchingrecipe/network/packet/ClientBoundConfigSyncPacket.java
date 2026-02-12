package com.coolerpromc.fletchingrecipe.network.packet;

import com.coolerpromc.fletchingrecipe.FletchingRecipe;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record ClientBoundConfigSyncPacket(boolean allowExplosiveCrafting, int tippedArrowCraftingAmount, int explosiveArrowCraftingAmount) implements CustomPacketPayload {
    public static final Type<ClientBoundConfigSyncPacket> TYPE = new Type<>(Identifier.fromNamespaceAndPath(FletchingRecipe.MOD_ID, "config_sync"));
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

    public static void handle(ClientBoundConfigSyncPacket packet, ClientPlayNetworking.Context context){
        context.client().execute(() -> {
            INSTANCE = packet;
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}