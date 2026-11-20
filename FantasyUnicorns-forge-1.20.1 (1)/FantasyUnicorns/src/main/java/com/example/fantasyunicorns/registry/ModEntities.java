package com.example.fantasyunicorns.registry;

import com.example.fantasyunicorns.FantasyUnicorns;
import com.example.fantasyunicorns.entity.UnicornEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES =
        DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, FantasyUnicorns.MODID);

    public static final RegistryObject<EntityType<UnicornEntity>> UNICORN = ENTITIES.register("unicorn",
        () -> EntityType.Builder.<UnicornEntity>of(UnicornEntity::new, MobCategory.CREATURE)
            .sized(1.4f, 1.7f).clientTrackingRange(10).build("unicorn"));
}
