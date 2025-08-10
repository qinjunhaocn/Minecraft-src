/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.DataFixUtils
 *  it.unimi.dsi.fastutil.longs.LongSets
 *  it.unimi.dsi.fastutil.longs.LongSets$EmptySet
 *  it.unimi.dsi.fastutil.objects.Object2IntMap
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fStack
 *  org.joml.Matrix4fc
 *  org.joml.Vector3f
 *  org.joml.Vector3fc
 *  org.joml.Vector4f
 *  org.joml.Vector4fc
 */
package net.minecraft.client.gui.components;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.datafixers.DataFixUtils;
import it.unimi.dsi.fastutil.longs.LongSets;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.client.Camera;
import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.debugchart.BandwidthDebugChart;
import net.minecraft.client.gui.components.debugchart.FpsDebugChart;
import net.minecraft.client.gui.components.debugchart.PingDebugChart;
import net.minecraft.client.gui.components.debugchart.ProfilerPieChart;
import net.minecraft.client.gui.components.debugchart.TpsDebugChart;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.renderer.DynamicUniforms;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.SectionPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.Connection;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.ServerTickRateManager;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.debugchart.LocalSampleLogger;
import net.minecraft.util.debugchart.RemoteDebugSampleType;
import net.minecraft.util.debugchart.TpsDebugDimensions;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.util.profiling.Zone;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.TickRateManager;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.joml.Matrix4fc;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.joml.Vector4f;
import org.joml.Vector4fc;

public class DebugScreenOverlay {
    private static final float CROSSHAIR_SCALE = 0.01f;
    private static final int CROSHAIR_INDEX_COUNT = 18;
    private static final int COLOR_GREY = -2039584;
    private static final int MARGIN_RIGHT = 2;
    private static final int MARGIN_LEFT = 2;
    private static final int MARGIN_TOP = 2;
    private static final Map<Heightmap.Types, String> HEIGHTMAP_NAMES = Maps.newEnumMap(Map.of((Object)Heightmap.Types.WORLD_SURFACE_WG, (Object)"SW", (Object)Heightmap.Types.WORLD_SURFACE, (Object)"S", (Object)Heightmap.Types.OCEAN_FLOOR_WG, (Object)"OW", (Object)Heightmap.Types.OCEAN_FLOOR, (Object)"O", (Object)Heightmap.Types.MOTION_BLOCKING, (Object)"M", (Object)Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (Object)"ML"));
    private final Minecraft minecraft;
    private final AllocationRateCalculator allocationRateCalculator;
    private final Font font;
    private final GpuBuffer crosshairBuffer;
    private final RenderSystem.AutoStorageIndexBuffer crosshairIndicies = RenderSystem.getSequentialBuffer(VertexFormat.Mode.LINES);
    private HitResult block;
    private HitResult liquid;
    @Nullable
    private ChunkPos lastPos;
    @Nullable
    private LevelChunk clientChunk;
    @Nullable
    private CompletableFuture<LevelChunk> serverChunk;
    private boolean renderDebug;
    private boolean renderProfilerChart;
    private boolean renderFpsCharts;
    private boolean renderNetworkCharts;
    private final LocalSampleLogger frameTimeLogger = new LocalSampleLogger(1);
    private final LocalSampleLogger tickTimeLogger = new LocalSampleLogger(TpsDebugDimensions.values().length);
    private final LocalSampleLogger pingLogger = new LocalSampleLogger(1);
    private final LocalSampleLogger bandwidthLogger = new LocalSampleLogger(1);
    private final Map<RemoteDebugSampleType, LocalSampleLogger> remoteSupportingLoggers = Map.of((Object)((Object)RemoteDebugSampleType.TICK_TIME), (Object)this.tickTimeLogger);
    private final FpsDebugChart fpsChart;
    private final TpsDebugChart tpsChart;
    private final PingDebugChart pingChart;
    private final BandwidthDebugChart bandwidthChart;
    private final ProfilerPieChart profilerPieChart;

