/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 */
package net.minecraft.client.renderer.item.properties.numeric;

import com.mojang.serialization.MapCodec;
import javax.annotation.Nullable;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.numeric.CompassAngleState;
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperty;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class CompassAngle
implements RangeSelectItemModelProperty {
    public static final MapCodec<CompassAngle> MAP_CODEC = CompassAngleState.MAP_CODEC.xmap(CompassAngle::new, $$0 -> $$0.state);
    private final CompassAngleState state;

    public CompassAngle(boolean $$0, CompassAngleState.CompassTarget $$1) {
        this(new CompassAngleState($$0, $$1));
    }

    private CompassAngle(CompassAngleState $$0) {
        this.state = $$0;
    }

    @Override
    public float get(ItemStack $$0, @Nullable ClientLevel $$1, @Nullable LivingEntity $$2, int $$3) {
        return this.state.get($$0, $$1, $$2, $$3);
    }

    public MapCodec<CompassAngle> type() {
        return MAP_CODEC;
    }
}

