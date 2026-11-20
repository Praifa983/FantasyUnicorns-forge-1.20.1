package com.example.fantasyunicorns.entity;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;

public enum UnicornVariant {
    WHITE("aurelia", Tier.COMMON, ParticleTypes.END_ROD, new MobEffect[]{MobEffects.REGENERATION}, null),
    BLACK("nyx", Tier.COMMON, ParticleTypes.SMOKE, new MobEffect[]{MobEffects.NIGHT_VISION}, MobEffects.BLINDNESS),
    PINK("rosalie", Tier.COMMON, ParticleTypes.CHERRY_LEAVES, new MobEffect[]{MobEffects.ABSORPTION}, MobEffects.WEAKNESS),
    BLUE("glacia", Tier.COMMON, ParticleTypes.SNOWFLAKE, new MobEffect[]{MobEffects.WATER_BREATHING}, MobEffects.MOVEMENT_SLOWDOWN),
    GOLD("solara", Tier.COMMON, ParticleTypes.WAX_ON, new MobEffect[]{MobEffects.MOVEMENT_SPEED, MobEffects.LUCK}, null),
    PURPLE("amethyra", Tier.COMMON, ParticleTypes.WITCH, new MobEffect[]{MobEffects.JUMP}, MobEffects.LEVITATION),
    SILVER("lunaris", Tier.RARE, ParticleTypes.ELECTRIC_SPARK, new MobEffect[]{MobEffects.SLOW_FALLING, MobEffects.NIGHT_VISION}, MobEffects.GLOWING),
    EMERALD("verdelia", Tier.RARE, ParticleTypes.HAPPY_VILLAGER, new MobEffect[]{MobEffects.DAMAGE_RESISTANCE}, MobEffects.POISON),
    CRIMSON("ignis", Tier.RARE, ParticleTypes.FLAME, new MobEffect[]{MobEffects.FIRE_RESISTANCE}, null),
    PRISMA("prisma", Tier.LEGENDARY, ParticleTypes.FIREWORK, new MobEffect[]{}, null),
    CELESTIA("celestia", Tier.LEGENDARY, ParticleTypes.ENCHANT,
        new MobEffect[]{MobEffects.SLOW_FALLING, MobEffects.MOVEMENT_SPEED, MobEffects.JUMP}, null);

    public enum Tier { COMMON, RARE, LEGENDARY }

    public final String id;
    public final Tier tier;
    public final ParticleOptions particle;
    public final MobEffect[] riderEffects;
    public final MobEffect beamEffect; // nullable

    UnicornVariant(String id, Tier tier, ParticleOptions particle, MobEffect[] riderEffects, MobEffect beamEffect) {
        this.id = id; this.tier = tier; this.particle = particle;
        this.riderEffects = riderEffects; this.beamEffect = beamEffect;
    }

    public static UnicornVariant byId(int i) {
        UnicornVariant[] v = values();
        return v[Mth.clamp(i, 0, v.length - 1)];
    }
}
