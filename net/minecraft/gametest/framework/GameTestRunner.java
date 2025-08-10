/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  it.unimi.dsi.fastutil.longs.LongArraySet
 */
package net.minecraft.gametest.framework;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.longs.LongArraySet;
import java.lang.invoke.LambdaMetafactory;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.gametest.framework.GameTestBatch;
import net.minecraft.gametest.framework.GameTestBatchFactory;
import net.minecraft.gametest.framework.GameTestBatchListener;
import net.minecraft.gametest.framework.GameTestInfo;
import net.minecraft.gametest.framework.GameTestListener;
import net.minecraft.gametest.framework.GameTestTicker;
import net.minecraft.gametest.framework.MultipleTestTracker;
import net.minecraft.gametest.framework.ReportGameListener;
import net.minecraft.gametest.framework.StructureGridSpawner;
import net.minecraft.gametest.framework.TestEnvironmentDefinition;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import org.slf4j.Logger;

public class GameTestRunner {
    public static final int DEFAULT_TESTS_PER_ROW = 8;
    private static final Logger LOGGER = LogUtils.getLogger();
    final ServerLevel level;
    private final GameTestTicker testTicker;
    private final List<GameTestInfo> allTestInfos;
    private ImmutableList<GameTestBatch> batches;
    final List<GameTestBatchListener> batchListeners = Lists.newArrayList();
    private final List<GameTestInfo> scheduledForRerun = Lists.newArrayList();
    private final GameTestBatcher testBatcher;
    private boolean stopped = true;
    @Nullable
    private Holder<TestEnvironmentDefinition> currentEnvironment;
    private final StructureSpawner existingStructureSpawner;
    private final StructureSpawner newStructureSpawner;
    final boolean haltOnError;

    protected GameTestRunner(GameTestBatcher $$02, Collection<GameTestBatch> $$1, ServerLevel $$2, GameTestTicker $$3, StructureSpawner $$4, StructureSpawner $$5, boolean $$6) {
        this.level = $$2;
        this.testTicker = $$3;
        this.testBatcher = $$02;
        this.existingStructureSpawner = $$4;
        this.newStructureSpawner = $$5;
        this.batches = ImmutableList.copyOf($$1);
        this.haltOnError = $$6;
        this.allTestInfos = this.batches.stream().flatMap($$0 -> $$0.gameTestInfos().stream()).collect(Util.toMutableList());
        $$3.setRunner(this);
        this.allTestInfos.forEach($$0 -> $$0.addListener(new ReportGameListener()));
    }

    public List<GameTestInfo> getTestInfos() {
        return this.allTestInfos;
    }

    public void start() {
        this.stopped = false;
        this.runBatch(0);
    }

    public void stop() {
        this.stopped = true;
        if (this.currentEnvironment != null) {
            this.endCurrentEnvironment();
        }
    }

    public void rerunTest(GameTestInfo $$0) {
        GameTestInfo $$1 = $$0.copyReset();
        $$0.getListeners().forEach($$2 -> $$2.testAddedForRerun($$0, $$1, this));
        this.allTestInfos.add($$1);
        this.scheduledForRerun.add($$1);
        if (this.stopped) {
            this.runScheduledRerunTests();
        }
    }

    void runBatch(final int $$0) {
        if ($$0 >= this.batches.size()) {
            this.endCurrentEnvironment();
            this.runScheduledRerunTests();
            return;
        }
        final GameTestBatch $$12 = (GameTestBatch)((Object)this.batches.get($$0));
        this.existingStructureSpawner.onBatchStart(this.level);
        this.newStructureSpawner.onBatchStart(this.level);
        Collection<GameTestInfo> $$2 = this.createStructuresForBatch($$12.gameTestInfos());
        LOGGER.info("Running test environment '{}' batch {} ({} tests)...", $$12.environment().getRegisteredName(), $$12.index(), $$2.size());
        if (this.currentEnvironment != $$12.environment()) {
            this.endCurrentEnvironment();
            this.currentEnvironment = $$12.environment();
            this.currentEnvironment.value().setup(this.level);
        }
        this.batchListeners.forEach($$1 -> $$1.testBatchStarting($$12));
        final MultipleTestTracker $$3 = new MultipleTestTracker();
        $$2.forEach($$3::addTestToTrack);
        $$3.addListener(new GameTestListener(){

            private void testCompleted() {
                if ($$3.isDone()) {
                    GameTestRunner.this.batchListeners.forEach($$1 -> $$1.testBatchFinished($$12));
                    LongArraySet $$02 = new LongArraySet(GameTestRunner.this.level.getForceLoadedChunks());
                    $$02.forEach($$0 -> GameTestRunner.this.level.setChunkForced(ChunkPos.getX($$0), ChunkPos.getZ($$0), false));
                    GameTestRunner.this.runBatch($$0 + 1);
                }
            }

            @Override
            public void testStructureLoaded(GameTestInfo $$02) {
            }

            @Override
            public void testPassed(GameTestInfo $$02, GameTestRunner $$1) {
                this.testCompleted();
            }

            @Override
            public void testFailed(GameTestInfo $$02, GameTestRunner $$1) {
                if (GameTestRunner.this.haltOnError) {
                    GameTestRunner.this.endCurrentEnvironment();
                    LongArraySet $$2 = new LongArraySet(GameTestRunner.this.level.getForceLoadedChunks());
                    $$2.forEach($$0 -> GameTestRunner.this.level.setChunkForced(ChunkPos.getX($$0), ChunkPos.getZ($$0), false));
                    GameTestTicker.SINGLETON.clear();
                } else {
                    this.testCompleted();
                }
            }

            @Override
            public void testAddedForRerun(GameTestInfo $$02, GameTestInfo $$1, GameTestRunner $$2) {
            }
        });
        $$2.forEach(this.testTicker::add);
    }

