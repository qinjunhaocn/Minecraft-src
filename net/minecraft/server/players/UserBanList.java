/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 *  com.mojang.authlib.GameProfile
 */
package net.minecraft.server.players;

import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import java.io.File;
import java.util.Objects;
import net.minecraft.server.players.StoredUserEntry;
import net.minecraft.server.players.StoredUserList;
import net.minecraft.server.players.UserBanListEntry;

public class UserBanList
extends StoredUserList<GameProfile, UserBanListEntry> {
    public UserBanList(File $$0) {
        super($$0);
    }

    @Override
    protected StoredUserEntry<GameProfile> createEntry(JsonObject $$0) {
        return new UserBanListEntry($$0);
    }

    public boolean isBanned(GameProfile $$0) {
        return this.contains($$0);
    }

    @Override
    public String[] a() {
        return (String[])this.getEntries().stream().map(StoredUserEntry::getUser).filter(Objects::nonNull).map(GameProfile::getName).toArray(String[]::new);
    }

    @Override
    protected String getKeyForUser(GameProfile $$0) {
        return $$0.getId().toString();
    }

    @Override
    protected /* synthetic */ String getKeyForUser(Object object) {
        return this.getKeyForUser((GameProfile)object);
    }
}

