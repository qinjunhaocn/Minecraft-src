/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.google.gson.annotations.SerializedName
 */
package com.mojang.realmsclient.dto;

import com.google.gson.annotations.SerializedName;
import com.mojang.realmsclient.dto.GuardedSerializer;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.ReflectionBasedSerialization;
import com.mojang.realmsclient.dto.ValueObject;
import javax.annotation.Nullable;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.util.StringUtil;
import net.minecraft.world.Difficulty;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.LevelSettings;

public class RealmsWorldOptions
extends ValueObject
implements ReflectionBasedSerialization {
    @SerializedName(value="pvp")
    public boolean pvp = true;
    @SerializedName(value="spawnMonsters")
    public boolean spawnMonsters = true;
    @SerializedName(value="spawnProtection")
    public int spawnProtection = 0;
    @SerializedName(value="commandBlocks")
    public boolean commandBlocks = false;
    @SerializedName(value="forceGameMode")
    public boolean forceGameMode = false;
    @SerializedName(value="difficulty")
    public int difficulty = 2;
    @SerializedName(value="gameMode")
    public int gameMode = 0;
    @SerializedName(value="slotName")
    private String slotName = "";
    @SerializedName(value="version")
    public String version = "";
    @SerializedName(value="compatibility")
    public RealmsServer.Compatibility compatibility = RealmsServer.Compatibility.UNVERIFIABLE;
    @SerializedName(value="worldTemplateId")
    public long templateId = -1L;
    @Nullable
    @SerializedName(value="worldTemplateImage")
    public String templateImage = null;
    public boolean empty;

    private RealmsWorldOptions() {
    }

    public RealmsWorldOptions(boolean $$0, boolean $$1, int $$2, boolean $$3, int $$4, int $$5, boolean $$6, String $$7, String $$8, RealmsServer.Compatibility $$9) {
        this.pvp = $$0;
        this.spawnMonsters = $$1;
        this.spawnProtection = $$2;
        this.commandBlocks = $$3;
        this.difficulty = $$4;
        this.gameMode = $$5;
        this.forceGameMode = $$6;
        this.slotName = $$7;
        this.version = $$8;
        this.compatibility = $$9;
    }

    public static RealmsWorldOptions createDefaults() {
        return new RealmsWorldOptions();
    }

    public static RealmsWorldOptions createDefaultsWith(GameType $$0, boolean $$1, Difficulty $$2, boolean $$3, String $$4, String $$5) {
        RealmsWorldOptions $$6 = RealmsWorldOptions.createDefaults();
        $$6.commandBlocks = $$1;
        $$6.difficulty = $$2.getId();
        $$6.gameMode = $$0.getId();
        $$6.slotName = $$5;
        $$6.version = $$4;
        return $$6;
    }

    public static RealmsWorldOptions createFromSettings(LevelSettings $$0, boolean $$1, String $$2) {
        return RealmsWorldOptions.createDefaultsWith($$0.gameType(), $$1, $$0.difficulty(), $$0.hardcore(), $$2, $$0.levelName());
    }

    public static RealmsWorldOptions createEmptyDefaults() {
        RealmsWorldOptions $$0 = RealmsWorldOptions.createDefaults();
        $$0.setEmpty(true);
        return $$0;
    }

    public void setEmpty(boolean $$0) {
        this.empty = $$0;
    }

    public static RealmsWorldOptions parse(GuardedSerializer $$0, String $$1) {
        RealmsWorldOptions $$2 = $$0.fromJson($$1, RealmsWorldOptions.class);
        if ($$2 == null) {
            return RealmsWorldOptions.createDefaults();
        }
        RealmsWorldOptions.finalize($$2);
        return $$2;
    }

    private static void finalize(RealmsWorldOptions $$0) {
        if ($$0.slotName == null) {
            $$0.slotName = "";
        }
        if ($$0.version == null) {
            $$0.version = "";
        }
        if ($$0.compatibility == null) {
            $$0.compatibility = RealmsServer.Compatibility.UNVERIFIABLE;
        }
    }

    public String getSlotName(int $$0) {
        if (StringUtil.isBlank(this.slotName)) {
            if (this.empty) {
                return I18n.a("mco.configure.world.slot.empty", new Object[0]);
            }
            return this.getDefaultSlotName($$0);
        }
        return this.slotName;
    }

    public String getDefaultSlotName(int $$0) {
        return I18n.a("mco.configure.world.slot", $$0);
    }

    public RealmsWorldOptions clone() {
        return new RealmsWorldOptions(this.pvp, this.spawnMonsters, this.spawnProtection, this.commandBlocks, this.difficulty, this.gameMode, this.forceGameMode, this.slotName, this.version, this.compatibility);
    }

    public /* synthetic */ Object clone() throws CloneNotSupportedException {
        return this.clone();
    }
}

