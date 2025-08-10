/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  java.lang.Record
 *  java.lang.runtime.ObjectMethods
 */
package net.minecraft.client.renderer.debug;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.vertex.PoseStack;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.client.renderer.debug.PathfindingRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.core.Vec3i;
import net.minecraft.network.protocol.common.custom.BeeDebugPayload;
import net.minecraft.network.protocol.common.custom.HiveDebugPayload;
import net.minecraft.network.protocol.game.DebugEntityNameGenerator;

public class BeeDebugRenderer
implements DebugRenderer.SimpleDebugRenderer {
    private static final boolean SHOW_GOAL_FOR_ALL_BEES = true;
    private static final boolean SHOW_NAME_FOR_ALL_BEES = true;
    private static final boolean SHOW_HIVE_FOR_ALL_BEES = true;
    private static final boolean SHOW_FLOWER_POS_FOR_ALL_BEES = true;
    private static final boolean SHOW_TRAVEL_TICKS_FOR_ALL_BEES = true;
    private static final boolean SHOW_PATH_FOR_ALL_BEES = false;
    private static final boolean SHOW_GOAL_FOR_SELECTED_BEE = true;
    private static final boolean SHOW_NAME_FOR_SELECTED_BEE = true;
    private static final boolean SHOW_HIVE_FOR_SELECTED_BEE = true;
    private static final boolean SHOW_FLOWER_POS_FOR_SELECTED_BEE = true;
    private static final boolean SHOW_TRAVEL_TICKS_FOR_SELECTED_BEE = true;
    private static final boolean SHOW_PATH_FOR_SELECTED_BEE = true;
    private static final boolean SHOW_HIVE_MEMBERS = true;
    private static final boolean SHOW_BLACKLISTS = true;
    private static final int MAX_RENDER_DIST_FOR_HIVE_OVERLAY = 30;
    private static final int MAX_RENDER_DIST_FOR_BEE_OVERLAY = 30;
    private static final int MAX_TARGETING_DIST = 8;
    private static final int HIVE_TIMEOUT = 20;
    private static final float TEXT_SCALE = 0.02f;
    private static final int ORANGE = -23296;
    private static final int GRAY = -3355444;
    private static final int PINK = -98404;
    private final Minecraft minecraft;
    private final Map<BlockPos, HiveDebugInfo> hives = new HashMap<BlockPos, HiveDebugInfo>();
    private final Map<UUID, BeeDebugPayload.BeeInfo> beeInfosPerEntity = new HashMap<UUID, BeeDebugPayload.BeeInfo>();
    @Nullable
    private UUID lastLookedAtUuid;

    public BeeDebugRenderer(Minecraft $$0) {
        this.minecraft = $$0;
    }

    @Override
    public void clear() {
        this.hives.clear();
        this.beeInfosPerEntity.clear();
        this.lastLookedAtUuid = null;
    }

    public void addOrUpdateHiveInfo(HiveDebugPayload.HiveInfo $$0, long $$1) {
        this.hives.put($$0.pos(), new HiveDebugInfo($$0, $$1));
    }

    public void addOrUpdateBeeInfo(BeeDebugPayload.BeeInfo $$0) {
        this.beeInfosPerEntity.put($$0.uuid(), $$0);
    }

    public void removeBeeInfo(int $$0) {
        this.beeInfosPerEntity.values().removeIf($$1 -> $$1.id() == $$0);
    }

    @Override
    public void render(PoseStack $$0, MultiBufferSource $$1, double $$2, double $$3, double $$4) {
        this.clearRemovedHives();
        this.clearRemovedBees();
        this.doRender($$0, $$1);
        if (!this.minecraft.player.isSpectator()) {
            this.updateLastLookedAtUuid();
        }
    }

    private void clearRemovedBees() {
        this.beeInfosPerEntity.entrySet().removeIf($$0 -> this.minecraft.level.getEntity(((BeeDebugPayload.BeeInfo)((Object)((Object)$$0.getValue()))).id()) == null);
    }

    private void clearRemovedHives() {
        long $$0 = this.minecraft.level.getGameTime() - 20L;
        this.hives.entrySet().removeIf($$1 -> ((HiveDebugInfo)((Object)((Object)$$1.getValue()))).lastSeen() < $$0);
    }

    private void doRender(PoseStack $$0, MultiBufferSource $$1) {
        BlockPos $$22 = this.getCamera().getBlockPosition();
        this.beeInfosPerEntity.values().forEach($$2 -> {
            if (this.isPlayerCloseEnoughToMob((BeeDebugPayload.BeeInfo)((Object)$$2))) {
                this.renderBeeInfo($$0, $$1, (BeeDebugPayload.BeeInfo)((Object)$$2));
            }
        });
        this.renderFlowerInfos($$0, $$1);
        for (BlockPos $$32 : this.hives.keySet()) {
            if (!$$22.closerThan($$32, 30.0)) continue;
            BeeDebugRenderer.highlightHive($$0, $$1, $$32);
        }
        Map<BlockPos, Set<UUID>> $$42 = this.createHiveBlacklistMap();
        this.hives.values().forEach($$4 -> {
            if ($$22.closerThan($$4.info.pos(), 30.0)) {
                Set $$5 = (Set)$$42.get($$4.info.pos());
                this.renderHiveInfo($$0, $$1, $$4.info, $$5 == null ? Sets.newHashSet() : $$5);
            }
        });
        this.getGhostHives().forEach(($$3, $$4) -> {
            if ($$22.closerThan((Vec3i)$$3, 30.0)) {
                this.renderGhostHive($$0, $$1, (BlockPos)$$3, (List<String>)$$4);
            }
        });
    }

    private Map<BlockPos, Set<UUID>> createHiveBlacklistMap() {
        HashMap<BlockPos, Set<UUID>> $$0 = Maps.newHashMap();
        this.beeInfosPerEntity.values().forEach($$1 -> $$1.blacklistedHives().forEach($$2 -> $$0.computeIfAbsent((BlockPos)$$2, $$0 -> Sets.newHashSet()).add($$1.uuid())));
        return $$0;
    }

    private void renderFlowerInfos(PoseStack $$0, MultiBufferSource $$12) {
        HashMap<BlockPos, Set> $$22 = Maps.newHashMap();
        this.beeInfosPerEntity.values().forEach($$1 -> {
            if ($$1.flowerPos() != null) {
                $$22.computeIfAbsent($$1.flowerPos(), $$0 -> new HashSet()).add($$1.uuid());
            }
        });
        $$22.forEach(($$2, $$3) -> {
            Set $$4 = $$3.stream().map(DebugEntityNameGenerator::getEntityName).collect(Collectors.toSet());
            int $$5 = 1;
            BeeDebugRenderer.renderTextOverPos($$0, $$12, $$4.toString(), $$2, $$5++, -256);
            BeeDebugRenderer.renderTextOverPos($$0, $$12, "Flower", $$2, $$5++, -1);
            float $$6 = 0.05f;
            DebugRenderer.renderFilledBox($$0, $$12, $$2, 0.05f, 0.8f, 0.8f, 0.0f, 0.3f);
        });
    }

    private static String getBeeUuidsAsString(Collection<UUID> $$0) {
        if ($$0.isEmpty()) {
            return "-";
        }
        if ($$0.size() > 3) {
            return $$0.size() + " bees";
        }
        return $$0.stream().map(DebugEntityNameGenerator::getEntityName).collect(Collectors.toSet()).toString();
    }

    private static void highlightHive(PoseStack $$0, MultiBufferSource $$1, BlockPos $$2) {
        float $$3 = 0.05f;
        DebugRenderer.renderFilledBox($$0, $$1, $$2, 0.05f, 0.2f, 0.2f, 1.0f, 0.3f);
    }

    private void renderGhostHive(PoseStack $$0, MultiBufferSource $$1, BlockPos $$2, List<String> $$3) {
        float $$4 = 0.05f;
        DebugRenderer.renderFilledBox($$0, $$1, $$2, 0.05f, 0.2f, 0.2f, 1.0f, 0.3f);
        BeeDebugRenderer.renderTextOverPos($$0, $$1, String.valueOf($$3), $$2, 0, -256);
        BeeDebugRenderer.renderTextOverPos($$0, $$1, "Ghost Hive", $$2, 1, -65536);
    }

    private void renderHiveInfo(PoseStack $$0, MultiBufferSource $$1, HiveDebugPayload.HiveInfo $$2, Collection<UUID> $$3) {
        int $$4 = 0;
        if (!$$3.isEmpty()) {
            BeeDebugRenderer.renderTextOverHive($$0, $$1, "Blacklisted by " + BeeDebugRenderer.getBeeUuidsAsString($$3), $$2, $$4++, -65536);
        }
        BeeDebugRenderer.renderTextOverHive($$0, $$1, "Out: " + BeeDebugRenderer.getBeeUuidsAsString(this.getHiveMembers($$2.pos())), $$2, $$4++, -3355444);
        if ($$2.occupantCount() == 0) {
            BeeDebugRenderer.renderTextOverHive($$0, $$1, "In: -", $$2, $$4++, -256);
        } else if ($$2.occupantCount() == 1) {
            BeeDebugRenderer.renderTextOverHive($$0, $$1, "In: 1 bee", $$2, $$4++, -256);
        } else {
            BeeDebugRenderer.renderTextOverHive($$0, $$1, "In: " + $$2.occupantCount() + " bees", $$2, $$4++, -256);
        }
        BeeDebugRenderer.renderTextOverHive($$0, $$1, "Honey: " + $$2.honeyLevel(), $$2, $$4++, -23296);
        BeeDebugRenderer.renderTextOverHive($$0, $$1, $$2.hiveType() + ($$2.sedated() ? " (sedated)" : ""), $$2, $$4++, -1);
    }

    private void renderPath(PoseStack $$0, MultiBufferSource $$1, BeeDebugPayload.BeeInfo $$2) {
        if ($$2.path() != null) {
            PathfindingRenderer.renderPath($$0, $$1, $$2.path(), 0.5f, false, false, this.getCamera().getPosition().x(), this.getCamera().getPosition().y(), this.getCamera().getPosition().z());
        }
    }

    private void renderBeeInfo(PoseStack $$0, MultiBufferSource $$1, BeeDebugPayload.BeeInfo $$2) {
        boolean $$3 = this.isBeeSelected($$2);
        int $$4 = 0;
        BeeDebugRenderer.renderTextOverMob($$0, $$1, $$2.pos(), $$4++, $$2.toString(), -1, 0.03f);
        if ($$2.hivePos() == null) {
            BeeDebugRenderer.renderTextOverMob($$0, $$1, $$2.pos(), $$4++, "No hive", -98404, 0.02f);
        } else {
            BeeDebugRenderer.renderTextOverMob($$0, $$1, $$2.pos(), $$4++, "Hive: " + this.getPosDescription($$2, $$2.hivePos()), -256, 0.02f);
        }
        if ($$2.flowerPos() == null) {
            BeeDebugRenderer.renderTextOverMob($$0, $$1, $$2.pos(), $$4++, "No flower", -98404, 0.02f);
        } else {
            BeeDebugRenderer.renderTextOverMob($$0, $$1, $$2.pos(), $$4++, "Flower: " + this.getPosDescription($$2, $$2.flowerPos()), -256, 0.02f);
        }
        for (String $$5 : $$2.goals()) {
            BeeDebugRenderer.renderTextOverMob($$0, $$1, $$2.pos(), $$4++, $$5, -16711936, 0.02f);
        }
        if ($$3) {
            this.renderPath($$0, $$1, $$2);
        }
        if ($$2.travelTicks() > 0) {
            int $$6 = $$2.travelTicks() < 2400 ? -3355444 : -23296;
            BeeDebugRenderer.renderTextOverMob($$0, $$1, $$2.pos(), $$4++, "Travelling: " + $$2.travelTicks() + " ticks", $$6, 0.02f);
        }
    }

    private static void renderTextOverHive(PoseStack $$0, MultiBufferSource $$1, String $$2, HiveDebugPayload.HiveInfo $$3, int $$4, int $$5) {
        BeeDebugRenderer.renderTextOverPos($$0, $$1, $$2, $$3.pos(), $$4, $$5);
    }

    private static void renderTextOverPos(PoseStack $$0, MultiBufferSource $$1, String $$2, BlockPos $$3, int $$4, int $$5) {
        double $$6 = 1.3;
        double $$7 = 0.2;
        double $$8 = (double)$$3.getX() + 0.5;
        double $$9 = (double)$$3.getY() + 1.3 + (double)$$4 * 0.2;
        double $$10 = (double)$$3.getZ() + 0.5;
        DebugRenderer.renderFloatingText($$0, $$1, $$2, $$8, $$9, $$10, $$5, 0.02f, true, 0.0f, true);
    }

    private static void renderTextOverMob(PoseStack $$0, MultiBufferSource $$1, Position $$2, int $$3, String $$4, int $$5, float $$6) {
        double $$7 = 2.4;
        double $$8 = 0.25;
        BlockPos $$9 = BlockPos.containing($$2);
        double $$10 = (double)$$9.getX() + 0.5;
        double $$11 = $$2.y() + 2.4 + (double)$$3 * 0.25;
        double $$12 = (double)$$9.getZ() + 0.5;
        float $$13 = 0.5f;
        DebugRenderer.renderFloatingText($$0, $$1, $$4, $$10, $$11, $$12, $$5, $$6, false, 0.5f, true);
    }

    private Camera getCamera() {
        return this.minecraft.gameRenderer.getMainCamera();
    }

    private Set<String> getHiveMemberNames(HiveDebugPayload.HiveInfo $$0) {
        return this.getHiveMembers($$0.pos()).stream().map(DebugEntityNameGenerator::getEntityName).collect(Collectors.toSet());
    }

    private String getPosDescription(BeeDebugPayload.BeeInfo $$0, BlockPos $$1) {
        double $$2 = Math.sqrt($$1.distToCenterSqr($$0.pos()));
        double $$3 = (double)Math.round($$2 * 10.0) / 10.0;
        return $$1.toShortString() + " (dist " + $$3 + ")";
    }

    private boolean isBeeSelected(BeeDebugPayload.BeeInfo $$0) {
        return Objects.equals(this.lastLookedAtUuid, $$0.uuid());
    }

    private boolean isPlayerCloseEnoughToMob(BeeDebugPayload.BeeInfo $$0) {
        LocalPlayer $$1 = this.minecraft.player;
        BlockPos $$2 = BlockPos.containing($$1.getX(), $$0.pos().y(), $$1.getZ());
        BlockPos $$3 = BlockPos.containing($$0.pos());
        return $$2.closerThan($$3, 30.0);
    }

    private Collection<UUID> getHiveMembers(BlockPos $$0) {
        return this.beeInfosPerEntity.values().stream().filter($$1 -> $$1.hasHive($$0)).map(BeeDebugPayload.BeeInfo::uuid).collect(Collectors.toSet());
    }

    private Map<BlockPos, List<String>> getGhostHives() {
        HashMap<BlockPos, List<String>> $$02 = Maps.newHashMap();
        for (BeeDebugPayload.BeeInfo $$1 : this.beeInfosPerEntity.values()) {
            if ($$1.hivePos() == null || this.hives.containsKey($$1.hivePos())) continue;
            $$02.computeIfAbsent($$1.hivePos(), $$0 -> Lists.newArrayList()).add($$1.generateName());
        }
        return $$02;
    }

    private void updateLastLookedAtUuid() {
        DebugRenderer.getTargetedEntity(this.minecraft.getCameraEntity(), 8).ifPresent($$0 -> {
            this.lastLookedAtUuid = $$0.getUUID();
        });
    }

    static final class HiveDebugInfo
    extends Record {
        final HiveDebugPayload.HiveInfo info;
        private final long lastSeen;

        HiveDebugInfo(HiveDebugPayload.HiveInfo $$0, long $$1) {
            this.info = $$0;
            this.lastSeen = $$1;
        }

        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{HiveDebugInfo.class, "info;lastSeen", "info", "lastSeen"}, this);
        }

        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{HiveDebugInfo.class, "info;lastSeen", "info", "lastSeen"}, this);
        }

        public final boolean equals(Object $$0) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{HiveDebugInfo.class, "info;lastSeen", "info", "lastSeen"}, this, $$0);
        }

        public HiveDebugPayload.HiveInfo info() {
            return this.info;
        }

        public long lastSeen() {
            return this.lastSeen;
        }
    }
}

