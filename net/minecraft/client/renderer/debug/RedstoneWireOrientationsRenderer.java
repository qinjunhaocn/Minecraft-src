/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.joml.Vector3f
 */
package net.minecraft.client.renderer.debug;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShapeRenderer;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.network.protocol.common.custom.RedstoneWireOrientationsDebugPayload;
import net.minecraft.world.level.redstone.Orientation;
import org.joml.Vector3f;

public class RedstoneWireOrientationsRenderer
implements DebugRenderer.SimpleDebugRenderer {
    public static final int TIMEOUT = 200;
    private final Minecraft minecraft;
    private final List<RedstoneWireOrientationsDebugPayload> updatedWires = Lists.newArrayList();

    RedstoneWireOrientationsRenderer(Minecraft $$0) {
        this.minecraft = $$0;
    }

    public void addWireOrientations(RedstoneWireOrientationsDebugPayload $$0) {
        this.updatedWires.add($$0);
    }

    @Override
    public void render(PoseStack $$0, MultiBufferSource $$1, double $$2, double $$3, double $$4) {
        VertexConsumer $$5 = $$1.getBuffer(RenderType.lines());
        long $$6 = this.minecraft.level.getGameTime();
        Iterator<RedstoneWireOrientationsDebugPayload> $$7 = this.updatedWires.iterator();
        while ($$7.hasNext()) {
            RedstoneWireOrientationsDebugPayload $$8 = $$7.next();
            long $$9 = $$6 - $$8.time();
            if ($$9 > 200L) {
                $$7.remove();
                continue;
            }
            for (RedstoneWireOrientationsDebugPayload.Wire $$10 : $$8.wires()) {
                Vector3f $$11 = $$10.pos().getBottomCenter().subtract($$2, $$3 - 0.1, $$4).toVector3f();
                Orientation $$12 = $$10.orientation();
                ShapeRenderer.renderVector($$0, $$5, $$11, $$12.getFront().getUnitVec3().scale(0.5), -16776961);
                ShapeRenderer.renderVector($$0, $$5, $$11, $$12.getUp().getUnitVec3().scale(0.4), -65536);
                ShapeRenderer.renderVector($$0, $$5, $$11, $$12.getSide().getUnitVec3().scale(0.3), -256);
            }
        }
    }
}

