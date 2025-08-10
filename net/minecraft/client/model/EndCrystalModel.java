/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.joml.Quaternionf
 */
package net.minecraft.client.model;

import com.mojang.math.Axis;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.entity.EndCrystalRenderer;
import net.minecraft.client.renderer.entity.state.EndCrystalRenderState;
import org.joml.Quaternionf;

public class EndCrystalModel
extends EntityModel<EndCrystalRenderState> {
    private static final String OUTER_GLASS = "outer_glass";
    private static final String INNER_GLASS = "inner_glass";
    private static final String BASE = "base";
    private static final float SIN_45 = (float)Math.sin(0.7853981633974483);
    public final ModelPart base;
    public final ModelPart outerGlass;
    public final ModelPart innerGlass;
    public final ModelPart cube;

    public EndCrystalModel(ModelPart $$0) {
        super($$0);
        this.base = $$0.getChild(BASE);
        this.outerGlass = $$0.getChild(OUTER_GLASS);
        this.innerGlass = this.outerGlass.getChild(INNER_GLASS);
        this.cube = this.innerGlass.getChild("cube");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition $$0 = new MeshDefinition();
        PartDefinition $$1 = $$0.getRoot();
        float $$2 = 0.875f;
        CubeListBuilder $$3 = CubeListBuilder.create().texOffs(0, 0).addBox(-4.0f, -4.0f, -4.0f, 8.0f, 8.0f, 8.0f);
        PartDefinition $$4 = $$1.addOrReplaceChild(OUTER_GLASS, $$3, PartPose.offset(0.0f, 24.0f, 0.0f));
        PartDefinition $$5 = $$4.addOrReplaceChild(INNER_GLASS, $$3, PartPose.ZERO.withScale(0.875f));
        $$5.addOrReplaceChild("cube", CubeListBuilder.create().texOffs(32, 0).addBox(-4.0f, -4.0f, -4.0f, 8.0f, 8.0f, 8.0f), PartPose.ZERO.withScale(0.765625f));
        $$1.addOrReplaceChild(BASE, CubeListBuilder.create().texOffs(0, 16).addBox(-6.0f, 0.0f, -6.0f, 12.0f, 4.0f, 12.0f), PartPose.ZERO);
        return LayerDefinition.create($$0, 64, 32);
    }

    @Override
    public void setupAnim(EndCrystalRenderState $$0) {
        super.setupAnim($$0);
        this.base.visible = $$0.showsBottom;
        float $$1 = $$0.ageInTicks * 3.0f;
        float $$2 = EndCrystalRenderer.getY($$0.ageInTicks) * 16.0f;
        this.outerGlass.y += $$2 / 2.0f;
        this.outerGlass.rotateBy(Axis.YP.rotationDegrees($$1).rotateAxis(1.0471976f, SIN_45, 0.0f, SIN_45));
        this.innerGlass.rotateBy(new Quaternionf().setAngleAxis(1.0471976f, SIN_45, 0.0f, SIN_45).rotateY($$1 * ((float)Math.PI / 180)));
        this.cube.rotateBy(new Quaternionf().setAngleAxis(1.0471976f, SIN_45, 0.0f, SIN_45).rotateY($$1 * ((float)Math.PI / 180)));
    }
}

