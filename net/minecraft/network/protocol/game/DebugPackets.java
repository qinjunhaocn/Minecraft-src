/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  it.unimi.dsi.fastutil.objects.Object2IntMap
 */
package net.minecraft.network.protocol.game;

import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.GameEventDebugPayload;
import net.minecraft.network.protocol.common.custom.GameTestAddMarkerDebugPayload;
import net.minecraft.network.protocol.common.custom.GameTestClearMarkersDebugPayload;
import net.minecraft.network.protocol.common.custom.GoalDebugPayload;
import net.minecraft.network.protocol.common.custom.RedstoneWireOrientationsDebugPayload;
import net.minecraft.network.protocol.game.DebugEntityNameGenerator;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.StringUtil;
import net.minecraft.world.Nameable;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.behavior.BlockPosTracker;
import net.minecraft.world.entity.ai.behavior.EntityTracker;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.entity.ai.memory.ExpirableValue;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.ai.village.poi.PoiRecord;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.monster.breeze.Breeze;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEventListener;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;

public class DebugPackets {
    private static final Logger LOGGER = LogUtils.getLogger();

    public static void sendGameTestAddMarker(ServerLevel $$0, BlockPos $$1, String $$2, int $$3, int $$4) {
        DebugPackets.sendPacketToAllPlayers($$0, new GameTestAddMarkerDebugPayload($$1, $$3, $$2, $$4));
    }

    public static void sendGameTestClearPacket(ServerLevel $$0) {
        DebugPackets.sendPacketToAllPlayers($$0, new GameTestClearMarkersDebugPayload());
    }

    public static void sendPoiPacketsForChunk(ServerLevel $$0, ChunkPos $$1) {
    }

    public static void sendPoiAddedPacket(ServerLevel $$0, BlockPos $$1) {
        DebugPackets.sendVillageSectionsPacket($$0, $$1);
    }

    public static void sendPoiRemovedPacket(ServerLevel $$0, BlockPos $$1) {
        DebugPackets.sendVillageSectionsPacket($$0, $$1);
    }

    public static void sendPoiTicketCountPacket(ServerLevel $$0, BlockPos $$1) {
        DebugPackets.sendVillageSectionsPacket($$0, $$1);
    }

    private static void sendVillageSectionsPacket(ServerLevel $$0, BlockPos $$1) {
    }

    public static void sendPathFindingPacket(Level $$0, Mob $$1, @Nullable Path $$2, float $$3) {
    }

    public static void sendNeighborsUpdatePacket(Level $$0, BlockPos $$1) {
    }

    public static void sendWireUpdates(Level $$0, RedstoneWireOrientationsDebugPayload $$1) {
        if ($$0 instanceof ServerLevel) {
            ServerLevel $$2 = (ServerLevel)$$0;
            DebugPackets.sendPacketToAllPlayers($$2, $$1);
        }
    }

    public static void sendStructurePacket(WorldGenLevel $$0, StructureStart $$1) {
    }

    public static void sendGoalSelector(Level $$0, Mob $$1, GoalSelector $$2) {
    }

    public static void sendRaids(ServerLevel $$0, Collection<Raid> $$1) {
    }

    public static void sendEntityBrain(LivingEntity $$0) {
    }

    public static void sendBeeInfo(Bee $$0) {
    }

    public static void sendBreezeInfo(Breeze $$0) {
    }

    public static void sendGameEventInfo(Level $$0, Holder<GameEvent> $$1, Vec3 $$2) {
    }

    public static void sendGameEventListenerInfo(Level $$0, GameEventListener $$1) {
    }

    public static void sendHiveInfo(Level $$0, BlockPos $$1, BlockState $$2, BeehiveBlockEntity $$3) {
    }

