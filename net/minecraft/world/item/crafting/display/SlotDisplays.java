/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.item.crafting.display;

import net.minecraft.core.Registry;
import net.minecraft.world.item.crafting.display.SlotDisplay;

public class SlotDisplays {
    public static SlotDisplay.Type<?> bootstrap(Registry<SlotDisplay.Type<?>> $$0) {
        Registry.register($$0, "empty", SlotDisplay.Empty.TYPE);
        Registry.register($$0, "any_fuel", SlotDisplay.AnyFuel.TYPE);
        Registry.register($$0, "item", SlotDisplay.ItemSlotDisplay.TYPE);
        Registry.register($$0, "item_stack", SlotDisplay.ItemStackSlotDisplay.TYPE);
        Registry.register($$0, "tag", SlotDisplay.TagSlotDisplay.TYPE);
        Registry.register($$0, "smithing_trim", SlotDisplay.SmithingTrimDemoSlotDisplay.TYPE);
        Registry.register($$0, "with_remainder", SlotDisplay.WithRemainder.TYPE);
        return Registry.register($$0, "composite", SlotDisplay.Composite.TYPE);
    }
}

