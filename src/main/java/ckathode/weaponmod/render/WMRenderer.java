package ckathode.weaponmod.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.Entity;

public abstract class WMRenderer<T extends Entity> extends EntityRenderer<T> {

    protected static final float SQRT2 = (float) Math.sqrt(2.0);

    protected WMRenderer(EntityRendererManager manager) {
        super(manager);
    }

    public void drawVertex(MatrixStack.Entry entry, IVertexBuilder builder, float x, float y, float z,
                           float uvX, float uvZ, float nmX, float nmY, float nmZ, int lm) {
        drawVertex(entry, builder, x, y, z, 1, 1, 1, 1, uvX, uvZ, nmX, nmY, nmZ, lm);
    }

    public void drawVertex(MatrixStack.Entry entry, IVertexBuilder builder, float x, float y, float z,
                           float r, float g, float b, float a, float uvX, float uvZ,
                           float nmX, float nmY, float nmZ, int lm) {
        builder.vertex(entry.pose(), x, y, z)
                .color(r, g, b, a)
                .uv(uvX, uvZ)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(lm)
                .normal(entry.normal(), nmX, nmY, nmZ)
                .endVertex();
    }

}
