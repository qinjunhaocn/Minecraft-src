/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.player;

import net.minecraft.client.Options;
import net.minecraft.client.player.ClientInput;
import net.minecraft.world.entity.player.Input;
import net.minecraft.world.phys.Vec2;

public class KeyboardInput
extends ClientInput {
    private final Options options;

    public KeyboardInput(Options $$0) {
        this.options = $$0;
    }

    private static float calculateImpulse(boolean $$0, boolean $$1) {
        if ($$0 == $$1) {
            return 0.0f;
        }
        return $$0 ? 1.0f : -1.0f;
    }

    @Override
    public void tick() {
        this.keyPresses = new Input(this.options.keyUp.isDown(), this.options.keyDown.isDown(), this.options.keyLeft.isDown(), this.options.keyRight.isDown(), this.options.keyJump.isDown(), this.options.keyShift.isDown(), this.options.keySprint.isDown());
        float $$0 = KeyboardInput.calculateImpulse(this.keyPresses.forward(), this.keyPresses.backward());
        float $$1 = KeyboardInput.calculateImpulse(this.keyPresses.left(), this.keyPresses.right());
        this.moveVector = new Vec2($$1, $$0).normalized();
    }
}

