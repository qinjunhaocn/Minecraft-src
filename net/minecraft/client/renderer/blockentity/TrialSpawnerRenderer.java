/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.SpawnerRenderer;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.TrialSpawnerBlockEntity;
import net.minecraft.world.level.block.entity.trialspawner.TrialSpawner;
import net.minecraft.world.level.block.entity.trialspawner.TrialSpawnerStateData;
import net.minecraft.world.phys.Vec3;

public class TrialSpawnerRenderer
implements BlockEntityRenderer<TrialSpawnerBlockEntity> {
    private final EntityRenderDispatcher entityRenderer;

    public TrialSpawnerRenderer(BlockEntityRendererProvider.Context $$0) {
        this.entityRenderer = $$0.getEntityRenderer();
    }

    @Override
    public void render(TrialSpawnerBlockEntity $$0, float $$1, PoseStack $$2, MultiBufferSource $$3, int $$4, int $$5, Vec3 $$6) {
        Level $$7 = $$0.getLevel();
        if ($$7 == null) {
            return;
        }
        TrialSpawner $$8 = $$0.getTrialSpawner();
        TrialSpawnerStateData $$9 = $$8.getStateData();
        Entity $$10 = $$9.getOrCreateDisplayEntity($$8, $$7, $$8.getState());
        if ($$10 != null) {
            SpawnerRenderer.renderEntityInSpawner($$1, $$2, $$3, $$4, $$10, this.entityRenderer, $$9.getOSpin(), $$9.getSpin());
        }
    }
}

