package com.example.fantasyunicorns.registry;

import com.example.fantasyunicorns.FantasyUnicorns;
import com.example.fantasyunicorns.entity.UnicornEntity;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.SpawnPlacementRegisterEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = FantasyUnicorns.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEvents {
    @SubscribeEvent
    public static void attributes(EntityAttributeCreationEvent e) {
        e.put(ModEntities.UNICORN.get(), UnicornEntity.createAttributes().build());
    }

    @SubscribeEvent
    public static void spawnPlacement(SpawnPlacementRegisterEvent e) {
        e.register(ModEntities.UNICORN.get(), SpawnPlacements.Type.ON_GROUND,
            Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules,
            SpawnPlacementRegisterEvent.Operation.OR);
    }
}