    public DebugScreenOverlay(Minecraft $$0) {
        this.minecraft = $$0;
        this.allocationRateCalculator = new AllocationRateCalculator();
        this.font = $$0.font;
        this.fpsChart = new FpsDebugChart(this.font, this.frameTimeLogger);
        this.tpsChart = new TpsDebugChart(this.font, this.tickTimeLogger, () -> Float.valueOf($$0.level.tickRateManager().millisecondsPerTick()));
        this.pingChart = new PingDebugChart(this.font, this.pingLogger);
        this.bandwidthChart = new BandwidthDebugChart(this.font, this.bandwidthLogger);
        this.profilerPieChart = new ProfilerPieChart(this.font);
        try (ByteBufferBuilder $$1 = ByteBufferBuilder.exactlySized(DefaultVertexFormat.POSITION_COLOR_NORMAL.getVertexSize() * 12);){
            BufferBuilder $$2 = new BufferBuilder($$1, VertexFormat.Mode.LINES, DefaultVertexFormat.POSITION_COLOR_NORMAL);
            $$2.addVertex(0.0f, 0.0f, 0.0f).setColor(-65536).setNormal(1.0f, 0.0f, 0.0f);
            $$2.addVertex(1.0f, 0.0f, 0.0f).setColor(-65536).setNormal(1.0f, 0.0f, 0.0f);
            $$2.addVertex(0.0f, 0.0f, 0.0f).setColor(-16711936).setNormal(0.0f, 1.0f, 0.0f);
            $$2.addVertex(0.0f, 1.0f, 0.0f).setColor(-16711936).setNormal(0.0f, 1.0f, 0.0f);
            $$2.addVertex(0.0f, 0.0f, 0.0f).setColor(-8421377).setNormal(0.0f, 0.0f, 1.0f);
            $$2.addVertex(0.0f, 0.0f, 1.0f).setColor(-8421377).setNormal(0.0f, 0.0f, 1.0f);
            try (MeshData $$3 = $$2.buildOrThrow();){
                this.crosshairBuffer = RenderSystem.getDevice().createBuffer(() -> "Crosshair vertex buffer", 32, $$3.vertexBuffer());
            }
        }
    }

    public void clearChunkCache() {
        this.serverChunk = null;
        this.clientChunk = null;
    }

    public void render(GuiGraphics $$0) {
        ProfilerFiller $$1 = Profiler.get();
        $$1.push("debug");
        Entity $$2 = this.minecraft.getCameraEntity();
        this.block = $$2.pick(20.0, 0.0f, false);
        this.liquid = $$2.pick(20.0, 0.0f, true);
        this.drawGameInformation($$0);
        this.drawSystemInformation($$0);
        $$0.nextStratum();
        this.profilerPieChart.setBottomOffset(10);
        if (this.renderFpsCharts) {
            int $$3 = $$0.guiWidth();
            int $$4 = $$3 / 2;
            this.fpsChart.drawChart($$0, 0, this.fpsChart.getWidth($$4));
            if (this.tickTimeLogger.size() > 0) {
                int $$5 = this.tpsChart.getWidth($$4);
                this.tpsChart.drawChart($$0, $$3 - $$5, $$5);
            }
            this.profilerPieChart.setBottomOffset(this.tpsChart.getFullHeight());
        }
        if (this.renderNetworkCharts) {
            int $$6 = $$0.guiWidth();
            int $$7 = $$6 / 2;
            if (!this.minecraft.isLocalServer()) {
                this.bandwidthChart.drawChart($$0, 0, this.bandwidthChart.getWidth($$7));
            }
            int $$8 = this.pingChart.getWidth($$7);
            this.pingChart.drawChart($$0, $$6 - $$8, $$8);
            this.profilerPieChart.setBottomOffset(this.pingChart.getFullHeight());
        }
        try (Zone $$9 = $$1.zone("profilerPie");){
            this.profilerPieChart.render($$0);
        }
        $$1.pop();
    }

