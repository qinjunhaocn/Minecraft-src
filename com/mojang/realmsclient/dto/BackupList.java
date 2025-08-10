/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.mojang.logging.LogUtils
 */
package com.mojang.realmsclient.dto;

import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.dto.Backup;
import com.mojang.realmsclient.dto.ValueObject;
import java.util.List;
import net.minecraft.util.LenientJsonParser;
import org.slf4j.Logger;

public class BackupList
extends ValueObject {
    private static final Logger LOGGER = LogUtils.getLogger();
    public List<Backup> backups;

    public static BackupList parse(String $$0) {
        BackupList $$1 = new BackupList();
        $$1.backups = Lists.newArrayList();
        try {
            JsonElement $$2 = LenientJsonParser.parse($$0).getAsJsonObject().get("backups");
            if ($$2.isJsonArray()) {
                for (JsonElement $$3 : $$2.getAsJsonArray()) {
                    $$1.backups.add(Backup.parse($$3));
                }
            }
        } catch (Exception $$4) {
            LOGGER.error("Could not parse BackupList: {}", (Object)$$4.getMessage());
        }
        return $$1;
    }
}

