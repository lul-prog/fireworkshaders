package com.fireworkshaders.client;

import com.fireworkshaders.entity.ShaderRocketEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

/**
 * El cohete subiendo: un diamante brillante con el mismo shader
 * de la explosión, a edad fija (núcleo brillante, sin anillos).
 */
public class ShaderRocketRenderer extends EntityRenderer<ShaderRocketEntity> {

    public ShaderRocketRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(ShaderRocketEntity entity, float entityYaw, float partialTick,
                       PoseStack pose, MultiBufferSource buffers, int packedLight) {
        float seed = (entity.getId() % 1000) / 1000.0F;

        int color = entity.getColor();
        float r = ((color >> 16) & 255) / 255.0F;
        float g = ((color >> 8) & 255) / 255.0F;
        float b = (color & 255) / 255.0F;

        pose.pushPose();
        VertexConsumer vc = buffers.getBuffer(FireworkRenderTypes.burst());
        FireworkMeshes.emitDiamond(vc, pose.last().pose(), pose.last().normal(),
                0.06F, seed, r, g, b);
        pose.popPose();

        super.render(entity, entityYaw, partialTick, pose, buffers, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(ShaderRocketEntity entity) {
        // El shader no usa texturas; se devuelve una que existe por seguridad.
        return new ResourceLocation("fireworkshaders", "textures/item/shader_rocket.png");
    }
}
