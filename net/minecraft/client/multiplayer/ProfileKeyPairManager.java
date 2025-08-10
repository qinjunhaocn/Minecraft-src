/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.minecraft.UserApiService
 */
package net.minecraft.client.multiplayer;

import com.mojang.authlib.minecraft.UserApiService;
import java.nio.file.Path;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import net.minecraft.client.User;
import net.minecraft.client.multiplayer.AccountProfileKeyPairManager;
import net.minecraft.world.entity.player.ProfileKeyPair;

public interface ProfileKeyPairManager {
    public static final ProfileKeyPairManager EMPTY_KEY_MANAGER = new ProfileKeyPairManager(){

        @Override
        public CompletableFuture<Optional<ProfileKeyPair>> prepareKeyPair() {
            return CompletableFuture.completedFuture(Optional.empty());
        }

        @Override
        public boolean shouldRefreshKeyPair() {
            return false;
        }
    };

    public static ProfileKeyPairManager create(UserApiService $$0, User $$1, Path $$2) {
        if ($$1.getType() == User.Type.MSA) {
            return new AccountProfileKeyPairManager($$0, $$1.getProfileId(), $$2);
        }
        return EMPTY_KEY_MANAGER;
    }

    public CompletableFuture<Optional<ProfileKeyPair>> prepareKeyPair();

    public boolean shouldRefreshKeyPair();
}

