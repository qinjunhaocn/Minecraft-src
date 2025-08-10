/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.joml.Matrix4f
 *  org.joml.Vector3f
 *  org.joml.Vector3fc
 *  org.lwjgl.system.MemoryStack
 */
package com.mojang.blaze3d.platform;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.Std140Builder;
import com.mojang.blaze3d.buffers.Std140SizeCalculator;
import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.RenderSystem;
import java.nio.ByteBuffer;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.lwjgl.system.MemoryStack;

public class Lighting
implements AutoCloseable {
    private static final Vector3f DIFFUSE_LIGHT_0 = new Vector3f(0.2f, 1.0f, -0.7f).normalize();
    private static final Vector3f DIFFUSE_LIGHT_1 = new Vector3f(-0.2f, 1.0f, 0.7f).normalize();
    private static final Vector3f NETHER_DIFFUSE_LIGHT_0 = new Vector3f(0.2f, 1.0f, -0.7f).normalize();
    private static final Vector3f NETHER_DIFFUSE_LIGHT_1 = new Vector3f(-0.2f, -1.0f, 0.7f).normalize();
    private static final Vector3f INVENTORY_DIFFUSE_LIGHT_0 = new Vector3f(0.2f, -1.0f, 1.0f).normalize();
    private static final Vector3f INVENTORY_DIFFUSE_LIGHT_1 = new Vector3f(-0.2f, -1.0f, 0.0f).normalize();
    public static final int UBO_SIZE = new Std140SizeCalculator().putVec3().putVec3().get();
    private final GpuBuffer buffer;
    private final int paddedSize;

    public Lighting() {
        GpuDevice $$0 = RenderSystem.getDevice();
        this.paddedSize = Mth.roundToward(UBO_SIZE, $$0.getUniformOffsetAlignment());
        this.buffer = $$0.createBuffer(() -> "Lighting UBO", 136, this.paddedSize * Entry.values().length);
        Matrix4f $$1 = new Matrix4f().rotationY(-0.3926991f).rotateX(2.3561945f);
        this.updateBuffer(Entry.ITEMS_FLAT, $$1.transformDirection((Vector3fc)DIFFUSE_LIGHT_0, new Vector3f()), $$1.transformDirection((Vector3fc)DIFFUSE_LIGHT_1, new Vector3f()));
        Matrix4f $$2 = new Matrix4f().scaling(1.0f, -1.0f, 1.0f).rotateYXZ(1.0821041f, 3.2375858f, 0.0f).rotateYXZ(-0.3926991f, 2.3561945f, 0.0f);
        this.updateBuffer(Entry.ITEMS_3D, $$2.transformDirection((Vector3fc)DIFFUSE_LIGHT_0, new Vector3f()), $$2.transformDirection((Vector3fc)DIFFUSE_LIGHT_1, new Vector3f()));
        this.updateBuffer(Entry.ENTITY_IN_UI, INVENTORY_DIFFUSE_LIGHT_0, INVENTORY_DIFFUSE_LIGHT_1);
        Matrix4f $$3 = new Matrix4f();
        this.updateBuffer(Entry.PLAYER_SKIN, $$3.transformDirection((Vector3fc)INVENTORY_DIFFUSE_LIGHT_0, new Vector3f()), $$3.transformDirection((Vector3fc)INVENTORY_DIFFUSE_LIGHT_1, new Vector3f()));
    }

    public void updateLevel(boolean $$0) {
        if ($$0) {
            this.updateBuffer(Entry.LEVEL, NETHER_DIFFUSE_LIGHT_0, NETHER_DIFFUSE_LIGHT_1);
        } else {
            this.updateBuffer(Entry.LEVEL, DIFFUSE_LIGHT_0, DIFFUSE_LIGHT_1);
        }
    }

    private void updateBuffer(Entry $$0, Vector3f $$1, Vector3f $$2) {
        try (MemoryStack $$3 = MemoryStack.stackPush();){
            ByteBuffer $$4 = Std140Builder.onStack($$3, UBO_SIZE).putVec3((Vector3fc)$$1).putVec3((Vector3fc)$$2).get();
            RenderSystem.getDevice().createCommandEncoder().writeToBuffer(this.buffer.slice($$0.ordinal() * this.paddedSize, this.paddedSize), $$4);
        }
    }

    public void setupFor(Entry $$0) {
        RenderSystem.setShaderLights(this.buffer.slice($$0.ordinal() * this.paddedSize, UBO_SIZE));
    }

    @Override
    public void close() {
        this.buffer.close();
    }

    public static final class Entry
    extends Enum<Entry> {
        public static final /* enum */ Entry LEVEL = new Entry();
        public static final /* enum */ Entry ITEMS_FLAT = new Entry();
        public static final /* enum */ Entry ITEMS_3D = new Entry();
        public static final /* enum */ Entry ENTITY_IN_UI = new Entry();
        public static final /* enum */ Entry PLAYER_SKIN = new Entry();
        private static final /* synthetic */ Entry[] $VALUES;

        public static Entry[] values() {
            return (Entry[])$VALUES.clone();
        }

        public static Entry valueOf(String $$0) {
            return Enum.valueOf(Entry.class, $$0);
        }

        private static /* synthetic */ Entry[] a() {
            return new Entry[]{LEVEL, ITEMS_FLAT, ITEMS_3D, ENTITY_IN_UI, PLAYER_SKIN};
        }

        static {
            $VALUES = Entry.a();
        }
    }
}

