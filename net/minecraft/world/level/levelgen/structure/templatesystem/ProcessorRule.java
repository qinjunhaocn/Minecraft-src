/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.PosAlwaysTrueTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.PosRuleTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.rule.blockentity.Passthrough;
import net.minecraft.world.level.levelgen.structure.templatesystem.rule.blockentity.RuleBlockEntityModifier;

public class ProcessorRule {
    public static final Passthrough DEFAULT_BLOCK_ENTITY_MODIFIER = Passthrough.INSTANCE;
    public static final Codec<ProcessorRule> CODEC = RecordCodecBuilder.create($$02 -> $$02.group((App)RuleTest.CODEC.fieldOf("input_predicate").forGetter($$0 -> $$0.inputPredicate), (App)RuleTest.CODEC.fieldOf("location_predicate").forGetter($$0 -> $$0.locPredicate), (App)PosRuleTest.CODEC.lenientOptionalFieldOf("position_predicate", (Object)PosAlwaysTrueTest.INSTANCE).forGetter($$0 -> $$0.posPredicate), (App)BlockState.CODEC.fieldOf("output_state").forGetter($$0 -> $$0.outputState), (App)RuleBlockEntityModifier.CODEC.lenientOptionalFieldOf("block_entity_modifier", (Object)DEFAULT_BLOCK_ENTITY_MODIFIER).forGetter($$0 -> $$0.blockEntityModifier)).apply((Applicative)$$02, ProcessorRule::new));
    private final RuleTest inputPredicate;
    private final RuleTest locPredicate;
    private final PosRuleTest posPredicate;
    private final BlockState outputState;
    private final RuleBlockEntityModifier blockEntityModifier;

    public ProcessorRule(RuleTest $$0, RuleTest $$1, BlockState $$2) {
        this($$0, $$1, PosAlwaysTrueTest.INSTANCE, $$2);
    }

    public ProcessorRule(RuleTest $$0, RuleTest $$1, PosRuleTest $$2, BlockState $$3) {
        this($$0, $$1, $$2, $$3, DEFAULT_BLOCK_ENTITY_MODIFIER);
    }

    public ProcessorRule(RuleTest $$0, RuleTest $$1, PosRuleTest $$2, BlockState $$3, RuleBlockEntityModifier $$4) {
        this.inputPredicate = $$0;
        this.locPredicate = $$1;
        this.posPredicate = $$2;
        this.outputState = $$3;
        this.blockEntityModifier = $$4;
    }

    public boolean test(BlockState $$0, BlockState $$1, BlockPos $$2, BlockPos $$3, BlockPos $$4, RandomSource $$5) {
        return this.inputPredicate.test($$0, $$5) && this.locPredicate.test($$1, $$5) && this.posPredicate.test($$2, $$3, $$4, $$5);
    }

    public BlockState getOutputState() {
        return this.outputState;
    }

    @Nullable
    public CompoundTag getOutputTag(RandomSource $$0, @Nullable CompoundTag $$1) {
        return this.blockEntityModifier.apply($$0, $$1);
    }
}

