/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.lwjgl.system.MemoryStack
 */
package net.minecraft.client.renderer;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.Std140Builder;
import com.mojang.blaze3d.buffers.Std140SizeCalculator;
import com.mojang.blaze3d.systems.RenderSystem;
import java.nio.ByteBuffer;
import net.minecraft.client.DeltaTracker;
import org.lwjgl.system.MemoryStack;

public class GlobalSettingsUniform
implements AutoCloseable {
    public static final int UBO_SIZE = new Std140SizeCalculator().putVec2().putFloat().putFloat().putInt().get();
    private final GpuBuffer buffer = RenderSystem.getDevice().createBuffer(() -> "Global Settings UBO", 136, UBO_SIZE);

    public void update(int $$0, int $$1, double $$2, long $$3, DeltaTracker $$4, int $$5) {
        try (MemoryStack $$6 = MemoryStack.stackPush();){
            ByteBuffer $$7 = Std140Builder.onStack($$6, UBO_SIZE).putVec2($$0, $$1).putFloat((float)$$2).putFloat(((float)($$3 % 24000L) + $$4.getGameTimeDeltaPartialTick(false)) / 24000.0f).putInt($$5).get();
            RenderSystem.getDevice().createCommandEncoder().writeToBuffer(this.buffer.slice(), $$7);
        }
        RenderSystem.setGlobalSettingsUniform(this.buffer);
    }

    @Override
    public void close() {
        this.buffer.close();
    }
}

