/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.util.parsing.packrat;

public record Atom<T>(String name) {
    public String toString() {
        return "<" + this.name + ">";
    }

    public static <T> Atom<T> of(String $$0) {
        return new Atom<T>($$0);
    }
}

