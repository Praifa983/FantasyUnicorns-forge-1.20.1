package com.example.fantasyunicorns.network;

import com.example.fantasyunicorns.FantasyUnicorns;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class ModNetwork {
    private static final String VERSION = "1";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
        new ResourceLocation(FantasyUnicorns.MODID, "main"), () -> VERSION, VERSION::equals, VERSION::equals);

    public static void register() {
        CHANNEL.registerMessage(0, CastPacket.class, CastPacket::encode, CastPacket::decode, CastPacket::handle);
    }
}
