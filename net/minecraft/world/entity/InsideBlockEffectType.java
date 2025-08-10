/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.entity;

import java.util.function.Consumer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.BaseFireBlock;

public final class InsideBlockEffectType
extends Enum<InsideBlockEffectType> {
    public static final /* enum */ InsideBlockEffectType FREEZE = new InsideBlockEffectType($$0 -> {
        $$0.setIsInPowderSnow(true);
        if ($$0.canFreeze()) {
            $$0.setTicksFrozen(Math.min($$0.getTicksRequiredToFreeze(), $$0.getTicksFrozen() + 1));
        }
    });
    public static final /* enum */ InsideBlockEffectType FIRE_IGNITE = new InsideBlockEffectType(BaseFireBlock::fireIgnite);
    public static final /* enum */ InsideBlockEffectType LAVA_IGNITE = new InsideBlockEffectType(Entity::lavaIgnite);
    public static final /* enum */ InsideBlockEffectType EXTINGUISH = new InsideBlockEffectType(Entity::clearFire);
    private final Consumer<Entity> effect;
    private static final /* synthetic */ InsideBlockEffectType[] $VALUES;

    public static InsideBlockEffectType[] values() {
        return (InsideBlockEffectType[])$VALUES.clone();
    }

    public static InsideBlockEffectType valueOf(String $$0) {
        return Enum.valueOf(InsideBlockEffectType.class, $$0);
    }

    private InsideBlockEffectType(Consumer<Entity> $$0) {
        this.effect = $$0;
    }

    public Consumer<Entity> effect() {
        return this.effect;
    }

    private static /* synthetic */ InsideBlockEffectType[] b() {
        return new InsideBlockEffectType[]{FREEZE, FIRE_IGNITE, LAVA_IGNITE, EXTINGUISH};
    }

    static {
        $VALUES = InsideBlockEffectType.b();
    }
}

