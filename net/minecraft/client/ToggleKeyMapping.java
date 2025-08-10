/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client;

import com.mojang.blaze3d.platform.InputConstants;
import java.util.function.BooleanSupplier;
import net.minecraft.client.KeyMapping;

public class ToggleKeyMapping
extends KeyMapping {
    private final BooleanSupplier needsToggle;

    public ToggleKeyMapping(String $$0, int $$1, String $$2, BooleanSupplier $$3) {
        super($$0, InputConstants.Type.KEYSYM, $$1, $$2);
        this.needsToggle = $$3;
    }

    @Override
    public void setDown(boolean $$0) {
        if (this.needsToggle.getAsBoolean()) {
            if ($$0) {
                super.setDown(!this.isDown());
            }
        } else {
            super.setDown($$0);
        }
    }

    protected void reset() {
        super.setDown(false);
    }
}

