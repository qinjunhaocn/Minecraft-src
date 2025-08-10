/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;

public class DripParticle
extends TextureSheetParticle {
    private final Fluid type;
    protected boolean isGlowing;

    DripParticle(ClientLevel $$0, double $$1, double $$2, double $$3, Fluid $$4) {
        super($$0, $$1, $$2, $$3);
        this.setSize(0.01f, 0.01f);
        this.gravity = 0.06f;
        this.type = $$4;
    }

    protected Fluid getType() {
        return this.type;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    @Override
    public int getLightColor(float $$0) {
        if (this.isGlowing) {
            return 240;
        }
        return super.getLightColor($$0);
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        this.preMoveUpdate();
        if (this.removed) {
            return;
        }
        this.yd -= (double)this.gravity;
        this.move(this.xd, this.yd, this.zd);
        this.postMoveUpdate();
        if (this.removed) {
            return;
        }
        this.xd *= (double)0.98f;
        this.yd *= (double)0.98f;
        this.zd *= (double)0.98f;
        if (this.type == Fluids.EMPTY) {
            return;
        }
        BlockPos $$0 = BlockPos.containing(this.x, this.y, this.z);
        FluidState $$1 = this.level.getFluidState($$0);
        if ($$1.getType() == this.type && this.y < (double)((float)$$0.getY() + $$1.getHeight(this.level, $$0))) {
            this.remove();
        }
    }

    protected void preMoveUpdate() {
        if (this.lifetime-- <= 0) {
            this.remove();
        }
    }

    protected void postMoveUpdate() {
    }

    public static TextureSheetParticle createWaterHangParticle(SimpleParticleType $$0, ClientLevel $$1, double $$2, double $$3, double $$4, double $$5, double $$6, double $$7) {
        DripHangParticle $$8 = new DripHangParticle($$1, $$2, $$3, $$4, Fluids.WATER, ParticleTypes.FALLING_WATER);
        $$8.setColor(0.2f, 0.3f, 1.0f);
        return $$8;
    }

    public static TextureSheetParticle createWaterFallParticle(SimpleParticleType $$0, ClientLevel $$1, double $$2, double $$3, double $$4, double $$5, double $$6, double $$7) {
        FallAndLandParticle $$8 = new FallAndLandParticle($$1, $$2, $$3, $$4, (Fluid)Fluids.WATER, ParticleTypes.SPLASH);
        $$8.setColor(0.2f, 0.3f, 1.0f);
        return $$8;
    }

    public static TextureSheetParticle createLavaHangParticle(SimpleParticleType $$0, ClientLevel $$1, double $$2, double $$3, double $$4, double $$5, double $$6, double $$7) {
        return new CoolingDripHangParticle($$1, $$2, $$3, $$4, Fluids.LAVA, ParticleTypes.FALLING_LAVA);
    }

    public static TextureSheetParticle createLavaFallParticle(SimpleParticleType $$0, ClientLevel $$1, double $$2, double $$3, double $$4, double $$5, double $$6, double $$7) {
        FallAndLandParticle $$8 = new FallAndLandParticle($$1, $$2, $$3, $$4, (Fluid)Fluids.LAVA, ParticleTypes.LANDING_LAVA);
        $$8.setColor(1.0f, 0.2857143f, 0.083333336f);
        return $$8;
    }

    public static TextureSheetParticle createLavaLandParticle(SimpleParticleType $$0, ClientLevel $$1, double $$2, double $$3, double $$4, double $$5, double $$6, double $$7) {
        DripLandParticle $$8 = new DripLandParticle($$1, $$2, $$3, $$4, Fluids.LAVA);
        $$8.setColor(1.0f, 0.2857143f, 0.083333336f);
        return $$8;
    }

    public static TextureSheetParticle createHoneyHangParticle(SimpleParticleType $$0, ClientLevel $$1, double $$2, double $$3, double $$4, double $$5, double $$6, double $$7) {
        DripHangParticle $$8 = new DripHangParticle($$1, $$2, $$3, $$4, Fluids.EMPTY, ParticleTypes.FALLING_HONEY);
        $$8.gravity *= 0.01f;
        $$8.lifetime = 100;
        $$8.setColor(0.622f, 0.508f, 0.082f);
        return $$8;
    }

    public static TextureSheetParticle createHoneyFallParticle(SimpleParticleType $$0, ClientLevel $$1, double $$2, double $$3, double $$4, double $$5, double $$6, double $$7) {
        HoneyFallAndLandParticle $$8 = new HoneyFallAndLandParticle($$1, $$2, $$3, $$4, Fluids.EMPTY, ParticleTypes.LANDING_HONEY);
        $$8.gravity = 0.01f;
        $$8.setColor(0.582f, 0.448f, 0.082f);
        return $$8;
    }

    public static TextureSheetParticle createHoneyLandParticle(SimpleParticleType $$0, ClientLevel $$1, double $$2, double $$3, double $$4, double $$5, double $$6, double $$7) {
        DripLandParticle $$8 = new DripLandParticle($$1, $$2, $$3, $$4, Fluids.EMPTY);
        $$8.lifetime = (int)(128.0 / (Math.random() * 0.8 + 0.2));
        $$8.setColor(0.522f, 0.408f, 0.082f);
        return $$8;
    }

    public static TextureSheetParticle createDripstoneWaterHangParticle(SimpleParticleType $$0, ClientLevel $$1, double $$2, double $$3, double $$4, double $$5, double $$6, double $$7) {
        DripHangParticle $$8 = new DripHangParticle($$1, $$2, $$3, $$4, Fluids.WATER, ParticleTypes.FALLING_DRIPSTONE_WATER);
        $$8.setColor(0.2f, 0.3f, 1.0f);
        return $$8;
    }

    public static TextureSheetParticle createDripstoneWaterFallParticle(SimpleParticleType $$0, ClientLevel $$1, double $$2, double $$3, double $$4, double $$5, double $$6, double $$7) {
        DripstoneFallAndLandParticle $$8 = new DripstoneFallAndLandParticle($$1, $$2, $$3, $$4, (Fluid)Fluids.WATER, ParticleTypes.SPLASH);
        $$8.setColor(0.2f, 0.3f, 1.0f);
        return $$8;
    }

    public static TextureSheetParticle createDripstoneLavaHangParticle(SimpleParticleType $$0, ClientLevel $$1, double $$2, double $$3, double $$4, double $$5, double $$6, double $$7) {
        return new CoolingDripHangParticle($$1, $$2, $$3, $$4, Fluids.LAVA, ParticleTypes.FALLING_DRIPSTONE_LAVA);
    }

    public static TextureSheetParticle createDripstoneLavaFallParticle(SimpleParticleType $$0, ClientLevel $$1, double $$2, double $$3, double $$4, double $$5, double $$6, double $$7) {
        DripstoneFallAndLandParticle $$8 = new DripstoneFallAndLandParticle($$1, $$2, $$3, $$4, (Fluid)Fluids.LAVA, ParticleTypes.LANDING_LAVA);
        $$8.setColor(1.0f, 0.2857143f, 0.083333336f);
        return $$8;
    }

    public static TextureSheetParticle createNectarFallParticle(SimpleParticleType $$0, ClientLevel $$1, double $$2, double $$3, double $$4, double $$5, double $$6, double $$7) {
        FallingParticle $$8 = new FallingParticle($$1, $$2, $$3, $$4, Fluids.EMPTY);
        $$8.lifetime = (int)(16.0 / (Math.random() * 0.8 + 0.2));
        $$8.gravity = 0.007f;
        $$8.setColor(0.92f, 0.782f, 0.72f);
        return $$8;
    }

    public static TextureSheetParticle createSporeBlossomFallParticle(SimpleParticleType $$0, ClientLevel $$1, double $$2, double $$3, double $$4, double $$5, double $$6, double $$7) {
        int $$8 = (int)(64.0f / Mth.randomBetween($$1.getRandom(), 0.1f, 0.9f));
        FallingParticle $$9 = new FallingParticle($$1, $$2, $$3, $$4, Fluids.EMPTY, $$8);
        $$9.gravity = 0.005f;
        $$9.setColor(0.32f, 0.5f, 0.22f);
        return $$9;
    }

    public static TextureSheetParticle createObsidianTearHangParticle(SimpleParticleType $$0, ClientLevel $$1, double $$2, double $$3, double $$4, double $$5, double $$6, double $$7) {
        DripHangParticle $$8 = new DripHangParticle($$1, $$2, $$3, $$4, Fluids.EMPTY, ParticleTypes.FALLING_OBSIDIAN_TEAR);
        $$8.isGlowing = true;
        $$8.gravity *= 0.01f;
        $$8.lifetime = 100;
        $$8.setColor(0.51171875f, 0.03125f, 0.890625f);
        return $$8;
    }

    public static TextureSheetParticle createObsidianTearFallParticle(SimpleParticleType $$0, ClientLevel $$1, double $$2, double $$3, double $$4, double $$5, double $$6, double $$7) {
        FallAndLandParticle $$8 = new FallAndLandParticle($$1, $$2, $$3, $$4, Fluids.EMPTY, ParticleTypes.LANDING_OBSIDIAN_TEAR);
        $$8.isGlowing = true;
        $$8.gravity = 0.01f;
        $$8.setColor(0.51171875f, 0.03125f, 0.890625f);
        return $$8;
    }

    public static TextureSheetParticle createObsidianTearLandParticle(SimpleParticleType $$0, ClientLevel $$1, double $$2, double $$3, double $$4, double $$5, double $$6, double $$7) {
        DripLandParticle $$8 = new DripLandParticle($$1, $$2, $$3, $$4, Fluids.EMPTY);
        $$8.isGlowing = true;
        $$8.lifetime = (int)(28.0 / (Math.random() * 0.8 + 0.2));
        $$8.setColor(0.51171875f, 0.03125f, 0.890625f);
        return $$8;
    }

    static class DripHangParticle
    extends DripParticle {
        private final ParticleOptions fallingParticle;

        DripHangParticle(ClientLevel $$0, double $$1, double $$2, double $$3, Fluid $$4, ParticleOptions $$5) {
            super($$0, $$1, $$2, $$3, $$4);
            this.fallingParticle = $$5;
            this.gravity *= 0.02f;
            this.lifetime = 40;
        }

        @Override
        protected void preMoveUpdate() {
            if (this.lifetime-- <= 0) {
                this.remove();
                this.level.addParticle(this.fallingParticle, this.x, this.y, this.z, this.xd, this.yd, this.zd);
            }
        }

        @Override
        protected void postMoveUpdate() {
            this.xd *= 0.02;
            this.yd *= 0.02;
            this.zd *= 0.02;
        }
    }

    static class FallAndLandParticle
    extends FallingParticle {
        protected final ParticleOptions landParticle;

        FallAndLandParticle(ClientLevel $$0, double $$1, double $$2, double $$3, Fluid $$4, ParticleOptions $$5) {
            super($$0, $$1, $$2, $$3, $$4);
            this.landParticle = $$5;
        }

        @Override
        protected void postMoveUpdate() {
            if (this.onGround) {
                this.remove();
                this.level.addParticle(this.landParticle, this.x, this.y, this.z, 0.0, 0.0, 0.0);
            }
        }
    }

    static class CoolingDripHangParticle
    extends DripHangParticle {
        CoolingDripHangParticle(ClientLevel $$0, double $$1, double $$2, double $$3, Fluid $$4, ParticleOptions $$5) {
            super($$0, $$1, $$2, $$3, $$4, $$5);
        }

        @Override
        protected void preMoveUpdate() {
            this.rCol = 1.0f;
            this.gCol = 16.0f / (float)(40 - this.lifetime + 16);
            this.bCol = 4.0f / (float)(40 - this.lifetime + 8);
            super.preMoveUpdate();
        }
    }

    static class DripLandParticle
    extends DripParticle {
        DripLandParticle(ClientLevel $$0, double $$1, double $$2, double $$3, Fluid $$4) {
            super($$0, $$1, $$2, $$3, $$4);
            this.lifetime = (int)(16.0 / (Math.random() * 0.8 + 0.2));
        }
    }

    static class HoneyFallAndLandParticle
    extends FallAndLandParticle {
        HoneyFallAndLandParticle(ClientLevel $$0, double $$1, double $$2, double $$3, Fluid $$4, ParticleOptions $$5) {
            super($$0, $$1, $$2, $$3, $$4, $$5);
        }

        @Override
        protected void postMoveUpdate() {
            if (this.onGround) {
                this.remove();
                this.level.addParticle(this.landParticle, this.x, this.y, this.z, 0.0, 0.0, 0.0);
                float $$0 = Mth.randomBetween(this.random, 0.3f, 1.0f);
                this.level.playLocalSound(this.x, this.y, this.z, SoundEvents.BEEHIVE_DRIP, SoundSource.BLOCKS, $$0, 1.0f, false);
            }
        }
    }

    static class DripstoneFallAndLandParticle
    extends FallAndLandParticle {
        DripstoneFallAndLandParticle(ClientLevel $$0, double $$1, double $$2, double $$3, Fluid $$4, ParticleOptions $$5) {
            super($$0, $$1, $$2, $$3, $$4, $$5);
        }

        @Override
        protected void postMoveUpdate() {
            if (this.onGround) {
                this.remove();
                this.level.addParticle(this.landParticle, this.x, this.y, this.z, 0.0, 0.0, 0.0);
                SoundEvent $$0 = this.getType() == Fluids.LAVA ? SoundEvents.POINTED_DRIPSTONE_DRIP_LAVA : SoundEvents.POINTED_DRIPSTONE_DRIP_WATER;
                float $$1 = Mth.randomBetween(this.random, 0.3f, 1.0f);
                this.level.playLocalSound(this.x, this.y, this.z, $$0, SoundSource.BLOCKS, $$1, 1.0f, false);
            }
        }
    }

    static class FallingParticle
    extends DripParticle {
        FallingParticle(ClientLevel $$0, double $$1, double $$2, double $$3, Fluid $$4) {
            this($$0, $$1, $$2, $$3, $$4, (int)(64.0 / (Math.random() * 0.8 + 0.2)));
        }

        FallingParticle(ClientLevel $$0, double $$1, double $$2, double $$3, Fluid $$4, int $$5) {
            super($$0, $$1, $$2, $$3, $$4);
            this.lifetime = $$5;
        }

        @Override
        protected void postMoveUpdate() {
            if (this.onGround) {
                this.remove();
            }
        }
    }
}

