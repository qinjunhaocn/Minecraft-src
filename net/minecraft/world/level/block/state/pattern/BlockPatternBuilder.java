/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.level.block.state.pattern;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.block.state.pattern.BlockPattern;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

public class BlockPatternBuilder {
    private static final Joiner COMMA_JOINED = Joiner.on(",");
    private final List<String[]> pattern = Lists.newArrayList();
    private final Map<Character, Predicate<BlockInWorld>> lookup = Maps.newHashMap();
    private int height;
    private int width;

    private BlockPatternBuilder() {
        this.lookup.put(Character.valueOf(' '), $$0 -> true);
    }

    public BlockPatternBuilder a(String ... $$0) {
        if (ArrayUtils.isEmpty($$0) || StringUtils.isEmpty($$0[0])) {
            throw new IllegalArgumentException("Empty pattern for aisle");
        }
        if (this.pattern.isEmpty()) {
            this.height = $$0.length;
            this.width = $$0[0].length();
        }
        if ($$0.length != this.height) {
            throw new IllegalArgumentException("Expected aisle with height of " + this.height + ", but was given one with a height of " + $$0.length + ")");
        }
        for (String $$1 : $$0) {
            if ($$1.length() != this.width) {
                throw new IllegalArgumentException("Not all rows in the given aisle are the correct width (expected " + this.width + ", found one with " + $$1.length() + ")");
            }
            for (char $$2 : $$1.toCharArray()) {
                if (this.lookup.containsKey(Character.valueOf($$2))) continue;
                this.lookup.put(Character.valueOf($$2), null);
            }
        }
        this.pattern.add($$0);
        return this;
    }

    public static BlockPatternBuilder start() {
        return new BlockPatternBuilder();
    }

    public BlockPatternBuilder a(char $$0, Predicate<BlockInWorld> $$1) {
        this.lookup.put(Character.valueOf($$0), $$1);
        return this;
    }

    public BlockPattern build() {
        return new BlockPattern(this.c());
    }

    private Predicate<BlockInWorld>[][][] c() {
        this.ensureAllCharactersMatched();
        Predicate[][][] $$0 = (Predicate[][][])Array.newInstance(Predicate.class, this.pattern.size(), this.height, this.width);
        for (int $$1 = 0; $$1 < this.pattern.size(); ++$$1) {
            for (int $$2 = 0; $$2 < this.height; ++$$2) {
                for (int $$3 = 0; $$3 < this.width; ++$$3) {
                    $$0[$$1][$$2][$$3] = this.lookup.get(Character.valueOf(this.pattern.get($$1)[$$2].charAt($$3)));
                }
            }
        }
        return $$0;
    }

    private void ensureAllCharactersMatched() {
        ArrayList<Character> $$0 = Lists.newArrayList();
        for (Map.Entry<Character, Predicate<BlockInWorld>> $$1 : this.lookup.entrySet()) {
            if ($$1.getValue() != null) continue;
            $$0.add($$1.getKey());
        }
        if (!$$0.isEmpty()) {
            throw new IllegalStateException("Predicates for character(s) " + COMMA_JOINED.join($$0) + " are missing");
        }
    }
}

