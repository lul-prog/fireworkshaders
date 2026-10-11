package com.fireworkshaders.client;

import com.fireworkshaders.entity.CustomRocketEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

/** Shader-lit projectile renderer; the vertex color is the selected chemistry powder. */
public class CustomRocketRenderer extends EntityRenderer<CustomRocketEntity> {
    public CustomRocketRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(CustomRocketEntity entity, float entityYaw, float partialTick,
                       PoseStack pose, MultiBufferSource buffers, int packedLight) {
        float seed = (entity.getId() % 1000) / 1000.0F;
        int color = entity.getColor();
        float r = ((color >> 16) & 255) / 255.0F;
        float g = ((color >> 8) & 255) / 255.0F;
        float b = (color & 255) / 255.0F;

        pose.pushPose();
        VertexConsumer consumer = buffers.getBuffer(FireworkRenderTypes.burst());
        FireworkMeshes.emitDiamond(consumer, pose.last().pose(), pose.last().normal(),
                0.0F, seed, r, g, b);
        pose.popPose();
        super.render(entity, entityYaw, partialTick, pose, buffers, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(CustomRocketEntity entity) {
        return new ResourceLocation("fireworkshaders", "textures/item/shader_rocket.png");
    }
}
