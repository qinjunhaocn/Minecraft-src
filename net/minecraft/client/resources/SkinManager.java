/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 *  com.mojang.authlib.SignatureState
 *  com.mojang.authlib.minecraft.MinecraftProfileTexture
 *  com.mojang.authlib.minecraft.MinecraftProfileTexture$Type
 *  com.mojang.authlib.minecraft.MinecraftProfileTextures
 *  com.mojang.authlib.minecraft.MinecraftSessionService
 *  com.mojang.authlib.properties.Property
 *  com.mojang.logging.LogUtils
 *  it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
 *  java.lang.MatchException
 *  java.lang.Record
 *  java.lang.runtime.ObjectMethods
 */
package net.minecraft.client.resources;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.hash.Hashing;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.SignatureState;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftProfileTextures;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.properties.Property;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.Optionull;
import net.minecraft.Util;
import net.minecraft.client.renderer.texture.SkinTextureDownloader;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;

public class SkinManager {
    static final Logger LOGGER = LogUtils.getLogger();
    private final MinecraftSessionService sessionService;
    private final LoadingCache<CacheKey, CompletableFuture<Optional<PlayerSkin>>> skinCache;
    private final TextureCache skinTextures;
    private final TextureCache capeTextures;
    private final TextureCache elytraTextures;

    public SkinManager(Path $$0, final MinecraftSessionService $$1, final Executor $$2) {
        this.sessionService = $$1;
        this.skinTextures = new TextureCache($$0, MinecraftProfileTexture.Type.SKIN);
        this.capeTextures = new TextureCache($$0, MinecraftProfileTexture.Type.CAPE);
        this.elytraTextures = new TextureCache($$0, MinecraftProfileTexture.Type.ELYTRA);
        this.skinCache = CacheBuilder.newBuilder().expireAfterAccess(Duration.ofSeconds(15L)).build(new CacheLoader<CacheKey, CompletableFuture<Optional<PlayerSkin>>>(){

            @Override
            public CompletableFuture<Optional<PlayerSkin>> load(CacheKey $$0) {
                return ((CompletableFuture)CompletableFuture.supplyAsync(() -> {
                    Property $$22 = $$0.packedTextures();
                    if ($$22 == null) {
                        return MinecraftProfileTextures.EMPTY;
                    }
                    MinecraftProfileTextures $$3 = $$1.unpackTextures($$22);
                    if ($$3.signatureState() == SignatureState.INVALID) {
                        LOGGER.warn("Profile contained invalid signature for textures property (profile id: {})", (Object)$$0.profileId());
                    }
                    return $$3;
                }, Util.backgroundExecutor().forName("unpackSkinTextures")).thenComposeAsync($$1 -> SkinManager.this.registerTextures($$0.profileId(), (MinecraftProfileTextures)$$1), $$2)).handle(($$1, $$2) -> {
                    if ($$2 != null) {
                        LOGGER.warn("Failed to load texture for profile {}", (Object)$$0.profileId, $$2);
                    }
                    return Optional.ofNullable($$1);
                });
            }

            @Override
            public /* synthetic */ Object load(Object object) throws Exception {
                return this.load((CacheKey)((Object)object));
            }
        });
    }

    public Supplier<PlayerSkin> lookupInsecure(GameProfile $$0) {
        CompletableFuture<Optional<PlayerSkin>> $$1 = this.getOrLoad($$0);
        PlayerSkin $$2 = DefaultPlayerSkin.get($$0);
        return () -> $$1.getNow(Optional.empty()).orElse($$2);
    }

    public PlayerSkin getInsecureSkin(GameProfile $$0) {
        PlayerSkin $$1 = this.getInsecureSkin($$0, null);
        if ($$1 != null) {
            return $$1;
        }
        return DefaultPlayerSkin.get($$0);
    }

    @Nullable
    public PlayerSkin getInsecureSkin(GameProfile $$0, @Nullable PlayerSkin $$1) {
        return this.getOrLoad($$0).getNow(Optional.empty()).orElse($$1);
    }

    public CompletableFuture<Optional<PlayerSkin>> getOrLoad(GameProfile $$0) {
        Property $$1 = this.sessionService.getPackedTextures($$0);
        return this.skinCache.getUnchecked(new CacheKey($$0.getId(), $$1));
    }

