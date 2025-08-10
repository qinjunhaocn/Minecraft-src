/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.renderer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SculkChargeParticleOptions;
import net.minecraft.core.particles.ShriekParticleOption;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.ParticleUtils;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.JukeboxSong;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BrushableBlock;
import net.minecraft.world.level.block.ComposterBlock;
import net.minecraft.world.level.block.MultifaceBlock;
import net.minecraft.world.level.block.PointedDripstoneBlock;
import net.minecraft.world.level.block.SculkShriekerBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.trialspawner.TrialSpawner;
import net.minecraft.world.level.block.entity.vault.VaultBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class LevelEventHandler {
    private final Minecraft minecraft;
    private final Level level;
    private final LevelRenderer levelRenderer;
    private final Map<BlockPos, SoundInstance> playingJukeboxSongs = new HashMap<BlockPos, SoundInstance>();

    public LevelEventHandler(Minecraft $$0, Level $$1, LevelRenderer $$2) {
        this.minecraft = $$0;
        this.level = $$1;
        this.levelRenderer = $$2;
    }

    public void globalLevelEvent(int $$0, BlockPos $$1, int $$2) {
        switch ($$0) {
            case 1023: 
            case 1028: 
            case 1038: {
                Camera $$3 = this.minecraft.gameRenderer.getMainCamera();
                if (!$$3.isInitialized()) break;
                Vec3 $$4 = Vec3.atCenterOf($$1).subtract($$3.getPosition()).normalize();
                Vec3 $$5 = $$3.getPosition().add($$4.scale(2.0));
                if ($$0 == 1023) {
                    this.level.playLocalSound($$5.x, $$5.y, $$5.z, SoundEvents.WITHER_SPAWN, SoundSource.HOSTILE, 1.0f, 1.0f, false);
                    break;
                }
                if ($$0 == 1038) {
                    this.level.playLocalSound($$5.x, $$5.y, $$5.z, SoundEvents.END_PORTAL_SPAWN, SoundSource.HOSTILE, 1.0f, 1.0f, false);
                    break;
                }
                this.level.playLocalSound($$5.x, $$5.y, $$5.z, SoundEvents.ENDER_DRAGON_DEATH, SoundSource.HOSTILE, 5.0f, 1.0f, false);
            }
        }
    }

    public void levelEvent(int $$0, BlockPos $$12, int $$2) {
        RandomSource $$3 = this.level.random;
        switch ($$0) {
            case 1035: {
                this.level.playLocalSound($$12, SoundEvents.BREWING_STAND_BREW, SoundSource.BLOCKS, 1.0f, 1.0f, false);
                break;
            }
            case 1033: {
                this.level.playLocalSound($$12, SoundEvents.CHORUS_FLOWER_GROW, SoundSource.BLOCKS, 1.0f, 1.0f, false);
                break;
            }
            case 1034: {
                this.level.playLocalSound($$12, SoundEvents.CHORUS_FLOWER_DEATH, SoundSource.BLOCKS, 1.0f, 1.0f, false);
                break;
            }
            case 1032: {
                this.minecraft.getSoundManager().play(SimpleSoundInstance.forLocalAmbience(SoundEvents.PORTAL_TRAVEL, $$3.nextFloat() * 0.4f + 0.8f, 0.25f));
                break;
            }
            case 1001: {
                this.level.playLocalSound($$12, SoundEvents.DISPENSER_FAIL, SoundSource.BLOCKS, 1.0f, 1.2f, false);
                break;
            }
            case 1000: {
                this.level.playLocalSound($$12, SoundEvents.DISPENSER_DISPENSE, SoundSource.BLOCKS, 1.0f, 1.0f, false);
                break;
            }
            case 1049: {
                this.level.playLocalSound($$12, SoundEvents.CRAFTER_CRAFT, SoundSource.BLOCKS, 1.0f, 1.0f, false);
                break;
            }
            case 1050: {
                this.level.playLocalSound($$12, SoundEvents.CRAFTER_FAIL, SoundSource.BLOCKS, 1.0f, 1.0f, false);
                break;
            }
            case 1004: {
                this.level.playLocalSound($$12, SoundEvents.FIREWORK_ROCKET_SHOOT, SoundSource.NEUTRAL, 1.0f, 1.2f, false);
                break;
            }
            case 1002: {
                this.level.playLocalSound($$12, SoundEvents.DISPENSER_LAUNCH, SoundSource.BLOCKS, 1.0f, 1.2f, false);
                break;
            }
            case 1051: {
                this.level.playLocalSound($$12, SoundEvents.WIND_CHARGE_THROW, SoundSource.BLOCKS, 0.5f, 0.4f / (this.level.getRandom().nextFloat() * 0.4f + 0.8f), false);
                break;
            }
            case 2010: {
                this.shootParticles($$2, $$12, $$3, ParticleTypes.WHITE_SMOKE);
                break;
            }
            case 2000: {
                this.shootParticles($$2, $$12, $$3, ParticleTypes.SMOKE);
                break;
            }
            case 2003: {
                double $$4 = (double)$$12.getX() + 0.5;
                double $$5 = $$12.getY();
                double $$6 = (double)$$12.getZ() + 0.5;
                for (int $$7 = 0; $$7 < 8; ++$$7) {
                    this.levelRenderer.addParticle(new ItemParticleOption(ParticleTypes.ITEM, new ItemStack(Items.ENDER_EYE)), $$4, $$5, $$6, $$3.nextGaussian() * 0.15, $$3.nextDouble() * 0.2, $$3.nextGaussian() * 0.15);
                }
                for (double $$8 = 0.0; $$8 < Math.PI * 2; $$8 += 0.15707963267948966) {
                    this.levelRenderer.addParticle(ParticleTypes.PORTAL, $$4 + Math.cos($$8) * 5.0, $$5 - 0.4, $$6 + Math.sin($$8) * 5.0, Math.cos($$8) * -5.0, 0.0, Math.sin($$8) * -5.0);
                    this.levelRenderer.addParticle(ParticleTypes.PORTAL, $$4 + Math.cos($$8) * 5.0, $$5 - 0.4, $$6 + Math.sin($$8) * 5.0, Math.cos($$8) * -7.0, 0.0, Math.sin($$8) * -7.0);
                }
                break;
            }
            case 2002: 
            case 2007: {
                Vec3 $$9 = Vec3.atBottomCenterOf($$12);
                for (int $$10 = 0; $$10 < 8; ++$$10) {
                    this.levelRenderer.addParticle(new ItemParticleOption(ParticleTypes.ITEM, new ItemStack(Items.SPLASH_POTION)), $$9.x, $$9.y, $$9.z, $$3.nextGaussian() * 0.15, $$3.nextDouble() * 0.2, $$3.nextGaussian() * 0.15);
                }
                float $$11 = (float)($$2 >> 16 & 0xFF) / 255.0f;
                float $$122 = (float)($$2 >> 8 & 0xFF) / 255.0f;
                float $$13 = (float)($$2 >> 0 & 0xFF) / 255.0f;
                SimpleParticleType $$14 = $$0 == 2007 ? ParticleTypes.INSTANT_EFFECT : ParticleTypes.EFFECT;
                for (int $$15 = 0; $$15 < 100; ++$$15) {
                    double $$16 = $$3.nextDouble() * 4.0;
                    double $$17 = $$3.nextDouble() * Math.PI * 2.0;
                    double $$18 = Math.cos($$17) * $$16;
                    double $$19 = 0.01 + $$3.nextDouble() * 0.5;
                    double $$20 = Math.sin($$17) * $$16;
                    Particle $$21 = this.levelRenderer.addParticleInternal($$14, $$14.getType().getOverrideLimiter(), $$9.x + $$18 * 0.1, $$9.y + 0.3, $$9.z + $$20 * 0.1, $$18, $$19, $$20);
                    if ($$21 == null) continue;
                    float $$22 = 0.75f + $$3.nextFloat() * 0.25f;
                    $$21.setColor($$11 * $$22, $$122 * $$22, $$13 * $$22);
                    $$21.setPower((float)$$16);
                }
                this.level.playLocalSound($$12, SoundEvents.SPLASH_POTION_BREAK, SoundSource.NEUTRAL, 1.0f, $$3.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 2001: {
                BlockState $$23 = Block.stateById($$2);
                if (!$$23.isAir()) {
                    SoundType $$24 = $$23.getSoundType();
                    this.level.playLocalSound($$12, $$24.getBreakSound(), SoundSource.BLOCKS, ($$24.getVolume() + 1.0f) / 2.0f, $$24.getPitch() * 0.8f, false);
                }
                this.level.addDestroyBlockEffect($$12, $$23);
                break;
            }
            case 3008: {
                BlockState $$25 = Block.stateById($$2);
                Block $$13 = $$25.getBlock();
                if ($$13 instanceof BrushableBlock) {
                    BrushableBlock $$26 = (BrushableBlock)$$13;
                    this.level.playLocalSound($$12, $$26.getBrushCompletedSound(), SoundSource.PLAYERS, 1.0f, 1.0f, false);
                }
                this.level.addDestroyBlockEffect($$12, $$25);
                break;
            }
            case 2004: {
                for (int $$27 = 0; $$27 < 20; ++$$27) {
                    double $$28 = (double)$$12.getX() + 0.5 + ($$3.nextDouble() - 0.5) * 2.0;
                    double $$29 = (double)$$12.getY() + 0.5 + ($$3.nextDouble() - 0.5) * 2.0;
                    double $$30 = (double)$$12.getZ() + 0.5 + ($$3.nextDouble() - 0.5) * 2.0;
                    this.level.addParticle(ParticleTypes.SMOKE, $$28, $$29, $$30, 0.0, 0.0, 0.0);
                    this.level.addParticle(ParticleTypes.FLAME, $$28, $$29, $$30, 0.0, 0.0, 0.0);
                }
                break;
            }
            case 3011: {
                TrialSpawner.addSpawnParticles(this.level, $$12, $$3, TrialSpawner.FlameParticle.decode((int)$$2).particleType);
                break;
            }
            case 3012: {
                this.level.playLocalSound($$12, SoundEvents.TRIAL_SPAWNER_SPAWN_MOB, SoundSource.BLOCKS, 1.0f, ($$3.nextFloat() - $$3.nextFloat()) * 0.2f + 1.0f, true);
                TrialSpawner.addSpawnParticles(this.level, $$12, $$3, TrialSpawner.FlameParticle.decode((int)$$2).particleType);
                break;
            }
            case 3021: {
                this.level.playLocalSound($$12, SoundEvents.TRIAL_SPAWNER_SPAWN_ITEM, SoundSource.BLOCKS, 1.0f, ($$3.nextFloat() - $$3.nextFloat()) * 0.2f + 1.0f, true);
                TrialSpawner.addSpawnParticles(this.level, $$12, $$3, TrialSpawner.FlameParticle.decode((int)$$2).particleType);
                break;
            }
            case 3013: {
                this.level.playLocalSound($$12, SoundEvents.TRIAL_SPAWNER_DETECT_PLAYER, SoundSource.BLOCKS, 1.0f, ($$3.nextFloat() - $$3.nextFloat()) * 0.2f + 1.0f, true);
                TrialSpawner.addDetectPlayerParticles(this.level, $$12, $$3, $$2, ParticleTypes.TRIAL_SPAWNER_DETECTED_PLAYER);
                break;
            }
            case 3019: {
                this.level.playLocalSound($$12, SoundEvents.TRIAL_SPAWNER_DETECT_PLAYER, SoundSource.BLOCKS, 1.0f, ($$3.nextFloat() - $$3.nextFloat()) * 0.2f + 1.0f, true);
                TrialSpawner.addDetectPlayerParticles(this.level, $$12, $$3, $$2, ParticleTypes.TRIAL_SPAWNER_DETECTED_PLAYER_OMINOUS);
                break;
            }
            case 3020: {
                this.level.playLocalSound($$12, SoundEvents.TRIAL_SPAWNER_OMINOUS_ACTIVATE, SoundSource.BLOCKS, $$2 == 0 ? 0.3f : 1.0f, ($$3.nextFloat() - $$3.nextFloat()) * 0.2f + 1.0f, true);
                TrialSpawner.addDetectPlayerParticles(this.level, $$12, $$3, 0, ParticleTypes.TRIAL_SPAWNER_DETECTED_PLAYER_OMINOUS);
                TrialSpawner.addBecomeOminousParticles(this.level, $$12, $$3);
                break;
            }
            case 3014: {
                this.level.playLocalSound($$12, SoundEvents.TRIAL_SPAWNER_EJECT_ITEM, SoundSource.BLOCKS, 1.0f, ($$3.nextFloat() - $$3.nextFloat()) * 0.2f + 1.0f, true);
                TrialSpawner.addEjectItemParticles(this.level, $$12, $$3);
                break;
            }
            case 3017: {
                TrialSpawner.addEjectItemParticles(this.level, $$12, $$3);
                break;
            }
            case 3015: {
                BlockEntity $$28 = this.level.getBlockEntity($$12);
                if (!($$28 instanceof VaultBlockEntity)) break;
                VaultBlockEntity $$31 = (VaultBlockEntity)$$28;
                VaultBlockEntity.Client.emitActivationParticles(this.level, $$31.getBlockPos(), $$31.getBlockState(), $$31.getSharedData(), $$2 == 0 ? ParticleTypes.SMALL_FLAME : ParticleTypes.SOUL_FIRE_FLAME);
                this.level.playLocalSound($$12, SoundEvents.VAULT_ACTIVATE, SoundSource.BLOCKS, 1.0f, ($$3.nextFloat() - $$3.nextFloat()) * 0.2f + 1.0f, true);
                break;
            }
            case 3016: {
                VaultBlockEntity.Client.emitDeactivationParticles(this.level, $$12, $$2 == 0 ? ParticleTypes.SMALL_FLAME : ParticleTypes.SOUL_FIRE_FLAME);
                this.level.playLocalSound($$12, SoundEvents.VAULT_DEACTIVATE, SoundSource.BLOCKS, 1.0f, ($$3.nextFloat() - $$3.nextFloat()) * 0.2f + 1.0f, true);
                break;
            }
            case 3018: {
                for (int $$32 = 0; $$32 < 10; ++$$32) {
                    double $$33 = $$3.nextGaussian() * 0.02;
                    double $$34 = $$3.nextGaussian() * 0.02;
                    double $$35 = $$3.nextGaussian() * 0.02;
                    this.level.addParticle(ParticleTypes.POOF, (double)$$12.getX() + $$3.nextDouble(), (double)$$12.getY() + $$3.nextDouble(), (double)$$12.getZ() + $$3.nextDouble(), $$33, $$34, $$35);
                }
                this.level.playLocalSound($$12, SoundEvents.COBWEB_PLACE, SoundSource.BLOCKS, 1.0f, ($$3.nextFloat() - $$3.nextFloat()) * 0.2f + 1.0f, true);
                break;
            }
            case 1505: {
                BoneMealItem.addGrowthParticles(this.level, $$12, $$2);
                this.level.playLocalSound($$12, SoundEvents.BONE_MEAL_USE, SoundSource.BLOCKS, 1.0f, 1.0f, false);
                break;
            }
            case 2011: {
                ParticleUtils.spawnParticleInBlock(this.level, $$12, $$2, ParticleTypes.HAPPY_VILLAGER);
                break;
            }
            case 2012: {
                ParticleUtils.spawnParticleInBlock(this.level, $$12, $$2, ParticleTypes.HAPPY_VILLAGER);
                break;
            }
            case 3009: {
                ParticleUtils.spawnParticlesOnBlockFaces(this.level, $$12, ParticleTypes.EGG_CRACK, UniformInt.of(3, 6));
                break;
            }
            case 3002: {
                if ($$2 >= 0 && $$2 < Direction.Axis.VALUES.length) {
                    ParticleUtils.spawnParticlesAlongAxis(Direction.Axis.VALUES[$$2], this.level, $$12, 0.125, ParticleTypes.ELECTRIC_SPARK, UniformInt.of(10, 19));
                    break;
                }
                ParticleUtils.spawnParticlesOnBlockFaces(this.level, $$12, ParticleTypes.ELECTRIC_SPARK, UniformInt.of(3, 5));
                break;
            }
            case 2013: {
                ParticleUtils.spawnSmashAttackParticles(this.level, $$12, $$2);
                break;
            }
            case 3006: {
                int $$36 = $$2 >> 6;
                if ($$36 > 0) {
                    if ($$3.nextFloat() < 0.3f + (float)$$36 * 0.1f) {
                        float $$37 = 0.15f + 0.02f * (float)$$36 * (float)$$36 * $$3.nextFloat();
                        float $$38 = 0.4f + 0.3f * (float)$$36 * $$3.nextFloat();
                        this.level.playLocalSound($$12, SoundEvents.SCULK_BLOCK_CHARGE, SoundSource.BLOCKS, $$37, $$38, false);
                    }
                    byte $$39 = (byte)($$2 & 0x3F);
                    UniformInt $$40 = UniformInt.of(0, $$36);
                    float $$41 = 0.005f;
                    Supplier<Vec3> $$42 = () -> new Vec3(Mth.nextDouble($$3, -0.005f, 0.005f), Mth.nextDouble($$3, -0.005f, 0.005f), Mth.nextDouble($$3, -0.005f, 0.005f));
                    if ($$39 == 0) {
                        for (Direction $$43 : Direction.values()) {
                            float $$44 = $$43 == Direction.DOWN ? (float)Math.PI : 0.0f;
                            double $$45 = $$43.getAxis() == Direction.Axis.Y ? 0.65 : 0.57;
                            ParticleUtils.spawnParticlesOnBlockFace(this.level, $$12, new SculkChargeParticleOptions($$44), $$40, $$43, $$42, $$45);
                        }
                    } else {
                        for (Direction $$46 : MultifaceBlock.unpack($$39)) {
                            float $$47 = $$46 == Direction.UP ? (float)Math.PI : 0.0f;
                            double $$48 = 0.35;
                            ParticleUtils.spawnParticlesOnBlockFace(this.level, $$12, new SculkChargeParticleOptions($$47), $$40, $$46, $$42, 0.35);
                        }
                    }
                } else {
                    this.level.playLocalSound($$12, SoundEvents.SCULK_BLOCK_CHARGE, SoundSource.BLOCKS, 1.0f, 1.0f, false);
                    boolean $$49 = this.level.getBlockState($$12).isCollisionShapeFullBlock(this.level, $$12);
                    int $$50 = $$49 ? 40 : 20;
                    float $$51 = $$49 ? 0.45f : 0.25f;
                    float $$52 = 0.07f;
                    for (int $$53 = 0; $$53 < $$50; ++$$53) {
                        float $$54 = 2.0f * $$3.nextFloat() - 1.0f;
                        float $$55 = 2.0f * $$3.nextFloat() - 1.0f;
                        float $$56 = 2.0f * $$3.nextFloat() - 1.0f;
                        this.level.addParticle(ParticleTypes.SCULK_CHARGE_POP, (double)$$12.getX() + 0.5 + (double)($$54 * $$51), (double)$$12.getY() + 0.5 + (double)($$55 * $$51), (double)$$12.getZ() + 0.5 + (double)($$56 * $$51), $$54 * 0.07f, $$55 * 0.07f, $$56 * 0.07f);
                    }
                }
                break;
            }
            case 3007: {
                boolean $$59;
                for (int $$57 = 0; $$57 < 10; ++$$57) {
                    this.level.addParticle(new ShriekParticleOption($$57 * 5), (double)$$12.getX() + 0.5, (double)$$12.getY() + SculkShriekerBlock.TOP_Y, (double)$$12.getZ() + 0.5, 0.0, 0.0, 0.0);
                }
                BlockState $$58 = this.level.getBlockState($$12);
                boolean bl = $$59 = $$58.hasProperty(BlockStateProperties.WATERLOGGED) && $$58.getValue(BlockStateProperties.WATERLOGGED) != false;
                if ($$59) break;
                this.level.playLocalSound((double)$$12.getX() + 0.5, (double)$$12.getY() + SculkShriekerBlock.TOP_Y, (double)$$12.getZ() + 0.5, SoundEvents.SCULK_SHRIEKER_SHRIEK, SoundSource.BLOCKS, 2.0f, 0.6f + this.level.random.nextFloat() * 0.4f, false);
                break;
            }
            case 3003: {
                ParticleUtils.spawnParticlesOnBlockFaces(this.level, $$12, ParticleTypes.WAX_ON, UniformInt.of(3, 5));
                this.level.playLocalSound($$12, SoundEvents.HONEYCOMB_WAX_ON, SoundSource.BLOCKS, 1.0f, 1.0f, false);
                break;
            }
            case 3004: {
                ParticleUtils.spawnParticlesOnBlockFaces(this.level, $$12, ParticleTypes.WAX_OFF, UniformInt.of(3, 5));
                break;
            }
            case 3005: {
                ParticleUtils.spawnParticlesOnBlockFaces(this.level, $$12, ParticleTypes.SCRAPE, UniformInt.of(3, 5));
                break;
            }
            case 2008: {
                this.level.addParticle(ParticleTypes.EXPLOSION, (double)$$12.getX() + 0.5, (double)$$12.getY() + 0.5, (double)$$12.getZ() + 0.5, 0.0, 0.0, 0.0);
                break;
            }
            case 1500: {
                ComposterBlock.handleFill(this.level, $$12, $$2 > 0);
                break;
            }
            case 1504: {
                PointedDripstoneBlock.spawnDripParticle(this.level, $$12, this.level.getBlockState($$12));
                break;
            }
            case 1501: {
                this.level.playLocalSound($$12, SoundEvents.LAVA_EXTINGUISH, SoundSource.BLOCKS, 0.5f, 2.6f + ($$3.nextFloat() - $$3.nextFloat()) * 0.8f, false);
                for (int $$60 = 0; $$60 < 8; ++$$60) {
                    this.level.addParticle(ParticleTypes.LARGE_SMOKE, (double)$$12.getX() + $$3.nextDouble(), (double)$$12.getY() + 1.2, (double)$$12.getZ() + $$3.nextDouble(), 0.0, 0.0, 0.0);
                }
                break;
            }
            case 1502: {
                this.level.playLocalSound($$12, SoundEvents.REDSTONE_TORCH_BURNOUT, SoundSource.BLOCKS, 0.5f, 2.6f + ($$3.nextFloat() - $$3.nextFloat()) * 0.8f, false);
                for (int $$61 = 0; $$61 < 5; ++$$61) {
                    double $$62 = (double)$$12.getX() + $$3.nextDouble() * 0.6 + 0.2;
                    double $$63 = (double)$$12.getY() + $$3.nextDouble() * 0.6 + 0.2;
                    double $$64 = (double)$$12.getZ() + $$3.nextDouble() * 0.6 + 0.2;
                    this.level.addParticle(ParticleTypes.SMOKE, $$62, $$63, $$64, 0.0, 0.0, 0.0);
                }
                break;
            }
            case 1503: {
                this.level.playLocalSound($$12, SoundEvents.END_PORTAL_FRAME_FILL, SoundSource.BLOCKS, 1.0f, 1.0f, false);
                for (int $$65 = 0; $$65 < 16; ++$$65) {
                    double $$66 = (double)$$12.getX() + (5.0 + $$3.nextDouble() * 6.0) / 16.0;
                    double $$67 = (double)$$12.getY() + 0.8125;
                    double $$68 = (double)$$12.getZ() + (5.0 + $$3.nextDouble() * 6.0) / 16.0;
                    this.level.addParticle(ParticleTypes.SMOKE, $$66, $$67, $$68, 0.0, 0.0, 0.0);
                }
                break;
            }
            case 2006: {
                for (int $$69 = 0; $$69 < 200; ++$$69) {
                    float $$70 = $$3.nextFloat() * 4.0f;
                    float $$71 = $$3.nextFloat() * ((float)Math.PI * 2);
                    double $$72 = Mth.cos($$71) * $$70;
                    double $$73 = 0.01 + $$3.nextDouble() * 0.5;
                    double $$74 = Mth.sin($$71) * $$70;
                    Particle $$75 = this.levelRenderer.addParticleInternal(ParticleTypes.DRAGON_BREATH, false, (double)$$12.getX() + $$72 * 0.1, (double)$$12.getY() + 0.3, (double)$$12.getZ() + $$74 * 0.1, $$72, $$73, $$74);
                    if ($$75 == null) continue;
                    $$75.setPower($$70);
                }
                if ($$2 != 1) break;
                this.level.playLocalSound($$12, SoundEvents.DRAGON_FIREBALL_EXPLODE, SoundSource.HOSTILE, 1.0f, $$3.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 2009: {
                for (int $$76 = 0; $$76 < 8; ++$$76) {
                    this.level.addParticle(ParticleTypes.CLOUD, (double)$$12.getX() + $$3.nextDouble(), (double)$$12.getY() + 1.2, (double)$$12.getZ() + $$3.nextDouble(), 0.0, 0.0, 0.0);
                }
                break;
            }
            case 1009: {
                if ($$2 == 0) {
                    this.level.playLocalSound($$12, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.5f, 2.6f + ($$3.nextFloat() - $$3.nextFloat()) * 0.8f, false);
                    break;
                }
                if ($$2 != 1) break;
                this.level.playLocalSound($$12, SoundEvents.GENERIC_EXTINGUISH_FIRE, SoundSource.BLOCKS, 0.7f, 1.6f + ($$3.nextFloat() - $$3.nextFloat()) * 0.4f, false);
                break;
            }
            case 1029: {
                this.level.playLocalSound($$12, SoundEvents.ANVIL_DESTROY, SoundSource.BLOCKS, 1.0f, $$3.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 1030: {
                this.level.playLocalSound($$12, SoundEvents.ANVIL_USE, SoundSource.BLOCKS, 1.0f, $$3.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 1044: {
                this.level.playLocalSound($$12, SoundEvents.SMITHING_TABLE_USE, SoundSource.BLOCKS, 1.0f, this.level.random.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 1031: {
                this.level.playLocalSound($$12, SoundEvents.ANVIL_LAND, SoundSource.BLOCKS, 0.3f, this.level.random.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 1039: {
                this.level.playLocalSound($$12, SoundEvents.PHANTOM_BITE, SoundSource.HOSTILE, 0.3f, this.level.random.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 1010: {
                this.level.registryAccess().lookupOrThrow(Registries.JUKEBOX_SONG).get($$2).ifPresent($$1 -> this.playJukeboxSong((Holder<JukeboxSong>)$$1, $$12));
                break;
            }
            case 1011: {
                this.stopJukeboxSongAndNotifyNearby($$12);
                break;
            }
            case 1015: {
                this.level.playLocalSound($$12, SoundEvents.GHAST_WARN, SoundSource.HOSTILE, 10.0f, ($$3.nextFloat() - $$3.nextFloat()) * 0.2f + 1.0f, false);
                break;
            }
            case 1017: {
                this.level.playLocalSound($$12, SoundEvents.ENDER_DRAGON_SHOOT, SoundSource.HOSTILE, 10.0f, ($$3.nextFloat() - $$3.nextFloat()) * 0.2f + 1.0f, false);
                break;
            }
            case 1016: {
                this.level.playLocalSound($$12, SoundEvents.GHAST_SHOOT, SoundSource.HOSTILE, 10.0f, ($$3.nextFloat() - $$3.nextFloat()) * 0.2f + 1.0f, false);
                break;
            }
            case 1019: {
                this.level.playLocalSound($$12, SoundEvents.ZOMBIE_ATTACK_WOODEN_DOOR, SoundSource.HOSTILE, 2.0f, ($$3.nextFloat() - $$3.nextFloat()) * 0.2f + 1.0f, false);
                break;
            }
            case 1022: {
                this.level.playLocalSound($$12, SoundEvents.WITHER_BREAK_BLOCK, SoundSource.HOSTILE, 2.0f, ($$3.nextFloat() - $$3.nextFloat()) * 0.2f + 1.0f, false);
                break;
            }
            case 1021: {
                this.level.playLocalSound($$12, SoundEvents.ZOMBIE_BREAK_WOODEN_DOOR, SoundSource.HOSTILE, 2.0f, ($$3.nextFloat() - $$3.nextFloat()) * 0.2f + 1.0f, false);
                break;
            }
            case 1020: {
                this.level.playLocalSound($$12, SoundEvents.ZOMBIE_ATTACK_IRON_DOOR, SoundSource.HOSTILE, 2.0f, ($$3.nextFloat() - $$3.nextFloat()) * 0.2f + 1.0f, false);
                break;
            }
            case 1018: {
                this.level.playLocalSound($$12, SoundEvents.BLAZE_SHOOT, SoundSource.HOSTILE, 2.0f, ($$3.nextFloat() - $$3.nextFloat()) * 0.2f + 1.0f, false);
                break;
            }
            case 1024: {
                this.level.playLocalSound($$12, SoundEvents.WITHER_SHOOT, SoundSource.HOSTILE, 2.0f, ($$3.nextFloat() - $$3.nextFloat()) * 0.2f + 1.0f, false);
                break;
            }
            case 1026: {
                this.level.playLocalSound($$12, SoundEvents.ZOMBIE_INFECT, SoundSource.HOSTILE, 2.0f, ($$3.nextFloat() - $$3.nextFloat()) * 0.2f + 1.0f, false);
                break;
            }
            case 1027: {
                this.level.playLocalSound($$12, SoundEvents.ZOMBIE_VILLAGER_CONVERTED, SoundSource.HOSTILE, 2.0f, ($$3.nextFloat() - $$3.nextFloat()) * 0.2f + 1.0f, false);
                break;
            }
            case 1040: {
                this.level.playLocalSound($$12, SoundEvents.ZOMBIE_CONVERTED_TO_DROWNED, SoundSource.HOSTILE, 2.0f, ($$3.nextFloat() - $$3.nextFloat()) * 0.2f + 1.0f, false);
                break;
            }
            case 1041: {
                this.level.playLocalSound($$12, SoundEvents.HUSK_CONVERTED_TO_ZOMBIE, SoundSource.HOSTILE, 2.0f, ($$3.nextFloat() - $$3.nextFloat()) * 0.2f + 1.0f, false);
                break;
            }
            case 1025: {
                this.level.playLocalSound($$12, SoundEvents.BAT_TAKEOFF, SoundSource.NEUTRAL, 0.05f, ($$3.nextFloat() - $$3.nextFloat()) * 0.2f + 1.0f, false);
                break;
            }
            case 1042: {
                this.level.playLocalSound($$12, SoundEvents.GRINDSTONE_USE, SoundSource.BLOCKS, 1.0f, this.level.random.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 1043: {
                this.level.playLocalSound($$12, SoundEvents.BOOK_PAGE_TURN, SoundSource.BLOCKS, 1.0f, this.level.random.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 3000: {
                this.level.addParticle(ParticleTypes.EXPLOSION_EMITTER, true, true, (double)$$12.getX() + 0.5, (double)$$12.getY() + 0.5, (double)$$12.getZ() + 0.5, 0.0, 0.0, 0.0);
                this.level.playLocalSound($$12, SoundEvents.END_GATEWAY_SPAWN, SoundSource.BLOCKS, 10.0f, (1.0f + (this.level.random.nextFloat() - this.level.random.nextFloat()) * 0.2f) * 0.7f, false);
                break;
            }
            case 3001: {
                this.level.playLocalSound($$12, SoundEvents.ENDER_DRAGON_GROWL, SoundSource.HOSTILE, 64.0f, 0.8f + this.level.random.nextFloat() * 0.3f, false);
                break;
            }
            case 1045: {
                this.level.playLocalSound($$12, SoundEvents.POINTED_DRIPSTONE_LAND, SoundSource.BLOCKS, 2.0f, this.level.random.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 1046: {
                this.level.playLocalSound($$12, SoundEvents.POINTED_DRIPSTONE_DRIP_LAVA_INTO_CAULDRON, SoundSource.BLOCKS, 2.0f, this.level.random.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 1047: {
                this.level.playLocalSound($$12, SoundEvents.POINTED_DRIPSTONE_DRIP_WATER_INTO_CAULDRON, SoundSource.BLOCKS, 2.0f, this.level.random.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 1048: {
                this.level.playLocalSound($$12, SoundEvents.SKELETON_CONVERTED_TO_STRAY, SoundSource.HOSTILE, 2.0f, ($$3.nextFloat() - $$3.nextFloat()) * 0.2f + 1.0f, false);
            }
        }
    }

    private void shootParticles(int $$0, BlockPos $$1, RandomSource $$2, SimpleParticleType $$3) {
        Direction $$4 = Direction.from3DDataValue($$0);
        int $$5 = $$4.getStepX();
        int $$6 = $$4.getStepY();
        int $$7 = $$4.getStepZ();
        for (int $$8 = 0; $$8 < 10; ++$$8) {
            double $$9 = $$2.nextDouble() * 0.2 + 0.01;
            double $$10 = (double)$$1.getX() + (double)$$5 * 0.6 + 0.5 + (double)$$5 * 0.01 + ($$2.nextDouble() - 0.5) * (double)$$7 * 0.5;
            double $$11 = (double)$$1.getY() + (double)$$6 * 0.6 + 0.5 + (double)$$6 * 0.01 + ($$2.nextDouble() - 0.5) * (double)$$6 * 0.5;
            double $$12 = (double)$$1.getZ() + (double)$$7 * 0.6 + 0.5 + (double)$$7 * 0.01 + ($$2.nextDouble() - 0.5) * (double)$$5 * 0.5;
            double $$13 = (double)$$5 * $$9 + $$2.nextGaussian() * 0.01;
            double $$14 = (double)$$6 * $$9 + $$2.nextGaussian() * 0.01;
            double $$15 = (double)$$7 * $$9 + $$2.nextGaussian() * 0.01;
            this.levelRenderer.addParticle($$3, $$10, $$11, $$12, $$13, $$14, $$15);
        }
    }

    private void playJukeboxSong(Holder<JukeboxSong> $$0, BlockPos $$1) {
        this.stopJukeboxSong($$1);
        JukeboxSong $$2 = $$0.value();
        SoundEvent $$3 = $$2.soundEvent().value();
        SimpleSoundInstance $$4 = SimpleSoundInstance.forJukeboxSong($$3, Vec3.atCenterOf($$1));
        this.playingJukeboxSongs.put($$1, $$4);
        this.minecraft.getSoundManager().play($$4);
        this.minecraft.gui.setNowPlaying($$2.description());
        this.notifyNearbyEntities(this.level, $$1, true);
    }

    private void stopJukeboxSong(BlockPos $$0) {
        SoundInstance $$1 = this.playingJukeboxSongs.remove($$0);
        if ($$1 != null) {
            this.minecraft.getSoundManager().stop($$1);
        }
    }

    private void stopJukeboxSongAndNotifyNearby(BlockPos $$0) {
        this.stopJukeboxSong($$0);
        this.notifyNearbyEntities(this.level, $$0, false);
    }

    private void notifyNearbyEntities(Level $$0, BlockPos $$1, boolean $$2) {
        List<LivingEntity> $$3 = $$0.getEntitiesOfClass(LivingEntity.class, new AABB($$1).inflate(3.0));
        for (LivingEntity $$4 : $$3) {
            $$4.setRecordPlayingNearby($$1, $$2);
        }
    }
}

