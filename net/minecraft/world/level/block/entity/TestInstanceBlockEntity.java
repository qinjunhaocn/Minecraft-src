/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  io.netty.buffer.ByteBuf
 *  java.lang.MatchException
 */
package net.minecraft.world.level.block.entity;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import net.minecraft.ChatFormatting;
import net.minecraft.FileUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.structures.NbtToSnbt;
import net.minecraft.gametest.framework.FailedTestTracker;
import net.minecraft.gametest.framework.GameTestInfo;
import net.minecraft.gametest.framework.GameTestInstance;
import net.minecraft.gametest.framework.GameTestRunner;
import net.minecraft.gametest.framework.GameTestTicker;
import net.minecraft.gametest.framework.RetryOptions;
import net.minecraft.gametest.framework.StructureUtils;
import net.minecraft.gametest.framework.TestCommand;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ARGB;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BeaconBeamOwner;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.BoundingBoxRenderable;
import net.minecraft.world.level.block.entity.StructureBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;

public class TestInstanceBlockEntity
extends BlockEntity
implements BeaconBeamOwner,
BoundingBoxRenderable {
    private static final Component INVALID_TEST_NAME = Component.translatable("test_instance_block.invalid_test");
    private static final List<BeaconBeamOwner.Section> BEAM_CLEARED = List.of();
    private static final List<BeaconBeamOwner.Section> BEAM_RUNNING = List.of((Object)new BeaconBeamOwner.Section(ARGB.color(128, 128, 128)));
    private static final List<BeaconBeamOwner.Section> BEAM_SUCCESS = List.of((Object)new BeaconBeamOwner.Section(ARGB.color(0, 255, 0)));
    private static final List<BeaconBeamOwner.Section> BEAM_REQUIRED_FAILED = List.of((Object)new BeaconBeamOwner.Section(ARGB.color(255, 0, 0)));
    private static final List<BeaconBeamOwner.Section> BEAM_OPTIONAL_FAILED = List.of((Object)new BeaconBeamOwner.Section(ARGB.color(255, 128, 0)));
    private static final Vec3i STRUCTURE_OFFSET = new Vec3i(0, 1, 1);
    private Data data = new Data(Optional.empty(), Vec3i.ZERO, Rotation.NONE, false, Status.CLEARED, Optional.empty());

    public TestInstanceBlockEntity(BlockPos $$0, BlockState $$1) {
        super(BlockEntityType.TEST_INSTANCE_BLOCK, $$0, $$1);
    }

    public void set(Data $$0) {
        this.data = $$0;
        this.setChanged();
    }

    public static Optional<Vec3i> getStructureSize(ServerLevel $$0, ResourceKey<GameTestInstance> $$1) {
        return TestInstanceBlockEntity.getStructureTemplate($$0, $$1).map(StructureTemplate::getSize);
    }

    public BoundingBox getStructureBoundingBox() {
        BlockPos $$0 = this.getStructurePos();
        BlockPos $$1 = $$0.offset(this.getTransformedSize()).offset(-1, -1, -1);
        return BoundingBox.fromCorners($$0, $$1);
    }

    public AABB getStructureBounds() {
        return AABB.of(this.getStructureBoundingBox());
    }

    private static Optional<StructureTemplate> getStructureTemplate(ServerLevel $$02, ResourceKey<GameTestInstance> $$12) {
        return $$02.registryAccess().get($$12).map($$0 -> ((GameTestInstance)$$0.value()).structure()).flatMap($$1 -> $$02.getStructureManager().get((ResourceLocation)$$1));
    }

    public Optional<ResourceKey<GameTestInstance>> test() {
        return this.data.test();
    }

    public Component getTestName() {
        return this.test().map($$0 -> Component.literal($$0.location().toString())).orElse(INVALID_TEST_NAME);
    }

    private Optional<Holder.Reference<GameTestInstance>> getTestHolder() {
        return this.test().flatMap(this.level.registryAccess()::get);
    }

    public boolean ignoreEntities() {
        return this.data.ignoreEntities();
    }

    public Vec3i getSize() {
        return this.data.size();
    }

    public Rotation getRotation() {
        return this.getTestHolder().map(Holder::value).map(GameTestInstance::rotation).orElse(Rotation.NONE).getRotated(this.data.rotation());
    }

    public Optional<Component> errorMessage() {
        return this.data.errorMessage();
    }

    public void setErrorMessage(Component $$0) {
        this.set(this.data.withError($$0));
    }

    public void setSuccess() {
        this.set(this.data.withStatus(Status.FINISHED));
        this.removeBarriers();
    }

    public void setRunning() {
        this.set(this.data.withStatus(Status.RUNNING));
    }

    @Override
    public void setChanged() {
        super.setChanged();
        if (this.level instanceof ServerLevel) {
            this.level.sendBlockUpdated(this.getBlockPos(), Blocks.AIR.defaultBlockState(), this.getBlockState(), 3);
        }
    }

    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider $$0) {
        return this.saveCustomOnly($$0);
    }

    @Override
    protected void loadAdditional(ValueInput $$0) {
        $$0.read("data", Data.CODEC).ifPresent(this::set);
    }

    @Override
    protected void saveAdditional(ValueOutput $$0) {
        $$0.store("data", Data.CODEC, this.data);
    }

    @Override
    public BoundingBoxRenderable.Mode renderMode() {
        return BoundingBoxRenderable.Mode.BOX;
    }

    public BlockPos getStructurePos() {
        return TestInstanceBlockEntity.getStructurePos(this.getBlockPos());
    }

    public static BlockPos getStructurePos(BlockPos $$0) {
        return $$0.offset(STRUCTURE_OFFSET);
    }

    @Override
    public BoundingBoxRenderable.RenderableBox getRenderableBox() {
        return new BoundingBoxRenderable.RenderableBox(new BlockPos(STRUCTURE_OFFSET), this.getTransformedSize());
    }

    @Override
    public List<BeaconBeamOwner.Section> getBeamSections() {
        return switch (this.data.status().ordinal()) {
            default -> throw new MatchException(null, null);
            case 0 -> BEAM_CLEARED;
            case 1 -> BEAM_RUNNING;
            case 2 -> this.errorMessage().isEmpty() ? BEAM_SUCCESS : (this.getTestHolder().map(Holder::value).map(GameTestInstance::required).orElse(true) != false ? BEAM_REQUIRED_FAILED : BEAM_OPTIONAL_FAILED);
        };
    }

    private Vec3i getTransformedSize() {
        Vec3i $$0 = this.getSize();
        Rotation $$1 = this.getRotation();
        boolean $$2 = $$1 == Rotation.CLOCKWISE_90 || $$1 == Rotation.COUNTERCLOCKWISE_90;
        int $$3 = $$2 ? $$0.getZ() : $$0.getX();
        int $$4 = $$2 ? $$0.getX() : $$0.getZ();
        return new Vec3i($$3, $$0.getY(), $$4);
    }

    public void resetTest(Consumer<Component> $$0) {
        this.removeBarriers();
        boolean $$1 = this.placeStructure();
        if ($$1) {
            $$0.accept(Component.a("test_instance_block.reset_success", this.getTestName()).withStyle(ChatFormatting.GREEN));
        }
        this.set(this.data.withStatus(Status.CLEARED));
    }

    public Optional<ResourceLocation> saveTest(Consumer<Component> $$0) {
        Optional<ResourceLocation> $$3;
        Optional<Holder.Reference<GameTestInstance>> $$1 = this.getTestHolder();
        if ($$1.isPresent()) {
            Optional<ResourceLocation> $$2 = Optional.of($$1.get().value().structure());
        } else {
            $$3 = this.test().map(ResourceKey::location);
        }
        if ($$3.isEmpty()) {
            BlockPos $$4 = this.getBlockPos();
            $$0.accept(Component.a("test_instance_block.error.unable_to_save", $$4.getX(), $$4.getY(), $$4.getZ()).withStyle(ChatFormatting.RED));
            return $$3;
        }
        Level level = this.level;
        if (level instanceof ServerLevel) {
            ServerLevel $$5 = (ServerLevel)level;
            StructureBlockEntity.saveStructure($$5, $$3.get(), this.getStructurePos(), this.getSize(), this.ignoreEntities(), "", true, List.of((Object)Blocks.AIR));
        }
        return $$3;
    }

    /*
     * WARNING - void declaration
     */
    public boolean exportTest(Consumer<Component> $$0) {
        void $$3;
        Level level;
        Optional<ResourceLocation> $$1 = this.saveTest($$0);
        if ($$1.isEmpty() || !((level = this.level) instanceof ServerLevel)) {
            return false;
        }
        ServerLevel $$2 = (ServerLevel)level;
        return TestInstanceBlockEntity.export((ServerLevel)$$3, $$1.get(), $$0);
    }

    public static boolean export(ServerLevel $$0, ResourceLocation $$1, Consumer<Component> $$2) {
        Path $$3 = StructureUtils.testStructuresDir;
        Path $$4 = $$0.getStructureManager().createAndValidatePathToGeneratedStructure($$1, ".nbt");
        Path $$5 = NbtToSnbt.convertStructure(CachedOutput.NO_CACHE, $$4, $$1.getPath(), $$3.resolve($$1.getNamespace()).resolve("structure"));
        if ($$5 == null) {
            $$2.accept(Component.literal("Failed to export " + String.valueOf($$4)).withStyle(ChatFormatting.RED));
            return true;
        }
        try {
            FileUtil.createDirectoriesSafe($$5.getParent());
        } catch (IOException $$6) {
            $$2.accept(Component.literal("Could not create folder " + String.valueOf($$5.getParent())).withStyle(ChatFormatting.RED));
            return true;
        }
        $$2.accept(Component.literal("Exported " + String.valueOf($$1) + " to " + String.valueOf($$5.toAbsolutePath())));
        return false;
    }

    /*
     * WARNING - void declaration
     */
    public void runTest(Consumer<Component> $$0) {
        void $$2;
        Level level = this.level;
        if (!(level instanceof ServerLevel)) {
            return;
        }
        ServerLevel $$1 = (ServerLevel)level;
        Optional<Holder.Reference<GameTestInstance>> $$3 = this.getTestHolder();
        BlockPos $$4 = this.getBlockPos();
        if ($$3.isEmpty()) {
            $$0.accept(Component.a("test_instance_block.error.no_test", $$4.getX(), $$4.getY(), $$4.getZ()).withStyle(ChatFormatting.RED));
            return;
        }
        if (!this.placeStructure()) {
            $$0.accept(Component.a("test_instance_block.error.no_test_structure", $$4.getX(), $$4.getY(), $$4.getZ()).withStyle(ChatFormatting.RED));
            return;
        }
        GameTestRunner.clearMarkers((ServerLevel)$$2);
        GameTestTicker.SINGLETON.clear();
        FailedTestTracker.forgetFailedTests();
        $$0.accept(Component.a("test_instance_block.starting", $$3.get().getRegisteredName()));
        GameTestInfo $$5 = new GameTestInfo($$3.get(), this.data.rotation(), (ServerLevel)$$2, RetryOptions.noRetries());
        $$5.setTestBlockPos($$4);
        GameTestRunner $$6 = GameTestRunner.Builder.fromInfo(List.of((Object)$$5), (ServerLevel)$$2).build();
        TestCommand.trackAndStartRunner($$2.getServer().createCommandSourceStack(), $$6);
    }

    public boolean placeStructure() {
        Level level = this.level;
        if (level instanceof ServerLevel) {
            ServerLevel $$0 = (ServerLevel)level;
            Optional $$12 = this.data.test().flatMap($$1 -> TestInstanceBlockEntity.getStructureTemplate($$0, $$1));
            if ($$12.isPresent()) {
                this.placeStructure($$0, (StructureTemplate)$$12.get());
                return true;
            }
        }
        return false;
    }

    private void placeStructure(ServerLevel $$0, StructureTemplate $$1) {
        StructurePlaceSettings $$2 = new StructurePlaceSettings().setRotation(this.getRotation()).setIgnoreEntities(this.data.ignoreEntities()).setKnownShape(true);
        BlockPos $$3 = this.getStartCorner();
        this.forceLoadChunks();
        this.removeEntities();
        $$1.placeInWorld($$0, $$3, $$3, $$2, $$0.getRandom(), 818);
    }

    private void removeEntities() {
        this.level.getEntities(null, this.getStructureBounds()).stream().filter($$0 -> !($$0 instanceof Player)).forEach(Entity::discard);
    }

    private void forceLoadChunks() {
        Level level = this.level;
        if (level instanceof ServerLevel) {
            ServerLevel $$0 = (ServerLevel)level;
            this.getStructureBoundingBox().intersectingChunks().forEach($$1 -> $$0.setChunkForced($$1.x, $$1.z, true));
        }
    }

    public BlockPos getStartCorner() {
        Vec3i $$0 = this.getSize();
        Rotation $$1 = this.getRotation();
        BlockPos $$2 = this.getStructurePos();
        return switch ($$1) {
            default -> throw new MatchException(null, null);
            case Rotation.NONE -> $$2;
            case Rotation.CLOCKWISE_90 -> $$2.offset($$0.getZ() - 1, 0, 0);
            case Rotation.CLOCKWISE_180 -> $$2.offset($$0.getX() - 1, 0, $$0.getZ() - 1);
            case Rotation.COUNTERCLOCKWISE_90 -> $$2.offset(0, 0, $$0.getX() - 1);
        };
    }

    public void encaseStructure() {
        this.processStructureBoundary($$0 -> {
            if (!this.level.getBlockState((BlockPos)$$0).is(Blocks.TEST_INSTANCE_BLOCK)) {
                this.level.setBlockAndUpdate((BlockPos)$$0, Blocks.BARRIER.defaultBlockState());
            }
        });
    }

    public void removeBarriers() {
        this.processStructureBoundary($$0 -> {
            if (this.level.getBlockState((BlockPos)$$0).is(Blocks.BARRIER)) {
                this.level.setBlockAndUpdate((BlockPos)$$0, Blocks.AIR.defaultBlockState());
            }
        });
    }

    public void processStructureBoundary(Consumer<BlockPos> $$02) {
        AABB $$1 = this.getStructureBounds();
        boolean $$2 = this.getTestHolder().map($$0 -> ((GameTestInstance)$$0.value()).skyAccess()).orElse(false) == false;
        BlockPos $$3 = BlockPos.containing($$1.minX, $$1.minY, $$1.minZ).offset(-1, -1, -1);
        BlockPos $$42 = BlockPos.containing($$1.maxX, $$1.maxY, $$1.maxZ);
        BlockPos.betweenClosedStream($$3, $$42).forEach($$4 -> {
            boolean $$6;
            boolean $$5 = $$4.getX() == $$3.getX() || $$4.getX() == $$42.getX() || $$4.getZ() == $$3.getZ() || $$4.getZ() == $$42.getZ() || $$4.getY() == $$3.getY();
            boolean bl = $$6 = $$4.getY() == $$42.getY();
            if ($$5 || $$6 && $$2) {
                $$02.accept((BlockPos)$$4);
            }
        });
    }

    public /* synthetic */ Packet getUpdatePacket() {
        return this.getUpdatePacket();
    }

    public record Data(Optional<ResourceKey<GameTestInstance>> test, Vec3i size, Rotation rotation, boolean ignoreEntities, Status status, Optional<Component> errorMessage) {
        public static final Codec<Data> CODEC = RecordCodecBuilder.create($$0 -> $$0.group((App)ResourceKey.codec(Registries.TEST_INSTANCE).optionalFieldOf("test").forGetter(Data::test), (App)Vec3i.CODEC.fieldOf("size").forGetter(Data::size), (App)Rotation.CODEC.fieldOf("rotation").forGetter(Data::rotation), (App)Codec.BOOL.fieldOf("ignore_entities").forGetter(Data::ignoreEntities), (App)Status.CODEC.fieldOf("status").forGetter(Data::status), (App)ComponentSerialization.CODEC.optionalFieldOf("error_message").forGetter(Data::errorMessage)).apply((Applicative)$$0, Data::new));
        public static final StreamCodec<RegistryFriendlyByteBuf, Data> STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.optional(ResourceKey.streamCodec(Registries.TEST_INSTANCE)), Data::test, Vec3i.STREAM_CODEC, Data::size, Rotation.STREAM_CODEC, Data::rotation, ByteBufCodecs.BOOL, Data::ignoreEntities, Status.STREAM_CODEC, Data::status, ByteBufCodecs.optional(ComponentSerialization.STREAM_CODEC), Data::errorMessage, Data::new);

        public Data withSize(Vec3i $$0) {
            return new Data(this.test, $$0, this.rotation, this.ignoreEntities, this.status, this.errorMessage);
        }

        public Data withStatus(Status $$0) {
            return new Data(this.test, this.size, this.rotation, this.ignoreEntities, $$0, Optional.empty());
        }

        public Data withError(Component $$0) {
            return new Data(this.test, this.size, this.rotation, this.ignoreEntities, Status.FINISHED, Optional.of($$0));
        }
    }

    public static final class Status
    extends Enum<Status>
    implements StringRepresentable {
        public static final /* enum */ Status CLEARED = new Status("cleared", 0);
        public static final /* enum */ Status RUNNING = new Status("running", 1);
        public static final /* enum */ Status FINISHED = new Status("finished", 2);
        private static final IntFunction<Status> ID_MAP;
        public static final Codec<Status> CODEC;
        public static final StreamCodec<ByteBuf, Status> STREAM_CODEC;
        private final String id;
        private final int index;
        private static final /* synthetic */ Status[] $VALUES;

        public static Status[] values() {
            return (Status[])$VALUES.clone();
        }

        public static Status valueOf(String $$0) {
            return Enum.valueOf(Status.class, $$0);
        }

        private Status(String $$0, int $$1) {
            this.id = $$0;
            this.index = $$1;
        }

        @Override
        public String getSerializedName() {
            return this.id;
        }

        public static Status byIndex(int $$0) {
            return ID_MAP.apply($$0);
        }

        private static /* synthetic */ Status[] a() {
            return new Status[]{CLEARED, RUNNING, FINISHED};
        }

        static {
            $VALUES = Status.a();
            ID_MAP = ByIdMap.a($$0 -> $$0.index, Status.values(), ByIdMap.OutOfBoundsStrategy.ZERO);
            CODEC = StringRepresentable.fromEnum(Status::values);
            STREAM_CODEC = ByteBufCodecs.idMapper(Status::byIndex, $$0 -> $$0.index);
        }
    }
}