    protected void drawGameInformation(GuiGraphics $$0) {
        List<String> $$1 = this.getGameInformation();
        $$1.add("");
        boolean $$2 = this.minecraft.getSingleplayerServer() != null;
        $$1.add("Debug charts: [F3+1] Profiler " + (this.renderProfilerChart ? "visible" : "hidden") + "; [F3+2] " + ($$2 ? "FPS + TPS " : "FPS ") + (this.renderFpsCharts ? "visible" : "hidden") + "; [F3+3] " + (!this.minecraft.isLocalServer() ? "Bandwidth + Ping" : "Ping") + (this.renderNetworkCharts ? " visible" : " hidden"));
        $$1.add("For help: press F3 + Q");
        this.renderLines($$0, $$1, true);
    }

    protected void drawSystemInformation(GuiGraphics $$0) {
        List<String> $$1 = this.getSystemInformation();
        this.renderLines($$0, $$1, false);
    }

    private void renderLines(GuiGraphics $$0, List<String> $$1, boolean $$2) {
        int $$3 = this.font.lineHeight;
        for (int $$4 = 0; $$4 < $$1.size(); ++$$4) {
            String $$5 = $$1.get($$4);
            if (Strings.isNullOrEmpty($$5)) continue;
            int $$6 = this.font.width($$5);
            int $$7 = $$2 ? 2 : $$0.guiWidth() - 2 - $$6;
            int $$8 = 2 + $$3 * $$4;
            $$0.fill($$7 - 1, $$8 - 1, $$7 + $$6 + 1, $$8 + $$3 - 1, -1873784752);
        }
        for (int $$9 = 0; $$9 < $$1.size(); ++$$9) {
            String $$10 = $$1.get($$9);
            if (Strings.isNullOrEmpty($$10)) continue;
            int $$11 = this.font.width($$10);
            int $$12 = $$2 ? 2 : $$0.guiWidth() - 2 - $$11;
            int $$13 = 2 + $$3 * $$9;
            $$0.drawString(this.font, $$10, $$12, $$13, -2039584, false);
        }
    }

