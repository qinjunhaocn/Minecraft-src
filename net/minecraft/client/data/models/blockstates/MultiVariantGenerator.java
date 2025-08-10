/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  java.lang.Record
 *  java.lang.runtime.ObjectMethods
 */
package net.minecraft.client.data.models.blockstates;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;
import net.minecraft.client.data.models.MultiVariant;
import net.minecraft.client.data.models.blockstates.BlockModelDefinitionGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.blockstates.PropertyValueList;
import net.minecraft.client.renderer.block.model.BlockModelDefinition;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.client.renderer.block.model.VariantMutator;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.Property;

public class MultiVariantGenerator
implements BlockModelDefinitionGenerator {
    private final Block block;
    private final List<Entry> entries;
    private final Set<Property<?>> seenProperties;

    MultiVariantGenerator(Block $$0, List<Entry> $$1, Set<Property<?>> $$2) {
        this.block = $$0;
        this.entries = $$1;
        this.seenProperties = $$2;
    }

    static Set<Property<?>> validateAndExpandProperties(Set<Property<?>> $$0, Block $$1, PropertyDispatch<?> $$22) {
        List<Property<?>> $$3 = $$22.getDefinedProperties();
        $$3.forEach($$2 -> {
            if ($$1.getStateDefinition().getProperty($$2.getName()) != $$2) {
                throw new IllegalStateException("Property " + String.valueOf($$2) + " is not defined for block " + String.valueOf($$1));
            }
            if ($$0.contains($$2)) {
                throw new IllegalStateException("Values of property " + String.valueOf($$2) + " already defined for block " + String.valueOf($$1));
            }
        });
        HashSet $$4 = new HashSet($$0);
        $$4.addAll($$3);
        return $$4;
    }

    public MultiVariantGenerator with(PropertyDispatch<VariantMutator> $$0) {
        Set<Property<?>> $$12 = MultiVariantGenerator.validateAndExpandProperties(this.seenProperties, this.block, $$0);
        List $$2 = this.entries.stream().flatMap($$1 -> $$1.apply($$0)).toList();
        return new MultiVariantGenerator(this.block, $$2, $$12);
    }

    public MultiVariantGenerator with(VariantMutator $$0) {
        List $$12 = this.entries.stream().flatMap($$1 -> $$1.apply($$0)).toList();
        return new MultiVariantGenerator(this.block, $$12, this.seenProperties);
    }

    @Override
    public BlockModelDefinition create() {
        HashMap<String, BlockStateModel.Unbaked> $$0 = new HashMap<String, BlockStateModel.Unbaked>();
        for (Entry $$1 : this.entries) {
            $$0.put($$1.properties.getKey(), $$1.variant.toUnbaked());
        }
        return new BlockModelDefinition(Optional.of(new BlockModelDefinition.SimpleModelSelectors($$0)), Optional.empty());
    }

    @Override
    public Block block() {
        return this.block;
    }

    public static Empty dispatch(Block $$0) {
        return new Empty($$0);
    }

    public static MultiVariantGenerator dispatch(Block $$0, MultiVariant $$1) {
        return new MultiVariantGenerator($$0, List.of((Object)((Object)new Entry(PropertyValueList.EMPTY, $$1))), Set.of());
    }

    static final class Entry
    extends Record {
        final PropertyValueList properties;
        final MultiVariant variant;

        Entry(PropertyValueList $$0, MultiVariant $$1) {
            this.properties = $$0;
            this.variant = $$1;
        }

        public Stream<Entry> apply(PropertyDispatch<VariantMutator> $$02) {
            return $$02.getEntries().entrySet().stream().map($$0 -> {
                PropertyValueList $$1 = this.properties.extend((PropertyValueList)((Object)((Object)$$0.getKey())));
                MultiVariant $$2 = this.variant.with((VariantMutator)$$0.getValue());
                return new Entry($$1, $$2);
            });
        }

        public Stream<Entry> apply(VariantMutator $$0) {
            return Stream.of(new Entry(this.properties, this.variant.with($$0)));
        }

        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{Entry.class, "properties;variant", "properties", "variant"}, this);
        }

        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{Entry.class, "properties;variant", "properties", "variant"}, this);
        }

        public final boolean equals(Object $$0) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{Entry.class, "properties;variant", "properties", "variant"}, this, $$0);
        }

        public PropertyValueList properties() {
            return this.properties;
        }

        public MultiVariant variant() {
            return this.variant;
        }
    }

    public static class Empty {
        private final Block block;

        public Empty(Block $$0) {
            this.block = $$0;
        }

        public MultiVariantGenerator with(PropertyDispatch<MultiVariant> $$02) {
            Set<Property<?>> $$1 = MultiVariantGenerator.validateAndExpandProperties(Set.of(), this.block, $$02);
            List $$2 = $$02.getEntries().entrySet().stream().map($$0 -> new Entry((PropertyValueList)((Object)((Object)$$0.getKey())), (MultiVariant)((Object)((Object)$$0.getValue())))).toList();
            return new MultiVariantGenerator(this.block, $$2, $$1);
        }
    }
}

