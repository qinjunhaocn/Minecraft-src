/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 */
package net.minecraft.client.resources;

import com.mojang.authlib.GameProfile;
import java.util.UUID;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.resources.ResourceLocation;

public class DefaultPlayerSkin {
    private static final PlayerSkin[] DEFAULT_SKINS = new PlayerSkin[]{DefaultPlayerSkin.create("textures/entity/player/slim/alex.png", PlayerSkin.Model.SLIM), DefaultPlayerSkin.create("textures/entity/player/slim/ari.png", PlayerSkin.Model.SLIM), DefaultPlayerSkin.create("textures/entity/player/slim/efe.png", PlayerSkin.Model.SLIM), DefaultPlayerSkin.create("textures/entity/player/slim/kai.png", PlayerSkin.Model.SLIM), DefaultPlayerSkin.create("textures/entity/player/slim/makena.png", PlayerSkin.Model.SLIM), DefaultPlayerSkin.create("textures/entity/player/slim/noor.png", PlayerSkin.Model.SLIM), DefaultPlayerSkin.create("textures/entity/player/slim/steve.png", PlayerSkin.Model.SLIM), DefaultPlayerSkin.create("textures/entity/player/slim/sunny.png", PlayerSkin.Model.SLIM), DefaultPlayerSkin.create("textures/entity/player/slim/zuri.png", PlayerSkin.Model.SLIM), DefaultPlayerSkin.create("textures/entity/player/wide/alex.png", PlayerSkin.Model.WIDE), DefaultPlayerSkin.create("textures/entity/player/wide/ari.png", PlayerSkin.Model.WIDE), DefaultPlayerSkin.create("textures/entity/player/wide/efe.png", PlayerSkin.Model.WIDE), DefaultPlayerSkin.create("textures/entity/player/wide/kai.png", PlayerSkin.Model.WIDE), DefaultPlayerSkin.create("textures/entity/player/wide/makena.png", PlayerSkin.Model.WIDE), DefaultPlayerSkin.create("textures/entity/player/wide/noor.png", PlayerSkin.Model.WIDE), DefaultPlayerSkin.create("textures/entity/player/wide/steve.png", PlayerSkin.Model.WIDE), DefaultPlayerSkin.create("textures/entity/player/wide/sunny.png", PlayerSkin.Model.WIDE), DefaultPlayerSkin.create("textures/entity/player/wide/zuri.png", PlayerSkin.Model.WIDE)};

    public static ResourceLocation getDefaultTexture() {
        return DefaultPlayerSkin.getDefaultSkin().texture();
    }

    public static PlayerSkin getDefaultSkin() {
        return DEFAULT_SKINS[6];
    }

    public static PlayerSkin get(UUID $$0) {
        return DEFAULT_SKINS[Math.floorMod($$0.hashCode(), DEFAULT_SKINS.length)];
    }

    public static PlayerSkin get(GameProfile $$0) {
        return DefaultPlayerSkin.get($$0.getId());
    }

    private static PlayerSkin create(String $$0, PlayerSkin.Model $$1) {
        return new PlayerSkin(ResourceLocation.withDefaultNamespace($$0), null, null, null, $$1, true);
    }
}

