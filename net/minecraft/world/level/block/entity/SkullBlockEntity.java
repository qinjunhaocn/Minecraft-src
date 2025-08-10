/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 *  com.mojang.authlib.yggdrasil.ProfileResult
 */
package net.minecraft.world.level.block.entity;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.yggdrasil.ProfileResult;
import java.time.Duration;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BooleanSupplier;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.Services;
import net.minecraft.util.StringUtil;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SkullBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class SkullBlockEntity
extends BlockEntity {
    private static final String TAG_PROFILE = "profile";
    private static final String TAG_NOTE_BLOCK_SOUND = "note_block_sound";
    private static final String TAG_CUSTOM_NAME = "custom_name";
    @Nullable
    private static Executor mainThreadExecutor;
    @Nullable
    private static LoadingCache<String, CompletableFuture<Optional<GameProfile>>> profileCacheByName;
    @Nullable
    private static LoadingCache<UUID, CompletableFuture<Optional<GameProfile>>> profileCacheById;
    public static final Executor CHECKED_MAIN_THREAD_EXECUTOR;
    @Nullable
    private ResolvableProfile owner;
    @Nullable
    private ResourceLocation noteBlockSound;
    private int animationTickCount;
    private boolean isAnimating;
    @Nullable
    private Component customName;

    public SkullBlockEntity(BlockPos $$0, BlockState $$1) {
        super(BlockEntityType.SKULL, $$0, $$1);
    }

    public static void setup(final Services $$0, Executor $$1) {
        mainThreadExecutor = $$1;
        final BooleanSupplier $$2 = () -> profileCacheById == null;
        profileCacheByName = CacheBuilder.newBuilder().expireAfterAccess(Duration.ofMinutes(10L)).maximumSize(256L).build(new CacheLoader<String, CompletableFuture<Optional<GameProfile>>>(){

            @Override
            public CompletableFuture<Optional<GameProfile>> load(String $$02) {
                return SkullBlockEntity.fetchProfileByName($$02, $$0);
            }

            @Override
            public /* synthetic */ Object load(Object object) throws Exception {
                return this.load((String)object);
            }
        });
        profileCacheById = CacheBuilder.newBuilder().expireAfterAccess(Duration.ofMinutes(10L)).maximumSize(256L).build(new CacheLoader<UUID, CompletableFuture<Optional<GameProfile>>>(){

            @Override
            public CompletableFuture<Optional<GameProfile>> load(UUID $$02) {
                return SkullBlockEntity.fetchProfileById($$02, $$0, $$2);
            }

            @Override
            public /* synthetic */ Object load(Object object) throws Exception {
                return this.load((UUID)object);
            }
        });
    }

    static CompletableFuture<Optional<GameProfile>> fetchProfileByName(String $$02, Services $$1) {
        return $$1.profileCache().getAsync($$02).thenCompose($$0 -> {
            LoadingCache<UUID, CompletableFuture<Optional<GameProfile>>> $$12 = profileCacheById;
            if ($$12 == null || $$0.isEmpty()) {
                return CompletableFuture.completedFuture(Optional.empty());
            }
            return $$12.getUnchecked(((GameProfile)$$0.get()).getId()).thenApply($$1 -> $$1.or(() -> $$0));
        });
    }

    static CompletableFuture<Optional<GameProfile>> fetchProfileById(UUID $$0, Services $$1, BooleanSupplier $$2) {
        return CompletableFuture.supplyAsync(() -> {
            if ($$2.getAsBoolean()) {
                return Optional.empty();
            }
            ProfileResult $$3 = $$1.sessionService().fetchProfile($$0, true);
            return Optional.ofNullable($$3).map(ProfileResult::profile);
        }, Util.backgroundExecutor().forName("fetchProfile"));
    }

    public static void clear() {
        mainThreadExecutor = null;
        profileCacheByName = null;
        profileCacheById = null;
    }

    @Override
    protected void saveAdditional(ValueOutput $$0) {
        super.saveAdditional($$0);
        $$0.storeNullable(TAG_PROFILE, ResolvableProfile.CODEC, this.owner);
        $$0.storeNullable(TAG_NOTE_BLOCK_SOUND, ResourceLocation.CODEC, this.noteBlockSound);
        $$0.storeNullable(TAG_CUSTOM_NAME, ComponentSerialization.CODEC, this.customName);
    }

    @Override
    protected void loadAdditional(ValueInput $$0) {
        super.loadAdditional($$0);
        this.setOwner($$0.read(TAG_PROFILE, ResolvableProfile.CODEC).orElse(null));
        this.noteBlockSound = $$0.read(TAG_NOTE_BLOCK_SOUND, ResourceLocation.CODEC).orElse(null);
        this.customName = SkullBlockEntity.parseCustomNameSafe($$0, TAG_CUSTOM_NAME);
    }

    public static void animation(Level $$0, BlockPos $$1, BlockState $$2, SkullBlockEntity $$3) {
        if ($$2.hasProperty(SkullBlock.POWERED) && $$2.getValue(SkullBlock.POWERED).booleanValue()) {
            $$3.isAnimating = true;
            ++$$3.animationTickCount;
        } else {
            $$3.isAnimating = false;
        }
    }

    public float getAnimation(float $$0) {
        if (this.isAnimating) {
            return (float)this.animationTickCount + $$0;
        }
        return this.animationTickCount;
    }

    @Nullable
    public ResolvableProfile getOwnerProfile() {
        return this.owner;
    }

    @Nullable
    public ResourceLocation getNoteBlockSound() {
        return this.noteBlockSound;
    }

    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider $$0) {
        return this.saveCustomOnly($$0);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void setOwner(@Nullable ResolvableProfile $$0) {
        SkullBlockEntity skullBlockEntity = this;
        synchronized (skullBlockEntity) {
            this.owner = $$0;
        }
        this.updateOwnerProfile();
    }

    private void updateOwnerProfile() {
        if (this.owner == null || this.owner.isResolved()) {
            this.setChanged();
            return;
        }
        this.owner.resolve().thenAcceptAsync($$0 -> {
            this.owner = $$0;
            this.setChanged();
        }, CHECKED_MAIN_THREAD_EXECUTOR);
    }

    public static CompletableFuture<Optional<GameProfile>> fetchGameProfile(String $$0) {
        LoadingCache<String, CompletableFuture<Optional<GameProfile>>> $$1 = profileCacheByName;
        if ($$1 != null && StringUtil.isValidPlayerName($$0)) {
            return $$1.getUnchecked($$0);
        }
        return CompletableFuture.completedFuture(Optional.empty());
    }

    public static CompletableFuture<Optional<GameProfile>> fetchGameProfile(UUID $$0) {
        LoadingCache<UUID, CompletableFuture<Optional<GameProfile>>> $$1 = profileCacheById;
        if ($$1 != null) {
            return $$1.getUnchecked($$0);
        }
        return CompletableFuture.completedFuture(Optional.empty());
    }

    @Override
    protected void applyImplicitComponents(DataComponentGetter $$0) {
        super.applyImplicitComponents($$0);
        this.setOwner($$0.get(DataComponents.PROFILE));
        this.noteBlockSound = $$0.get(DataComponents.NOTE_BLOCK_SOUND);
        this.customName = $$0.get(DataComponents.CUSTOM_NAME);
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder $$0) {
        super.collectImplicitComponents($$0);
        $$0.set(DataComponents.PROFILE, this.owner);
        $$0.set(DataComponents.NOTE_BLOCK_SOUND, this.noteBlockSound);
        $$0.set(DataComponents.CUSTOM_NAME, this.customName);
    }

    @Override
    public void removeComponentsFromTag(ValueOutput $$0) {
        super.removeComponentsFromTag($$0);
        $$0.discard(TAG_PROFILE);
        $$0.discard(TAG_NOTE_BLOCK_SOUND);
        $$0.discard(TAG_CUSTOM_NAME);
    }

    public /* synthetic */ Packet getUpdatePacket() {
        return this.getUpdatePacket();
    }

    static {
        CHECKED_MAIN_THREAD_EXECUTOR = $$0 -> {
            Executor $$1 = mainThreadExecutor;
            if ($$1 != null) {
                $$1.execute($$0);
            }
        };
    }
}

