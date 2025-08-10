/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.level.gameevent;

import com.mojang.serialization.Codec;
import javax.annotation.Nullable;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEventListener;
import net.minecraft.world.phys.Vec3;

public record GameEvent(int notificationRadius) {
    public static final Holder.Reference<GameEvent> BLOCK_ACTIVATE = GameEvent.register("block_activate");
    public static final Holder.Reference<GameEvent> BLOCK_ATTACH = GameEvent.register("block_attach");
    public static final Holder.Reference<GameEvent> BLOCK_CHANGE = GameEvent.register("block_change");
    public static final Holder.Reference<GameEvent> BLOCK_CLOSE = GameEvent.register("block_close");
    public static final Holder.Reference<GameEvent> BLOCK_DEACTIVATE = GameEvent.register("block_deactivate");
    public static final Holder.Reference<GameEvent> BLOCK_DESTROY = GameEvent.register("block_destroy");
    public static final Holder.Reference<GameEvent> BLOCK_DETACH = GameEvent.register("block_detach");
    public static final Holder.Reference<GameEvent> BLOCK_OPEN = GameEvent.register("block_open");
    public static final Holder.Reference<GameEvent> BLOCK_PLACE = GameEvent.register("block_place");
    public static final Holder.Reference<GameEvent> CONTAINER_CLOSE = GameEvent.register("container_close");
    public static final Holder.Reference<GameEvent> CONTAINER_OPEN = GameEvent.register("container_open");
    public static final Holder.Reference<GameEvent> DRINK = GameEvent.register("drink");
    public static final Holder.Reference<GameEvent> EAT = GameEvent.register("eat");
    public static final Holder.Reference<GameEvent> ELYTRA_GLIDE = GameEvent.register("elytra_glide");
    public static final Holder.Reference<GameEvent> ENTITY_DAMAGE = GameEvent.register("entity_damage");
    public static final Holder.Reference<GameEvent> ENTITY_DIE = GameEvent.register("entity_die");
    public static final Holder.Reference<GameEvent> ENTITY_DISMOUNT = GameEvent.register("entity_dismount");
    public static final Holder.Reference<GameEvent> ENTITY_INTERACT = GameEvent.register("entity_interact");
    public static final Holder.Reference<GameEvent> ENTITY_MOUNT = GameEvent.register("entity_mount");
    public static final Holder.Reference<GameEvent> ENTITY_PLACE = GameEvent.register("entity_place");
    public static final Holder.Reference<GameEvent> ENTITY_ACTION = GameEvent.register("entity_action");
    public static final Holder.Reference<GameEvent> EQUIP = GameEvent.register("equip");
    public static final Holder.Reference<GameEvent> EXPLODE = GameEvent.register("explode");
    public static final Holder.Reference<GameEvent> FLAP = GameEvent.register("flap");
    public static final Holder.Reference<GameEvent> FLUID_PICKUP = GameEvent.register("fluid_pickup");
    public static final Holder.Reference<GameEvent> FLUID_PLACE = GameEvent.register("fluid_place");
    public static final Holder.Reference<GameEvent> HIT_GROUND = GameEvent.register("hit_ground");
    public static final Holder.Reference<GameEvent> INSTRUMENT_PLAY = GameEvent.register("instrument_play");
    public static final Holder.Reference<GameEvent> ITEM_INTERACT_FINISH = GameEvent.register("item_interact_finish");
    public static final Holder.Reference<GameEvent> ITEM_INTERACT_START = GameEvent.register("item_interact_start");
    public static final Holder.Reference<GameEvent> JUKEBOX_PLAY = GameEvent.register("jukebox_play", 10);
    public static final Holder.Reference<GameEvent> JUKEBOX_STOP_PLAY = GameEvent.register("jukebox_stop_play", 10);
    public static final Holder.Reference<GameEvent> LIGHTNING_STRIKE = GameEvent.register("lightning_strike");
    public static final Holder.Reference<GameEvent> NOTE_BLOCK_PLAY = GameEvent.register("note_block_play");
    public static final Holder.Reference<GameEvent> PRIME_FUSE = GameEvent.register("prime_fuse");
    public static final Holder.Reference<GameEvent> PROJECTILE_LAND = GameEvent.register("projectile_land");
    public static final Holder.Reference<GameEvent> PROJECTILE_SHOOT = GameEvent.register("projectile_shoot");
    public static final Holder.Reference<GameEvent> SCULK_SENSOR_TENDRILS_CLICKING = GameEvent.register("sculk_sensor_tendrils_clicking");
    public static final Holder.Reference<GameEvent> SHEAR = GameEvent.register("shear");
    public static final Holder.Reference<GameEvent> SHRIEK = GameEvent.register("shriek", 32);
    public static final Holder.Reference<GameEvent> SPLASH = GameEvent.register("splash");
    public static final Holder.Reference<GameEvent> STEP = GameEvent.register("step");
    public static final Holder.Reference<GameEvent> SWIM = GameEvent.register("swim");
    public static final Holder.Reference<GameEvent> TELEPORT = GameEvent.register("teleport");
    public static final Holder.Reference<GameEvent> UNEQUIP = GameEvent.register("unequip");
    public static final Holder.Reference<GameEvent> RESONATE_1 = GameEvent.register("resonate_1");
    public static final Holder.Reference<GameEvent> RESONATE_2 = GameEvent.register("resonate_2");
    public static final Holder.Reference<GameEvent> RESONATE_3 = GameEvent.register("resonate_3");
    public static final Holder.Reference<GameEvent> RESONATE_4 = GameEvent.register("resonate_4");
    public static final Holder.Reference<GameEvent> RESONATE_5 = GameEvent.register("resonate_5");
    public static final Holder.Reference<GameEvent> RESONATE_6 = GameEvent.register("resonate_6");
    public static final Holder.Reference<GameEvent> RESONATE_7 = GameEvent.register("resonate_7");
    public static final Holder.Reference<GameEvent> RESONATE_8 = GameEvent.register("resonate_8");
    public static final Holder.Reference<GameEvent> RESONATE_9 = GameEvent.register("resonate_9");
    public static final Holder.Reference<GameEvent> RESONATE_10 = GameEvent.register("resonate_10");
    public static final Holder.Reference<GameEvent> RESONATE_11 = GameEvent.register("resonate_11");
    public static final Holder.Reference<GameEvent> RESONATE_12 = GameEvent.register("resonate_12");
    public static final Holder.Reference<GameEvent> RESONATE_13 = GameEvent.register("resonate_13");
    public static final Holder.Reference<GameEvent> RESONATE_14 = GameEvent.register("resonate_14");
    public static final Holder.Reference<GameEvent> RESONATE_15 = GameEvent.register("resonate_15");
    public static final int DEFAULT_NOTIFICATION_RADIUS = 16;
    public static final Codec<Holder<GameEvent>> CODEC = RegistryFixedCodec.create(Registries.GAME_EVENT);

