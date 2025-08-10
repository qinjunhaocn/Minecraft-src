/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.level.block.state.properties;

import java.util.List;
import java.util.Optional;
import net.minecraft.world.level.block.state.properties.Property;

public final class BooleanProperty
extends Property<Boolean> {
    private static final List<Boolean> VALUES = List.of((Object)true, (Object)false);
    private static final int TRUE_INDEX = 0;
    private static final int FALSE_INDEX = 1;

    private BooleanProperty(String $$0) {
        super($$0, Boolean.class);
    }

    @Override
    public List<Boolean> getPossibleValues() {
        return VALUES;
    }

    public static BooleanProperty create(String $$0) {
        return new BooleanProperty($$0);
    }

    @Override
    public Optional<Boolean> getValue(String $$0) {
        return switch ($$0) {
            case "true" -> Optional.of(true);
            case "false" -> Optional.of(false);
            default -> Optional.empty();
        };
    }

    @Override
    public String getName(Boolean $$0) {
        return $$0.toString();
    }

    @Override
    public int getInternalIndex(Boolean $$0) {
        return $$0 != false ? 0 : 1;
    }

    @Override
    public /* synthetic */ int getInternalIndex(Comparable comparable) {
        return this.getInternalIndex((Boolean)comparable);
    }

    @Override
    public /* synthetic */ String getName(Comparable comparable) {
        return this.getName((Boolean)comparable);
    }
}

