/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.joml.Quaternionf
 */
package net.minecraft.client.model;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
import org.joml.Quaternionf;

public class PlayerCapeModel<T extends PlayerRenderState>
extends HumanoidModel<T> {
    private static final String CAPE = "cape";
    private final ModelPart cape;

    public PlayerCapeModel(ModelPart $$0) {
        super($$0);
        this.cape = this.body.getChild(CAPE);
    }

    public static LayerDefinition createCapeLayer() {
        MeshDefinition $$0 = HumanoidModel.createMesh(CubeDeformation.NONE, 0.0f);
        PartDefinition $$1 = $$0.getRoot();
        PartDefinition $$2 = $$1.clearChild("head");
        $$2.clearChild("hat");
        PartDefinition $$3 = $$1.clearChild("body");
        $$1.clearChild("left_arm");
        $$1.clearChild("right_arm");
        $$1.clearChild("left_leg");
        $$1.clearChild("right_leg");
        $$3.addOrReplaceChild(CAPE, CubeListBuilder.create().texOffs(0, 0).addBox(-5.0f, 0.0f, -1.0f, 10.0f, 16.0f, 1.0f, CubeDeformation.NONE, 1.0f, 0.5f), PartPose.offsetAndRotation(0.0f, 0.0f, 2.0f, 0.0f, (float)Math.PI, 0.0f));
        return LayerDefinition.create($$0, 64, 64);
    }

    @Override
    public void setupAnim(T $$0) {
        super.setupAnim($$0);
        this.cape.rotateBy(new Quaternionf().rotateY((float)(-Math.PI)).rotateX((6.0f + ((PlayerRenderState)$$0).capeLean / 2.0f + ((PlayerRenderState)$$0).capeFlap) * ((float)Math.PI / 180)).rotateZ(((PlayerRenderState)$$0).capeLean2 / 2.0f * ((float)Math.PI / 180)).rotateY((180.0f - ((PlayerRenderState)$$0).capeLean2 / 2.0f) * ((float)Math.PI / 180)));
    }
}

