/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.particle;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.phys.Vec3;

public class ItemPickupParticle
extends Particle {
    private static final int LIFE_TIME = 3;
    private final Entity itemEntity;
    private final Entity target;
    private int life;
    private final EntityRenderDispatcher entityRenderDispatcher;
    private double targetX;
    private double targetY;
    private double targetZ;
    private double targetXOld;
    private double targetYOld;
    private double targetZOld;

    public ItemPickupParticle(EntityRenderDispatcher $$0, ClientLevel $$1, Entity $$2, Entity $$3) {
        this($$0, $$1, $$2, $$3, $$2.getDeltaMovement());
    }

    private ItemPickupParticle(EntityRenderDispatcher $$0, ClientLevel $$1, Entity $$2, Entity $$3, Vec3 $$4) {
        super($$1, $$2.getX(), $$2.getY(), $$2.getZ(), $$4.x, $$4.y, $$4.z);
        this.itemEntity = this.getSafeCopy($$2);
        this.target = $$3;
        this.entityRenderDispatcher = $$0;
        this.updatePosition();
        this.saveOldPosition();
    }

    private Entity getSafeCopy(Entity $$0) {
        if (!($$0 instanceof ItemEntity)) {
            return $$0;
        }
        return ((ItemEntity)$$0).copy();
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.CUSTOM;
    }

    @Override
    public void renderCustom(PoseStack $$0, MultiBufferSource $$1, Camera $$2, float $$3) {
        float $$4 = ((float)this.life + $$3) / 3.0f;
        $$4 *= $$4;
        double $$5 = Mth.lerp((double)$$3, this.targetXOld, this.targetX);
        double $$6 = Mth.lerp((double)$$3, this.targetYOld, this.targetY);
        double $$7 = Mth.lerp((double)$$3, this.targetZOld, this.targetZ);
        double $$8 = Mth.lerp((double)$$4, this.itemEntity.getX(), $$5);
        double $$9 = Mth.lerp((double)$$4, this.itemEntity.getY(), $$6);
        double $$10 = Mth.lerp((double)$$4, this.itemEntity.getZ(), $$7);
        Vec3 $$11 = $$2.getPosition();
        this.entityRenderDispatcher.render(this.itemEntity, $$8 - $$11.x(), $$9 - $$11.y(), $$10 - $$11.z(), $$3, new PoseStack(), $$1, this.entityRenderDispatcher.getPackedLightCoords(this.itemEntity, $$3));
    }

    @Override
    public void render(VertexConsumer $$0, Camera $$1, float $$2) {
    }

    @Override
    public void tick() {
        ++this.life;
        if (this.life == 3) {
            this.remove();
        }
        this.saveOldPosition();
        this.updatePosition();
    }

    private void updatePosition() {
        this.targetX = this.target.getX();
        this.targetY = (this.target.getY() + this.target.getEyeY()) / 2.0;
        this.targetZ = this.target.getZ();
    }

    private void saveOldPosition() {
        this.targetXOld = this.targetX;
        this.targetYOld = this.targetY;
        this.targetZOld = this.targetZ;
    }
}

