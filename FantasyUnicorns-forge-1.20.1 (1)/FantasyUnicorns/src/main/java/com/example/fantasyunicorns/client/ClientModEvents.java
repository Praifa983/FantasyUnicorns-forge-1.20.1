package com.example.fantasyunicorns.client;

import com.example.fantasyunicorns.FantasyUnicorns;
import com.example.fantasyunicorns.registry.ModEntities;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(modid = FantasyUnicorns.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientModEvents {
    public static final KeyMapping CAST_KEY =
        new KeyMapping("key.fantasyunicorns.cast", GLFW.GLFW_KEY_R, "key.categories.fantasyunicorns");

    @SubscribeEvent
    public static void renderers(EntityRenderersEvent.RegisterRenderers e) {
        e.registerEntityRenderer(ModEntities.UNICORN.get(), UnicornRenderer::new);
    }

    @SubscribeEvent
    public static void keys(RegisterKeyMappingsEvent e) {
        e.register(CAST_KEY);
    }
}