    protected List<String> getGameInformation() {
        ResourceLocation $$47;
        Level $$23;
        String $$21;
        String $$13;
        String $$8;
        IntegratedServer $$0 = this.minecraft.getSingleplayerServer();
        ClientPacketListener $$12 = this.minecraft.getConnection();
        Connection $$2 = $$12.getConnection();
        float $$3 = $$2.getAverageSentPackets();
        float $$4 = $$2.getAverageReceivedPackets();
        TickRateManager $$5 = this.getLevel().tickRateManager();
        if ($$5.isSteppingForward()) {
            String $$6 = " (frozen - stepping)";
        } else if ($$5.isFrozen()) {
            String $$7 = " (frozen)";
        } else {
            $$8 = "";
        }
        if ($$0 != null) {
            ServerTickRateManager $$9 = $$0.tickRateManager();
            boolean $$10 = $$9.isSprinting();
            if ($$10) {
                $$8 = " (sprinting)";
            }
            String $$11 = $$10 ? "-" : String.format(Locale.ROOT, "%.1f", Float.valueOf($$5.millisecondsPerTick()));
            String $$122 = String.format(Locale.ROOT, "Integrated server @ %.1f/%s ms%s, %.0f tx, %.0f rx", Float.valueOf($$0.getCurrentSmoothedTickTime()), $$11, $$8, Float.valueOf($$3), Float.valueOf($$4));
        } else {
            $$13 = String.format(Locale.ROOT, "\"%s\" server%s, %.0f tx, %.0f rx", $$12.serverBrand(), $$8, Float.valueOf($$3), Float.valueOf($$4));
        }
        BlockPos $$14 = this.minecraft.getCameraEntity().blockPosition();
        if (this.minecraft.showOnlyReducedInfo()) {
            return Lists.newArrayList("Minecraft " + SharedConstants.getCurrentVersion().name() + " (" + this.minecraft.getLaunchedVersion() + "/" + ClientBrandRetriever.getClientModName() + ")", this.minecraft.fpsString, $$13, this.minecraft.levelRenderer.getSectionStatistics(), this.minecraft.levelRenderer.getEntityStatistics(), "P: " + this.minecraft.particleEngine.countParticles() + ". T: " + this.minecraft.level.getEntityCount(), this.minecraft.level.gatherChunkSourceStats(), "", String.format(Locale.ROOT, "Chunk-relative: %d %d %d", $$14.getX() & 0xF, $$14.getY() & 0xF, $$14.getZ() & 0xF));
        }
        Entity $$15 = this.minecraft.getCameraEntity();
        Direction $$16 = $$15.getDirection();
        switch ($$16) {
            case NORTH: {
                String $$17 = "Towards negative Z";
                break;
            }
            case SOUTH: {
                String $$18 = "Towards positive Z";
                break;
            }
            case WEST: {
                String $$19 = "Towards negative X";
                break;
            }
            case EAST: {
                String $$20 = "Towards positive X";
                break;
            }
            default: {
                $$21 = "Invalid";
            }
        }
        ChunkPos $$22 = new ChunkPos($$14);
        if (!Objects.equals(this.lastPos, $$22)) {
            this.lastPos = $$22;
            this.clearChunkCache();
        }
        LongSets.EmptySet $$24 = ($$23 = this.getLevel()) instanceof ServerLevel ? ((ServerLevel)$$23).getForceLoadedChunks() : LongSets.EMPTY_SET;
        ArrayList<String> $$25 = Lists.newArrayList("Minecraft " + SharedConstants.getCurrentVersion().name() + " (" + this.minecraft.getLaunchedVersion() + "/" + ClientBrandRetriever.getClientModName() + (String)("release".equalsIgnoreCase(this.minecraft.getVersionType()) ? "" : "/" + this.minecraft.getVersionType()) + ")", this.minecraft.fpsString, $$13, this.minecraft.levelRenderer.getSectionStatistics(), this.minecraft.levelRenderer.getEntityStatistics(), "P: " + this.minecraft.particleEngine.countParticles() + ". T: " + this.minecraft.level.getEntityCount(), this.minecraft.level.gatherChunkSourceStats());
        String $$26 = this.getServerChunkStats();
        if ($$26 != null) {
            $$25.add($$26);
        }
        $$25.add(String.valueOf(this.minecraft.level.dimension().location()) + " FC: " + $$24.size());
        $$25.add("");
        $$25.add(String.format(Locale.ROOT, "XYZ: %.3f / %.5f / %.3f", this.minecraft.getCameraEntity().getX(), this.minecraft.getCameraEntity().getY(), this.minecraft.getCameraEntity().getZ()));
        $$25.add(String.format(Locale.ROOT, "Block: %d %d %d [%d %d %d]", $$14.getX(), $$14.getY(), $$14.getZ(), $$14.getX() & 0xF, $$14.getY() & 0xF, $$14.getZ() & 0xF));
        $$25.add(String.format(Locale.ROOT, "Chunk: %d %d %d [%d %d in r.%d.%d.mca]", $$22.x, SectionPos.blockToSectionCoord($$14.getY()), $$22.z, $$22.getRegionLocalX(), $$22.getRegionLocalZ(), $$22.getRegionX(), $$22.getRegionZ()));
        $$25.add(String.format(Locale.ROOT, "Facing: %s (%s) (%.1f / %.1f)", $$16, $$21, Float.valueOf(Mth.wrapDegrees($$15.getYRot())), Float.valueOf(Mth.wrapDegrees($$15.getXRot()))));
        LevelChunk $$27 = this.getClientChunk();
        if ($$27.isEmpty()) {
            $$25.add("Waiting for chunk...");
        } else {
            int $$28 = this.minecraft.level.getChunkSource().getLightEngine().getRawBrightness($$14, 0);
            int $$29 = this.minecraft.level.getBrightness(LightLayer.SKY, $$14);
            int $$30 = this.minecraft.level.getBrightness(LightLayer.BLOCK, $$14);
            $$25.add("Client Light: " + $$28 + " (" + $$29 + " sky, " + $$30 + " block)");
            LevelChunk $$31 = this.getServerChunk();
            StringBuilder $$32 = new StringBuilder("CH");
            for (Heightmap.Types $$33 : Heightmap.Types.values()) {
                if (!$$33.sendToClient()) continue;
                $$32.append(" ").append(HEIGHTMAP_NAMES.get($$33)).append(": ").append($$27.getHeight($$33, $$14.getX(), $$14.getZ()));
            }
            $$25.add($$32.toString());
            $$32.setLength(0);
            $$32.append("SH");
            for (Heightmap.Types $$34 : Heightmap.Types.values()) {
                if (!$$34.keepAfterWorldgen()) continue;
                $$32.append(" ").append(HEIGHTMAP_NAMES.get($$34)).append(": ");
                if ($$31 != null) {
                    $$32.append($$31.getHeight($$34, $$14.getX(), $$14.getZ()));
                    continue;
                }
                $$32.append("??");
            }
            $$25.add($$32.toString());
            if (this.minecraft.level.isInsideBuildHeight($$14.getY())) {
                $$25.add("Biome: " + DebugScreenOverlay.printBiome(this.minecraft.level.getBiome($$14)));
                if ($$31 != null) {
                    float $$35 = $$23.getMoonBrightness();
                    long $$36 = $$31.getInhabitedTime();
                    DifficultyInstance $$37 = new DifficultyInstance($$23.getDifficulty(), $$23.getDayTime(), $$36, $$35);
                    $$25.add(String.format(Locale.ROOT, "Local Difficulty: %.2f // %.2f (Day %d)", Float.valueOf($$37.getEffectiveDifficulty()), Float.valueOf($$37.getSpecialMultiplier()), this.minecraft.level.getDayTime() / 24000L));
                } else {
                    $$25.add("Local Difficulty: ??");
                }
            }
            if ($$31 != null && $$31.isOldNoiseGeneration()) {
                $$25.add("Blending: Old");
            }
        }
        ServerLevel $$38 = this.getServerLevel();
        if ($$38 != null) {
            ServerChunkCache $$39 = $$38.getChunkSource();
            ChunkGenerator $$40 = $$39.getGenerator();
            RandomState $$41 = $$39.randomState();
            $$40.addDebugScreenInfo($$25, $$41, $$14);
            Climate.Sampler $$42 = $$41.sampler();
            BiomeSource $$43 = $$40.getBiomeSource();
            $$43.addDebugInfo($$25, $$14, $$42);
            NaturalSpawner.SpawnState $$44 = $$39.getLastSpawnState();
            if ($$44 != null) {
                Object2IntMap<MobCategory> $$45 = $$44.getMobCategoryCounts();
                int $$46 = $$44.getSpawnableChunkCount();
                $$25.add("SC: " + $$46 + ", " + Stream.of(MobCategory.values()).map($$1 -> Character.toUpperCase($$1.getName().charAt(0)) + ": " + $$45.getInt($$1)).collect(Collectors.joining(", ")));
            } else {
                $$25.add("SC: N/A");
            }
        }
        if (($$47 = this.minecraft.gameRenderer.currentPostEffect()) != null) {
            $$25.add("Post: " + String.valueOf($$47));
        }
        $$25.add(this.minecraft.getSoundManager().getDebugString() + String.format(Locale.ROOT, " (Mood %d%%)", Math.round(this.minecraft.player.getCurrentMood() * 100.0f)));
        return $$25;
    }

