/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  java.lang.Record
 *  java.lang.runtime.ObjectMethods
 */
package net.minecraft.client.renderer.block.model.multipart;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.Util;
import net.minecraft.client.renderer.block.model.multipart.Condition;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.StateHolder;
import net.minecraft.world.level.block.state.properties.Property;
import org.slf4j.Logger;

public record KeyValueCondition(Map<String, Terms> tests) implements Condition
{
    static final Logger LOGGER = LogUtils.getLogger();
    public static final Codec<KeyValueCondition> CODEC = ExtraCodecs.nonEmptyMap(Codec.unboundedMap((Codec)Codec.STRING, Terms.CODEC)).xmap(KeyValueCondition::new, KeyValueCondition::tests);

    @Override
    public <O, S extends StateHolder<O, S>> Predicate<S> instantiate(StateDefinition<O, S> $$0) {
        ArrayList $$1 = new ArrayList(this.tests.size());
        this.tests.forEach(($$2, $$3) -> $$1.add(KeyValueCondition.instantiate($$0, $$2, $$3)));
        return Util.allOf($$1);
    }

    private static <O, S extends StateHolder<O, S>> Predicate<S> instantiate(StateDefinition<O, S> $$0, String $$1, Terms $$2) {
        Property<?> $$3 = $$0.getProperty($$1);
        if ($$3 == null) {
            throw new IllegalArgumentException(String.format(Locale.ROOT, "Unknown property '%s' on '%s'", $$1, $$0.getOwner()));
        }
        return $$2.instantiate($$0.getOwner(), $$3);
    }

    public record Terms(List<Term> entries) {
        private static final char SEPARATOR = '|';
        private static final Joiner JOINER = Joiner.on('|');
        private static final Splitter SPLITTER = Splitter.on('|');
        private static final Codec<String> LEGACY_REPRESENTATION_CODEC = Codec.either((Codec)Codec.INT, (Codec)Codec.BOOL).flatComapMap($$0 -> (String)$$0.map(String::valueOf, String::valueOf), $$0 -> DataResult.error(() -> "This codec can't be used for encoding"));
        public static final Codec<Terms> CODEC = Codec.withAlternative((Codec)Codec.STRING, LEGACY_REPRESENTATION_CODEC).comapFlatMap(Terms::parse, Terms::toString);

        public Terms {
            if ($$0.isEmpty()) {
                throw new IllegalArgumentException("Empty value for property");
            }
        }

        public static DataResult<Terms> parse(String $$0) {
            List $$1 = SPLITTER.splitToStream($$0).map(Term::parse).toList();
            if ($$1.isEmpty()) {
                return DataResult.error(() -> "Empty value for property");
            }
            for (Term $$2 : $$1) {
                if (!$$2.value.isEmpty()) continue;
                return DataResult.error(() -> "Empty term in value '" + $$0 + "'");
            }
            return DataResult.success((Object)((Object)new Terms($$1)));
        }

        public String toString() {
            return JOINER.join(this.entries);
        }

        public <O, S extends StateHolder<O, S>, T extends Comparable<T>> Predicate<S> instantiate(O $$02, Property<T> $$1) {
            ArrayList $$11;
            boolean $$9;
            Predicate $$22 = Util.anyOf(Lists.transform(this.entries, $$2 -> this.instantiate($$02, $$1, (Term)((Object)$$2))));
            ArrayList<T> $$32 = new ArrayList<T>($$1.getPossibleValues());
            int $$4 = $$32.size();
            $$32.removeIf($$22.negate());
            int $$5 = $$32.size();
            if ($$5 == 0) {
                LOGGER.warn("Condition {} for property {} on {} is always false", new Object[]{this, $$1.getName(), $$02});
                return $$0 -> false;
            }
            int $$6 = $$4 - $$5;
            if ($$6 == 0) {
                LOGGER.warn("Condition {} for property {} on {} is always true", new Object[]{this, $$1.getName(), $$02});
                return $$0 -> true;
            }
            if ($$5 <= $$6) {
                boolean $$7 = false;
                ArrayList<T> $$8 = $$32;
            } else {
                $$9 = true;
                ArrayList $$10 = new ArrayList($$1.getPossibleValues());
                $$10.removeIf($$22);
                $$11 = $$10;
            }
            if ($$11.size() == 1) {
                Comparable $$12 = (Comparable)$$11.getFirst();
                return $$3 -> {
                    Object $$4 = $$3.getValue($$1);
                    return $$12.equals($$4) ^ $$9;
                };
            }
            return $$3 -> {
                Object $$4 = $$3.getValue($$1);
                return $$11.contains($$4) ^ $$9;
            };
        }

        private <T extends Comparable<T>> T getValueOrThrow(Object $$0, Property<T> $$1, String $$2) {
            Optional<T> $$3 = $$1.getValue($$2);
            if ($$3.isEmpty()) {
                throw new RuntimeException(String.format(Locale.ROOT, "Unknown value '%s' for property '%s' on '%s' in '%s'", new Object[]{$$2, $$1, $$0, this}));
            }
            return (T)((Comparable)$$3.get());
        }

        private <T extends Comparable<T>> Predicate<T> instantiate(Object $$0, Property<T> $$12, Term $$2) {
            Object $$3 = this.getValueOrThrow($$0, $$12, $$2.value);
            if ($$2.negated) {
                return $$1 -> !$$1.equals($$3);
            }
            return $$1 -> $$1.equals($$3);
        }
    }

    public static final class Term
    extends Record {
        final String value;
        final boolean negated;
        private static final String NEGATE = "!";

        public Term(String $$0, boolean $$1) {
            if ($$0.isEmpty()) {
                throw new IllegalArgumentException("Empty term");
            }
            this.value = $$0;
            this.negated = $$1;
        }

        public static Term parse(String $$0) {
            if ($$0.startsWith(NEGATE)) {
                return new Term($$0.substring(1), true);
            }
            return new Term($$0, false);
        }

        public String toString() {
            return this.negated ? NEGATE + this.value : this.value;
        }

        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{Term.class, "value;negated", "value", "negated"}, this);
        }

        public final boolean equals(Object $$0) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{Term.class, "value;negated", "value", "negated"}, this, $$0);
        }

        public String value() {
            return this.value;
        }

        public boolean negated() {
            return this.negated;
        }
    }
}

