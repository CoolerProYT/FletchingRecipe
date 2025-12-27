package com.coolerpromc.fletchingrecipe.network.packet;

import com.coolerpromc.fletchingrecipe.FletchingRecipe;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record ClientBoundConfigSyncPacket(boolean allowExplosiveCrafting, int tippedArrowCraftingAmount, int explosiveArrowCraftingAmount) implements CustomPayload {
    public static final Id<ClientBoundConfigSyncPacket> TYPE = new Id<>(Identifier.of(FletchingRecipe.MOD_ID, "config_sync"));
    public static ClientBoundConfigSyncPacket INSTANCE = null;

    public static final PacketCodec<RegistryByteBuf, ClientBoundConfigSyncPacket> STREAM_CODEC = PacketCodec.tuple(
            PacketCodecs.BOOL,
            ClientBoundConfigSyncPacket::allowExplosiveCrafting,
            PacketCodecs.INTEGER,
            ClientBoundConfigSyncPacket::tippedArrowCraftingAmount,
            PacketCodecs.INTEGER,
            ClientBoundConfigSyncPacket::explosiveArrowCraftingAmount,
            ClientBoundConfigSyncPacket::new
    );

    public static void handle(ClientBoundConfigSyncPacket packet, ClientPlayNetworking.Context context){
        context.client().execute(() -> {
            INSTANCE = packet;
        });
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return TYPE;
    }
}