    void endCurrentEnvironment() {
        if (this.currentEnvironment != null) {
            this.currentEnvironment.value().teardown(this.level);
            this.currentEnvironment = null;
        }
    }

    private void runScheduledRerunTests() {
        if (!this.scheduledForRerun.isEmpty()) {
            LOGGER.info("Starting re-run of tests: {}", (Object)this.scheduledForRerun.stream().map($$0 -> $$0.id().toString()).collect(Collectors.joining(", ")));
            this.batches = ImmutableList.copyOf(this.testBatcher.batch(this.scheduledForRerun));
            this.scheduledForRerun.clear();
            this.stopped = false;
            this.runBatch(0);
        } else {
            this.batches = ImmutableList.of();
            this.stopped = true;
        }
    }

    public void addListener(GameTestBatchListener $$0) {
        this.batchListeners.add($$0);
    }

    private Collection<GameTestInfo> createStructuresForBatch(Collection<GameTestInfo> $$0) {
        return $$0.stream().map(this::spawn).flatMap((Function<Optional, Stream>)LambdaMetafactory.metafactory(null, null, null, (Ljava/lang/Object;)Ljava/lang/Object;, stream(), (Ljava/util/Optional;)Ljava/util/stream/Stream;)()).toList();
    }

    private Optional<GameTestInfo> spawn(GameTestInfo $$0) {
        if ($$0.getTestBlockPos() == null) {
            return this.newStructureSpawner.spawnStructure($$0);
        }
        return this.existingStructureSpawner.spawnStructure($$0);
    }

    public static void clearMarkers(ServerLevel $$0) {
        DebugPackets.sendGameTestClearPacket($$0);
    }

    public static interface GameTestBatcher {
        public Collection<GameTestBatch> batch(Collection<GameTestInfo> var1);
    }

    public static interface StructureSpawner {
        public static final StructureSpawner IN_PLACE = $$02 -> Optional.ofNullable($$02.prepareTestStructure()).map($$0 -> $$0.startExecution(1));
        public static final StructureSpawner NOT_SET = $$0 -> Optional.empty();

        public Optional<GameTestInfo> spawnStructure(GameTestInfo var1);

        default public void onBatchStart(ServerLevel $$0) {
        }
    }

    public static class Builder {
        private final ServerLevel level;
        private final GameTestTicker testTicker = GameTestTicker.SINGLETON;
        private GameTestBatcher batcher = GameTestBatchFactory.fromGameTestInfo();
        private StructureSpawner existingStructureSpawner = StructureSpawner.IN_PLACE;
        private StructureSpawner newStructureSpawner = StructureSpawner.NOT_SET;
        private final Collection<GameTestBatch> batches;
        private boolean haltOnError = false;

        private Builder(Collection<GameTestBatch> $$0, ServerLevel $$1) {
            this.batches = $$0;
            this.level = $$1;
        }

        public static Builder fromBatches(Collection<GameTestBatch> $$0, ServerLevel $$1) {
            return new Builder($$0, $$1);
        }

        public static Builder fromInfo(Collection<GameTestInfo> $$0, ServerLevel $$1) {
            return Builder.fromBatches(GameTestBatchFactory.fromGameTestInfo().batch($$0), $$1);
        }

        public Builder haltOnError(boolean $$0) {
            this.haltOnError = $$0;
            return this;
        }

        public Builder newStructureSpawner(StructureSpawner $$0) {
            this.newStructureSpawner = $$0;
            return this;
        }

        public Builder existingStructureSpawner(StructureGridSpawner $$0) {
            this.existingStructureSpawner = $$0;
            return this;
        }

        public Builder batcher(GameTestBatcher $$0) {
            this.batcher = $$0;
            return this;
        }

        public GameTestRunner build() {
            return new GameTestRunner(this.batcher, this.batches, this.level, this.testTicker, this.existingStructureSpawner, this.newStructureSpawner, this.haltOnError);
        }
    }
}

