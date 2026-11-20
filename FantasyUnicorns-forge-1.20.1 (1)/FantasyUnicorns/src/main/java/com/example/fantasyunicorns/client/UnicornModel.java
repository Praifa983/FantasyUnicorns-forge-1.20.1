package com.example.fantasyunicorns.client;

import com.example.fantasyunicorns.FantasyUnicorns;
import com.example.fantasyunicorns.entity.UnicornEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;

public class UnicornModel extends GeoModel<UnicornEntity> {
    private static final ResourceLocation MODEL = new ResourceLocation(FantasyUnicorns.MODID, "geo/entity/unicorn.geo.json");
    private static final ResourceLocation ANIM = new ResourceLocation(FantasyUnicorns.MODID, "animations/entity/unicorn.animation.json");

    @Override public ResourceLocation getModelResource(UnicornEntity e) { return MODEL; }
    @Override public ResourceLocation getAnimationResource(UnicornEntity e) { return ANIM; }
    @Override public ResourceLocation getTextureResource(UnicornEntity e) {
        return new ResourceLocation(FantasyUnicorns.MODID, "textures/entity/unicorn/" + e.getVariant().id + ".png");
    }

    @Override
    public void setCustomAnimations(UnicornEntity e, long instanceId, AnimationState<UnicornEntity> state) {
        super.setCustomAnimations(e, instanceId, state);
        CoreGeoBone saddle = getAnimationProcessor().getBone("saddle");
        if (saddle != null) saddle.setHidden(!e.isSaddled()); // แสดงอานเมื่อใส่ Saddle
    }
}