    private static String printBiome(Holder<Biome> $$02) {
        return (String)$$02.unwrap().map($$0 -> $$0.location().toString(), $$0 -> "[unregistered " + String.valueOf($$0) + "]");
    }

    @Nullable
    private ServerLevel getServerLevel() {
        IntegratedServer $$0 = this.minecraft.getSingleplayerServer();
        if ($$0 != null) {
            return $$0.getLevel(this.minecraft.level.dimension());
        }
        return null;
    }

    @Nullable
    private String getServerChunkStats() {
        ServerLevel $$0 = this.getServerLevel();
        if ($$0 != null) {
            return $$0.gatherChunkSourceStats();
        }
        return null;
    }

    private Level getLevel() {
        return (Level)DataFixUtils.orElse(Optional.ofNullable(this.minecraft.getSingleplayerServer()).flatMap($$0 -> Optional.ofNullable($$0.getLevel(this.minecraft.level.dimension()))), (Object)this.minecraft.level);
    }

    @Nullable
    private LevelChunk getServerChunk() {
        if (this.serverChunk == null) {
            ServerLevel $$02 = this.getServerLevel();
            if ($$02 == null) {
                return null;
            }
            this.serverChunk = $$02.getChunkSource().getChunkFuture(this.lastPos.x, this.lastPos.z, ChunkStatus.FULL, false).thenApply($$0 -> $$0.orElse(null));
        }
        return this.serverChunk.getNow(null);
    }

