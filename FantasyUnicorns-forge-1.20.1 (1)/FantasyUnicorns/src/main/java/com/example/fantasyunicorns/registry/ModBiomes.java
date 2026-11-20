package com.example.fantasyunicorns.registry;

import com.example.fantasyunicorns.FantasyUnicorns;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;

public class ModBiomes {
    public static final ResourceKey<Biome> MYSTIC_MEADOW =
        ResourceKey.create(Registries.BIOME, new ResourceLocation(FantasyUnicorns.MODID, "mystic_meadow"));
}
