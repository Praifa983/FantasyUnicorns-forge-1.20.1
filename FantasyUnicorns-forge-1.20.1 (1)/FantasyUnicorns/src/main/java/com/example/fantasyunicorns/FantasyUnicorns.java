package com.example.fantasyunicorns;

import com.example.fantasyunicorns.network.ModNetwork;
import com.example.fantasyunicorns.registry.*;
import com.example.fantasyunicorns.worldgen.ModRegion;
import com.example.fantasyunicorns.worldgen.ModSurfaceRules;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import terrablender.api.Regions;
import terrablender.api.SurfaceRuleManager;

@Mod(FantasyUnicorns.MODID)
public class FantasyUnicorns {
    public static final String MODID = "fantasyunicorns";

    public FantasyUnicorns() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        ModBlocks.BLOCKS.register(bus);
        ModItems.ITEMS.register(bus);
        ModEntities.ENTITIES.register(bus);
        ModCreativeTabs.TABS.register(bus);
        bus.addListener(this::commonSetup);
    }

    private void commonSetup(FMLCommonSetupEvent e) {
        e.enqueueWork(() -> {
            ModNetwork.register();
            // น้ำหนัก 3 = ไบโอมใหม่โผล่แทนป่า/ป่าดอกไม้ของ vanilla ในบางส่วนของโลก (ปรับเลขได้)
            Regions.register(new ModRegion(new ResourceLocation(MODID, "overworld"), 3));
            SurfaceRuleManager.addSurfaceRules(SurfaceRuleManager.RuleCategory.OVERWORLD, MODID, ModSurfaceRules.makeRules());
        });
    }
}