    CompletableFuture<PlayerSkin> registerTextures(UUID $$0, MinecraftProfileTextures $$1) {
        PlayerSkin.Model $$7;
        CompletableFuture<ResourceLocation> $$62;
        MinecraftProfileTexture $$2 = $$1.skin();
        if ($$2 != null) {
            CompletableFuture<ResourceLocation> $$3 = this.skinTextures.getOrLoad($$2);
            PlayerSkin.Model $$4 = PlayerSkin.Model.byName($$2.getMetadata("model"));
        } else {
            PlayerSkin $$5 = DefaultPlayerSkin.get($$0);
            $$62 = CompletableFuture.completedFuture($$5.texture());
            $$7 = $$5.model();
        }
        String $$8 = Optionull.map($$2, MinecraftProfileTexture::getUrl);
        MinecraftProfileTexture $$9 = $$1.cape();
        CompletableFuture<Object> $$10 = $$9 != null ? this.capeTextures.getOrLoad($$9) : CompletableFuture.completedFuture(null);
        MinecraftProfileTexture $$11 = $$1.elytra();
        CompletableFuture<Object> $$12 = $$11 != null ? this.elytraTextures.getOrLoad($$11) : CompletableFuture.completedFuture(null);
        return CompletableFuture.allOf($$62, $$10, $$12).thenApply($$6 -> new PlayerSkin((ResourceLocation)$$62.join(), $$8, (ResourceLocation)$$10.join(), (ResourceLocation)$$12.join(), $$7, $$1.signatureState() == SignatureState.SIGNED));
    }

    static class TextureCache {
        private final Path root;
        private final MinecraftProfileTexture.Type type;
        private final Map<String, CompletableFuture<ResourceLocation>> textures = new Object2ObjectOpenHashMap();

        TextureCache(Path $$0, MinecraftProfileTexture.Type $$1) {
            this.root = $$0;
            this.type = $$1;
        }

        public CompletableFuture<ResourceLocation> getOrLoad(MinecraftProfileTexture $$0) {
            String $$1 = $$0.getHash();
            CompletableFuture<ResourceLocation> $$2 = this.textures.get($$1);
            if ($$2 == null) {
                $$2 = this.registerTexture($$0);
                this.textures.put($$1, $$2);
            }
            return $$2;
        }

        private CompletableFuture<ResourceLocation> registerTexture(MinecraftProfileTexture $$0) {
            String $$1 = Hashing.sha1().hashUnencodedChars($$0.getHash()).toString();
            ResourceLocation $$2 = this.getTextureLocation($$1);
            Path $$3 = this.root.resolve($$1.length() > 2 ? $$1.substring(0, 2) : "xx").resolve($$1);
            return SkinTextureDownloader.downloadAndRegisterSkin($$2, $$3, $$0.getUrl(), this.type == MinecraftProfileTexture.Type.SKIN);
        }

        private ResourceLocation getTextureLocation(String $$0) {
            String $$1 = switch (this.type) {
                default -> throw new MatchException(null, null);
                case MinecraftProfileTexture.Type.SKIN -> "skins";
                case MinecraftProfileTexture.Type.CAPE -> "capes";
                case MinecraftProfileTexture.Type.ELYTRA -> "elytra";
            };
            return ResourceLocation.withDefaultNamespace($$1 + "/" + $$0);
        }
    }

    static final class CacheKey
    extends Record {
        final UUID profileId;
        @Nullable
        private final Property packedTextures;

        CacheKey(UUID $$0, @Nullable Property $$1) {
            this.profileId = $$0;
            this.packedTextures = $$1;
        }

        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{CacheKey.class, "profileId;packedTextures", "profileId", "packedTextures"}, this);
        }

        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{CacheKey.class, "profileId;packedTextures", "profileId", "packedTextures"}, this);
        }

        public final boolean equals(Object $$0) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{CacheKey.class, "profileId;packedTextures", "profileId", "packedTextures"}, this, $$0);
        }

        public UUID profileId() {
            return this.profileId;
        }

        @Nullable
        public Property packedTextures() {
            return this.packedTextures;
        }
    }
}