    private static List<String> getMemoryDescriptions(LivingEntity $$0, long $$1) {
        Map<MemoryModuleType<?>, Optional<ExpirableValue<?>>> $$2 = $$0.getBrain().getMemories();
        ArrayList<String> $$3 = Lists.newArrayList();
        for (Map.Entry<MemoryModuleType<?>, Optional<ExpirableValue<?>>> $$4 : $$2.entrySet()) {
            String $$13;
            MemoryModuleType<?> $$5 = $$4.getKey();
            Optional<ExpirableValue<?>> $$6 = $$4.getValue();
            if ($$6.isPresent()) {
                ExpirableValue<?> $$7 = $$6.get();
                Object $$8 = $$7.getValue();
                if ($$5 == MemoryModuleType.HEARD_BELL_TIME) {
                    long $$9 = $$1 - (Long)$$8;
                    String $$10 = $$9 + " ticks ago";
                } else if ($$7.canExpire()) {
                    String $$11 = DebugPackets.getShortDescription((ServerLevel)$$0.level(), $$8) + " (ttl: " + $$7.getTimeToLive() + ")";
                } else {
                    String $$12 = DebugPackets.getShortDescription((ServerLevel)$$0.level(), $$8);
                }
            } else {
                $$13 = "-";
            }
            $$3.add(BuiltInRegistries.MEMORY_MODULE_TYPE.getKey($$5).getPath() + ": " + $$13);
        }
        $$3.sort(String::compareTo);
        return $$3;
    }

    private static String getShortDescription(ServerLevel $$0, @Nullable Object $$1) {
        if ($$1 == null) {
            return "-";
        }
        if ($$1 instanceof UUID) {
            return DebugPackets.getShortDescription($$0, $$0.getEntity((UUID)$$1));
        }
        if ($$1 instanceof LivingEntity) {
            Entity $$2 = (Entity)$$1;
            return DebugEntityNameGenerator.getEntityName($$2);
        }
        if ($$1 instanceof Nameable) {
            return ((Nameable)$$1).getName().getString();
        }
        if ($$1 instanceof WalkTarget) {
            return DebugPackets.getShortDescription($$0, ((WalkTarget)$$1).getTarget());
        }
        if ($$1 instanceof EntityTracker) {
            return DebugPackets.getShortDescription($$0, ((EntityTracker)$$1).getEntity());
        }
        if ($$1 instanceof GlobalPos) {
            return DebugPackets.getShortDescription($$0, ((GlobalPos)((Object)$$1)).pos());
        }
        if ($$1 instanceof BlockPosTracker) {
            return DebugPackets.getShortDescription($$0, ((BlockPosTracker)$$1).currentBlockPosition());
        }
        if ($$1 instanceof DamageSource) {
            Entity $$3 = ((DamageSource)$$1).getEntity();
            return $$3 == null ? $$1.toString() : DebugPackets.getShortDescription($$0, $$3);
        }
        if ($$1 instanceof Collection) {
            ArrayList<String> $$4 = Lists.newArrayList();
            for (Object $$5 : (Iterable)$$1) {
                $$4.add(DebugPackets.getShortDescription($$0, $$5));
            }
            return ((Object)$$4).toString();
        }
        return $$1.toString();
    }

    private static void sendPacketToAllPlayers(ServerLevel $$0, CustomPacketPayload $$1) {
        ClientboundCustomPayloadPacket $$2 = new ClientboundCustomPayloadPacket($$1);
        for (ServerPlayer $$3 : $$0.players()) {
            $$3.connection.send($$2);
        }
    }

    private static /* synthetic */ void lambda$sendGameEventInfo$7(ServerLevel $$0, Vec3 $$1, ResourceKey $$2) {
        DebugPackets.sendPacketToAllPlayers($$0, new GameEventDebugPayload($$2, $$1));
    }

    private static /* synthetic */ void lambda$sendEntityBrain$6(List $$0, UUID $$1, Object2IntMap $$22) {
        String $$32 = DebugEntityNameGenerator.getEntityName($$1);
        $$22.forEach(($$2, $$3) -> $$0.add($$32 + ": " + String.valueOf($$2) + ": " + $$3));
    }

    private static /* synthetic */ String lambda$sendEntityBrain$4(String $$0) {
        return StringUtil.truncateStringIfNecessary($$0, 255, true);
    }

    private static /* synthetic */ void lambda$sendGoalSelector$3(List $$0, WrappedGoal $$1) {
        $$0.add(new GoalDebugPayload.DebugGoal($$1.getPriority(), $$1.isRunning(), $$1.getGoal().getClass().getSimpleName()));
    }

    private static /* synthetic */ String lambda$sendPoiAddedPacket$2(ResourceKey $$0) {
        return $$0.location().toString();
    }

    private static /* synthetic */ void lambda$sendPoiPacketsForChunk$1(ServerLevel $$0, PoiRecord $$1) {
        DebugPackets.sendPoiAddedPacket($$0, $$1.getPos());
    }

    private static /* synthetic */ boolean lambda$sendPoiPacketsForChunk$0(Holder $$0) {
        return true;
    }
}

