/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  it.unimi.dsi.fastutil.longs.Long2ObjectMap$Entry
 *  it.unimi.dsi.fastutil.longs.Long2ObjectMaps
 *  it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap
 *  it.unimi.dsi.fastutil.longs.LongOpenHashSet
 *  it.unimi.dsi.fastutil.longs.LongSet
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  it.unimi.dsi.fastutil.objects.ObjectIterator
 */
package net.minecraft.world.level;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMaps;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ChunkLevel;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.FullChunkStatus;
import net.minecraft.server.level.Ticket;
import net.minecraft.server.level.TicketType;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import org.slf4j.Logger;

public class TicketStorage
extends SavedData {
    private static final int INITIAL_TICKET_LIST_CAPACITY = 4;
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Codec<Pair<ChunkPos, Ticket>> TICKET_ENTRY = Codec.mapPair((MapCodec)ChunkPos.CODEC.fieldOf("chunk_pos"), Ticket.CODEC).codec();
    public static final Codec<TicketStorage> CODEC = RecordCodecBuilder.create($$0 -> $$0.group((App)TICKET_ENTRY.listOf().optionalFieldOf("tickets", (Object)List.of()).forGetter(TicketStorage::packTickets)).apply((Applicative)$$0, TicketStorage::fromPacked));
    public static final SavedDataType<TicketStorage> TYPE = new SavedDataType<TicketStorage>("chunks", TicketStorage::new, CODEC, DataFixTypes.SAVED_DATA_FORCED_CHUNKS);
    private final Long2ObjectOpenHashMap<List<Ticket>> tickets;
    private final Long2ObjectOpenHashMap<List<Ticket>> deactivatedTickets;
    private LongSet chunksWithForcedTickets = new LongOpenHashSet();
    @Nullable
    private ChunkUpdated loadingChunkUpdatedListener;
    @Nullable
    private ChunkUpdated simulationChunkUpdatedListener;

    private TicketStorage(Long2ObjectOpenHashMap<List<Ticket>> $$0, Long2ObjectOpenHashMap<List<Ticket>> $$1) {
        this.tickets = $$0;
        this.deactivatedTickets = $$1;
        this.updateForcedChunks();
    }

    public TicketStorage() {
        this((Long2ObjectOpenHashMap<List<Ticket>>)new Long2ObjectOpenHashMap(4), (Long2ObjectOpenHashMap<List<Ticket>>)new Long2ObjectOpenHashMap());
    }

    private static TicketStorage fromPacked(List<Pair<ChunkPos, Ticket>> $$02) {
        Long2ObjectOpenHashMap $$1 = new Long2ObjectOpenHashMap();
        for (Pair<ChunkPos, Ticket> $$2 : $$02) {
            ChunkPos $$3 = (ChunkPos)$$2.getFirst();
            List $$4 = (List)$$1.computeIfAbsent($$3.toLong(), $$0 -> new ObjectArrayList(4));
            $$4.add((Ticket)$$2.getSecond());
        }
        return new TicketStorage((Long2ObjectOpenHashMap<List<Ticket>>)new Long2ObjectOpenHashMap(4), (Long2ObjectOpenHashMap<List<Ticket>>)$$1);
    }

    private List<Pair<ChunkPos, Ticket>> packTickets() {
        ArrayList<Pair<ChunkPos, Ticket>> $$0 = new ArrayList<Pair<ChunkPos, Ticket>>();
        this.forEachTicket(($$1, $$2) -> {
            if ($$2.getType().persist()) {
                $$0.add(new Pair($$1, $$2));
            }
        });
        return $$0;
    }

    private void forEachTicket(BiConsumer<ChunkPos, Ticket> $$0) {
        TicketStorage.forEachTicket($$0, this.tickets);
        TicketStorage.forEachTicket($$0, this.deactivatedTickets);
    }

    private static void forEachTicket(BiConsumer<ChunkPos, Ticket> $$0, Long2ObjectOpenHashMap<List<Ticket>> $$1) {
        for (Long2ObjectMap.Entry $$2 : Long2ObjectMaps.fastIterable($$1)) {
            ChunkPos $$3 = new ChunkPos($$2.getLongKey());
            for (Ticket $$4 : (List)$$2.getValue()) {
                $$0.accept($$3, $$4);
            }
        }
    }

    public void activateAllDeactivatedTickets() {
        for (Long2ObjectMap.Entry $$0 : Long2ObjectMaps.fastIterable(this.deactivatedTickets)) {
            for (Ticket $$1 : (List)$$0.getValue()) {
                this.addTicket($$0.getLongKey(), $$1);
            }
        }
        this.deactivatedTickets.clear();
    }

    public void setLoadingChunkUpdatedListener(@Nullable ChunkUpdated $$0) {
        this.loadingChunkUpdatedListener = $$0;
    }

    public void setSimulationChunkUpdatedListener(@Nullable ChunkUpdated $$0) {
        this.simulationChunkUpdatedListener = $$0;
    }

    public boolean hasTickets() {
        return !this.tickets.isEmpty();
    }

    public List<Ticket> getTickets(long $$0) {
        return (List)this.tickets.getOrDefault($$0, (Object)List.of());
    }

    private List<Ticket> getOrCreateTickets(long $$02) {
        return (List)this.tickets.computeIfAbsent($$02, $$0 -> new ObjectArrayList(4));
    }

    public void addTicketWithRadius(TicketType $$0, ChunkPos $$1, int $$2) {
        Ticket $$3 = new Ticket($$0, ChunkLevel.byStatus(FullChunkStatus.FULL) - $$2);
        this.addTicket($$1.toLong(), $$3);
    }

    public void addTicket(Ticket $$0, ChunkPos $$1) {
        this.addTicket($$1.toLong(), $$0);
    }

    public boolean addTicket(long $$0, Ticket $$1) {
        List<Ticket> $$2 = this.getOrCreateTickets($$0);
        for (Ticket $$3 : $$2) {
            if (!TicketStorage.isTicketSameTypeAndLevel($$1, $$3)) continue;
            $$3.resetTicksLeft();
            this.setDirty();
            return false;
        }
        int $$4 = TicketStorage.getTicketLevelAt($$2, true);
        int $$5 = TicketStorage.getTicketLevelAt($$2, false);
        $$2.add($$1);
        if ($$1.getType().doesSimulate() && $$1.getTicketLevel() < $$4 && this.simulationChunkUpdatedListener != null) {
            this.simulationChunkUpdatedListener.update($$0, $$1.getTicketLevel(), true);
        }
        if ($$1.getType().doesLoad() && $$1.getTicketLevel() < $$5 && this.loadingChunkUpdatedListener != null) {
            this.loadingChunkUpdatedListener.update($$0, $$1.getTicketLevel(), true);
        }
        if ($$1.getType().equals((Object)TicketType.FORCED)) {
            this.chunksWithForcedTickets.add($$0);
        }
        this.setDirty();
        return true;
    }

    private static boolean isTicketSameTypeAndLevel(Ticket $$0, Ticket $$1) {
        return $$1.getType() == $$0.getType() && $$1.getTicketLevel() == $$0.getTicketLevel();
    }

    public int getTicketLevelAt(long $$0, boolean $$1) {
        return TicketStorage.getTicketLevelAt(this.getTickets($$0), $$1);
    }

    private static int getTicketLevelAt(List<Ticket> $$0, boolean $$1) {
        Ticket $$2 = TicketStorage.getLowestTicket($$0, $$1);
        return $$2 == null ? ChunkLevel.MAX_LEVEL + 1 : $$2.getTicketLevel();
    }

    @Nullable
    private static Ticket getLowestTicket(@Nullable List<Ticket> $$0, boolean $$1) {
        if ($$0 == null) {
            return null;
        }
        Ticket $$2 = null;
        for (Ticket $$3 : $$0) {
            if ($$2 != null && $$3.getTicketLevel() >= $$2.getTicketLevel()) continue;
            if ($$1 && $$3.getType().doesSimulate()) {
                $$2 = $$3;
                continue;
            }
            if ($$1 || !$$3.getType().doesLoad()) continue;
            $$2 = $$3;
        }
        return $$2;
    }

    public void removeTicketWithRadius(TicketType $$0, ChunkPos $$1, int $$2) {
        Ticket $$3 = new Ticket($$0, ChunkLevel.byStatus(FullChunkStatus.FULL) - $$2);
        this.removeTicket($$1.toLong(), $$3);
    }

    public void removeTicket(Ticket $$0, ChunkPos $$1) {
        this.removeTicket($$1.toLong(), $$0);
    }

    public boolean removeTicket(long $$0, Ticket $$1) {
        List $$2 = (List)this.tickets.get($$0);
        if ($$2 == null) {
            return false;
        }
        boolean $$3 = false;
        Iterator $$4 = $$2.iterator();
        while ($$4.hasNext()) {
            Ticket $$5 = (Ticket)$$4.next();
            if (!TicketStorage.isTicketSameTypeAndLevel($$1, $$5)) continue;
            $$4.remove();
            $$3 = true;
            break;
        }
        if (!$$3) {
            return false;
        }
        if ($$2.isEmpty()) {
            this.tickets.remove($$0);
        }
        if ($$1.getType().doesSimulate() && this.simulationChunkUpdatedListener != null) {
            this.simulationChunkUpdatedListener.update($$0, TicketStorage.getTicketLevelAt($$2, true), false);
        }
        if ($$1.getType().doesLoad() && this.loadingChunkUpdatedListener != null) {
            this.loadingChunkUpdatedListener.update($$0, TicketStorage.getTicketLevelAt($$2, false), false);
        }
        if ($$1.getType().equals((Object)TicketType.FORCED)) {
            this.updateForcedChunks();
        }
        this.setDirty();
        return true;
    }

    private void updateForcedChunks() {
        this.chunksWithForcedTickets = this.getAllChunksWithTicketThat($$0 -> $$0.getType().equals((Object)TicketType.FORCED));
    }

    public String getTicketDebugString(long $$0, boolean $$1) {
        List<Ticket> $$2 = this.getTickets($$0);
        Ticket $$3 = TicketStorage.getLowestTicket($$2, $$1);
        return $$3 == null ? "no_ticket" : $$3.toString();
    }

    public void purgeStaleTickets(ChunkMap $$0) {
        this.removeTicketIf(($$1, $$2) -> {
            boolean $$4;
            ChunkHolder $$3 = $$0.getUpdatingChunkIfPresent((long)$$1);
            boolean bl = $$4 = $$3 != null && !$$3.isReadyForSaving() && $$2.getType().doesSimulate();
            if ($$4) {
                return false;
            }
            $$2.decreaseTicksLeft();
            return $$2.isTimedOut();
        }, null);
        this.setDirty();
    }

    public void deactivateTicketsOnClosing() {
        this.removeTicketIf(($$0, $$1) -> $$1.getType() != TicketType.UNKNOWN, this.deactivatedTickets);
    }

    public void removeTicketIf(BiPredicate<Long, Ticket> $$0, @Nullable Long2ObjectOpenHashMap<List<Ticket>> $$12) {
        ObjectIterator $$2 = this.tickets.long2ObjectEntrySet().fastIterator();
        boolean $$3 = false;
        while ($$2.hasNext()) {
            Long2ObjectMap.Entry $$4 = (Long2ObjectMap.Entry)$$2.next();
            Iterator $$5 = ((List)$$4.getValue()).iterator();
            long $$6 = $$4.getLongKey();
            boolean $$7 = false;
            boolean $$8 = false;
            while ($$5.hasNext()) {
                Ticket $$9 = (Ticket)$$5.next();
                if (!$$0.test($$6, $$9)) continue;
                if ($$12 != null) {
                    List $$10 = (List)$$12.computeIfAbsent($$6, $$1 -> new ObjectArrayList(((List)$$4.getValue()).size()));
                    $$10.add($$9);
                }
                $$5.remove();
                if ($$9.getType().doesLoad()) {
                    $$8 = true;
                }
                if ($$9.getType().doesSimulate()) {
                    $$7 = true;
                }
                if (!$$9.getType().equals((Object)TicketType.FORCED)) continue;
                $$3 = true;
            }
            if (!$$8 && !$$7) continue;
            if ($$8 && this.loadingChunkUpdatedListener != null) {
                this.loadingChunkUpdatedListener.update($$6, TicketStorage.getTicketLevelAt((List)$$4.getValue(), false), false);
            }
            if ($$7 && this.simulationChunkUpdatedListener != null) {
                this.simulationChunkUpdatedListener.update($$6, TicketStorage.getTicketLevelAt((List)$$4.getValue(), true), false);
            }
            this.setDirty();
            if (!((List)$$4.getValue()).isEmpty()) continue;
            $$2.remove();
        }
        if ($$3) {
            this.updateForcedChunks();
        }
    }

    public void replaceTicketLevelOfType(int $$0, TicketType $$1) {
        ArrayList<Pair> $$2 = new ArrayList<Pair>();
        for (Long2ObjectMap.Entry $$3 : this.tickets.long2ObjectEntrySet()) {
            for (Ticket $$4 : (List)$$3.getValue()) {
                if ($$4.getType() != $$1) continue;
                $$2.add(Pair.of((Object)$$4, (Object)$$3.getLongKey()));
            }
        }
        for (Pair $$5 : $$2) {
            Long $$6 = (Long)$$5.getSecond();
            Ticket $$7 = (Ticket)$$5.getFirst();
            this.removeTicket($$6, $$7);
            TicketType $$8 = $$7.getType();
            this.addTicket($$6, new Ticket($$8, $$0));
        }
    }

    public boolean updateChunkForced(ChunkPos $$0, boolean $$1) {
        Ticket $$2 = new Ticket(TicketType.FORCED, ChunkMap.FORCED_TICKET_LEVEL);
        if ($$1) {
            return this.addTicket($$0.toLong(), $$2);
        }
        return this.removeTicket($$0.toLong(), $$2);
    }

    public LongSet getForceLoadedChunks() {
        return this.chunksWithForcedTickets;
    }

    private LongSet getAllChunksWithTicketThat(Predicate<Ticket> $$0) {
        LongOpenHashSet $$1 = new LongOpenHashSet();
        block0: for (Long2ObjectMap.Entry $$2 : Long2ObjectMaps.fastIterable(this.tickets)) {
            for (Ticket $$3 : (List)$$2.getValue()) {
                if (!$$0.test($$3)) continue;
                $$1.add($$2.getLongKey());
                continue block0;
            }
        }
        return $$1;
    }

    @FunctionalInterface
    public static interface ChunkUpdated {
        public void update(long var1, int var3, boolean var4);
    }
}

