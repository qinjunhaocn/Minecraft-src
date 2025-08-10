/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.util.parsing.packrat;

import net.minecraft.util.parsing.packrat.Atom;
import net.minecraft.util.parsing.packrat.Rule;

public interface NamedRule<S, T> {
    public Atom<T> name();

    public Rule<S, T> value();
}

