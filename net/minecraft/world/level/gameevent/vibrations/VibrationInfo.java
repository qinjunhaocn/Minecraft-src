/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.world.level.gameevent.vibrations;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.Holder;
import net.minecraft.core.UUIDUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;

public record VibrationInfo(Holder<GameEvent> gameEvent, float distance, Vec3 pos, @Nullable UUID uuid, @Nullable UUID projectileOwnerUuid, @Nullable Entity entity) {
    public static final Codec<VibrationInfo> CODEC = RecordCodecBuilder.create($$02 -> $$02.group((App)GameEvent.CODEC.fieldOf("game_event").forGetter(VibrationInfo::gameEvent), (App)Codec.floatRange((float)0.0f, (float)Float.MAX_VALUE).fieldOf("distance").forGetter(VibrationInfo::distance), (App)Vec3.CODEC.fieldOf("pos").forGetter(VibrationInfo::pos), (App)UUIDUtil.CODEC.lenientOptionalFieldOf("source").forGetter($$0 -> Optional.ofNullable($$0.uuid())), (App)UUIDUtil.CODEC.lenientOptionalFieldOf("projectile_owner").forGetter($$0 -> Optional.ofNullable($$0.projectileOwnerUuid()))).apply((Applicative)$$02, ($$0, $$1, $$2, $$3, $$4) -> new VibrationInfo((Holder<GameEvent>)$$0, $$1.floatValue(), (Vec3)$$2, $$3.orElse(null), $$4.orElse(null))));

    public VibrationInfo(Holder<GameEvent> $$0, float $$1, Vec3 $$2, @Nullable UUID $$3, @Nullable UUID $$4) {
        this($$0, $$1, $$2, $$3, $$4, null);
    }

    public VibrationInfo(Holder<GameEvent> $$0, float $$1, Vec3 $$2, @Nullable Entity $$3) {
        this($$0, $$1, $$2, $$3 == null ? null : $$3.getUUID(), VibrationInfo.getProjectileOwner($$3), $$3);
    }

    @Nullable
    private static UUID getProjectileOwner(@Nullable Entity $$0) {
        Projectile $$1;
        if ($$0 instanceof Projectile && ($$1 = (Projectile)$$0).getOwner() != null) {
            return $$1.getOwner().getUUID();
        }
        return null;
    }

    public Optional<Entity> getEntity(ServerLevel $$0) {
        return Optional.ofNullable(this.entity).or(() -> Optional.ofNullable(this.uuid).map($$0::getEntity));
    }

    public Optional<Entity> getProjectileOwner(ServerLevel $$02) {
        return this.getEntity($$02).filter($$0 -> $$0 instanceof Projectile).map($$0 -> (Projectile)$$0).map(Projectile::getOwner).or(() -> Optional.ofNullable(this.projectileOwnerUuid).map($$02::getEntity));
    }

    @Nullable
    public UUID uuid() {
        return this.uuid;
    }

    @Nullable
    public UUID projectileOwnerUuid() {
        return this.projectileOwnerUuid;
    }

    @Nullable
    public Entity entity() {
        return this.entity;
    }
}

