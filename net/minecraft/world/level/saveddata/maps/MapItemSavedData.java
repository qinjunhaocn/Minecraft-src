/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  io.netty.buffer.ByteBuf
 *  java.lang.Record
 *  java.lang.runtime.ObjectMethods
 */
package net.minecraft.world.level.saveddata.maps;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundMapItemDataPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.MapDecorations;
import net.minecraft.world.item.component.MapItemColor;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import net.minecraft.world.level.saveddata.maps.MapBanner;
import net.minecraft.world.level.saveddata.maps.MapDecoration;
import net.minecraft.world.level.saveddata.maps.MapDecorationType;
import net.minecraft.world.level.saveddata.maps.MapDecorationTypes;
import net.minecraft.world.level.saveddata.maps.MapFrame;
import net.minecraft.world.level.saveddata.maps.MapId;
import org.slf4j.Logger;

public class MapItemSavedData
extends SavedData {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final int MAP_SIZE = 128;
    private static final int HALF_MAP_SIZE = 64;
    public static final int MAX_SCALE = 4;
    public static final int TRACKED_DECORATION_LIMIT = 256;
    private static final String FRAME_PREFIX = "frame-";
    public static final Codec<MapItemSavedData> CODEC = RecordCodecBuilder.create($$02 -> $$02.group((App)Level.RESOURCE_KEY_CODEC.fieldOf("dimension").forGetter($$0 -> $$0.dimension), (App)Codec.INT.fieldOf("xCenter").forGetter($$0 -> $$0.centerX), (App)Codec.INT.fieldOf("zCenter").forGetter($$0 -> $$0.centerZ), (App)Codec.BYTE.optionalFieldOf("scale", (Object)0).forGetter($$0 -> $$0.scale), (App)Codec.BYTE_BUFFER.fieldOf("colors").forGetter($$0 -> ByteBuffer.wrap($$0.colors)), (App)Codec.BOOL.optionalFieldOf("trackingPosition", (Object)true).forGetter($$0 -> $$0.trackingPosition), (App)Codec.BOOL.optionalFieldOf("unlimitedTracking", (Object)false).forGetter($$0 -> $$0.unlimitedTracking), (App)Codec.BOOL.optionalFieldOf("locked", (Object)false).forGetter($$0 -> $$0.locked), (App)MapBanner.CODEC.listOf().optionalFieldOf("banners", (Object)List.of()).forGetter($$0 -> List.copyOf($$0.bannerMarkers.values())), (App)MapFrame.CODEC.listOf().optionalFieldOf("frames", (Object)List.of()).forGetter($$0 -> List.copyOf($$0.frameMarkers.values()))).apply((Applicative)$$02, MapItemSavedData::new));
    public final int centerX;
    public final int centerZ;
    public final ResourceKey<Level> dimension;
    private final boolean trackingPosition;
    private final boolean unlimitedTracking;
    public final byte scale;
    public byte[] colors = new byte[16384];
    public final boolean locked;
    private final List<HoldingPlayer> carriedBy = Lists.newArrayList();
    private final Map<Player, HoldingPlayer> carriedByPlayers = Maps.newHashMap();
    private final Map<String, MapBanner> bannerMarkers = Maps.newHashMap();
    final Map<String, MapDecoration> decorations = Maps.newLinkedHashMap();
    private final Map<String, MapFrame> frameMarkers = Maps.newHashMap();
    private int trackedDecorationCount;

    public static SavedDataType<MapItemSavedData> type(MapId $$0) {
        return new SavedDataType<MapItemSavedData>($$0.key(), () -> {
            throw new IllegalStateException("Should never create an empty map saved data");
        }, CODEC, DataFixTypes.SAVED_DATA_MAP_DATA);
    }

    private MapItemSavedData(int $$0, int $$1, byte $$2, boolean $$3, boolean $$4, boolean $$5, ResourceKey<Level> $$6) {
        this.scale = $$2;
        this.centerX = $$0;
        this.centerZ = $$1;
        this.dimension = $$6;
        this.trackingPosition = $$3;
        this.unlimitedTracking = $$4;
        this.locked = $$5;
    }

    private MapItemSavedData(ResourceKey<Level> $$0, int $$1, int $$2, byte $$3, ByteBuffer $$4, boolean $$5, boolean $$6, boolean $$7, List<MapBanner> $$8, List<MapFrame> $$9) {
        this($$1, $$2, (byte)Mth.clamp($$3, 0, 4), $$5, $$6, $$7, $$0);
        if ($$4.array().length == 16384) {
            this.colors = $$4.array();
        }
        for (MapBanner $$10 : $$8) {
            this.bannerMarkers.put($$10.getId(), $$10);
            this.addDecoration($$10.getDecoration(), null, $$10.getId(), $$10.pos().getX(), $$10.pos().getZ(), 180.0, $$10.name().orElse(null));
        }
        for (MapFrame $$11 : $$9) {
            this.frameMarkers.put($$11.getId(), $$11);
            this.addDecoration(MapDecorationTypes.FRAME, null, MapItemSavedData.getFrameKey($$11.entityId()), $$11.pos().getX(), $$11.pos().getZ(), $$11.rotation(), null);
        }
    }

    public static MapItemSavedData createFresh(double $$0, double $$1, byte $$2, boolean $$3, boolean $$4, ResourceKey<Level> $$5) {
        int $$6 = 128 * (1 << $$2);
        int $$7 = Mth.floor(($$0 + 64.0) / (double)$$6);
        int $$8 = Mth.floor(($$1 + 64.0) / (double)$$6);
        int $$9 = $$7 * $$6 + $$6 / 2 - 64;
        int $$10 = $$8 * $$6 + $$6 / 2 - 64;
        return new MapItemSavedData($$9, $$10, $$2, $$3, $$4, false, $$5);
    }

    public static MapItemSavedData createForClient(byte $$0, boolean $$1, ResourceKey<Level> $$2) {
        return new MapItemSavedData(0, 0, $$0, false, false, $$1, $$2);
    }

    public MapItemSavedData locked() {
        MapItemSavedData $$0 = new MapItemSavedData(this.centerX, this.centerZ, this.scale, this.trackingPosition, this.unlimitedTracking, true, this.dimension);
        $$0.bannerMarkers.putAll(this.bannerMarkers);
        $$0.decorations.putAll(this.decorations);
        $$0.trackedDecorationCount = this.trackedDecorationCount;
        System.arraycopy(this.colors, 0, $$0.colors, 0, this.colors.length);
        return $$0;
    }

    public MapItemSavedData scaled() {
        return MapItemSavedData.createFresh(this.centerX, this.centerZ, (byte)Mth.clamp(this.scale + 1, 0, 4), this.trackingPosition, this.unlimitedTracking, this.dimension);
    }

    private static Predicate<ItemStack> mapMatcher(ItemStack $$0) {
        MapId $$1 = $$0.get(DataComponents.MAP_ID);
        return $$2 -> {
            if ($$2 == $$0) {
                return true;
            }
            return $$2.is($$0.getItem()) && Objects.equals($$1, $$2.get(DataComponents.MAP_ID));
        };
    }

    public void tickCarriedBy(Player $$0, ItemStack $$12) {
        if (!this.carriedByPlayers.containsKey($$0)) {
            HoldingPlayer $$22 = new HoldingPlayer($$0);
            this.carriedByPlayers.put($$0, $$22);
            this.carriedBy.add($$22);
        }
        Predicate<ItemStack> $$3 = MapItemSavedData.mapMatcher($$12);
        if (!$$0.getInventory().contains($$3)) {
            this.removeDecoration($$0.getName().getString());
        }
        for (int $$4 = 0; $$4 < this.carriedBy.size(); ++$$4) {
            HoldingPlayer $$5 = this.carriedBy.get($$4);
            Player $$6 = $$5.player;
            String $$7 = $$6.getName().getString();
            if ($$6.isRemoved() || !$$6.getInventory().contains($$3) && !$$12.isFramed()) {
                this.carriedByPlayers.remove($$6);
                this.carriedBy.remove($$5);
                this.removeDecoration($$7);
            } else if (!$$12.isFramed() && $$6.level().dimension() == this.dimension && this.trackingPosition) {
                this.addDecoration(MapDecorationTypes.PLAYER, $$6.level(), $$7, $$6.getX(), $$6.getZ(), $$6.getYRot(), null);
            }
            if ($$6.equals($$0) || !MapItemSavedData.hasMapInvisibilityItemEquipped($$6)) continue;
            this.removeDecoration($$7);
        }
        if ($$12.isFramed() && this.trackingPosition) {
            ItemFrame $$8 = $$12.getFrame();
            BlockPos $$9 = $$8.getPos();
            MapFrame $$10 = this.frameMarkers.get(MapFrame.frameId($$9));
            if ($$10 != null && $$8.getId() != $$10.entityId() && this.frameMarkers.containsKey($$10.getId())) {
                this.removeDecoration(MapItemSavedData.getFrameKey($$10.entityId()));
            }
            MapFrame $$11 = new MapFrame($$9, $$8.getDirection().get2DDataValue() * 90, $$8.getId());
            this.addDecoration(MapDecorationTypes.FRAME, $$0.level(), MapItemSavedData.getFrameKey($$8.getId()), $$9.getX(), $$9.getZ(), $$8.getDirection().get2DDataValue() * 90, null);
            MapFrame $$122 = this.frameMarkers.put($$11.getId(), $$11);
            if (!$$11.equals((Object)$$122)) {
                this.setDirty();
            }
        }
        MapDecorations $$13 = $$12.getOrDefault(DataComponents.MAP_DECORATIONS, MapDecorations.EMPTY);
        if (!this.decorations.keySet().containsAll($$13.decorations().keySet())) {
            $$13.decorations().forEach(($$1, $$2) -> {
                if (!this.decorations.containsKey($$1)) {
                    this.addDecoration($$2.type(), $$0.level(), (String)$$1, $$2.x(), $$2.z(), $$2.rotation(), null);
                }
            });
        }
    }

    private static boolean hasMapInvisibilityItemEquipped(Player $$0) {
        for (EquipmentSlot $$1 : EquipmentSlot.values()) {
            if ($$1 == EquipmentSlot.MAINHAND || $$1 == EquipmentSlot.OFFHAND || !$$0.getItemBySlot($$1).is(ItemTags.MAP_INVISIBILITY_EQUIPMENT)) continue;
            return true;
        }
        return false;
    }

    private void removeDecoration(String $$0) {
        MapDecoration $$1 = this.decorations.remove($$0);
        if ($$1 != null && $$1.type().value().trackCount()) {
            --this.trackedDecorationCount;
        }
        this.setDecorationsDirty();
    }

    public static void addTargetDecoration(ItemStack $$0, BlockPos $$1, String $$22, Holder<MapDecorationType> $$3) {
        MapDecorations.Entry $$4 = new MapDecorations.Entry($$3, $$1.getX(), $$1.getZ(), 180.0f);
        $$0.update(DataComponents.MAP_DECORATIONS, MapDecorations.EMPTY, $$2 -> $$2.withDecoration($$22, $$4));
        if ($$3.value().hasMapColor()) {
            $$0.set(DataComponents.MAP_COLOR, new MapItemColor($$3.value().mapColor()));
        }
    }

    private void addDecoration(Holder<MapDecorationType> $$0, @Nullable LevelAccessor $$1, String $$2, double $$3, double $$4, double $$5, @Nullable Component $$6) {
        MapDecoration $$12;
        int $$7 = 1 << this.scale;
        float $$8 = (float)($$3 - (double)this.centerX) / (float)$$7;
        float $$9 = (float)($$4 - (double)this.centerZ) / (float)$$7;
        MapDecorationLocation $$10 = this.calculateDecorationLocationAndType($$0, $$1, $$5, $$8, $$9);
        if ($$10 == null) {
            this.removeDecoration($$2);
            return;
        }
        MapDecoration $$11 = new MapDecoration($$10.type(), $$10.x(), $$10.y(), $$10.rot(), Optional.ofNullable($$6));
        if (!$$11.equals((Object)($$12 = this.decorations.put($$2, $$11)))) {
            if ($$12 != null && $$12.type().value().trackCount()) {
                --this.trackedDecorationCount;
            }
            if ($$10.type().value().trackCount()) {
                ++this.trackedDecorationCount;
            }
            this.setDecorationsDirty();
        }
    }

    @Nullable
    private MapDecorationLocation calculateDecorationLocationAndType(Holder<MapDecorationType> $$0, @Nullable LevelAccessor $$1, double $$2, float $$3, float $$4) {
        byte $$5 = MapItemSavedData.clampMapCoordinate($$3);
        byte $$6 = MapItemSavedData.clampMapCoordinate($$4);
        if ($$0.is(MapDecorationTypes.PLAYER)) {
            Pair<Holder<MapDecorationType>, Byte> $$7 = this.playerDecorationTypeAndRotation($$0, $$1, $$2, $$3, $$4);
            return $$7 == null ? null : new MapDecorationLocation((Holder)$$7.getFirst(), $$5, $$6, (Byte)$$7.getSecond());
        }
        if (MapItemSavedData.isInsideMap($$3, $$4) || this.unlimitedTracking) {
            return new MapDecorationLocation($$0, $$5, $$6, this.calculateRotation($$1, $$2));
        }
        return null;
    }

    @Nullable
    private Pair<Holder<MapDecorationType>, Byte> playerDecorationTypeAndRotation(Holder<MapDecorationType> $$0, @Nullable LevelAccessor $$1, double $$2, float $$3, float $$4) {
        if (MapItemSavedData.isInsideMap($$3, $$4)) {
            return Pair.of($$0, (Object)this.calculateRotation($$1, $$2));
        }
        Holder<MapDecorationType> $$5 = this.decorationTypeForPlayerOutsideMap($$3, $$4);
        if ($$5 == null) {
            return null;
        }
        return Pair.of($$5, (Object)0);
    }

    private byte calculateRotation(@Nullable LevelAccessor $$0, double $$1) {
        if (this.dimension == Level.NETHER && $$0 != null) {
            int $$2 = (int)($$0.getLevelData().getDayTime() / 10L);
            return (byte)($$2 * $$2 * 34187121 + $$2 * 121 >> 15 & 0xF);
        }
        double $$3 = $$1 < 0.0 ? $$1 - 8.0 : $$1 + 8.0;
        return (byte)($$3 * 16.0 / 360.0);
    }

    private static boolean isInsideMap(float $$0, float $$1) {
        int $$2 = 63;
        return $$0 >= -63.0f && $$1 >= -63.0f && $$0 <= 63.0f && $$1 <= 63.0f;
    }

    @Nullable
    private Holder<MapDecorationType> decorationTypeForPlayerOutsideMap(float $$0, float $$1) {
        boolean $$3;
        int $$2 = 320;
        boolean bl = $$3 = Math.abs($$0) < 320.0f && Math.abs($$1) < 320.0f;
        if ($$3) {
            return MapDecorationTypes.PLAYER_OFF_MAP;
        }
        return this.unlimitedTracking ? MapDecorationTypes.PLAYER_OFF_LIMITS : null;
    }

    private static byte clampMapCoordinate(float $$0) {
        int $$1 = 63;
        if ($$0 <= -63.0f) {
            return -128;
        }
        if ($$0 >= 63.0f) {
            return 127;
        }
        return (byte)((double)($$0 * 2.0f) + 0.5);
    }

    @Nullable
    public Packet<?> getUpdatePacket(MapId $$0, Player $$1) {
        HoldingPlayer $$2 = this.carriedByPlayers.get($$1);
        if ($$2 == null) {
            return null;
        }
        return $$2.nextUpdatePacket($$0);
    }

    private void setColorsDirty(int $$0, int $$1) {
        this.setDirty();
        for (HoldingPlayer $$2 : this.carriedBy) {
            $$2.markColorsDirty($$0, $$1);
        }
    }

    private void setDecorationsDirty() {
        this.carriedBy.forEach(HoldingPlayer::markDecorationsDirty);
    }

    public HoldingPlayer getHoldingPlayer(Player $$0) {
        HoldingPlayer $$1 = this.carriedByPlayers.get($$0);
        if ($$1 == null) {
            $$1 = new HoldingPlayer($$0);
            this.carriedByPlayers.put($$0, $$1);
            this.carriedBy.add($$1);
        }
        return $$1;
    }

    public boolean toggleBanner(LevelAccessor $$0, BlockPos $$1) {
        double $$2 = (double)$$1.getX() + 0.5;
        double $$3 = (double)$$1.getZ() + 0.5;
        int $$4 = 1 << this.scale;
        double $$5 = ($$2 - (double)this.centerX) / (double)$$4;
        double $$6 = ($$3 - (double)this.centerZ) / (double)$$4;
        int $$7 = 63;
        if ($$5 >= -63.0 && $$6 >= -63.0 && $$5 <= 63.0 && $$6 <= 63.0) {
            MapBanner $$8 = MapBanner.fromWorld($$0, $$1);
            if ($$8 == null) {
                return false;
            }
            if (this.bannerMarkers.remove($$8.getId(), (Object)$$8)) {
                this.removeDecoration($$8.getId());
                this.setDirty();
                return true;
            }
            if (!this.isTrackedCountOverLimit(256)) {
                this.bannerMarkers.put($$8.getId(), $$8);
                this.addDecoration($$8.getDecoration(), $$0, $$8.getId(), $$2, $$3, 180.0, $$8.name().orElse(null));
                this.setDirty();
                return true;
            }
        }
        return false;
    }

    public void checkBanners(BlockGetter $$0, int $$1, int $$2) {
        Iterator<MapBanner> $$3 = this.bannerMarkers.values().iterator();
        while ($$3.hasNext()) {
            MapBanner $$5;
            MapBanner $$4 = $$3.next();
            if ($$4.pos().getX() != $$1 || $$4.pos().getZ() != $$2 || $$4.equals((Object)($$5 = MapBanner.fromWorld($$0, $$4.pos())))) continue;
            $$3.remove();
            this.removeDecoration($$4.getId());
            this.setDirty();
        }
    }

    public Collection<MapBanner> getBanners() {
        return this.bannerMarkers.values();
    }

    public void removedFromFrame(BlockPos $$0, int $$1) {
        this.removeDecoration(MapItemSavedData.getFrameKey($$1));
        this.frameMarkers.remove(MapFrame.frameId($$0));
        this.setDirty();
    }

    public boolean updateColor(int $$0, int $$1, byte $$2) {
        byte $$3 = this.colors[$$0 + $$1 * 128];
        if ($$3 != $$2) {
            this.setColor($$0, $$1, $$2);
            return true;
        }
        return false;
    }

    public void setColor(int $$0, int $$1, byte $$2) {
        this.colors[$$0 + $$1 * 128] = $$2;
        this.setColorsDirty($$0, $$1);
    }

    public boolean isExplorationMap() {
        for (MapDecoration $$0 : this.decorations.values()) {
            if (!$$0.type().value().explorationMapElement()) continue;
            return true;
        }
        return false;
    }

    public void addClientSideDecorations(List<MapDecoration> $$0) {
        this.decorations.clear();
        this.trackedDecorationCount = 0;
        for (int $$1 = 0; $$1 < $$0.size(); ++$$1) {
            MapDecoration $$2 = $$0.get($$1);
            this.decorations.put("icon-" + $$1, $$2);
            if (!$$2.type().value().trackCount()) continue;
            ++this.trackedDecorationCount;
        }
    }

    public Iterable<MapDecoration> getDecorations() {
        return this.decorations.values();
    }

    public boolean isTrackedCountOverLimit(int $$0) {
        return this.trackedDecorationCount >= $$0;
    }

    private static String getFrameKey(int $$0) {
        return FRAME_PREFIX + $$0;
    }

    public class HoldingPlayer {
        public final Player player;
        private boolean dirtyData = true;
        private int minDirtyX;
        private int minDirtyY;
        private int maxDirtyX = 127;
        private int maxDirtyY = 127;
        private boolean dirtyDecorations = true;
        private int tick;
        public int step;

        HoldingPlayer(Player $$1) {
            this.player = $$1;
        }

        private MapPatch createPatch() {
            int $$0 = this.minDirtyX;
            int $$1 = this.minDirtyY;
            int $$2 = this.maxDirtyX + 1 - this.minDirtyX;
            int $$3 = this.maxDirtyY + 1 - this.minDirtyY;
            byte[] $$4 = new byte[$$2 * $$3];
            for (int $$5 = 0; $$5 < $$2; ++$$5) {
                for (int $$6 = 0; $$6 < $$3; ++$$6) {
                    $$4[$$5 + $$6 * $$2] = MapItemSavedData.this.colors[$$0 + $$5 + ($$1 + $$6) * 128];
                }
            }
            return new MapPatch($$0, $$1, $$2, $$3, $$4);
        }

        @Nullable
        Packet<?> nextUpdatePacket(MapId $$0) {
            Collection<MapDecoration> $$4;
            MapPatch $$2;
            if (this.dirtyData) {
                this.dirtyData = false;
                MapPatch $$1 = this.createPatch();
            } else {
                $$2 = null;
            }
            if (this.dirtyDecorations && this.tick++ % 5 == 0) {
                this.dirtyDecorations = false;
                Collection<MapDecoration> $$3 = MapItemSavedData.this.decorations.values();
            } else {
                $$4 = null;
            }
            if ($$4 != null || $$2 != null) {
                return new ClientboundMapItemDataPacket($$0, MapItemSavedData.this.scale, MapItemSavedData.this.locked, $$4, $$2);
            }
            return null;
        }

        void markColorsDirty(int $$0, int $$1) {
            if (this.dirtyData) {
                this.minDirtyX = Math.min(this.minDirtyX, $$0);
                this.minDirtyY = Math.min(this.minDirtyY, $$1);
                this.maxDirtyX = Math.max(this.maxDirtyX, $$0);
                this.maxDirtyY = Math.max(this.maxDirtyY, $$1);
            } else {
                this.dirtyData = true;
                this.minDirtyX = $$0;
                this.minDirtyY = $$1;
                this.maxDirtyX = $$0;
                this.maxDirtyY = $$1;
            }
        }

        private void markDecorationsDirty() {
            this.dirtyDecorations = true;
        }
    }

    record MapDecorationLocation(Holder<MapDecorationType> type, byte x, byte y, byte rot) {
    }

    public static final class MapPatch
    extends Record {
        private final int startX;
        private final int startY;
        private final int width;
        private final int height;
        private final byte[] mapColors;
        public static final StreamCodec<ByteBuf, Optional<MapPatch>> STREAM_CODEC = StreamCodec.of(MapPatch::write, MapPatch::read);

        public MapPatch(int $$0, int $$1, int $$2, int $$3, byte[] $$4) {
            this.startX = $$0;
            this.startY = $$1;
            this.width = $$2;
            this.height = $$3;
            this.mapColors = $$4;
        }

        private static void write(ByteBuf $$0, Optional<MapPatch> $$1) {
            if ($$1.isPresent()) {
                MapPatch $$2 = $$1.get();
                $$0.writeByte($$2.width);
                $$0.writeByte($$2.height);
                $$0.writeByte($$2.startX);
                $$0.writeByte($$2.startY);
                FriendlyByteBuf.a($$0, $$2.mapColors);
            } else {
                $$0.writeByte(0);
            }
        }

        private static Optional<MapPatch> read(ByteBuf $$0) {
            short $$1 = $$0.readUnsignedByte();
            if ($$1 > 0) {
                short $$2 = $$0.readUnsignedByte();
                short $$3 = $$0.readUnsignedByte();
                short $$4 = $$0.readUnsignedByte();
                byte[] $$5 = FriendlyByteBuf.a($$0);
                return Optional.of(new MapPatch($$3, $$4, $$1, $$2, $$5));
            }
            return Optional.empty();
        }

        public void applyToMap(MapItemSavedData $$0) {
            for (int $$1 = 0; $$1 < this.width; ++$$1) {
                for (int $$2 = 0; $$2 < this.height; ++$$2) {
                    $$0.setColor(this.startX + $$1, this.startY + $$2, this.mapColors[$$1 + $$2 * this.width]);
                }
            }
        }

        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{MapPatch.class, "startX;startY;width;height;mapColors", "startX", "startY", "width", "height", "mapColors"}, this);
        }

        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{MapPatch.class, "startX;startY;width;height;mapColors", "startX", "startY", "width", "height", "mapColors"}, this);
        }

        public final boolean equals(Object $$0) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{MapPatch.class, "startX;startY;width;height;mapColors", "startX", "startY", "width", "height", "mapColors"}, this, $$0);
        }

        public int startX() {
            return this.startX;
        }

        public int startY() {
            return this.startY;
        }

        public int width() {
            return this.width;
        }

        public int height() {
            return this.height;
        }

        public byte[] e() {
            return this.mapColors;
        }
    }
}

