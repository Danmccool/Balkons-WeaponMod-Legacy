package ckathode.weaponmod.render;

import ckathode.weaponmod.entity.EntityDummy;
import net.minecraft.client.renderer.model.ModelRenderer;

public class ModelDummy extends WMModel<EntityDummy> {
    public final ModelRenderer armLeft;
    public final ModelRenderer armRight;
    public final ModelRenderer body;
    public final ModelRenderer head;
    public final ModelRenderer stick;
    public final ModelRenderer inside;

    public ModelDummy() {
        armLeft = new ModelRenderer(this, 0, 24);
        armLeft.addBox(0.0f, 0.0f, 0.0f, 10, 4, 4);
        armLeft.setPos(6.0f, 18.0f, -2.0f);
        armRight = new ModelRenderer(this, 0, 24);
        armRight.addBox(-10.0f, 0.0f, 0.0f, 10, 4, 4);
        armRight.setPos(-6.0f, 18.0f, -2.0f);
        body = new ModelRenderer(this, 40, 0);
        body.addBox(0.0f, 0.0f, 0.0f, 6, 8, 6, 3.0f);
        body.setPos(-3.0f, 11.0f, -3.0f);
        inside = new ModelRenderer(this, 40, 14);
        inside.addBox(0.0f, 0.0f, 0.0f, 6, 8, 6, 2.0f);
        inside.setPos(-3.0f, 11.0f, -3.0f);
        head = new ModelRenderer(this, 0, 0);
        head.addBox(-5.0f, 0.0f, -5.0f, 6, 6, 6, 2.0f);
        head.setPos(1.5f, 25.0f, 1.5f);
        stick = new ModelRenderer(this, 24, 0);
        stick.addBox(0.0f, 0.0f, 0.0f, 4, 10, 4);
        stick.setPos(-2.0f, 0.0f, -2.0f);
    }

}
