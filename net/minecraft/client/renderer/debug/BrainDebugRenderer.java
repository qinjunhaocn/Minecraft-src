/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 */
package net.minecraft.client.renderer.debug;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.logging.LogUtils;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.client.renderer.debug.PathfindingRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.core.Vec3i;
import net.minecraft.network.protocol.common.custom.BrainDebugPayload;
import net.minecraft.network.protocol.game.DebugEntityNameGenerator;
import net.minecraft.world.entity.Entity;
import org.slf4j.Logger;

public class BrainDebugRenderer
implements DebugRenderer.SimpleDebugRenderer {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final boolean SHOW_NAME_FOR_ALL = true;
    private static final boolean SHOW_PROFESSION_FOR_ALL = false;
    private static final boolean SHOW_BEHAVIORS_FOR_ALL = false;
    private static final boolean SHOW_ACTIVITIES_FOR_ALL = false;
    private static final boolean SHOW_INVENTORY_FOR_ALL = false;
    private static final boolean SHOW_GOSSIPS_FOR_ALL = false;
    private static final boolean SHOW_PATH_FOR_ALL = false;
    private static final boolean SHOW_HEALTH_FOR_ALL = false;
    private static final boolean SHOW_WANTS_GOLEM_FOR_ALL = true;
    private static final boolean SHOW_ANGER_LEVEL_FOR_ALL = false;
    private static final boolean SHOW_NAME_FOR_SELECTED = true;
    private static final boolean SHOW_PROFESSION_FOR_SELECTED = true;
    private static final boolean SHOW_BEHAVIORS_FOR_SELECTED = true;
    private static final boolean SHOW_ACTIVITIES_FOR_SELECTED = true;
    private static final boolean SHOW_MEMORIES_FOR_SELECTED = true;
    private static final boolean SHOW_INVENTORY_FOR_SELECTED = true;
    private static final boolean SHOW_GOSSIPS_FOR_SELECTED = true;
    private static final boolean SHOW_PATH_FOR_SELECTED = true;
    private static final boolean SHOW_HEALTH_FOR_SELECTED = true;
    private static final boolean SHOW_WANTS_GOLEM_FOR_SELECTED = true;
    private static final boolean SHOW_ANGER_LEVEL_FOR_SELECTED = true;
    private static final boolean SHOW_POI_INFO = true;
    private static final int MAX_RENDER_DIST_FOR_BRAIN_INFO = 30;
    private static final int MAX_RENDER_DIST_FOR_POI_INFO = 30;
    private static final int MAX_TARGETING_DIST = 8;
    private static final float TEXT_SCALE = 0.02f;
    private static final int CYAN = -16711681;
    private static final int GRAY = -3355444;
    private static final int PINK = -98404;
    private static final int ORANGE = -23296;
    private final Minecraft minecraft;
    private final Map<BlockPos, PoiInfo> pois = Maps.newHashMap();
    private final Map<UUID, BrainDebugPayload.BrainDump> brainDumpsPerEntity = Maps.newHashMap();
    @Nullable
    private UUID lastLookedAtUuid;

    public BrainDebugRenderer(Minecraft $$0) {
        this.minecraft = $$0;
    }

    @Override
    public void clear() {
        this.pois.clear();
        this.brainDumpsPerEntity.clear();
        this.lastLookedAtUuid = null;
    }

    public void addPoi(PoiInfo $$0) {
        this.pois.put($$0.pos, $$0);
    }

    public void removePoi(BlockPos $$0) {
        this.pois.remove($$0);
    }

    public void setFreeTicketCount(BlockPos $$0, int $$1) {
        PoiInfo $$2 = this.pois.get($$0);
        if ($$2 == null) {
            LOGGER.warn("Strange, setFreeTicketCount was called for an unknown POI: {}", (Object)$$0);
            return;
        }
        $$2.freeTicketCount = $$1;
    }

    public void addOrUpdateBrainDump(BrainDebugPayload.BrainDump $$0) {
        this.brainDumpsPerEntity.put($$0.uuid(), $$0);
    }

    public void removeBrainDump(int $$0) {
        this.brainDumpsPerEntity.values().removeIf($$1 -> $$1.id() == $$0);
    }

    @Override
    public void render(PoseStack $$0, MultiBufferSource $$1, double $$2, double $$3, double $$4) {
        this.clearRemovedEntities();
        this.doRender($$0, $$1, $$2, $$3, $$4);
        if (!this.minecraft.player.isSpectator()) {
            this.updateLastLookedAtUuid();
        }
    }

    private void clearRemovedEntities() {
        this.brainDumpsPerEntity.entrySet().removeIf($$0 -> {
            Entity $$1 = this.minecraft.level.getEntity(((BrainDebugPayload.BrainDump)((Object)((Object)$$0.getValue()))).id());
            return $$1 == null || $$1.isRemoved();
        });
    }

    private void doRender(PoseStack $$0, MultiBufferSource $$1, double $$2, double $$32, double $$42) {
        BlockPos $$52 = BlockPos.containing($$2, $$32, $$42);
        this.brainDumpsPerEntity.values().forEach($$5 -> {
            if (this.isPlayerCloseEnoughToMob((BrainDebugPayload.BrainDump)((Object)$$5))) {
                this.renderBrainInfo($$0, $$1, (BrainDebugPayload.BrainDump)((Object)$$5), $$2, $$32, $$42);
            }
        });
        for (BlockPos $$6 : this.pois.keySet()) {
            if (!$$52.closerThan($$6, 30.0)) continue;
            BrainDebugRenderer.highlightPoi($$0, $$1, $$6);
        }
        this.pois.values().forEach($$3 -> {
            if ($$52.closerThan($$3.pos, 30.0)) {
                this.renderPoiInfo($$0, $$1, (PoiInfo)$$3);
            }
        });
        this.getGhostPois().forEach(($$3, $$4) -> {
            if ($$52.closerThan((Vec3i)$$3, 30.0)) {
                this.renderGhostPoi($$0, $$1, (BlockPos)$$3, (List<String>)$$4);
            }
        });
    }

    private static void highlightPoi(PoseStack $$0, MultiBufferSource $$1, BlockPos $$2) {
        float $$3 = 0.05f;
        DebugRenderer.renderFilledBox($$0, $$1, $$2, 0.05f, 0.2f, 0.2f, 1.0f, 0.3f);
    }

    private void renderGhostPoi(PoseStack $$0, MultiBufferSource $$1, BlockPos $$2, List<String> $$3) {
        float $$4 = 0.05f;
        DebugRenderer.renderFilledBox($$0, $$1, $$2, 0.05f, 0.2f, 0.2f, 1.0f, 0.3f);
        BrainDebugRenderer.renderTextOverPos($$0, $$1, String.valueOf($$3), $$2, 0, -256);
        BrainDebugRenderer.renderTextOverPos($$0, $$1, "Ghost POI", $$2, 1, -65536);
    }

    private void renderPoiInfo(PoseStack $$0, MultiBufferSource $$1, PoiInfo $$2) {
        int $$3 = 0;
        Set<String> $$4 = this.getTicketHolderNames($$2);
        if ($$4.size() < 4) {
            BrainDebugRenderer.renderTextOverPoi($$0, $$1, "Owners: " + String.valueOf($$4), $$2, $$3, -256);
        } else {
            BrainDebugRenderer.renderTextOverPoi($$0, $$1, $$4.size() + " ticket holders", $$2, $$3, -256);
        }
        ++$$3;
        Set<String> $$5 = this.getPotentialTicketHolderNames($$2);
        if ($$5.size() < 4) {
            BrainDebugRenderer.renderTextOverPoi($$0, $$1, "Candidates: " + String.valueOf($$5), $$2, $$3, -23296);
        } else {
            BrainDebugRenderer.renderTextOverPoi($$0, $$1, $$5.size() + " potential owners", $$2, $$3, -23296);
        }
        BrainDebugRenderer.renderTextOverPoi($$0, $$1, "Free tickets: " + $$2.freeTicketCount, $$2, ++$$3, -256);
        BrainDebugRenderer.renderTextOverPoi($$0, $$1, $$2.type, $$2, ++$$3, -1);
    }

    private void renderPath(PoseStack $$0, MultiBufferSource $$1, BrainDebugPayload.BrainDump $$2, double $$3, double $$4, double $$5) {
        if ($$2.path() != null) {
            PathfindingRenderer.renderPath($$0, $$1, $$2.path(), 0.5f, false, false, $$3, $$4, $$5);
        }
    }

    private void renderBrainInfo(PoseStack $$0, MultiBufferSource $$1, BrainDebugPayload.BrainDump $$2, double $$3, double $$4, double $$5) {
        boolean $$6 = this.isMobSelected($$2);
        int $$7 = 0;
        BrainDebugRenderer.renderTextOverMob($$0, $$1, $$2.pos(), $$7, $$2.name(), -1, 0.03f);
        ++$$7;
        if ($$6) {
            BrainDebugRenderer.renderTextOverMob($$0, $$1, $$2.pos(), $$7, $$2.profession() + " " + $$2.xp() + " xp", -1, 0.02f);
            ++$$7;
        }
        if ($$6) {
            int $$8 = $$2.health() < $$2.maxHealth() ? -23296 : -1;
            BrainDebugRenderer.renderTextOverMob($$0, $$1, $$2.pos(), $$7, "health: " + String.format(Locale.ROOT, "%.1f", Float.valueOf($$2.health())) + " / " + String.format(Locale.ROOT, "%.1f", Float.valueOf($$2.maxHealth())), $$8, 0.02f);
            ++$$7;
        }
        if ($$6 && !$$2.inventory().equals("")) {
            BrainDebugRenderer.renderTextOverMob($$0, $$1, $$2.pos(), $$7, $$2.inventory(), -98404, 0.02f);
            ++$$7;
        }
        if ($$6) {
            for (String $$9 : $$2.behaviors()) {
                BrainDebugRenderer.renderTextOverMob($$0, $$1, $$2.pos(), $$7, $$9, -16711681, 0.02f);
                ++$$7;
            }
        }
        if ($$6) {
            for (String $$10 : $$2.activities()) {
                BrainDebugRenderer.renderTextOverMob($$0, $$1, $$2.pos(), $$7, $$10, -16711936, 0.02f);
                ++$$7;
            }
        }
        if ($$2.wantsGolem()) {
            BrainDebugRenderer.renderTextOverMob($$0, $$1, $$2.pos(), $$7, "Wants Golem", -23296, 0.02f);
            ++$$7;
        }
        if ($$6 && $$2.angerLevel() != -1) {
            BrainDebugRenderer.renderTextOverMob($$0, $$1, $$2.pos(), $$7, "Anger Level: " + $$2.angerLevel(), -98404, 0.02f);
            ++$$7;
        }
        if ($$6) {
            for (String $$11 : $$2.gossips()) {
                if ($$11.startsWith($$2.name())) {
                    BrainDebugRenderer.renderTextOverMob($$0, $$1, $$2.pos(), $$7, $$11, -1, 0.02f);
                } else {
                    BrainDebugRenderer.renderTextOverMob($$0, $$1, $$2.pos(), $$7, $$11, -23296, 0.02f);
                }
                ++$$7;
            }
        }
        if ($$6) {
            for (String $$12 : Lists.reverse($$2.memories())) {
                BrainDebugRenderer.renderTextOverMob($$0, $$1, $$2.pos(), $$7, $$12, -3355444, 0.02f);
                ++$$7;
            }
        }
        if ($$6) {
            this.renderPath($$0, $$1, $$2, $$3, $$4, $$5);
        }
    }

    private static void renderTextOverPoi(PoseStack $$0, MultiBufferSource $$1, String $$2, PoiInfo $$3, int $$4, int $$5) {
        BrainDebugRenderer.renderTextOverPos($$0, $$1, $$2, $$3.pos, $$4, $$5);
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

    private Set<String> getTicketHolderNames(PoiInfo $$0) {
        return this.getTicketHolders($$0.pos).stream().map(DebugEntityNameGenerator::getEntityName).collect(Collectors.toSet());
    }

    private Set<String> getPotentialTicketHolderNames(PoiInfo $$0) {
        return this.getPotentialTicketHolders($$0.pos).stream().map(DebugEntityNameGenerator::getEntityName).collect(Collectors.toSet());
    }

    private boolean isMobSelected(BrainDebugPayload.BrainDump $$0) {
        return Objects.equals(this.lastLookedAtUuid, $$0.uuid());
    }

    private boolean isPlayerCloseEnoughToMob(BrainDebugPayload.BrainDump $$0) {
        LocalPlayer $$1 = this.minecraft.player;
        BlockPos $$2 = BlockPos.containing($$1.getX(), $$0.pos().y(), $$1.getZ());
        BlockPos $$3 = BlockPos.containing($$0.pos());
        return $$2.closerThan($$3, 30.0);
    }

    private Collection<UUID> getTicketHolders(BlockPos $$0) {
        return this.brainDumpsPerEntity.values().stream().filter($$1 -> $$1.hasPoi($$0)).map(BrainDebugPayload.BrainDump::uuid).collect(Collectors.toSet());
    }

    private Collection<UUID> getPotentialTicketHolders(BlockPos $$0) {
        return this.brainDumpsPerEntity.values().stream().filter($$1 -> $$1.hasPotentialPoi($$0)).map(BrainDebugPayload.BrainDump::uuid).collect(Collectors.toSet());
    }

    private Map<BlockPos, List<String>> getGhostPois() {
        HashMap<BlockPos, List<String>> $$02 = Maps.newHashMap();
        for (BrainDebugPayload.BrainDump $$1 : this.brainDumpsPerEntity.values()) {
            for (BlockPos $$2 : Iterables.concat($$1.pois(), $$1.potentialPois())) {
                if (this.pois.containsKey($$2)) continue;
                $$02.computeIfAbsent($$2, $$0 -> Lists.newArrayList()).add($$1.name());
            }
        }
        return $$02;
    }

    private void updateLastLookedAtUuid() {
        DebugRenderer.getTargetedEntity(this.minecraft.getCameraEntity(), 8).ifPresent($$0 -> {
            this.lastLookedAtUuid = $$0.getUUID();
        });
    }

    public static class PoiInfo {
        public final BlockPos pos;
        public final String type;
        public int freeTicketCount;

        public PoiInfo(BlockPos $$0, String $$1, int $$2) {
            this.pos = $$0;
            this.type = $$1;
            this.freeTicketCount = $$2;
        }
    }
}

