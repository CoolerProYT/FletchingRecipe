package com.coolerpromc.fletchingrecipe.network.packet;

import com.coolerpromc.fletchingrecipe.Constants;
import com.coolerpromc.fletchingrecipe.platform.util.PayloadContext;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record ClientBoundConfigSyncPacket(boolean allowExplosiveCrafting, int tippedArrowCraftingAmount, int explosiveArrowCraftingAmount) implements CustomPacketPayload {
    public static final Type<ClientBoundConfigSyncPacket> TYPE = new Type<>(Constants.id("config_sync"));
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

    public static void handle(ClientBoundConfigSyncPacket packet, PayloadContext context){
        context.execute(() -> INSTANCE = packet);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}