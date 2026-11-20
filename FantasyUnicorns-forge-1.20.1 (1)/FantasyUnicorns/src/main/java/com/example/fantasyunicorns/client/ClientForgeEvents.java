package com.example.fantasyunicorns.client;

import com.example.fantasyunicorns.FantasyUnicorns;
import com.example.fantasyunicorns.entity.UnicornEntity;
import com.example.fantasyunicorns.network.CastPacket;
import com.example.fantasyunicorns.network.ModNetwork;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = FantasyUnicorns.MODID, value = Dist.CLIENT)
public class ClientForgeEvents {
    @SubscribeEvent
    public static void tick(TickEvent.ClientTickEvent e) {
        if (e.phase != TickEvent.Phase.END) return;
        Minecraft mc = Minecraft.getInstance();
        while (ClientModEvents.CAST_KEY.consumeClick()) {
            if (mc.player != null && mc.player.getVehicle() instanceof UnicornEntity) {
                ModNetwork.CHANNEL.sendToServer(new CastPacket());
            }
        }
    }
}
