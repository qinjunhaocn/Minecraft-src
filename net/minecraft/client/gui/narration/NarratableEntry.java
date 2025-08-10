/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.gui.narration;

import java.util.Collection;
import java.util.List;
import net.minecraft.client.gui.components.TabOrderedElement;
import net.minecraft.client.gui.narration.NarrationSupplier;

public interface NarratableEntry
extends TabOrderedElement,
NarrationSupplier {
    public NarrationPriority narrationPriority();

    default public boolean isActive() {
        return true;
    }

    default public Collection<? extends NarratableEntry> getNarratables() {
        return List.of((Object)this);
    }

    public static final class NarrationPriority
    extends Enum<NarrationPriority> {
        public static final /* enum */ NarrationPriority NONE = new NarrationPriority();
        public static final /* enum */ NarrationPriority HOVERED = new NarrationPriority();
        public static final /* enum */ NarrationPriority FOCUSED = new NarrationPriority();
        private static final /* synthetic */ NarrationPriority[] $VALUES;

        public static NarrationPriority[] values() {
            return (NarrationPriority[])$VALUES.clone();
        }

        public static NarrationPriority valueOf(String $$0) {
            return Enum.valueOf(NarrationPriority.class, $$0);
        }

        public boolean isTerminal() {
            return this == FOCUSED;
        }

        private static /* synthetic */ NarrationPriority[] b() {
            return new NarrationPriority[]{NONE, HOVERED, FOCUSED};
        }

        static {
            $VALUES = NarrationPriority.b();
        }
    }
}

