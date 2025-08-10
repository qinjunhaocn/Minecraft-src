/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.level.block.entity;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.Pools;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.JigsawBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.pools.JigsawPlacement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class JigsawBlockEntity
extends BlockEntity {
    public static final Codec<ResourceKey<StructureTemplatePool>> POOL_CODEC = ResourceKey.codec(Registries.TEMPLATE_POOL);
    public static final ResourceLocation EMPTY_ID = ResourceLocation.withDefaultNamespace("empty");
    private static final int DEFAULT_PLACEMENT_PRIORITY = 0;
    private static final int DEFAULT_SELECTION_PRIORITY = 0;
    public static final String TARGET = "target";
    public static final String POOL = "pool";
    public static final String JOINT = "joint";
    public static final String PLACEMENT_PRIORITY = "placement_priority";
    public static final String SELECTION_PRIORITY = "selection_priority";
    public static final String NAME = "name";
    public static final String FINAL_STATE = "final_state";
    public static final String DEFAULT_FINAL_STATE = "minecraft:air";
    private ResourceLocation name = EMPTY_ID;
    private ResourceLocation target = EMPTY_ID;
    private ResourceKey<StructureTemplatePool> pool = Pools.EMPTY;
    private JointType joint = JointType.ROLLABLE;
    private String finalState = "minecraft:air";
    private int placementPriority = 0;
    private int selectionPriority = 0;

    public JigsawBlockEntity(BlockPos $$0, BlockState $$1) {
        super(BlockEntityType.JIGSAW, $$0, $$1);
    }

    public ResourceLocation getName() {
        return this.name;
    }

    public ResourceLocation getTarget() {
        return this.target;
    }

    public ResourceKey<StructureTemplatePool> getPool() {
        return this.pool;
    }

    public String getFinalState() {
        return this.finalState;
    }

    public JointType getJoint() {
        return this.joint;
    }

    public int getPlacementPriority() {
        return this.placementPriority;
    }

    public int getSelectionPriority() {
        return this.selectionPriority;
    }

    public void setName(ResourceLocation $$0) {
        this.name = $$0;
    }

    public void setTarget(ResourceLocation $$0) {
        this.target = $$0;
    }

    public void setPool(ResourceKey<StructureTemplatePool> $$0) {
        this.pool = $$0;
    }

    public void setFinalState(String $$0) {
        this.finalState = $$0;
    }

    public void setJoint(JointType $$0) {
        this.joint = $$0;
    }

    public void setPlacementPriority(int $$0) {
        this.placementPriority = $$0;
    }

    public void setSelectionPriority(int $$0) {
        this.selectionPriority = $$0;
    }

    @Override
    protected void saveAdditional(ValueOutput $$0) {
        super.saveAdditional($$0);
        $$0.store(NAME, ResourceLocation.CODEC, this.name);
        $$0.store(TARGET, ResourceLocation.CODEC, this.target);
        $$0.store(POOL, POOL_CODEC, this.pool);
        $$0.putString(FINAL_STATE, this.finalState);
        $$0.store(JOINT, JointType.CODEC, this.joint);
        $$0.putInt(PLACEMENT_PRIORITY, this.placementPriority);
        $$0.putInt(SELECTION_PRIORITY, this.selectionPriority);
    }

    @Override
    protected void loadAdditional(ValueInput $$0) {
        super.loadAdditional($$0);
        this.name = $$0.read(NAME, ResourceLocation.CODEC).orElse(EMPTY_ID);
        this.target = $$0.read(TARGET, ResourceLocation.CODEC).orElse(EMPTY_ID);
        this.pool = $$0.read(POOL, POOL_CODEC).orElse(Pools.EMPTY);
        this.finalState = $$0.getStringOr(FINAL_STATE, DEFAULT_FINAL_STATE);
        this.joint = $$0.read(JOINT, JointType.CODEC).orElseGet(() -> StructureTemplate.getDefaultJointType(this.getBlockState()));
        this.placementPriority = $$0.getIntOr(PLACEMENT_PRIORITY, 0);
        this.selectionPriority = $$0.getIntOr(SELECTION_PRIORITY, 0);
    }

    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider $$0) {
        return this.saveCustomOnly($$0);
    }

    public void generate(ServerLevel $$0, int $$1, boolean $$2) {
        BlockPos $$3 = this.getBlockPos().relative(this.getBlockState().getValue(JigsawBlock.ORIENTATION).front());
        HolderLookup.RegistryLookup $$4 = $$0.registryAccess().lookupOrThrow(Registries.TEMPLATE_POOL);
        Holder.Reference $$5 = $$4.getOrThrow(this.pool);
        JigsawPlacement.generateJigsaw($$0, $$5, this.target, $$1, $$3, $$2);
    }

    public /* synthetic */ Packet getUpdatePacket() {
        return this.getUpdatePacket();
    }

    public static final class JointType
    extends Enum<JointType>
    implements StringRepresentable {
        public static final /* enum */ JointType ROLLABLE = new JointType("rollable");
        public static final /* enum */ JointType ALIGNED = new JointType("aligned");
        public static final StringRepresentable.EnumCodec<JointType> CODEC;
        private final String name;
        private static final /* synthetic */ JointType[] $VALUES;

        public static JointType[] values() {
            return (JointType[])$VALUES.clone();
        }

        public static JointType valueOf(String $$0) {
            return Enum.valueOf(JointType.class, $$0);
        }

        private JointType(String $$0) {
            this.name = $$0;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }

        public Component getTranslatedName() {
            return Component.translatable("jigsaw_block.joint." + this.name);
        }

        private static /* synthetic */ JointType[] b() {
            return new JointType[]{ROLLABLE, ALIGNED};
        }

        static {
            $VALUES = JointType.b();
            CODEC = StringRepresentable.fromEnum(JointType::values);
        }
    }
}

