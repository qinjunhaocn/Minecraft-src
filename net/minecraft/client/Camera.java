/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.joml.Quaternionf
 *  org.joml.Quaternionfc
 *  org.joml.Vector3f
 *  org.joml.Vector3fc
 */
package net.minecraft.client;

import java.util.Arrays;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.vehicle.Minecart;
import net.minecraft.world.entity.vehicle.NewMinecartBehavior;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.FogType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.waypoints.TrackedWaypoint;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public class Camera
implements TrackedWaypoint.Camera {
    private static final float DEFAULT_CAMERA_DISTANCE = 4.0f;
    private static final Vector3f FORWARDS = new Vector3f(0.0f, 0.0f, -1.0f);
    private static final Vector3f UP = new Vector3f(0.0f, 1.0f, 0.0f);
    private static final Vector3f LEFT = new Vector3f(-1.0f, 0.0f, 0.0f);
    private boolean initialized;
    private BlockGetter level;
    private Entity entity;
    private Vec3 position = Vec3.ZERO;
    private final BlockPos.MutableBlockPos blockPosition = new BlockPos.MutableBlockPos();
    private final Vector3f forwards = new Vector3f((Vector3fc)FORWARDS);
    private final Vector3f up = new Vector3f((Vector3fc)UP);
    private final Vector3f left = new Vector3f((Vector3fc)LEFT);
    private float xRot;
    private float yRot;
    private final Quaternionf rotation = new Quaternionf();
    private boolean detached;
    private float eyeHeight;
    private float eyeHeightOld;
    private float partialTickTime;
    public static final float FOG_DISTANCE_SCALE = 0.083333336f;

    public void setup(BlockGetter $$0, Entity $$1, boolean $$2, boolean $$3, float $$4) {
        NewMinecartBehavior $$6;
        Minecart $$5;
        Object object;
        this.initialized = true;
        this.level = $$0;
        this.entity = $$1;
        this.detached = $$2;
        this.partialTickTime = $$4;
        if ($$1.isPassenger() && (object = $$1.getVehicle()) instanceof Minecart && (object = ($$5 = (Minecart)object).getBehavior()) instanceof NewMinecartBehavior && ($$6 = (NewMinecartBehavior)object).cartHasPosRotLerp()) {
            Vec3 $$7 = $$5.getPassengerRidingPosition($$1).subtract($$5.position()).subtract($$1.getVehicleAttachmentPoint($$5)).add(new Vec3(0.0, Mth.lerp($$4, this.eyeHeightOld, this.eyeHeight), 0.0));
            this.setRotation($$1.getViewYRot($$4), $$1.getViewXRot($$4));
            this.setPosition($$6.getCartLerpPosition($$4).add($$7));
        } else {
            this.setRotation($$1.getViewYRot($$4), $$1.getViewXRot($$4));
            this.setPosition(Mth.lerp((double)$$4, $$1.xo, $$1.getX()), Mth.lerp((double)$$4, $$1.yo, $$1.getY()) + (double)Mth.lerp($$4, this.eyeHeightOld, this.eyeHeight), Mth.lerp((double)$$4, $$1.zo, $$1.getZ()));
        }
        if ($$2) {
            Entity entity;
            if ($$3) {
                this.setRotation(this.yRot + 180.0f, -this.xRot);
            }
            float $$8 = 4.0f;
            float $$9 = 1.0f;
            if ($$1 instanceof LivingEntity) {
                LivingEntity $$10 = (LivingEntity)$$1;
                $$9 = $$10.getScale();
                $$8 = (float)$$10.getAttributeValue(Attributes.CAMERA_DISTANCE);
            }
            float $$11 = $$9;
            float $$12 = $$8;
            if ($$1.isPassenger() && (entity = $$1.getVehicle()) instanceof LivingEntity) {
                LivingEntity $$13 = (LivingEntity)entity;
                $$11 = $$13.getScale();
                $$12 = (float)$$13.getAttributeValue(Attributes.CAMERA_DISTANCE);
            }
            this.move(-this.getMaxZoom(Math.max($$9 * $$8, $$11 * $$12)), 0.0f, 0.0f);
        } else if ($$1 instanceof LivingEntity && ((LivingEntity)$$1).isSleeping()) {
            Direction $$14 = ((LivingEntity)$$1).getBedOrientation();
            this.setRotation($$14 != null ? $$14.toYRot() - 180.0f : 0.0f, 0.0f);
            this.move(0.0f, 0.3f, 0.0f);
        }
    }

    public void tick() {
        if (this.entity != null) {
            this.eyeHeightOld = this.eyeHeight;
            this.eyeHeight += (this.entity.getEyeHeight() - this.eyeHeight) * 0.5f;
        }
    }

    private float getMaxZoom(float $$0) {
        float $$1 = 0.1f;
        for (int $$2 = 0; $$2 < 8; ++$$2) {
            float $$9;
            Vec3 $$7;
            float $$3 = ($$2 & 1) * 2 - 1;
            float $$4 = ($$2 >> 1 & 1) * 2 - 1;
            float $$5 = ($$2 >> 2 & 1) * 2 - 1;
            Vec3 $$6 = this.position.add($$3 * 0.1f, $$4 * 0.1f, $$5 * 0.1f);
            BlockHitResult $$8 = this.level.clip(new ClipContext($$6, $$7 = $$6.add(new Vec3(this.forwards).scale(-$$0)), ClipContext.Block.VISUAL, ClipContext.Fluid.NONE, this.entity));
            if (((HitResult)$$8).getType() == HitResult.Type.MISS || !(($$9 = (float)$$8.getLocation().distanceToSqr(this.position)) < Mth.square($$0))) continue;
            $$0 = Mth.sqrt($$9);
        }
        return $$0;
    }

    protected void move(float $$0, float $$1, float $$2) {
        Vector3f $$3 = new Vector3f($$2, $$1, -$$0).rotate((Quaternionfc)this.rotation);
        this.setPosition(new Vec3(this.position.x + (double)$$3.x, this.position.y + (double)$$3.y, this.position.z + (double)$$3.z));
    }

    protected void setRotation(float $$0, float $$1) {
        this.xRot = $$1;
        this.yRot = $$0;
        this.rotation.rotationYXZ((float)Math.PI - $$0 * ((float)Math.PI / 180), -$$1 * ((float)Math.PI / 180), 0.0f);
        FORWARDS.rotate((Quaternionfc)this.rotation, this.forwards);
        UP.rotate((Quaternionfc)this.rotation, this.up);
        LEFT.rotate((Quaternionfc)this.rotation, this.left);
    }

    protected void setPosition(double $$0, double $$1, double $$2) {
        this.setPosition(new Vec3($$0, $$1, $$2));
    }

    protected void setPosition(Vec3 $$0) {
        this.position = $$0;
        this.blockPosition.set($$0.x, $$0.y, $$0.z);
    }

    public Vec3 getPosition() {
        return this.position;
    }

    public BlockPos getBlockPosition() {
        return this.blockPosition;
    }

    public float getXRot() {
        return this.xRot;
    }

    public float getYRot() {
        return this.yRot;
    }

    public Quaternionf rotation() {
        return this.rotation;
    }

    public Entity getEntity() {
        return this.entity;
    }

    public boolean isInitialized() {
        return this.initialized;
    }

    public boolean isDetached() {
        return this.detached;
    }

    public NearPlane getNearPlane() {
        Minecraft $$0 = Minecraft.getInstance();
        double $$1 = (double)$$0.getWindow().getWidth() / (double)$$0.getWindow().getHeight();
        double $$2 = Math.tan((double)((float)$$0.options.fov().get().intValue() * ((float)Math.PI / 180)) / 2.0) * (double)0.05f;
        double $$3 = $$2 * $$1;
        Vec3 $$4 = new Vec3(this.forwards).scale(0.05f);
        Vec3 $$5 = new Vec3(this.left).scale($$3);
        Vec3 $$6 = new Vec3(this.up).scale($$2);
        return new NearPlane($$4, $$5, $$6);
    }

    public FogType getFluidInCamera() {
        if (!this.initialized) {
            return FogType.NONE;
        }
        FluidState $$0 = this.level.getFluidState(this.blockPosition);
        if ($$0.is(FluidTags.WATER) && this.position.y < (double)((float)this.blockPosition.getY() + $$0.getHeight(this.level, this.blockPosition))) {
            return FogType.WATER;
        }
        NearPlane $$1 = this.getNearPlane();
        List<Vec3> $$2 = Arrays.asList($$1.forward, $$1.getTopLeft(), $$1.getTopRight(), $$1.getBottomLeft(), $$1.getBottomRight());
        for (Vec3 $$3 : $$2) {
            Vec3 $$4 = this.position.add($$3);
            BlockPos $$5 = BlockPos.containing($$4);
            FluidState $$6 = this.level.getFluidState($$5);
            if ($$6.is(FluidTags.LAVA)) {
                if (!($$4.y <= (double)($$6.getHeight(this.level, $$5) + (float)$$5.getY()))) continue;
                return FogType.LAVA;
            }
            BlockState $$7 = this.level.getBlockState($$5);
            if (!$$7.is(Blocks.POWDER_SNOW)) continue;
            return FogType.POWDER_SNOW;
        }
        return FogType.NONE;
    }

    public final Vector3f getLookVector() {
        return this.forwards;
    }

    public final Vector3f getUpVector() {
        return this.up;
    }

    public final Vector3f getLeftVector() {
        return this.left;
    }

    public void reset() {
        this.level = null;
        this.entity = null;
        this.initialized = false;
    }

    public float getPartialTickTime() {
        return this.partialTickTime;
    }

    @Override
    public float yaw() {
        return Mth.wrapDegrees(this.getYRot());
    }

    @Override
    public Vec3 position() {
        return this.getPosition();
    }

    public static class NearPlane {
        final Vec3 forward;
        private final Vec3 left;
        private final Vec3 up;

        NearPlane(Vec3 $$0, Vec3 $$1, Vec3 $$2) {
            this.forward = $$0;
            this.left = $$1;
            this.up = $$2;
        }

        public Vec3 getTopLeft() {
            return this.forward.add(this.up).add(this.left);
        }

        public Vec3 getTopRight() {
            return this.forward.add(this.up).subtract(this.left);
        }

        public Vec3 getBottomLeft() {
            return this.forward.subtract(this.up).add(this.left);
        }

        public Vec3 getBottomRight() {
            return this.forward.subtract(this.up).subtract(this.left);
        }

        public Vec3 getPointOnPlane(float $$0, float $$1) {
            return this.forward.add(this.up.scale($$1)).subtract(this.left.scale($$0));
        }
    }
}

