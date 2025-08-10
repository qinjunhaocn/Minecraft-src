/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.world.level.block;

import com.google.common.collect.Maps;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Silverfish;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

public class InfestedBlock
extends Block {
    public static final MapCodec<InfestedBlock> CODEC = RecordCodecBuilder.mapCodec($$0 -> $$0.group((App)BuiltInRegistries.BLOCK.byNameCodec().fieldOf("host").forGetter(InfestedBlock::getHostBlock), InfestedBlock.propertiesCodec()).apply((Applicative)$$0, InfestedBlock::new));
    private final Block hostBlock;
    private static final Map<Block, Block> BLOCK_BY_HOST_BLOCK = Maps.newIdentityHashMap();
    private static final Map<BlockState, BlockState> HOST_TO_INFESTED_STATES = Maps.newIdentityHashMap();
    private static final Map<BlockState, BlockState> INFESTED_TO_HOST_STATES = Maps.newIdentityHashMap();

    public MapCodec<? extends InfestedBlock> codec() {
        return CODEC;
    }

    public InfestedBlock(Block $$0, BlockBehaviour.Properties $$1) {
        super($$1.destroyTime($$0.defaultDestroyTime() / 2.0f).explosionResistance(0.75f));
        this.hostBlock = $$0;
        BLOCK_BY_HOST_BLOCK.put($$0, this);
    }

    public Block getHostBlock() {
        return this.hostBlock;
    }

    public static boolean isCompatibleHostBlock(BlockState $$0) {
        return BLOCK_BY_HOST_BLOCK.containsKey($$0.getBlock());
    }

    private void spawnInfestation(ServerLevel $$0, BlockPos $$1) {
        Silverfish $$2 = EntityType.SILVERFISH.create($$0, EntitySpawnReason.TRIGGERED);
        if ($$2 != null) {
            $$2.snapTo((double)$$1.getX() + 0.5, $$1.getY(), (double)$$1.getZ() + 0.5, 0.0f, 0.0f);
            $$0.addFreshEntity($$2);
            $$2.spawnAnim();
        }
    }

    @Override
    protected void spawnAfterBreak(BlockState $$0, ServerLevel $$1, BlockPos $$2, ItemStack $$3, boolean $$4) {
        super.spawnAfterBreak($$0, $$1, $$2, $$3, $$4);
        if ($$1.getGameRules().getBoolean(GameRules.RULE_DOBLOCKDROPS) && !EnchantmentHelper.hasTag($$3, EnchantmentTags.PREVENTS_INFESTED_SPAWNS)) {
            this.spawnInfestation($$1, $$2);
        }
    }

    public static BlockState infestedStateByHost(BlockState $$0) {
        return InfestedBlock.getNewStateWithProperties(HOST_TO_INFESTED_STATES, $$0, () -> BLOCK_BY_HOST_BLOCK.get($$0.getBlock()).defaultBlockState());
    }

    public BlockState hostStateByInfested(BlockState $$0) {
        return InfestedBlock.getNewStateWithProperties(INFESTED_TO_HOST_STATES, $$0, () -> this.getHostBlock().defaultBlockState());
    }

    private static BlockState getNewStateWithProperties(Map<BlockState, BlockState> $$0, BlockState $$12, Supplier<BlockState> $$2) {
        return $$0.computeIfAbsent($$12, $$1 -> {
            BlockState $$2 = (BlockState)$$2.get();
            for (Property<?> $$3 : $$1.getProperties()) {
                $$2 = $$2.hasProperty($$3) ? (BlockState)$$2.setValue($$3, $$1.getValue($$3)) : $$2;
            }
            return $$2;
        });
    }
}

