package ckathode.weaponmod.render;

import ckathode.weaponmod.BalkonsWeaponMod;
import ckathode.weaponmod.WeaponModResources;
import ckathode.weaponmod.entity.projectile.EntitySpear;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;

public class RenderSpear extends WMRenderer<EntitySpear> {

    public RenderSpear(EntityRendererManager renderManager) {
        super(renderManager);
    }

    @Override
    @ParametersAreNonnullByDefault
    public void render(EntitySpear entityspear, float f, float f1,
                       MatrixStack ms, IRenderTypeBuffer bufs, int lm) {
        if (!BalkonsWeaponMod.instance.modConfig.itemModelForEntity.get()) {
            IVertexBuilder builder = bufs.getBuffer(RenderType.entityCutout(getTextureLocation(entityspear)));
            ms.pushPose();
            ms.mulPose(Vector3f.YP.rotationDegrees(entityspear.yRotO + (entityspear.yRot - entityspear.yRotO) * f1 - 90.0f));
            ms.mulPose(Vector3f.ZP.rotationDegrees(entityspear.xRotO + (entityspear.xRot - entityspear.xRotO) * f1));
            float[] color = entityspear.getMaterialColor();
            float length = 20.0f;
            float f13 = entityspear.shakeTime - f1;
            if (f13 > 0.0f) {
                float f14 = -MathHelper.sin(f13 * 3.0f) * f13;
                ms.mulPose(Vector3f.ZP.rotationDegrees(f14));
            }
            ms.mulPose(Vector3f.XP.rotationDegrees(45.0f));
            ms.scale(0.05625f, 0.05625f, 0.05625f);
            ms.translate(-4.0f, 0.0f, 0.0f);
            MatrixStack.Entry last = ms.last();
            drawVertex(last, builder, -length, -2.0f, -2.0f, 0.0f, 0.15625f, 0.05625f, 0.0f, 0.0f, lm);
            drawVertex(last, builder, -length, -2.0f, 2.0f, 0.15625f, 0.15625f, 0.05625f, 0.0f, 0.0f, lm);
            drawVertex(last, builder, -length, 2.0f, 2.0f, 0.15625f, 0.3125f, 0.05625f, 0.0f, 0.0f, lm);
            drawVertex(last, builder, -length, 2.0f, -2.0f, 0.0f, 0.3125f, 0.05625f, 0.0f, 0.0f, lm);
            drawVertex(last, builder, -length, 2.0f, -2.0f, 0.0f, 0.15625f, -0.05625f, 0.0f, 0.0f, lm);
            drawVertex(last, builder, -length, 2.0f, 2.0f, 0.15625f, 0.15625f, -0.05625f, 0.0f, 0.0f, lm);
            drawVertex(last, builder, -length, -2.0f, 2.0f, 0.15625f, 0.3125f, -0.05625f, 0.0f, 0.0f, lm);
            drawVertex(last, builder, -length, -2.0f, -2.0f, 0.0f, 0.3125f, -0.05625f, 0.0f, 0.0f, lm);
            for (int j = 0; j < 4; ++j) {
                ms.mulPose(Vector3f.XP.rotationDegrees(90.0f));
                last = ms.last();
                drawVertex(last, builder, -length, -2.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.05625f, lm);
                drawVertex(last, builder, length, -2.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.05625f, lm);
                drawVertex(last, builder, length, 2.0f, 0.0f, 1.0f, 0.15625f, 0.0f, 0.0f, 0.05625f, lm);
                drawVertex(last, builder, -length, 2.0f, 0.0f, 0.0f, 0.15625f, 0.0f, 0.0f, 0.05625f, lm);
                drawVertex(last, builder, -length, -2.0f, 0.0f, color[0], color[1], color[2], 1.0f, 0.0f, 0.3125f,
                        0.0f, 0.0f, 0.05625f, lm);
                drawVertex(last, builder, length, -2.0f, 0.0f, color[0], color[1], color[2], 1.0f, 1.0f, 0.3125f,
                        0.0f, 0.0f, 0.05625f, lm);
                drawVertex(last, builder, length, 2.0f, 0.0f, color[0], color[1], color[2], 1.0f, 1.0f, 0.46875f,
                        0.0f, 0.0f, 0.05625f, lm);
                drawVertex(last, builder, -length, 2.0f, 0.0f, color[0], color[1], color[2], 1.0f, 0.0f, 0.46875f,
                        0.0f, 0.0f, 0.05625f, lm);
            }
            ms.popPose();
        } else {
            ItemRenderer itemRender = Minecraft.getInstance().getItemRenderer();
            ms.pushPose();
            ms.scale(1.7f, 1.7f, 1.7f);
            ms.mulPose(Vector3f.YP.rotationDegrees(entityspear.yRotO + (entityspear.yRot - entityspear.yRotO) * f1 - 90.0f));
            ms.mulPose(Vector3f.ZP.rotationDegrees(entityspear.xRotO + (entityspear.xRot - entityspear.xRotO) * f1 - 45.0f));
            float f15 = entityspear.shakeTime - f1;
            if (f15 > 0.0f) {
                float f16 = -MathHelper.sin(f15 * 3.0f) * f15;
                ms.mulPose(Vector3f.ZP.rotationDegrees(f16));
            }
            ms.translate(-0.35f, -0.35f, 0.0f);
            itemRender.renderStatic(getStackToRender(entityspear), TransformType.NONE, lm, OverlayTexture.NO_OVERLAY,
                    ms, bufs);
            ms.popPose();
        }
        super.render(entityspear, f, f1, ms, bufs, lm);
    }

    public ItemStack getStackToRender(EntitySpear entity) {
        return entity.getWeapon();
    }

    @Override
    @Nonnull
    @ParametersAreNonnullByDefault
    public ResourceLocation getTextureLocation(@Nonnull EntitySpear entity) {
        return WeaponModResources.Entity.SPEAR;
    }

}