    private LevelChunk getClientChunk() {
        if (this.clientChunk == null) {
            this.clientChunk = this.minecraft.level.getChunk(this.lastPos.x, this.lastPos.z);
        }
        return this.clientChunk;
    }

    protected List<String> getSystemInformation() {
        Entity $$12;
        long $$02 = Runtime.getRuntime().maxMemory();
        long $$1 = Runtime.getRuntime().totalMemory();
        long $$2 = Runtime.getRuntime().freeMemory();
        long $$3 = $$1 - $$2;
        GpuDevice $$4 = RenderSystem.getDevice();
        ArrayList<String> $$5 = Lists.newArrayList(String.format(Locale.ROOT, "Java: %s", System.getProperty("java.version")), String.format(Locale.ROOT, "Mem: %2d%% %03d/%03dMB", $$3 * 100L / $$02, DebugScreenOverlay.bytesToMegabytes($$3), DebugScreenOverlay.bytesToMegabytes($$02)), String.format(Locale.ROOT, "Allocation rate: %03dMB/s", DebugScreenOverlay.bytesToMegabytes(this.allocationRateCalculator.bytesAllocatedPerSecond($$3))), String.format(Locale.ROOT, "Allocated: %2d%% %03dMB", $$1 * 100L / $$02, DebugScreenOverlay.bytesToMegabytes($$1)), "", String.format(Locale.ROOT, "CPU: %s", GLX._getCpuInfo()), "", String.format(Locale.ROOT, "Display: %dx%d (%s)", Minecraft.getInstance().getWindow().getWidth(), Minecraft.getInstance().getWindow().getHeight(), $$4.getVendor()), $$4.getRenderer(), String.format(Locale.ROOT, "%s %s", $$4.getBackendName(), $$4.getVersion()));
        if (this.minecraft.showOnlyReducedInfo()) {
            return $$5;
        }
        if (this.block.getType() == HitResult.Type.BLOCK) {
            BlockPos $$6 = ((BlockHitResult)this.block).getBlockPos();
            BlockState $$7 = this.minecraft.level.getBlockState($$6);
            $$5.add("");
            $$5.add(String.valueOf(ChatFormatting.UNDERLINE) + "Targeted Block: " + $$6.getX() + ", " + $$6.getY() + ", " + $$6.getZ());
            $$5.add(String.valueOf(BuiltInRegistries.BLOCK.getKey($$7.getBlock())));
            for (Map.Entry<Property<?>, Comparable<?>> $$8 : $$7.getValues().entrySet()) {
                $$5.add(this.getPropertyValueString($$8));
            }
            $$7.getTags().map($$0 -> "#" + String.valueOf($$0.location())).forEach($$5::add);
        }
        if (this.liquid.getType() == HitResult.Type.BLOCK) {
            BlockPos $$9 = ((BlockHitResult)this.liquid).getBlockPos();
            FluidState $$10 = this.minecraft.level.getFluidState($$9);
            $$5.add("");
            $$5.add(String.valueOf(ChatFormatting.UNDERLINE) + "Targeted Fluid: " + $$9.getX() + ", " + $$9.getY() + ", " + $$9.getZ());
            $$5.add(String.valueOf(BuiltInRegistries.FLUID.getKey($$10.getType())));
            for (Map.Entry<Property<?>, Comparable<?>> $$11 : $$10.getValues().entrySet()) {
                $$5.add(this.getPropertyValueString($$11));
            }
            $$10.getTags().map($$0 -> "#" + String.valueOf($$0.location())).forEach($$5::add);
        }
        if (($$12 = this.minecraft.crosshairPickEntity) != null) {
            $$5.add("");
            $$5.add(String.valueOf(ChatFormatting.UNDERLINE) + "Targeted Entity");
            $$5.add(String.valueOf(BuiltInRegistries.ENTITY_TYPE.getKey($$12.getType())));
        }
        return $$5;
    }