    public static Holder<GameEvent> bootstrap(Registry<GameEvent> $$0) {
        return BLOCK_ACTIVATE;
    }

    private static Holder.Reference<GameEvent> register(String $$0) {
        return GameEvent.register($$0, 16);
    }

    private static Holder.Reference<GameEvent> register(String $$0, int $$1) {
        return Registry.registerForHolder(BuiltInRegistries.GAME_EVENT, ResourceLocation.withDefaultNamespace($$0), new GameEvent($$1));
    }

    public static final class ListenerInfo
    implements Comparable<ListenerInfo> {
        private final Holder<GameEvent> gameEvent;
        private final Vec3 source;
        private final Context context;
        private final GameEventListener recipient;
        private final double distanceToRecipient;

        public ListenerInfo(Holder<GameEvent> $$0, Vec3 $$1, Context $$2, GameEventListener $$3, Vec3 $$4) {
            this.gameEvent = $$0;
            this.source = $$1;
            this.context = $$2;
            this.recipient = $$3;
            this.distanceToRecipient = $$1.distanceToSqr($$4);
        }

        @Override
        public int compareTo(ListenerInfo $$0) {
            return Double.compare(this.distanceToRecipient, $$0.distanceToRecipient);
        }

        public Holder<GameEvent> gameEvent() {
            return this.gameEvent;
        }

        public Vec3 source() {
            return this.source;
        }

        public Context context() {
            return this.context;
        }

        public GameEventListener recipient() {
            return this.recipient;
        }

        @Override
        public /* synthetic */ int compareTo(Object object) {
            return this.compareTo((ListenerInfo)object);
        }
    }

    public record Context(@Nullable Entity sourceEntity, @Nullable BlockState affectedState) {
        public static Context of(@Nullable Entity $$0) {
            return new Context($$0, null);
        }

        public static Context of(@Nullable BlockState $$0) {
            return new Context(null, $$0);
        }

        public static Context of(@Nullable Entity $$0, @Nullable BlockState $$1) {
            return new Context($$0, $$1);
        }

        @Nullable
        public Entity sourceEntity() {
            return this.sourceEntity;
        }

        @Nullable
        public BlockState affectedState() {
            return this.affectedState;
        }
    }
}

