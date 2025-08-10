/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.data.models.blockstates;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.client.data.models.MultiVariant;
import net.minecraft.client.data.models.blockstates.BlockModelDefinitionGenerator;
import net.minecraft.client.data.models.blockstates.ConditionBuilder;
import net.minecraft.client.renderer.block.model.BlockModelDefinition;
import net.minecraft.client.renderer.block.model.multipart.Condition;
import net.minecraft.client.renderer.block.model.multipart.Selector;
import net.minecraft.world.level.block.Block;

public class MultiPartGenerator
implements BlockModelDefinitionGenerator {
    private final Block block;
    private final List<Entry> parts = new ArrayList<Entry>();

    private MultiPartGenerator(Block $$0) {
        this.block = $$0;
    }

    @Override
    public Block block() {
        return this.block;
    }

    public static MultiPartGenerator multiPart(Block $$0) {
        return new MultiPartGenerator($$0);
    }

    public MultiPartGenerator with(MultiVariant $$0) {
        this.parts.add(new Entry(Optional.empty(), $$0));
        return this;
    }

    private void validateCondition(Condition $$0) {
        $$0.instantiate(this.block.getStateDefinition());
    }

    public MultiPartGenerator with(Condition $$0, MultiVariant $$1) {
        this.validateCondition($$0);
        this.parts.add(new Entry(Optional.of($$0), $$1));
        return this;
    }

    public MultiPartGenerator with(ConditionBuilder $$0, MultiVariant $$1) {
        return this.with($$0.build(), $$1);
    }

    @Override
    public BlockModelDefinition create() {
        return new BlockModelDefinition(Optional.empty(), Optional.of(new BlockModelDefinition.MultiPartDefinition(this.parts.stream().map(Entry::toUnbaked).toList())));
    }

    record Entry(Optional<Condition> condition, MultiVariant variants) {
        public Selector toUnbaked() {
            return new Selector(this.condition, this.variants.toUnbaked());
        }
    }
}

