package com.fireworkshaders.client;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

/**
 * Mallas procedurales para los renderers. Cada vértice lleva:
 * Position (esfera/octaedro unitario), Color (rgb = color del fuego
 * artificial), UV0 (x = edad 0..1, y = semilla), Normal.
 */
public final class FireworkMeshes {
    private FireworkMeshes() {
    }

    /** Esfera unitaria (la explosión la escala el vertex shader según la edad). */
    public static void emitSphere(VertexConsumer vc, Matrix4f mat, Matrix3f nmat,
                                  float age, float seed, float r, float g, float b) {
        int lats = 12;
        int lons = 18;
        double[][][] grid = new double[lats + 1][lons + 1][3];
        for (int i = 0; i <= lats; i++) {
            double theta = Math.PI * i / lats;
            double sinT = Math.sin(theta);
            double cosT = Math.cos(theta);
            for (int j = 0; j <= lons; j++) {
                double phi = 2.0D * Math.PI * j / lons;
                grid[i][j][0] = sinT * Math.cos(phi);
                grid[i][j][1] = cosT;
                grid[i][j][2] = sinT * Math.sin(phi);
            }
        }
        for (int i = 0; i < lats; i++) {
            for (int j = 0; j < lons; j++) {
                double[] a = grid[i][j];
                double[] b2 = grid[i + 1][j];
                double[] c = grid[i + 1][j + 1];
                double[] d = grid[i][j + 1];
                tri(vc, mat, nmat, a, b2, c, age, seed, r, g, b);
                tri(vc, mat, nmat, a, c, d, age, seed, r, g, b);
            }
        }
    }

    /** Diamante alargado: la cabeza brillante del cohete mientras sube. */
    public static void emitDiamond(VertexConsumer vc, Matrix4f mat, Matrix3f nmat,
                                   float age, float seed, float r, float g, float b) {
        double[] top = {0.0D, 0.32D, 0.0D};
        double[] bottom = {0.0D, -0.22D, 0.0D};
        double rad = 0.11D;
        double y = 0.05D;
        double[][] eq = {
                {rad, y, 0.0D}, {0.0D, y, rad}, {-rad, y, 0.0D}, {0.0D, y, -rad},
        };
        for (int i = 0; i < 4; i++) {
            double[] p1 = eq[i];
            double[] p2 = eq[(i + 1) % 4];
            tri(vc, mat, nmat, top, p1, p2, age, seed, r, g, b);
            tri(vc, mat, nmat, bottom, p2, p1, age, seed, r, g, b);
        }
    }

    private static void tri(VertexConsumer vc, Matrix4f mat, Matrix3f nmat,
                            double[] p1, double[] p2, double[] p3,
                            float age, float seed, float r, float g, float b) {
        vert(vc, mat, nmat, p1, age, seed, r, g, b);
        vert(vc, mat, nmat, p2, age, seed, r, g, b);
        vert(vc, mat, nmat, p3, age, seed, r, g, b);
    }

    private static void vert(VertexConsumer vc, Matrix4f mat, Matrix3f nmat,
                             double[] p, float age, float seed, float r, float g, float b) {
        // En la esfera unitaria la normal es la posición normalizada.
        float nx = (float) p[0];
        float ny = (float) p[1];
        float nz = (float) p[2];
        float len = (float) Math.sqrt(nx * nx + ny * ny + nz * nz);
        if (len > 1e-6F) {
            nx /= len;
            ny /= len;
            nz /= len;
        }
        vc.vertex(mat, (float) p[0], (float) p[1], (float) p[2])
                .color(r, g, b, 1.0F)
                .uv(age, seed)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(0)
                .normal(nmat, nx, ny, nz)
                .endVertex();
    }
}
