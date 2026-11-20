package com.example.fantasyunicorns.registry;

import com.example.fantasyunicorns.FantasyUnicorns;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> TABS =
        DeferredRegister.create(Registries.CREATIVE_MODE_TAB, FantasyUnicorns.MODID);

    public static final RegistryObject<CreativeModeTab> MAIN = TABS.register("main",
        () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.fantasyunicorns"))
            .icon(() -> new ItemStack(ModItems.MAGIC_APPLE.get()))
            .displayItems((params, out) -> {
                out.accept(ModItems.MAGIC_APPLE.get());
                out.accept(ModItems.UNICORN_SPAWN_EGG.get());
                out.accept(ModItems.PASTEL_LEAVES.get());
                out.accept(ModItems.GLOW_FLOWER.get());
            }).build());
}
