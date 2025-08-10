/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.datafixers.util.Either
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  io.netty.buffer.ByteBuf
 */
package net.minecraft.world.level.gameevent;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.PositionSource;
import net.minecraft.world.level.gameevent.PositionSourceType;
import net.minecraft.world.phys.Vec3;

public class EntityPositionSource
implements PositionSource {
    public static final MapCodec<EntityPositionSource> CODEC = RecordCodecBuilder.mapCodec($$02 -> $$02.group((App)UUIDUtil.CODEC.fieldOf("source_entity").forGetter(EntityPositionSource::getUuid), (App)Codec.FLOAT.fieldOf("y_offset").orElse((Object)Float.valueOf(0.0f)).forGetter($$0 -> Float.valueOf($$0.yOffset))).apply((Applicative)$$02, ($$0, $$1) -> new EntityPositionSource((Either<Entity, Either<UUID, Integer>>)Either.right((Object)Either.left((Object)$$0)), $$1.floatValue())));
    public static final StreamCodec<ByteBuf, EntityPositionSource> STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.VAR_INT, EntityPositionSource::getId, ByteBufCodecs.FLOAT, $$0 -> Float.valueOf($$0.yOffset), ($$0, $$1) -> new EntityPositionSource((Either<Entity, Either<UUID, Integer>>)Either.right((Object)Either.right((Object)$$0)), $$1.floatValue()));
    private Either<Entity, Either<UUID, Integer>> entityOrUuidOrId;
    private final float yOffset;

    public EntityPositionSource(Entity $$0, float $$1) {
        this((Either<Entity, Either<UUID, Integer>>)Either.left((Object)$$0), $$1);
    }

    private EntityPositionSource(Either<Entity, Either<UUID, Integer>> $$0, float $$1) {
        this.entityOrUuidOrId = $$0;
        this.yOffset = $$1;
    }

    @Override
    public Optional<Vec3> getPosition(Level $$02) {
        if (this.entityOrUuidOrId.left().isEmpty()) {
            this.resolveEntity($$02);
        }
        return this.entityOrUuidOrId.left().map($$0 -> $$0.position().add(0.0, this.yOffset, 0.0));
    }

    private void resolveEntity(Level $$02) {
        ((Optional)this.entityOrUuidOrId.map(Optional::of, $$12 -> Optional.ofNullable((Entity)$$12.map($$1 -> {
            Entity entity;
            if ($$02 instanceof ServerLevel) {
                ServerLevel $$2 = (ServerLevel)$$02;
                entity = $$2.getEntity((UUID)$$1);
            } else {
                entity = null;
            }
            return entity;
        }, $$02::getEntity)))).ifPresent($$0 -> {
            this.entityOrUuidOrId = Either.left((Object)$$0);
        });
    }

    private UUID getUuid() {
        return (UUID)this.entityOrUuidOrId.map(Entity::getUUID, $$02 -> (UUID)$$02.map(Function.identity(), $$0 -> {
            throw new RuntimeException("Unable to get entityId from uuid");
        }));
    }

    private int getId() {
        return (Integer)this.entityOrUuidOrId.map(Entity::getId, $$02 -> (Integer)$$02.map($$0 -> {
            throw new IllegalStateException("Unable to get entityId from uuid");
        }, Function.identity()));
    }

    public PositionSourceType<EntityPositionSource> getType() {
        return PositionSourceType.ENTITY;
    }

    public static class Type
    implements PositionSourceType<EntityPositionSource> {
        @Override
        public MapCodec<EntityPositionSource> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<ByteBuf, EntityPositionSource> streamCodec() {
            return STREAM_CODEC;
        }
    }
}

