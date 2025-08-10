/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.renderer.item.properties.numeric;

import javax.annotation.Nullable;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public abstract class NeedleDirectionHelper {
    private final boolean wobble;

    protected NeedleDirectionHelper(boolean $$0) {
        this.wobble = $$0;
    }

    public float get(ItemStack $$0, @Nullable ClientLevel $$1, @Nullable LivingEntity $$2, int $$3) {
        Level level;
        Entity $$4;
        Entity entity = $$4 = $$2 != null ? $$2 : $$0.getEntityRepresentation();
        if ($$4 == null) {
            return 0.0f;
        }
        if ($$1 == null && (level = $$4.level()) instanceof ClientLevel) {
            ClientLevel $$5;
            $$1 = $$5 = (ClientLevel)level;
        }
        if ($$1 == null) {
            return 0.0f;
        }
        return this.calculate($$0, $$1, $$3, $$4);
    }

    protected abstract float calculate(ItemStack var1, ClientLevel var2, int var3, Entity var4);

    protected boolean wobble() {
        return this.wobble;
    }

    protected Wobbler newWobbler(float $$0) {
        return this.wobble ? NeedleDirectionHelper.standardWobbler($$0) : NeedleDirectionHelper.nonWobbler();
    }

    public static Wobbler standardWobbler(final float $$0) {
        return new Wobbler(){
            private float rotation;
            private float deltaRotation;
            private long lastUpdateTick;

            @Override
            public float rotation() {
                return this.rotation;
            }

            @Override
            public boolean shouldUpdate(long $$02) {
                return this.lastUpdateTick != $$02;
            }

            @Override
            public void update(long $$02, float $$1) {
                this.lastUpdateTick = $$02;
                float $$2 = Mth.positiveModulo($$1 - this.rotation + 0.5f, 1.0f) - 0.5f;
                this.deltaRotation += $$2 * 0.1f;
                this.deltaRotation *= $$0;
                this.rotation = Mth.positiveModulo(this.rotation + this.deltaRotation, 1.0f);
            }
        };
    }

    public static Wobbler nonWobbler() {
        return new Wobbler(){
            private float targetValue;

            @Override
            public float rotation() {
                return this.targetValue;
            }

            @Override
            public boolean shouldUpdate(long $$0) {
                return true;
            }

            @Override
            public void update(long $$0, float $$1) {
                this.targetValue = $$1;
            }
        };
    }

    public static interface Wobbler {
        public float rotation();

        public boolean shouldUpdate(long var1);

        public void update(long var1, float var3);
    }
}

