/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.google.gson.annotations.SerializedName
 *  com.mojang.logging.LogUtils
 */
package com.mojang.realmsclient.util;

import com.google.gson.annotations.SerializedName;
import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.dto.GuardedSerializer;
import com.mojang.realmsclient.dto.ReflectionBasedSerialization;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import net.minecraft.client.Minecraft;
import org.slf4j.Logger;

public class RealmsPersistence {
    private static final String FILE_NAME = "realms_persistence.json";
    private static final GuardedSerializer GSON = new GuardedSerializer();
    private static final Logger LOGGER = LogUtils.getLogger();

    public RealmsPersistenceData read() {
        return RealmsPersistence.readFile();
    }

    public void save(RealmsPersistenceData $$0) {
        RealmsPersistence.writeFile($$0);
    }

    public static RealmsPersistenceData readFile() {
        Path $$0 = RealmsPersistence.getPathToData();
        try {
            String $$1 = Files.readString((Path)$$0, (Charset)StandardCharsets.UTF_8);
            RealmsPersistenceData $$2 = GSON.fromJson($$1, RealmsPersistenceData.class);
            if ($$2 != null) {
                return $$2;
            }
        } catch (NoSuchFileException $$1) {
        } catch (Exception $$3) {
            LOGGER.warn("Failed to read Realms storage {}", (Object)$$0, (Object)$$3);
        }
        return new RealmsPersistenceData();
    }

    public static void writeFile(RealmsPersistenceData $$0) {
        Path $$1 = RealmsPersistence.getPathToData();
        try {
            Files.writeString((Path)$$1, (CharSequence)GSON.toJson($$0), (Charset)StandardCharsets.UTF_8, (OpenOption[])new OpenOption[0]);
        } catch (Exception exception) {
            // empty catch block
        }
    }

    private static Path getPathToData() {
        return Minecraft.getInstance().gameDirectory.toPath().resolve(FILE_NAME);
    }

    public static class RealmsPersistenceData
    implements ReflectionBasedSerialization {
        @SerializedName(value="newsLink")
        public String newsLink;
        @SerializedName(value="hasUnreadNews")
        public boolean hasUnreadNews;
    }
}

