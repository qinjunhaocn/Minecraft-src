/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.google.gson.annotations.SerializedName
 */
package com.mojang.realmsclient.dto;

import com.google.gson.annotations.SerializedName;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.RealmsWorldOptions;
import com.mojang.realmsclient.dto.ReflectionBasedSerialization;
import javax.annotation.Nullable;

public final class RealmsSlotUpdateDto
implements ReflectionBasedSerialization {
    @SerializedName(value="slotId")
    public final int slotId;
    @SerializedName(value="pvp")
    private final boolean pvp;
    @SerializedName(value="spawnMonsters")
    private final boolean spawnMonsters;
    @SerializedName(value="spawnProtection")
    private final int spawnProtection;
    @SerializedName(value="commandBlocks")
    private final boolean commandBlocks;
    @SerializedName(value="forceGameMode")
    private final boolean forceGameMode;
    @SerializedName(value="difficulty")
    private final int difficulty;
    @SerializedName(value="gameMode")
    private final int gameMode;
    @SerializedName(value="slotName")
    private final String slotName;
    @SerializedName(value="version")
    private final String version;
    @SerializedName(value="compatibility")
    private final RealmsServer.Compatibility compatibility;
    @SerializedName(value="worldTemplateId")
    private final long templateId;
    @Nullable
    @SerializedName(value="worldTemplateImage")
    private final String templateImage;
    @SerializedName(value="hardcore")
    private final boolean hardcore;

    public RealmsSlotUpdateDto(int $$0, RealmsWorldOptions $$1, boolean $$2) {
        this.slotId = $$0;
        this.pvp = $$1.pvp;
        this.spawnMonsters = $$1.spawnMonsters;
        this.spawnProtection = $$1.spawnProtection;
        this.commandBlocks = $$1.commandBlocks;
        this.forceGameMode = $$1.forceGameMode;
        this.difficulty = $$1.difficulty;
        this.gameMode = $$1.gameMode;
        this.slotName = $$1.getSlotName($$0);
        this.version = $$1.version;
        this.compatibility = $$1.compatibility;
        this.templateId = $$1.templateId;
        this.templateImage = $$1.templateImage;
        this.hardcore = $$2;
    }
}

