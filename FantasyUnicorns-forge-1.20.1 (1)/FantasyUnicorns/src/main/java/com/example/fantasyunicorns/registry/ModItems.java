package com.example.fantasyunicorns.registry;

import com.example.fantasyunicorns.FantasyUnicorns;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
        DeferredRegister.create(ForgeRegistries.ITEMS, FantasyUnicorns.MODID);

    public static final RegistryObject<Item> MAGIC_APPLE = ITEMS.register("magic_apple",
        () -> new Item(new Item.Properties().rarity(Rarity.EPIC).food(
            new FoodProperties.Builder().nutrition(4).saturationMod(1.2f).alwaysEat()
                .effect(new MobEffectInstance(MobEffects.REGENERATION, 100), 1.0f).build())) {
            @Override public boolean isFoil(ItemStack stack) { return true; }
        });

    public static final RegistryObject<Item> PASTEL_LEAVES = ITEMS.register("pastel_leaves",
        () -> new BlockItem(ModBlocks.PASTEL_LEAVES.get(), new Item.Properties()));

    public static final RegistryObject<Item> GLOW_FLOWER = ITEMS.register("glow_flower",
        () -> new BlockItem(ModBlocks.GLOW_FLOWER.get(), new Item.Properties()));

    public static final RegistryObject<Item> UNICORN_SPAWN_EGG = ITEMS.register("unicorn_spawn_egg",
        () -> new ForgeSpawnEggItem(ModEntities.UNICORN, 0xF5F0FF, 0xC77DFF, new Item.Properties()));
}
