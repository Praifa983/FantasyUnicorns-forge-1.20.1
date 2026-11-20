package com.example.fantasyunicorns.registry;

import com.example.fantasyunicorns.FantasyUnicorns;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS =
        DeferredRegister.create(ForgeRegistries.BLOCKS, FantasyUnicorns.MODID);

    public static final RegistryObject<Block> PASTEL_LEAVES = BLOCKS.register("pastel_leaves",
        () -> new LeavesBlock(BlockBehaviour.Properties.copy(Blocks.OAK_LEAVES).lightLevel(s -> 8)));

    public static final RegistryObject<Block> GLOW_FLOWER = BLOCKS.register("glow_flower",
        () -> new FlowerBlock(MobEffects.GLOWING, 5,
            BlockBehaviour.Properties.copy(Blocks.DANDELION).lightLevel(s -> 10)));
}
