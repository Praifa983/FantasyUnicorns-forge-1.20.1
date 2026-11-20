package com.example.fantasyunicorns.worldgen;

import com.example.fantasyunicorns.registry.ModBiomes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.SurfaceRules;

public class ModSurfaceRules {
    public static SurfaceRules.RuleSource makeRules() {
        SurfaceRules.RuleSource grass = SurfaceRules.state(Blocks.GRASS_BLOCK.defaultBlockState());
        SurfaceRules.RuleSource dirt = SurfaceRules.state(Blocks.DIRT.defaultBlockState());
        return SurfaceRules.ifTrue(SurfaceRules.isBiome(ModBiomes.MYSTIC_MEADOW),
            SurfaceRules.sequence(
                SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR,
                    SurfaceRules.ifTrue(SurfaceRules.waterBlockCheck(-1, 0), grass)),
                SurfaceRules.ifTrue(SurfaceRules.UNDER_FLOOR, dirt)));
    }
}
