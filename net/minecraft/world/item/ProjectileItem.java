/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.item;

import java.util.OptionalInt;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.phys.Vec3;

public interface ProjectileItem {
    public Projectile asProjectile(Level var1, Position var2, ItemStack var3, Direction var4);

    default public DispenseConfig createDispenseConfig() {
        return DispenseConfig.DEFAULT;
    }

    default public void shoot(Projectile $$0, double $$1, double $$2, double $$3, float $$4, float $$5) {
        $$0.shoot($$1, $$2, $$3, $$4, $$5);
    }

    public record DispenseConfig(PositionFunction positionFunction, float uncertainty, float power, OptionalInt overrideDispenseEvent) {
        public static final DispenseConfig DEFAULT = DispenseConfig.builder().build();

        public static Builder builder() {
            return new Builder();
        }

        public static class Builder {
            private PositionFunction positionFunction = ($$0, $$1) -> DispenserBlock.getDispensePosition($$0, 0.7, new Vec3(0.0, 0.1, 0.0));
            private float uncertainty = 6.0f;
            private float power = 1.1f;
            private OptionalInt overrideDispenseEvent = OptionalInt.empty();

            public Builder positionFunction(PositionFunction $$0) {
                this.positionFunction = $$0;
                return this;
            }

            public Builder uncertainty(float $$0) {
                this.uncertainty = $$0;
                return this;
            }

            public Builder power(float $$0) {
                this.power = $$0;
                return this;
            }

            public Builder overrideDispenseEvent(int $$0) {
                this.overrideDispenseEvent = OptionalInt.of($$0);
                return this;
            }

            public DispenseConfig build() {
                return new DispenseConfig(this.positionFunction, this.uncertainty, this.power, this.overrideDispenseEvent);
            }
        }
    }

    @FunctionalInterface
    public static interface PositionFunction {
        public Position getDispensePosition(BlockSource var1, Direction var2);
    }
}

