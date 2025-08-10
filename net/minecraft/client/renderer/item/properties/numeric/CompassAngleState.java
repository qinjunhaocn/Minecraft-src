/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.client.renderer.item.properties.numeric;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import javax.annotation.Nullable;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.numeric.NeedleDirectionHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.LodestoneTracker;
import net.minecraft.world.phys.Vec3;

public class CompassAngleState
extends NeedleDirectionHelper {
    public static final MapCodec<CompassAngleState> MAP_CODEC = RecordCodecBuilder.mapCodec($$0 -> $$0.group((App)Codec.BOOL.optionalFieldOf("wobble", (Object)true).forGetter(NeedleDirectionHelper::wobble), (App)CompassTarget.CODEC.fieldOf("target").forGetter(CompassAngleState::target)).apply((Applicative)$$0, CompassAngleState::new));
    private final NeedleDirectionHelper.Wobbler wobbler;
    private final NeedleDirectionHelper.Wobbler noTargetWobbler;
    private final CompassTarget compassTarget;
    private final RandomSource random = RandomSource.create();

    public CompassAngleState(boolean $$0, CompassTarget $$1) {
        super($$0);
        this.wobbler = this.newWobbler(0.8f);
        this.noTargetWobbler = this.newWobbler(0.8f);
        this.compassTarget = $$1;
    }

    @Override
    protected float calculate(ItemStack $$0, ClientLevel $$1, int $$2, Entity $$3) {
        GlobalPos $$4 = this.compassTarget.get($$1, $$0, $$3);
        long $$5 = $$1.getGameTime();
        if (!CompassAngleState.isValidCompassTargetPos($$3, $$4)) {
            return this.getRandomlySpinningRotation($$2, $$5);
        }
        return this.getRotationTowardsCompassTarget($$3, $$5, $$4.pos());
    }

    private float getRandomlySpinningRotation(int $$0, long $$1) {
        if (this.noTargetWobbler.shouldUpdate($$1)) {
            this.noTargetWobbler.update($$1, this.random.nextFloat());
        }
        float $$2 = this.noTargetWobbler.rotation() + (float)CompassAngleState.hash($$0) / 2.14748365E9f;
        return Mth.positiveModulo($$2, 1.0f);
    }

    private float getRotationTowardsCompassTarget(Entity $$0, long $$1, BlockPos $$2) {
        float $$7;
        Player $$5;
        float $$3 = (float)CompassAngleState.getAngleFromEntityToPos($$0, $$2);
        float $$4 = CompassAngleState.getWrappedVisualRotationY($$0);
        if ($$0 instanceof Player && ($$5 = (Player)$$0).isLocalPlayer() && $$5.level().tickRateManager().runsNormally()) {
            if (this.wobbler.shouldUpdate($$1)) {
                this.wobbler.update($$1, 0.5f - ($$4 - 0.25f));
            }
            float $$6 = $$3 + this.wobbler.rotation();
        } else {
            $$7 = 0.5f - ($$4 - 0.25f - $$3);
        }
        return Mth.positiveModulo($$7, 1.0f);
    }

    private static boolean isValidCompassTargetPos(Entity $$0, @Nullable GlobalPos $$1) {
        return $$1 != null && $$1.dimension() == $$0.level().dimension() && !($$1.pos().distToCenterSqr($$0.position()) < (double)1.0E-5f);
    }

    private static double getAngleFromEntityToPos(Entity $$0, BlockPos $$1) {
        Vec3 $$2 = Vec3.atCenterOf($$1);
        return Math.atan2($$2.z() - $$0.getZ(), $$2.x() - $$0.getX()) / 6.2831854820251465;
    }

    private static float getWrappedVisualRotationY(Entity $$0) {
        return Mth.positiveModulo($$0.getVisualRotationYInDegrees() / 360.0f, 1.0f);
    }

    private static int hash(int $$0) {
        return $$0 * 1327217883;
    }

    protected CompassTarget target() {
        return this.compassTarget;
    }

    public static abstract sealed class CompassTarget
    extends Enum<CompassTarget>
    implements StringRepresentable {
        public static final /* enum */ CompassTarget NONE = new CompassTarget("none"){

            @Override
            @Nullable
            public GlobalPos get(ClientLevel $$0, ItemStack $$1, Entity $$2) {
                return null;
            }
        };
        public static final /* enum */ CompassTarget LODESTONE = new CompassTarget("lodestone"){

            @Override
            @Nullable
            public GlobalPos get(ClientLevel $$0, ItemStack $$1, Entity $$2) {
                LodestoneTracker $$3 = $$1.get(DataComponents.LODESTONE_TRACKER);
                return $$3 != null ? (GlobalPos)$$3.target().orElse(null) : null;
            }
        };
        public static final /* enum */ CompassTarget SPAWN = new CompassTarget("spawn"){

            @Override
            public GlobalPos get(ClientLevel $$0, ItemStack $$1, Entity $$2) {
                return GlobalPos.of($$0.dimension(), $$0.getSharedSpawnPos());
            }
        };
        public static final /* enum */ CompassTarget RECOVERY = new CompassTarget("recovery"){

            @Override
            @Nullable
            public GlobalPos get(ClientLevel $$0, ItemStack $$1, Entity $$2) {
                GlobalPos globalPos;
                if ($$2 instanceof Player) {
                    Player $$3 = (Player)$$2;
                    globalPos = $$3.getLastDeathLocation().orElse(null);
                } else {
                    globalPos = null;
                }
                return globalPos;
            }
        };
        public static final Codec<CompassTarget> CODEC;
        private final String name;
        private static final /* synthetic */ CompassTarget[] $VALUES;

        public static CompassTarget[] values() {
            return (CompassTarget[])$VALUES.clone();
        }

        public static CompassTarget valueOf(String $$0) {
            return Enum.valueOf(CompassTarget.class, $$0);
        }

        CompassTarget(String $$0) {
            this.name = $$0;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }

        @Nullable
        abstract GlobalPos get(ClientLevel var1, ItemStack var2, Entity var3);

        private static /* synthetic */ CompassTarget[] a() {
            return new CompassTarget[]{NONE, LODESTONE, SPAWN, RECOVERY};
        }

        static {
            $VALUES = CompassTarget.a();
            CODEC = StringRepresentable.fromEnum(CompassTarget::values);
        }
    }
}

