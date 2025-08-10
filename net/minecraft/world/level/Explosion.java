/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  java.lang.runtime.SwitchBootstraps
 */
package net.minecraft.world.level;

import java.lang.runtime.SwitchBootstraps;
import javax.annotation.Nullable;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public interface Explosion {
    public static DamageSource getDefaultDamageSource(Level $$0, @Nullable Entity $$1) {
        return $$0.damageSources().explosion($$1, Explosion.getIndirectSourceEntity($$1));
    }

    /*
     * WARNING - void declaration
     */
    @Nullable
    public static LivingEntity getIndirectSourceEntity(@Nullable Entity $$0) {
        LivingEntity livingEntity;
        Entity entity = $$0;
        int n = 0;
        block5: while (true) {
            switch (SwitchBootstraps.typeSwitch("typeSwitch", new Object[]{PrimedTnt.class, LivingEntity.class, Projectile.class}, (Object)entity, (int)n)) {
                case 0: {
                    PrimedTnt $$1 = (PrimedTnt)entity;
                    livingEntity = $$1.getOwner();
                    break block5;
                }
                case 1: {
                    LivingEntity $$2;
                    livingEntity = $$2 = (LivingEntity)entity;
                    break block5;
                }
                case 2: {
                    void $$5;
                    Projectile $$3 = (Projectile)entity;
                    Entity entity2 = $$3.getOwner();
                    if (!(entity2 instanceof LivingEntity)) {
                        n = 3;
                        continue block5;
                    }
                    LivingEntity $$4 = (LivingEntity)entity2;
                    livingEntity = $$5;
                    break block5;
                }
                default: {
                    livingEntity = null;
                    break block5;
                }
            }
            break;
        }
        return livingEntity;
    }

    public ServerLevel level();

    public BlockInteraction getBlockInteraction();

    @Nullable
    public LivingEntity getIndirectSourceEntity();

    @Nullable
    public Entity getDirectSourceEntity();

    public float radius();

    public Vec3 center();

    public boolean canTriggerBlocks();

    public boolean shouldAffectBlocklikeEntities();

    public static final class BlockInteraction
    extends Enum<BlockInteraction> {
        public static final /* enum */ BlockInteraction KEEP = new BlockInteraction(false);
        public static final /* enum */ BlockInteraction DESTROY = new BlockInteraction(true);
        public static final /* enum */ BlockInteraction DESTROY_WITH_DECAY = new BlockInteraction(true);
        public static final /* enum */ BlockInteraction TRIGGER_BLOCK = new BlockInteraction(false);
        private final boolean shouldAffectBlocklikeEntities;
        private static final /* synthetic */ BlockInteraction[] $VALUES;

        public static BlockInteraction[] values() {
            return (BlockInteraction[])$VALUES.clone();
        }

        public static BlockInteraction valueOf(String $$0) {
            return Enum.valueOf(BlockInteraction.class, $$0);
        }

        private BlockInteraction(boolean $$0) {
            this.shouldAffectBlocklikeEntities = $$0;
        }

        public boolean shouldAffectBlocklikeEntities() {
            return this.shouldAffectBlocklikeEntities;
        }

        private static /* synthetic */ BlockInteraction[] b() {
            return new BlockInteraction[]{KEEP, DESTROY, DESTROY_WITH_DECAY, TRIGGER_BLOCK};
        }

        static {
            $VALUES = BlockInteraction.b();
        }
    }
}

