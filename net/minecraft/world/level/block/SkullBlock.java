/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap
 */
package net.minecraft.world.level.block;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.AbstractSkullBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.RotationSegment;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class SkullBlock
extends AbstractSkullBlock {
    public static final MapCodec<SkullBlock> CODEC = RecordCodecBuilder.mapCodec($$0 -> $$0.group((App)Type.CODEC.fieldOf("kind").forGetter(AbstractSkullBlock::getType), SkullBlock.propertiesCodec()).apply((Applicative)$$0, SkullBlock::new));
    public static final int MAX = RotationSegment.getMaxSegmentIndex();
    private static final int ROTATIONS = MAX + 1;
    public static final IntegerProperty ROTATION = BlockStateProperties.ROTATION_16;
    private static final VoxelShape SHAPE = Block.column(8.0, 0.0, 8.0);
    private static final VoxelShape SHAPE_PIGLIN = Block.column(10.0, 0.0, 8.0);

    public MapCodec<? extends SkullBlock> codec() {
        return CODEC;
    }

    protected SkullBlock(Type $$0, BlockBehaviour.Properties $$1) {
        super($$0, $$1);
        this.registerDefaultState((BlockState)this.defaultBlockState().setValue(ROTATION, 0));
    }

    @Override
    protected VoxelShape getShape(BlockState $$0, BlockGetter $$1, BlockPos $$2, CollisionContext $$3) {
        return this.getType() == Types.PIGLIN ? SHAPE_PIGLIN : SHAPE;
    }

    @Override
    protected VoxelShape getOcclusionShape(BlockState $$0) {
        return Shapes.empty();
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext $$0) {
        return (BlockState)super.getStateForPlacement($$0).setValue(ROTATION, RotationSegment.convertToSegment($$0.getRotation()));
    }

    @Override
    protected BlockState rotate(BlockState $$0, Rotation $$1) {
        return (BlockState)$$0.setValue(ROTATION, $$1.rotate($$0.getValue(ROTATION), ROTATIONS));
    }

    @Override
    protected BlockState mirror(BlockState $$0, Mirror $$1) {
        return (BlockState)$$0.setValue(ROTATION, $$1.mirror($$0.getValue(ROTATION), ROTATIONS));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> $$0) {
        super.createBlockStateDefinition($$0);
        $$0.a(ROTATION);
    }

    public static interface Type
    extends StringRepresentable {
        public static final Map<String, Type> TYPES = new Object2ObjectArrayMap();
        public static final Codec<Type> CODEC = Codec.stringResolver(StringRepresentable::getSerializedName, TYPES::get);
    }

    public static final class Types
    extends Enum<Types>
    implements Type {
        public static final /* enum */ Types SKELETON = new Types("skeleton");
        public static final /* enum */ Types WITHER_SKELETON = new Types("wither_skeleton");
        public static final /* enum */ Types PLAYER = new Types("player");
        public static final /* enum */ Types ZOMBIE = new Types("zombie");
        public static final /* enum */ Types CREEPER = new Types("creeper");
        public static final /* enum */ Types PIGLIN = new Types("piglin");
        public static final /* enum */ Types DRAGON = new Types("dragon");
        private final String name;
        private static final /* synthetic */ Types[] $VALUES;

        public static Types[] values() {
            return (Types[])$VALUES.clone();
        }

        public static Types valueOf(String $$0) {
            return Enum.valueOf(Types.class, $$0);
        }

        private Types(String $$0) {
            this.name = $$0;
            TYPES.put($$0, this);
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }

        private static /* synthetic */ Types[] a() {
            return new Types[]{SKELETON, WITHER_SKELETON, PLAYER, ZOMBIE, CREEPER, PIGLIN, DRAGON};
        }

        static {
            $VALUES = Types.a();
        }
    }
}

