package com.coolerpromc.fletchingrecipe.network.packet;

import com.coolerpromc.fletchingrecipe.FletchingRecipe;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ClientBoundConfigSyncPacket(boolean allowExplosiveCrafting, int tippedArrowCraftingAmount, int explosiveArrowCraftingAmount) implements CustomPacketPayload {
    public static final Type<ClientBoundConfigSyncPacket> TYPE = new Type<>(Identifier.fromNamespaceAndPath(FletchingRecipe.MODID, "config_sync"));
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

    public static void handle(ClientBoundConfigSyncPacket packet, IPayloadContext context){
        context.enqueueWork(() -> {
            INSTANCE = packet;
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
