/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.renderer.block.model;

import com.google.common.base.Splitter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.StateHolder;
import net.minecraft.world.level.block.state.properties.Property;

public class VariantSelector {
    private static final Splitter COMMA_SPLITTER = Splitter.on(',');
    private static final Splitter EQUAL_SPLITTER = Splitter.on('=').limit(2);

    public static <O, S extends StateHolder<O, S>> Predicate<StateHolder<O, S>> predicate(StateDefinition<O, S> $$0, String $$12) {
        HashMap $$2 = new HashMap();
        for (String $$3 : COMMA_SPLITTER.split($$12)) {
            Iterator<String> $$4 = EQUAL_SPLITTER.split($$3).iterator();
            if (!$$4.hasNext()) continue;
            String $$5 = $$4.next();
            Property<?> $$6 = $$0.getProperty($$5);
            if ($$6 != null && $$4.hasNext()) {
                String $$7 = $$4.next();
                Object $$8 = VariantSelector.getValueHelper($$6, $$7);
                if ($$8 != null) {
                    $$2.put($$6, $$8);
                    continue;
                }
                throw new RuntimeException("Unknown value: '" + $$7 + "' for blockstate property: '" + $$5 + "' " + String.valueOf($$6.getPossibleValues()));
            }
            if ($$5.isEmpty()) continue;
            throw new RuntimeException("Unknown blockstate property: '" + $$5 + "'");
        }
        return $$1 -> {
            for (Map.Entry $$2 : $$2.entrySet()) {
                if (Objects.equals($$1.getValue((Property)$$2.getKey()), $$2.getValue())) continue;
                return false;
            }
            return true;
        };
    }

    @Nullable
    private static <T extends Comparable<T>> T getValueHelper(Property<T> $$0, String $$1) {
        return (T)((Comparable)$$0.getValue($$1).orElse(null));
    }
}

