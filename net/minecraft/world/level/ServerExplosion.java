/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 */
package net.minecraft.world.level;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.EntityBasedExplosionDamageCalculator;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class ServerExplosion
implements Explosion {
    private static final ExplosionDamageCalculator EXPLOSION_DAMAGE_CALCULATOR = new ExplosionDamageCalculator();
    private static final int MAX_DROPS_PER_COMBINED_STACK = 16;
    private static final float LARGE_EXPLOSION_RADIUS = 2.0f;
    private final boolean fire;
    private final Explosion.BlockInteraction blockInteraction;
    private final ServerLevel level;
    private final Vec3 center;
    @Nullable
    private final Entity source;
    private final float radius;
    private final DamageSource damageSource;
    private final ExplosionDamageCalculator damageCalculator;
    private final Map<Player, Vec3> hitPlayers = new HashMap<Player, Vec3>();

    public ServerExplosion(ServerLevel $$0, @Nullable Entity $$1, @Nullable DamageSource $$2, @Nullable ExplosionDamageCalculator $$3, Vec3 $$4, float $$5, boolean $$6, Explosion.BlockInteraction $$7) {
        this.level = $$0;
        this.source = $$1;
        this.radius = $$5;
        this.center = $$4;
        this.fire = $$6;
        this.blockInteraction = $$7;
        this.damageSource = $$2 == null ? $$0.damageSources().explosion(this) : $$2;
        this.damageCalculator = $$3 == null ? this.makeDamageCalculator($$1) : $$3;
    }

    private ExplosionDamageCalculator makeDamageCalculator(@Nullable Entity $$0) {
        return $$0 == null ? EXPLOSION_DAMAGE_CALCULATOR : new EntityBasedExplosionDamageCalculator($$0);
    }

    public static float getSeenPercent(Vec3 $$0, Entity $$1) {
        AABB $$2 = $$1.getBoundingBox();
        double $$3 = 1.0 / (($$2.maxX - $$2.minX) * 2.0 + 1.0);
        double $$4 = 1.0 / (($$2.maxY - $$2.minY) * 2.0 + 1.0);
        double $$5 = 1.0 / (($$2.maxZ - $$2.minZ) * 2.0 + 1.0);
        double $$6 = (1.0 - Math.floor(1.0 / $$3) * $$3) / 2.0;
        double $$7 = (1.0 - Math.floor(1.0 / $$5) * $$5) / 2.0;
        if ($$3 < 0.0 || $$4 < 0.0 || $$5 < 0.0) {
            return 0.0f;
        }
        int $$8 = 0;
        int $$9 = 0;
        for (double $$10 = 0.0; $$10 <= 1.0; $$10 += $$3) {
            for (double $$11 = 0.0; $$11 <= 1.0; $$11 += $$4) {
                for (double $$12 = 0.0; $$12 <= 1.0; $$12 += $$5) {
                    double $$13 = Mth.lerp($$10, $$2.minX, $$2.maxX);
                    double $$14 = Mth.lerp($$11, $$2.minY, $$2.maxY);
                    double $$15 = Mth.lerp($$12, $$2.minZ, $$2.maxZ);
                    Vec3 $$16 = new Vec3($$13 + $$6, $$14, $$15 + $$7);
                    if ($$1.level().clip(new ClipContext($$16, $$0, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, $$1)).getType() == HitResult.Type.MISS) {
                        ++$$8;
                    }
                    ++$$9;
                }
            }
        }
        return (float)$$8 / (float)$$9;
    }

    @Override
    public float radius() {
        return this.radius;
    }

    @Override
    public Vec3 center() {
        return this.center;
    }

    private List<BlockPos> calculateExplodedPositions() {
        HashSet<BlockPos> $$0 = new HashSet<BlockPos>();
        int $$1 = 16;
        for (int $$2 = 0; $$2 < 16; ++$$2) {
            for (int $$3 = 0; $$3 < 16; ++$$3) {
                block2: for (int $$4 = 0; $$4 < 16; ++$$4) {
                    if ($$2 != 0 && $$2 != 15 && $$3 != 0 && $$3 != 15 && $$4 != 0 && $$4 != 15) continue;
                    double $$5 = (float)$$2 / 15.0f * 2.0f - 1.0f;
                    double $$6 = (float)$$3 / 15.0f * 2.0f - 1.0f;
                    double $$7 = (float)$$4 / 15.0f * 2.0f - 1.0f;
                    double $$8 = Math.sqrt($$5 * $$5 + $$6 * $$6 + $$7 * $$7);
                    $$5 /= $$8;
                    $$6 /= $$8;
                    $$7 /= $$8;
                    double $$10 = this.center.x;
                    double $$11 = this.center.y;
                    double $$12 = this.center.z;
                    float $$13 = 0.3f;
                    for (float $$9 = this.radius * (0.7f + this.level.random.nextFloat() * 0.6f); $$9 > 0.0f; $$9 -= 0.22500001f) {
                        BlockPos $$14 = BlockPos.containing($$10, $$11, $$12);
                        BlockState $$15 = this.level.getBlockState($$14);
                        FluidState $$16 = this.level.getFluidState($$14);
                        if (!this.level.isInWorldBounds($$14)) continue block2;
                        Optional<Float> $$17 = this.damageCalculator.getBlockExplosionResistance(this, this.level, $$14, $$15, $$16);
                        if ($$17.isPresent()) {
                            $$9 -= ($$17.get().floatValue() + 0.3f) * 0.3f;
                        }
                        if ($$9 > 0.0f && this.damageCalculator.shouldBlockExplode(this, this.level, $$14, $$15, $$9)) {
                            $$0.add($$14);
                        }
                        $$10 += $$5 * (double)0.3f;
                        $$11 += $$6 * (double)0.3f;
                        $$12 += $$7 * (double)0.3f;
                    }
                }
            }
        }
        return new ObjectArrayList($$0);
    }

    private void hurtEntities() {
        float $$0 = this.radius * 2.0f;
        int $$1 = Mth.floor(this.center.x - (double)$$0 - 1.0);
        int $$2 = Mth.floor(this.center.x + (double)$$0 + 1.0);
        int $$3 = Mth.floor(this.center.y - (double)$$0 - 1.0);
        int $$4 = Mth.floor(this.center.y + (double)$$0 + 1.0);
        int $$5 = Mth.floor(this.center.z - (double)$$0 - 1.0);
        int $$6 = Mth.floor(this.center.z + (double)$$0 + 1.0);
        List<Entity> $$7 = this.level.getEntities(this.source, new AABB($$1, $$3, $$5, $$2, $$4, $$6));
        for (Entity $$8 : $$7) {
            Player $$22;
            double $$20;
            float $$16;
            double $$12;
            double $$11;
            double $$10;
            double $$13;
            double $$9;
            if ($$8.ignoreExplosion(this) || !(($$9 = Math.sqrt($$8.distanceToSqr(this.center)) / (double)$$0) <= 1.0) || ($$13 = Math.sqrt(($$10 = $$8.getX() - this.center.x) * $$10 + ($$11 = ($$8 instanceof PrimedTnt ? $$8.getY() : $$8.getEyeY()) - this.center.y) * $$11 + ($$12 = $$8.getZ() - this.center.z) * $$12)) == 0.0) continue;
            $$10 /= $$13;
            $$11 /= $$13;
            $$12 /= $$13;
            boolean $$14 = this.damageCalculator.shouldDamageEntity(this, $$8);
            float $$15 = this.damageCalculator.getKnockbackMultiplier($$8);
            float f = $$16 = $$14 || $$15 != 0.0f ? ServerExplosion.getSeenPercent(this.center, $$8) : 0.0f;
            if ($$14) {
                $$8.hurtServer(this.level, this.damageSource, this.damageCalculator.getEntityDamageAmount(this, $$8, $$16));
            }
            double $$17 = (1.0 - $$9) * (double)$$16 * (double)$$15;
            if ($$8 instanceof LivingEntity) {
                LivingEntity $$18 = (LivingEntity)$$8;
                double $$19 = $$17 * (1.0 - $$18.getAttributeValue(Attributes.EXPLOSION_KNOCKBACK_RESISTANCE));
            } else {
                $$20 = $$17;
            }
            Vec3 $$21 = new Vec3($$10 *= $$20, $$11 *= $$20, $$12 *= $$20);
            $$8.push($$21);
            if (!(!($$8 instanceof Player) || ($$22 = (Player)$$8).isSpectator() || $$22.isCreative() && $$22.getAbilities().flying)) {
                this.hitPlayers.put($$22, $$21);
            }
            $$8.onExplosionHit(this.source);
        }
    }

    private void interactWithBlocks(List<BlockPos> $$0) {
        ArrayList $$12 = new ArrayList();
        Util.shuffle($$0, this.level.random);
        for (BlockPos $$22 : $$0) {
            this.level.getBlockState($$22).onExplosionHit(this.level, $$22, this, ($$1, $$2) -> ServerExplosion.addOrAppendStack($$12, $$1, $$2));
        }
        for (StackCollector $$3 : $$12) {
            Block.popResource((Level)this.level, $$3.pos, $$3.stack);
        }
    }

    private void createFire(List<BlockPos> $$0) {
        for (BlockPos $$1 : $$0) {
            if (this.level.random.nextInt(3) != 0 || !this.level.getBlockState($$1).isAir() || !this.level.getBlockState($$1.below()).isSolidRender()) continue;
            this.level.setBlockAndUpdate($$1, BaseFireBlock.getState(this.level, $$1));
        }
    }

    public void explode() {
        this.level.gameEvent(this.source, GameEvent.EXPLODE, this.center);
        List<BlockPos> $$0 = this.calculateExplodedPositions();
        this.hurtEntities();
        if (this.interactsWithBlocks()) {
            ProfilerFiller $$1 = Profiler.get();
            $$1.push("explosion_blocks");
            this.interactWithBlocks($$0);
            $$1.pop();
        }
        if (this.fire) {
            this.createFire($$0);
        }
    }

    private static void addOrAppendStack(List<StackCollector> $$0, ItemStack $$1, BlockPos $$2) {
        for (StackCollector $$3 : $$0) {
            $$3.tryMerge($$1);
            if (!$$1.isEmpty()) continue;
            return;
        }
        $$0.add(new StackCollector($$2, $$1));
    }

    private boolean interactsWithBlocks() {
        return this.blockInteraction != Explosion.BlockInteraction.KEEP;
    }

    public Map<Player, Vec3> getHitPlayers() {
        return this.hitPlayers;
    }

    @Override
    public ServerLevel level() {
        return this.level;
    }

    @Override
    @Nullable
    public LivingEntity getIndirectSourceEntity() {
        return Explosion.getIndirectSourceEntity(this.source);
    }

    @Override
    @Nullable
    public Entity getDirectSourceEntity() {
        return this.source;
    }

    public DamageSource getDamageSource() {
        return this.damageSource;
    }

    @Override
    public Explosion.BlockInteraction getBlockInteraction() {
        return this.blockInteraction;
    }

    @Override
    public boolean canTriggerBlocks() {
        if (this.blockInteraction != Explosion.BlockInteraction.TRIGGER_BLOCK) {
            return false;
        }
        if (this.source != null && this.source.getType() == EntityType.BREEZE_WIND_CHARGE) {
            return this.level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING);
        }
        return true;
    }

    @Override
    public boolean shouldAffectBlocklikeEntities() {
        boolean $$1;
        boolean $$0 = this.level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING);
        boolean bl = $$1 = this.source == null || this.source.getType() != EntityType.BREEZE_WIND_CHARGE && this.source.getType() != EntityType.WIND_CHARGE;
        if ($$0) {
            return $$1;
        }
        return this.blockInteraction.shouldAffectBlocklikeEntities() && $$1;
    }

    public boolean isSmall() {
        return this.radius < 2.0f || !this.interactsWithBlocks();
    }

    static class StackCollector {
        final BlockPos pos;
        ItemStack stack;

        StackCollector(BlockPos $$0, ItemStack $$1) {
            this.pos = $$0;
            this.stack = $$1;
        }

        public void tryMerge(ItemStack $$0) {
            if (ItemEntity.areMergable(this.stack, $$0)) {
                this.stack = ItemEntity.merge(this.stack, $$0, 16);
            }
        }
    }
}

