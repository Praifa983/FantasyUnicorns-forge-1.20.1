package com.example.fantasyunicorns.network;

import com.example.fantasyunicorns.entity.UnicornEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record CastPacket() {
    public static void encode(CastPacket p, FriendlyByteBuf buf) {}
    public static CastPacket decode(FriendlyByteBuf buf) { return new CastPacket(); }

    public static void handle(CastPacket p, Supplier<NetworkEvent.Context> sup) {
        NetworkEvent.Context ctx = sup.get();
        ctx.enqueueWork(() -> {
            ServerPlayer pl = ctx.getSender();
            if (pl != null && pl.getVehicle() instanceof UnicornEntity u && u.getControllingPassenger() == pl) {
                u.fireBeam(pl.getLookAngle());
            }
        });
        ctx.setPacketHandled(true);
    }
}
