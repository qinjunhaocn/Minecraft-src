/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.level.block.entity.trialspawner;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;

public interface PlayerDetector {
    public static final PlayerDetector NO_CREATIVE_PLAYERS = ($$0, $$1, $$22, $$32, $$4) -> $$1.getPlayers($$0, $$2 -> $$2.blockPosition().closerThan($$22, $$32) && !$$2.isCreative() && !$$2.isSpectator()).stream().filter($$3 -> !$$4 || PlayerDetector.inLineOfSight($$0, $$22.getCenter(), $$3.getEyePosition())).map(Entity::getUUID).toList();
    public static final PlayerDetector INCLUDING_CREATIVE_PLAYERS = ($$0, $$1, $$22, $$32, $$4) -> $$1.getPlayers($$0, $$2 -> $$2.blockPosition().closerThan($$22, $$32) && !$$2.isSpectator()).stream().filter($$3 -> !$$4 || PlayerDetector.inLineOfSight($$0, $$22.getCenter(), $$3.getEyePosition())).map(Entity::getUUID).toList();
    public static final PlayerDetector SHEEP = ($$0, $$1, $$2, $$32, $$4) -> {
        AABB $$5 = new AABB($$2).inflate($$32);
        return $$1.getEntities($$0, EntityType.SHEEP, $$5, LivingEntity::isAlive).stream().filter($$3 -> !$$4 || PlayerDetector.inLineOfSight($$0, $$2.getCenter(), $$3.getEyePosition())).map(Entity::getUUID).toList();
    };

    public List<UUID> detect(ServerLevel var1, EntitySelector var2, BlockPos var3, double var4, boolean var6);

    private static boolean inLineOfSight(Level $$0, Vec3 $$1, Vec3 $$2) {
        BlockHitResult $$3 = $$0.clip(new ClipContext($$2, $$1, ClipContext.Block.VISUAL, ClipContext.Fluid.NONE, CollisionContext.empty()));
        return $$3.getBlockPos().equals(BlockPos.containing($$1)) || $$3.getType() == HitResult.Type.MISS;
    }

    public static interface EntitySelector {
        public static final EntitySelector SELECT_FROM_LEVEL = new EntitySelector(){

            public List<ServerPlayer> getPlayers(ServerLevel $$0, Predicate<? super Player> $$1) {
                return $$0.getPlayers($$1);
            }

            @Override
            public <T extends Entity> List<T> getEntities(ServerLevel $$0, EntityTypeTest<Entity, T> $$1, AABB $$2, Predicate<? super T> $$3) {
                return $$0.getEntities($$1, $$2, $$3);
            }
        };

        public List<? extends Player> getPlayers(ServerLevel var1, Predicate<? super Player> var2);

        public <T extends Entity> List<T> getEntities(ServerLevel var1, EntityTypeTest<Entity, T> var2, AABB var3, Predicate<? super T> var4);

        public static EntitySelector onlySelectPlayer(Player $$0) {
            return EntitySelector.onlySelectPlayers(List.of((Object)$$0));
        }

        public static EntitySelector onlySelectPlayers(final List<Player> $$0) {
            return new EntitySelector(){

                public List<Player> getPlayers(ServerLevel $$02, Predicate<? super Player> $$1) {
                    return $$0.stream().filter($$1).toList();
                }

                @Override
                public <T extends Entity> List<T> getEntities(ServerLevel $$02, EntityTypeTest<Entity, T> $$1, AABB $$2, Predicate<? super T> $$3) {
                    return $$0.stream().map($$1::tryCast).filter(Objects::nonNull).filter($$3).toList();
                }
            };
        }
    }
}

