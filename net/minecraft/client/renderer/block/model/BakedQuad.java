/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  java.lang.Record
 *  java.lang.runtime.ObjectMethods
 */
package net.minecraft.client.renderer.block.model;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;

public final class BakedQuad
extends Record {
    private final int[] vertices;
    private final int tintIndex;
    private final Direction direction;
    private final TextureAtlasSprite sprite;
    private final boolean shade;
    private final int lightEmission;

    public BakedQuad(int[] $$0, int $$1, Direction $$2, TextureAtlasSprite $$3, boolean $$4, int $$5) {
        this.vertices = $$0;
        this.tintIndex = $$1;
        this.direction = $$2;
        this.sprite = $$3;
        this.shade = $$4;
        this.lightEmission = $$5;
    }

    public boolean isTinted() {
        return this.tintIndex != -1;
    }

    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{BakedQuad.class, "vertices;tintIndex;direction;sprite;shade;lightEmission", "vertices", "tintIndex", "direction", "sprite", "shade", "lightEmission"}, this);
    }

    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{BakedQuad.class, "vertices;tintIndex;direction;sprite;shade;lightEmission", "vertices", "tintIndex", "direction", "sprite", "shade", "lightEmission"}, this);
    }

    public final boolean equals(Object $$0) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{BakedQuad.class, "vertices;tintIndex;direction;sprite;shade;lightEmission", "vertices", "tintIndex", "direction", "sprite", "shade", "lightEmission"}, this, $$0);
    }

    public int[] b() {
        return this.vertices;
    }

    public int tintIndex() {
        return this.tintIndex;
    }

    public Direction direction() {
        return this.direction;
    }

    public TextureAtlasSprite sprite() {
        return this.sprite;
    }

    public boolean shade() {
        return this.shade;
    }

    public int lightEmission() {
        return this.lightEmission;
    }
}

