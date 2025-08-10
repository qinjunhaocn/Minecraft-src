/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  java.lang.Record
 *  java.lang.runtime.ObjectMethods
 */
package net.minecraft.world.item;

import com.google.common.collect.Maps;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.Iterator;
import java.util.Map;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.UseCooldown;

public class ItemCooldowns {
    private final Map<ResourceLocation, CooldownInstance> cooldowns = Maps.newHashMap();
    private int tickCount;

    public boolean isOnCooldown(ItemStack $$0) {
        return this.getCooldownPercent($$0, 0.0f) > 0.0f;
    }

    public float getCooldownPercent(ItemStack $$0, float $$1) {
        ResourceLocation $$2 = this.getCooldownGroup($$0);
        CooldownInstance $$3 = this.cooldowns.get($$2);
        if ($$3 != null) {
            float $$4 = $$3.endTime - $$3.startTime;
            float $$5 = (float)$$3.endTime - ((float)this.tickCount + $$1);
            return Mth.clamp($$5 / $$4, 0.0f, 1.0f);
        }
        return 0.0f;
    }

    public void tick() {
        ++this.tickCount;
        if (!this.cooldowns.isEmpty()) {
            Iterator<Map.Entry<ResourceLocation, CooldownInstance>> $$0 = this.cooldowns.entrySet().iterator();
            while ($$0.hasNext()) {
                Map.Entry<ResourceLocation, CooldownInstance> $$1 = $$0.next();
                if ($$1.getValue().endTime > this.tickCount) continue;
                $$0.remove();
                this.onCooldownEnded($$1.getKey());
            }
        }
    }

    public ResourceLocation getCooldownGroup(ItemStack $$0) {
        UseCooldown $$1 = $$0.get(DataComponents.USE_COOLDOWN);
        ResourceLocation $$2 = BuiltInRegistries.ITEM.getKey($$0.getItem());
        if ($$1 == null) {
            return $$2;
        }
        return $$1.cooldownGroup().orElse($$2);
    }

    public void addCooldown(ItemStack $$0, int $$1) {
        this.addCooldown(this.getCooldownGroup($$0), $$1);
    }

    public void addCooldown(ResourceLocation $$0, int $$1) {
        this.cooldowns.put($$0, new CooldownInstance(this.tickCount, this.tickCount + $$1));
        this.onCooldownStarted($$0, $$1);
    }

    public void removeCooldown(ResourceLocation $$0) {
        this.cooldowns.remove($$0);
        this.onCooldownEnded($$0);
    }

    protected void onCooldownStarted(ResourceLocation $$0, int $$1) {
    }

    protected void onCooldownEnded(ResourceLocation $$0) {
    }

    static final class CooldownInstance
    extends Record {
        final int startTime;
        final int endTime;

        CooldownInstance(int $$0, int $$1) {
            this.startTime = $$0;
            this.endTime = $$1;
        }

        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{CooldownInstance.class, "startTime;endTime", "startTime", "endTime"}, this);
        }

        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{CooldownInstance.class, "startTime;endTime", "startTime", "endTime"}, this);
        }

        public final boolean equals(Object $$0) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{CooldownInstance.class, "startTime;endTime", "startTime", "endTime"}, this, $$0);
        }

        public int startTime() {
            return this.startTime;
        }

        public int endTime() {
            return this.endTime;
        }
    }
}

