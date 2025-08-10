/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Lifecycle
 */
package net.minecraft.world.level.storage;

import com.mojang.serialization.Lifecycle;
import java.util.Locale;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.CrashReportCategory;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Difficulty;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.WorldDataConfiguration;
import net.minecraft.world.level.dimension.end.EndDragonFight;
import net.minecraft.world.level.levelgen.WorldOptions;
import net.minecraft.world.level.storage.ServerLevelData;

public interface WorldData {
    public static final int ANVIL_VERSION_ID = 19133;
    public static final int MCREGION_VERSION_ID = 19132;

    public WorldDataConfiguration getDataConfiguration();

    public void setDataConfiguration(WorldDataConfiguration var1);

    public boolean wasModded();

    public Set<String> getKnownServerBrands();

    public Set<String> getRemovedFeatureFlags();

    public void setModdedInfo(String var1, boolean var2);

    default public void fillCrashReportCategory(CrashReportCategory $$0) {
        $$0.setDetail("Known server brands", () -> String.join((CharSequence)", ", this.getKnownServerBrands()));
        $$0.setDetail("Removed feature flags", () -> String.join((CharSequence)", ", this.getRemovedFeatureFlags()));
        $$0.setDetail("Level was modded", () -> Boolean.toString(this.wasModded()));
        $$0.setDetail("Level storage version", () -> {
            int $$0 = this.getVersion();
            return String.format(Locale.ROOT, "0x%05X - %s", $$0, this.getStorageVersionName($$0));
        });
    }

    default public String getStorageVersionName(int $$0) {
        switch ($$0) {
            case 19133: {
                return "Anvil";
            }
            case 19132: {
                return "McRegion";
            }
        }
        return "Unknown?";
    }

    @Nullable
    public CompoundTag getCustomBossEvents();

    public void setCustomBossEvents(@Nullable CompoundTag var1);

    public ServerLevelData overworldData();

    public LevelSettings getLevelSettings();

    public CompoundTag createTag(RegistryAccess var1, @Nullable CompoundTag var2);

    public boolean isHardcore();

    public int getVersion();

    public String getLevelName();

    public GameType getGameType();

    public void setGameType(GameType var1);

    public boolean isAllowCommands();

    public Difficulty getDifficulty();

    public void setDifficulty(Difficulty var1);

    public boolean isDifficultyLocked();

    public void setDifficultyLocked(boolean var1);

    public GameRules getGameRules();

    @Nullable
    public CompoundTag getLoadedPlayerTag();

    public EndDragonFight.Data endDragonFightData();

    public void setEndDragonFightData(EndDragonFight.Data var1);

    public WorldOptions worldGenOptions();

    public boolean isFlatWorld();

    public boolean isDebugWorld();

    public Lifecycle worldGenSettingsLifecycle();

    default public FeatureFlagSet enabledFeatures() {
        return this.getDataConfiguration().enabledFeatures();
    }
}

