/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.lwjgl.openal.AL10
 */
package com.mojang.blaze3d.audio;

import com.mojang.blaze3d.audio.ListenerTransform;
import net.minecraft.world.phys.Vec3;
import org.lwjgl.openal.AL10;

public class Listener {
    private float gain = 1.0f;
    private ListenerTransform transform = ListenerTransform.INITIAL;

    public void setTransform(ListenerTransform $$0) {
        this.transform = $$0;
        Vec3 $$1 = $$0.position();
        Vec3 $$2 = $$0.forward();
        Vec3 $$3 = $$0.up();
        AL10.alListener3f((int)4100, (float)((float)$$1.x), (float)((float)$$1.y), (float)((float)$$1.z));
        AL10.alListenerfv((int)4111, (float[])new float[]{(float)$$2.x, (float)$$2.y, (float)$$2.z, (float)$$3.x(), (float)$$3.y(), (float)$$3.z()});
    }

    public void setGain(float $$0) {
        AL10.alListenerf((int)4106, (float)$$0);
        this.gain = $$0;
    }

    public float getGain() {
        return this.gain;
    }

    public void reset() {
        this.setTransform(ListenerTransform.INITIAL);
    }

    public ListenerTransform getTransform() {
        return this.transform;
    }
}

