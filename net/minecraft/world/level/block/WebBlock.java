/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 */
package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class WebBlock
extends Block {
    public static final MapCodec<WebBlock> CODEC = WebBlock.simpleCodec(WebBlock::new);

    public MapCodec<WebBlock> codec() {
        return CODEC;
    }

    public WebBlock(BlockBehaviour.Properties $$0) {
        super($$0);
    }

    @Override
    protected void entityInside(BlockState $$0, Level $$1, BlockPos $$2, Entity $$3, InsideBlockEffectApplier $$4) {
        LivingEntity $$6;
        Vec3 $$5 = new Vec3(0.25, 0.05f, 0.25);
        if ($$3 instanceof LivingEntity && ($$6 = (LivingEntity)$$3).hasEffect(MobEffects.WEAVING)) {
            $$5 = new Vec3(0.5, 0.25, 0.5);
        }
        $$3.makeStuckInBlock($$0, $$5);
    }
}

