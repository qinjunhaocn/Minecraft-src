/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.world.level.levelgen.structure.templatesystem.rule.blockentity;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.structure.templatesystem.rule.blockentity.RuleBlockEntityModifier;
import net.minecraft.world.level.levelgen.structure.templatesystem.rule.blockentity.RuleBlockEntityModifierType;
import net.minecraft.world.level.storage.loot.LootTable;

public class AppendLoot
implements RuleBlockEntityModifier {
    public static final MapCodec<AppendLoot> CODEC = RecordCodecBuilder.mapCodec($$02 -> $$02.group((App)LootTable.KEY_CODEC.fieldOf("loot_table").forGetter($$0 -> $$0.lootTable)).apply((Applicative)$$02, AppendLoot::new));
    private final ResourceKey<LootTable> lootTable;

    public AppendLoot(ResourceKey<LootTable> $$0) {
        this.lootTable = $$0;
    }

    @Override
    public CompoundTag apply(RandomSource $$0, @Nullable CompoundTag $$1) {
        CompoundTag $$2 = $$1 == null ? new CompoundTag() : $$1.copy();
        $$2.store("LootTable", LootTable.KEY_CODEC, this.lootTable);
        $$2.putLong("LootTableSeed", $$0.nextLong());
        return $$2;
    }

    @Override
    public RuleBlockEntityModifierType<?> getType() {
        return RuleBlockEntityModifierType.APPEND_LOOT;
    }
}

