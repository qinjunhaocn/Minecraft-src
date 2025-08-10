/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.util.parsing.packrat;

public interface Control {
    public static final Control UNBOUND = new Control(){

        @Override
        public void cut() {
        }

        @Override
        public boolean hasCut() {
            return false;
        }
    };

    public void cut();

    public boolean hasCut();
}

