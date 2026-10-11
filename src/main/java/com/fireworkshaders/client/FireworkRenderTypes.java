package com.fireworkshaders.client;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;

/**
 * RenderType de la explosión: shader propio, blending aditivo
 * (SRC_ALPHA, ONE) para el glow, sin cull, sin escritura de profundidad.
 */
public abstract class FireworkRenderTypes extends RenderType {

    private static RenderType BURST;

    protected FireworkRenderTypes(String name, VertexFormat format, VertexFormat.Mode mode, int bufferSize,
                                  boolean affectsCrumbling, boolean sortOnUpload,
                                  Runnable setupState, Runnable clearState) {
        super(name, format, mode, bufferSize, affectsCrumbling, sortOnUpload, setupState, clearState);
        throw new IllegalStateException("No instanciar");
    }

    public static RenderType burst() {
        if (BURST == null) {
            CompositeState state = CompositeState.builder()
                    .setShaderState(new RenderStateShard.ShaderStateShard(FireworkShadersGL::getBurst))
                    .setTransparencyState(new RenderStateShard.TransparencyStateShard(
                            "fireworkshaders:additive",
                            () -> {
                                RenderSystem.enableBlend();
                                RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA,
                                        GlStateManager.DestFactor.ONE);
                            },
                            () -> {
                                RenderSystem.disableBlend();
                                RenderSystem.defaultBlendFunc();
                            }))
                    .setTextureState(RenderStateShard.NO_TEXTURE)
                    .setCullState(RenderStateShard.NO_CULL)
                    .setLightmapState(RenderStateShard.NO_LIGHTMAP)
                    .setOverlayState(RenderStateShard.NO_OVERLAY)
                    .setDepthTestState(RenderStateShard.LEQUAL_DEPTH_TEST)
                    .setWriteMaskState(RenderStateShard.COLOR_WRITE)
                    .createCompositeState(true);
            BURST = RenderType.create("fireworkshaders:burst",
                    DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.TRIANGLES, 65536,
                    true, false, state);
        }
        return BURST;
    }
}
