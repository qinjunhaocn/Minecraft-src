/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.world.level.block;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.TrailParticleOption;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CreakingHeartBlock;
import net.minecraft.world.level.block.FlowerBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;

public class EyeblossomBlock
extends FlowerBlock {
    public static final MapCodec<EyeblossomBlock> CODEC = RecordCodecBuilder.mapCodec($$02 -> $$02.group((App)Codec.BOOL.fieldOf("open").forGetter($$0 -> $$0.type.open), EyeblossomBlock.propertiesCodec()).apply((Applicative)$$02, EyeblossomBlock::new));
    private static final int EYEBLOSSOM_XZ_RANGE = 3;
    private static final int EYEBLOSSOM_Y_RANGE = 2;
    private final Type type;

    public MapCodec<? extends EyeblossomBlock> codec() {
        return CODEC;
    }

    public EyeblossomBlock(Type $$0, BlockBehaviour.Properties $$1) {
        super($$0.effect, $$0.effectDuration, $$1);
        this.type = $$0;
    }

    public EyeblossomBlock(boolean $$0, BlockBehaviour.Properties $$1) {
        super(Type.fromBoolean((boolean)$$0).effect, Type.fromBoolean((boolean)$$0).effectDuration, $$1);
        this.type = Type.fromBoolean($$0);
    }

    @Override
    public void animateTick(BlockState $$0, Level $$1, BlockPos $$2, RandomSource $$3) {
        BlockState $$4;
        if (this.type.emitSounds() && $$3.nextInt(700) == 0 && ($$4 = $$1.getBlockState($$2.below())).is(Blocks.PALE_MOSS_BLOCK)) {
            $$1.playLocalSound($$2.getX(), $$2.getY(), $$2.getZ(), SoundEvents.EYEBLOSSOM_IDLE, SoundSource.AMBIENT, 1.0f, 1.0f, false);
        }
    }

    @Override
    protected void randomTick(BlockState $$0, ServerLevel $$1, BlockPos $$2, RandomSource $$3) {
        if (this.tryChangingState($$0, $$1, $$2, $$3)) {
            $$1.playSound(null, $$2, this.type.transform().longSwitchSound, SoundSource.BLOCKS, 1.0f, 1.0f);
        }
        super.randomTick($$0, $$1, $$2, $$3);
    }

    @Override
    protected void tick(BlockState $$0, ServerLevel $$1, BlockPos $$2, RandomSource $$3) {
        if (this.tryChangingState($$0, $$1, $$2, $$3)) {
            $$1.playSound(null, $$2, this.type.transform().shortSwitchSound, SoundSource.BLOCKS, 1.0f, 1.0f);
        }
        super.tick($$0, $$1, $$2, $$3);
    }

    private boolean tryChangingState(BlockState $$0, ServerLevel $$1, BlockPos $$2, RandomSource $$3) {
        if (!$$1.dimensionType().natural()) {
            return false;
        }
        if (CreakingHeartBlock.isNaturalNight($$1) == this.type.open) {
            return false;
        }
        Type $$42 = this.type.transform();
        $$1.setBlock($$2, $$42.state(), 3);
        $$1.gameEvent(GameEvent.BLOCK_CHANGE, $$2, GameEvent.Context.of($$0));
        $$42.spawnTransformParticle($$1, $$2, $$3);
        BlockPos.betweenClosed($$2.offset(-3, -2, -3), $$2.offset(3, 2, 3)).forEach($$4 -> {
            BlockState $$5 = $$1.getBlockState((BlockPos)$$4);
            if ($$5 == $$0) {
                double $$6 = Math.sqrt($$2.distSqr((Vec3i)$$4));
                int $$7 = $$3.nextIntBetweenInclusive((int)($$6 * 5.0), (int)($$6 * 10.0));
                $$1.scheduleTick((BlockPos)$$4, $$0.getBlock(), $$7);
            }
        });
        return true;
    }

    @Override
    protected void entityInside(BlockState $$0, Level $$1, BlockPos $$2, Entity $$3, InsideBlockEffectApplier $$4) {
        if (!$$1.isClientSide() && $$1.getDifficulty() != Difficulty.PEACEFUL && $$3 instanceof Bee) {
            Bee $$5 = (Bee)$$3;
            if (Bee.attractsBees($$0) && !$$5.hasEffect(MobEffects.POISON)) {
                $$5.addEffect(this.getBeeInteractionEffect());
            }
        }
    }

    @Override
    public MobEffectInstance getBeeInteractionEffect() {
        return new MobEffectInstance(MobEffects.POISON, 25);
    }

    public static final class Type
    extends Enum<Type> {
        public static final /* enum */ Type OPEN = new Type(true, MobEffects.BLINDNESS, 11.0f, SoundEvents.EYEBLOSSOM_OPEN_LONG, SoundEvents.EYEBLOSSOM_OPEN, 16545810);
        public static final /* enum */ Type CLOSED = new Type(false, MobEffects.NAUSEA, 7.0f, SoundEvents.EYEBLOSSOM_CLOSE_LONG, SoundEvents.EYEBLOSSOM_CLOSE, 0x5F5F5F);
        final boolean open;
        final Holder<MobEffect> effect;
        final float effectDuration;
        final SoundEvent longSwitchSound;
        final SoundEvent shortSwitchSound;
        private final int particleColor;
        private static final /* synthetic */ Type[] $VALUES;

        public static Type[] values() {
            return (Type[])$VALUES.clone();
        }

        public static Type valueOf(String $$0) {
            return Enum.valueOf(Type.class, $$0);
        }

        private Type(boolean $$0, Holder<MobEffect> $$1, float $$2, SoundEvent $$3, SoundEvent $$4, int $$5) {
            this.open = $$0;
            this.effect = $$1;
            this.effectDuration = $$2;
            this.longSwitchSound = $$3;
            this.shortSwitchSound = $$4;
            this.particleColor = $$5;
        }

        public Block block() {
            return this.open ? Blocks.OPEN_EYEBLOSSOM : Blocks.CLOSED_EYEBLOSSOM;
        }

        public BlockState state() {
            return this.block().defaultBlockState();
        }

        public Type transform() {
            return Type.fromBoolean(!this.open);
        }

        public boolean emitSounds() {
            return this.open;
        }

        public static Type fromBoolean(boolean $$0) {
            return $$0 ? OPEN : CLOSED;
        }

        public void spawnTransformParticle(ServerLevel $$0, BlockPos $$1, RandomSource $$2) {
            Vec3 $$3 = $$1.getCenter();
            double $$4 = 0.5 + $$2.nextDouble();
            Vec3 $$5 = new Vec3($$2.nextDouble() - 0.5, $$2.nextDouble() + 1.0, $$2.nextDouble() - 0.5);
            Vec3 $$6 = $$3.add($$5.scale($$4));
            TrailParticleOption $$7 = new TrailParticleOption($$6, this.particleColor, (int)(20.0 * $$4));
            $$0.sendParticles($$7, $$3.x, $$3.y, $$3.z, 1, 0.0, 0.0, 0.0, 0.0);
        }

        public SoundEvent longSwitchSound() {
            return this.longSwitchSound;
        }

        private static /* synthetic */ Type[] f() {
            return new Type[]{OPEN, CLOSED};
        }

        static {
            $VALUES = Type.f();
        }
    }
}

