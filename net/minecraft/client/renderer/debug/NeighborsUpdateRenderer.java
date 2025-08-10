/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.renderer.debug;

import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShapeRenderer;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;

public class NeighborsUpdateRenderer
implements DebugRenderer.SimpleDebugRenderer {
    private final Minecraft minecraft;
    private final Map<Long, Map<BlockPos, Integer>> lastUpdate = Maps.newTreeMap(Ordering.natural().reverse());

    NeighborsUpdateRenderer(Minecraft $$0) {
        this.minecraft = $$0;
    }

    public void addUpdate(long $$02, BlockPos $$1) {
        Map $$2 = this.lastUpdate.computeIfAbsent($$02, $$0 -> Maps.newHashMap());
        int $$3 = $$2.getOrDefault($$1, 0);
        $$2.put($$1, $$3 + 1);
    }

    @Override
    public void render(PoseStack $$0, MultiBufferSource $$1, double $$2, double $$3, double $$4) {
        long $$5 = this.minecraft.level.getGameTime();
        int $$6 = 200;
        double $$7 = 0.0025;
        HashSet<BlockPos> $$8 = Sets.newHashSet();
        HashMap<BlockPos, Integer> $$9 = Maps.newHashMap();
        VertexConsumer $$10 = $$1.getBuffer(RenderType.lines());
        Iterator<Map.Entry<Long, Map<BlockPos, Integer>>> $$11 = this.lastUpdate.entrySet().iterator();
        while ($$11.hasNext()) {
            Map.Entry<Long, Map<BlockPos, Integer>> $$12 = $$11.next();
            Long $$13 = $$12.getKey();
            Map<BlockPos, Integer> $$14 = $$12.getValue();
            long $$15 = $$5 - $$13;
            if ($$15 > 200L) {
                $$11.remove();
                continue;
            }
            for (Map.Entry<BlockPos, Integer> $$16 : $$14.entrySet()) {
                BlockPos $$17 = $$16.getKey();
                Integer $$18 = $$16.getValue();
                if (!$$8.add($$17)) continue;
                AABB $$19 = new AABB(BlockPos.ZERO).inflate(0.002).deflate(0.0025 * (double)$$15).move($$17.getX(), $$17.getY(), $$17.getZ()).move(-$$2, -$$3, -$$4);
                ShapeRenderer.renderLineBox($$0, $$10, $$19.minX, $$19.minY, $$19.minZ, $$19.maxX, $$19.maxY, $$19.maxZ, 1.0f, 1.0f, 1.0f, 1.0f);
                $$9.put($$17, $$18);
            }
        }
        for (Map.Entry $$20 : $$9.entrySet()) {
            BlockPos $$21 = (BlockPos)$$20.getKey();
            Integer $$22 = (Integer)$$20.getValue();
            DebugRenderer.renderFloatingText($$0, $$1, String.valueOf($$22), $$21.getX(), $$21.getY(), $$21.getZ(), -1);
        }
    }
}

