package com.example.fantasyunicorns.client;

import com.example.fantasyunicorns.entity.UnicornEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

public class UnicornRenderer extends GeoEntityRenderer<UnicornEntity> {
    public UnicornRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new UnicornModel());
        this.shadowRadius = 0.8f;
        addRenderLayer(new AutoGlowingGeoLayer<>(this)); // อ่าน <สี>_glowmask.png
    }
}
