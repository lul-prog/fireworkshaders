package com.fireworkshaders.client;

import com.fireworkshaders.entity.ShaderBurstEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

/**
 * Dibuja la explosión: una esfera cuyo crecimiento, anillos, fresnel
 * y desvanecido los calcula el shader fireworkshaders:burst.
 * La edad (0..1) viaja en UV0.x con interpolación del partialTick.
 */
public class ShaderBurstRenderer extends EntityRenderer<ShaderBurstEntity> {

    public ShaderBurstRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(ShaderBurstEntity entity, float entityYaw, float partialTick,
                       PoseStack pose, MultiBufferSource buffers, int packedLight) {
        float age = Math.min(1.0F,
                (entity.tickCount + partialTick) / (float) ShaderBurstEntity.DURATION);
        float seed = (entity.getId() % 1000) / 1000.0F;

        int color = entity.getColor();
        float r = ((color >> 16) & 255) / 255.0F;
        float g = ((color >> 8) & 255) / 255.0F;
        float b = (color & 255) / 255.0F;

        pose.pushPose();
        VertexConsumer vc = buffers.getBuffer(FireworkRenderTypes.burst());
        FireworkMeshes.emitSphere(vc, pose.last().pose(), pose.last().normal(),
                age, seed, r, g, b);
        pose.popPose();

        super.render(entity, entityYaw, partialTick, pose, buffers, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(ShaderBurstEntity entity) {
        // El shader no usa texturas; se devuelve una que existe por seguridad.
        return new ResourceLocation("fireworkshaders", "textures/item/shader_rocket.png");
    }
}
