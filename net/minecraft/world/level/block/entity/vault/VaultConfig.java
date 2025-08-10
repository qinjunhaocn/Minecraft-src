/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.world.level.block.entity.vault;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.trialspawner.PlayerDetector;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootTable;

public record VaultConfig(ResourceKey<LootTable> lootTable, double activationRange, double deactivationRange, ItemStack keyItem, Optional<ResourceKey<LootTable>> overrideLootTableToDisplay, PlayerDetector playerDetector, PlayerDetector.EntitySelector entitySelector) {
    static final String TAG_NAME = "config";
    static VaultConfig DEFAULT = new VaultConfig();
    static Codec<VaultConfig> CODEC = RecordCodecBuilder.create($$0 -> $$0.group((App)LootTable.KEY_CODEC.lenientOptionalFieldOf("loot_table", DEFAULT.lootTable()).forGetter(VaultConfig::lootTable), (App)Codec.DOUBLE.lenientOptionalFieldOf("activation_range", (Object)DEFAULT.activationRange()).forGetter(VaultConfig::activationRange), (App)Codec.DOUBLE.lenientOptionalFieldOf("deactivation_range", (Object)DEFAULT.deactivationRange()).forGetter(VaultConfig::deactivationRange), (App)ItemStack.lenientOptionalFieldOf("key_item").forGetter(VaultConfig::keyItem), (App)LootTable.KEY_CODEC.lenientOptionalFieldOf("override_loot_table_to_display").forGetter(VaultConfig::overrideLootTableToDisplay)).apply((Applicative)$$0, VaultConfig::new)).validate(VaultConfig::validate);

    private VaultConfig() {
        this(BuiltInLootTables.TRIAL_CHAMBERS_REWARD, 4.0, 4.5, new ItemStack(Items.TRIAL_KEY), Optional.empty(), PlayerDetector.INCLUDING_CREATIVE_PLAYERS, PlayerDetector.EntitySelector.SELECT_FROM_LEVEL);
    }

    public VaultConfig(ResourceKey<LootTable> $$0, double $$1, double $$2, ItemStack $$3, Optional<ResourceKey<LootTable>> $$4) {
        this($$0, $$1, $$2, $$3, $$4, DEFAULT.playerDetector(), DEFAULT.entitySelector());
    }

    private DataResult<VaultConfig> validate() {
        if (this.activationRange > this.deactivationRange) {
            return DataResult.error(() -> "Activation range must (" + this.activationRange + ") be less or equal to deactivation range (" + this.deactivationRange + ")");
        }
        return DataResult.success((Object)((Object)this));
    }
}