    private String getPropertyValueString(Map.Entry<Property<?>, Comparable<?>> $$0) {
        Property<?> $$1 = $$0.getKey();
        Comparable<?> $$2 = $$0.getValue();
        Object $$3 = Util.getPropertyName($$1, $$2);
        if (Boolean.TRUE.equals($$2)) {
            $$3 = String.valueOf(ChatFormatting.GREEN) + (String)$$3;
        } else if (Boolean.FALSE.equals($$2)) {
            $$3 = String.valueOf(ChatFormatting.RED) + (String)$$3;
        }
        return $$1.getName() + ": " + (String)$$3;
    }

    private static long bytesToMegabytes(long $$0) {
        return $$0 / 1024L / 1024L;
    }

    public boolean showDebugScreen() {
        return this.renderDebug && !this.minecraft.options.hideGui;
    }

    public boolean showProfilerChart() {
        return this.showDebugScreen() && this.renderProfilerChart;
    }

    public boolean showNetworkCharts() {
        return this.showDebugScreen() && this.renderNetworkCharts;
    }

    public boolean showFpsCharts() {
        return this.showDebugScreen() && this.renderFpsCharts;
    }

    public void toggleOverlay() {
        this.renderDebug = !this.renderDebug;
    }

    public void toggleNetworkCharts() {
        boolean bl = this.renderNetworkCharts = !this.renderDebug || !this.renderNetworkCharts;
        if (this.renderNetworkCharts) {
            this.renderDebug = true;
            this.renderFpsCharts = false;
        }
    }

    public void toggleFpsCharts() {
        boolean bl = this.renderFpsCharts = !this.renderDebug || !this.renderFpsCharts;
        if (this.renderFpsCharts) {
            this.renderDebug = true;
            this.renderNetworkCharts = false;
        }
    }

    public void toggleProfilerChart() {
        boolean bl = this.renderProfilerChart = !this.renderDebug || !this.renderProfilerChart;
        if (this.renderProfilerChart) {
            this.renderDebug = true;
        }
    }

    public void logFrameDuration(long $$0) {
        this.frameTimeLogger.logSample($$0);
    }

    public LocalSampleLogger getTickTimeLogger() {
        return this.tickTimeLogger;
    }

    public LocalSampleLogger getPingLogger() {
        return this.pingLogger;
    }

    public LocalSampleLogger getBandwidthLogger() {
        return this.bandwidthLogger;
    }

    public ProfilerPieChart getProfilerPieChart() {
        return this.profilerPieChart;
    }

    public void a(long[] $$0, RemoteDebugSampleType $$1) {
        LocalSampleLogger $$2 = this.remoteSupportingLoggers.get((Object)$$1);
        if ($$2 != null) {
            $$2.a($$0);
        }
    }

