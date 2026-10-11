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

    /** Toroidal annulus used by the ring-shape module. */
    public static void emitRing(VertexConsumer vc, Matrix4f mat, Matrix3f nmat,
                                float age, float seed, float r, float g, float b) {
        int segments = 36;
        int sides = 8;
        double majorRadius = 0.78D;
        double minorRadius = 0.13D;
        for (int i = 0; i < segments; i++) {
            double u0 = Math.PI * 2.0D * i / segments;
            double u1 = Math.PI * 2.0D * (i + 1) / segments;
            for (int j = 0; j < sides; j++) {
                double v0 = Math.PI * 2.0D * j / sides;
                double v1 = Math.PI * 2.0D * (j + 1) / sides;
                double[] a = torus(u0, v0, majorRadius, minorRadius);
                double[] b1 = torus(u1, v0, majorRadius, minorRadius);
                double[] c = torus(u1, v1, majorRadius, minorRadius);
                double[] d = torus(u0, v1, majorRadius, minorRadius);
                tri(vc, mat, nmat, a, b1, c, age, seed, r, g, b);
                tri(vc, mat, nmat, a, c, d, age, seed, r, g, b);
            }
        }
    }

    /** Downward ribbons give the cascade module a distinct waterfall silhouette. */
    public static void emitCascade(VertexConsumer vc, Matrix4f mat, Matrix3f nmat,
                                   float age, float seed, float r, float g, float b) {
        int strands = 16;
        for (int i = 0; i < strands; i++) {
            double angle = Math.PI * 2.0D * i / strands + seed * 0.21D;
            double dx = Math.cos(angle);
            double dz = Math.sin(angle);
            double tx = -dz;
            double tz = dx;
            double[][] center = {
                    {dx * 0.06D, 0.28D, dz * 0.06D},
                    {dx * 0.23D, -0.18D, dz * 0.23D},
                    {dx * 0.37D, -0.75D, dz * 0.37D},
                    {dx * 0.43D, -1.0D, dz * 0.43D},
            };
            double[] widths = {0.025D, 0.035D, 0.022D, 0.002D};
            for (int point = 0; point < center.length - 1; point++) {
                double[] a = offset(center[point], tx, tz, widths[point]);
                double[] b1 = offset(center[point], tx, tz, -widths[point]);
                double[] c = offset(center[point + 1], tx, tz, -widths[point + 1]);
                double[] d = offset(center[point + 1], tx, tz, widths[point + 1]);
                tri(vc, mat, nmat, a, b1, c, age, seed, r, g, b);
                tri(vc, mat, nmat, a, c, d, age, seed, r, g, b);
            }
        }
    }

    private static double[] torus(double u, double v, double majorRadius, double minorRadius) {
        double ring = majorRadius + minorRadius * Math.cos(v);
        return new double[]{ring * Math.cos(u), minorRadius * Math.sin(v) * 0.5D,
                ring * Math.sin(u)};
    }

    private static double[] offset(double[] point, double tangentX, double tangentZ, double width) {
        return new double[]{point[0] + tangentX * width, point[1], point[2] + tangentZ * width};
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
