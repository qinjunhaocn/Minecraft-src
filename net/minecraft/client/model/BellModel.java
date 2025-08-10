/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.model;

import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BellBlockEntity;

public class BellModel
extends Model {
    private static final String BELL_BODY = "bell_body";
    private final ModelPart bellBody;

    public BellModel(ModelPart $$0) {
        super($$0, RenderType::entitySolid);
        this.bellBody = $$0.getChild(BELL_BODY);
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition $$0 = new MeshDefinition();
        PartDefinition $$1 = $$0.getRoot();
        PartDefinition $$2 = $$1.addOrReplaceChild(BELL_BODY, CubeListBuilder.create().texOffs(0, 0).addBox(-3.0f, -6.0f, -3.0f, 6.0f, 7.0f, 6.0f), PartPose.offset(8.0f, 12.0f, 8.0f));
        $$2.addOrReplaceChild("bell_base", CubeListBuilder.create().texOffs(0, 13).addBox(4.0f, 4.0f, 4.0f, 8.0f, 2.0f, 8.0f), PartPose.offset(-8.0f, -12.0f, -8.0f));
        return LayerDefinition.create($$0, 32, 32);
    }

    public void setupAnim(BellBlockEntity $$0, float $$1) {
        float $$2 = (float)$$0.ticks + $$1;
        float $$3 = 0.0f;
        float $$4 = 0.0f;
        if ($$0.shaking) {
            float $$5 = Mth.sin($$2 / (float)Math.PI) / (4.0f + $$2 / 3.0f);
            if ($$0.clickDirection == Direction.NORTH) {
                $$3 = -$$5;
            } else if ($$0.clickDirection == Direction.SOUTH) {
                $$3 = $$5;
            } else if ($$0.clickDirection == Direction.EAST) {
                $$4 = -$$5;
            } else if ($$0.clickDirection == Direction.WEST) {
                $$4 = $$5;
            }
        }
        this.bellBody.xRot = $$3;
        this.bellBody.zRot = $$4;
    }
}