    public void reset() {
        this.renderDebug = false;
        this.tickTimeLogger.reset();
        this.pingLogger.reset();
        this.bandwidthLogger.reset();
    }

    public void render3dCrosshair(Camera $$0) {
        Matrix4fStack $$1 = RenderSystem.getModelViewStack();
        $$1.pushMatrix();
        $$1.translate(0.0f, 0.0f, -1.0f);
        $$1.rotateX($$0.getXRot() * ((float)Math.PI / 180));
        $$1.rotateY($$0.getYRot() * ((float)Math.PI / 180));
        float $$2 = 0.01f * (float)this.minecraft.getWindow().getGuiScale();
        $$1.scale(-$$2, $$2, -$$2);
        RenderPipeline $$3 = RenderPipelines.LINES;
        RenderTarget $$4 = Minecraft.getInstance().getMainRenderTarget();
        GpuTextureView $$5 = $$4.getColorTextureView();
        GpuTextureView $$6 = $$4.getDepthTextureView();
        GpuBuffer $$7 = this.crosshairIndicies.getBuffer(18);
        GpuBufferSlice[] $$8 = RenderSystem.getDynamicUniforms().a(new DynamicUniforms.Transform((Matrix4fc)new Matrix4f((Matrix4fc)$$1), (Vector4fc)new Vector4f(0.0f, 0.0f, 0.0f, 1.0f), (Vector3fc)new Vector3f(), (Matrix4fc)new Matrix4f(), 4.0f), new DynamicUniforms.Transform((Matrix4fc)new Matrix4f((Matrix4fc)$$1), (Vector4fc)new Vector4f(1.0f, 1.0f, 1.0f, 1.0f), (Vector3fc)new Vector3f(), (Matrix4fc)new Matrix4f(), 2.0f));
        try (RenderPass $$9 = RenderSystem.getDevice().createCommandEncoder().createRenderPass(() -> "3d crosshair", $$5, OptionalInt.empty(), $$6, OptionalDouble.empty());){
            $$9.setPipeline($$3);
            RenderSystem.bindDefaultUniforms($$9);
            $$9.setVertexBuffer(0, this.crosshairBuffer);
            $$9.setIndexBuffer($$7, this.crosshairIndicies.type());
            $$9.setUniform("DynamicTransforms", $$8[0]);
            $$9.drawIndexed(0, 0, 18, 1);
            $$9.setUniform("DynamicTransforms", $$8[1]);
            $$9.drawIndexed(0, 0, 18, 1);
        }
        $$1.popMatrix();
    }

    static class AllocationRateCalculator {
        private static final int UPDATE_INTERVAL_MS = 500;
        private static final List<GarbageCollectorMXBean> GC_MBEANS = ManagementFactory.getGarbageCollectorMXBeans();
        private long lastTime = 0L;
        private long lastHeapUsage = -1L;
        private long lastGcCounts = -1L;
        private long lastRate = 0L;

        AllocationRateCalculator() {
        }

        long bytesAllocatedPerSecond(long $$0) {
            long $$1 = System.currentTimeMillis();
            if ($$1 - this.lastTime < 500L) {
                return this.lastRate;
            }
            long $$2 = AllocationRateCalculator.gcCounts();
            if (this.lastTime != 0L && $$2 == this.lastGcCounts) {
                double $$3 = (double)TimeUnit.SECONDS.toMillis(1L) / (double)($$1 - this.lastTime);
                long $$4 = $$0 - this.lastHeapUsage;
                this.lastRate = Math.round((double)$$4 * $$3);
            }
            this.lastTime = $$1;
            this.lastHeapUsage = $$0;
            this.lastGcCounts = $$2;
            return this.lastRate;
        }

        private static long gcCounts() {
            long $$0 = 0L;
            for (GarbageCollectorMXBean $$1 : GC_MBEANS) {
                $$0 += $$1.getCollectionCount();
            }
            return $$0;
        }
    }
}

